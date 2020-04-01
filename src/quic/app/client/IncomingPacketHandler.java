package quic.app.client;

import quic.serialization.frame.QuicConnectionCloseFrame;
import quic.serialization.frame.QuicFrame;
import quic.serialization.frame.QuicStreamFrame;
import quic.serialization.packet.QuicInitialPacket;
import quic.serialization.packet.QuicPacket;
import quic.serialization.packet.QuicShortHeaderPacket;
import quic.app.util.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IncomingPacketHandler implements Runnable {
    private BlockingQueue packetBuffer = new BlockingQueue(1200);

    public IncomingPacketHandler() {
        Thread t = new Thread(this);
        t.start();
    }

    public void addNewPacketToBuffer(QuicPacket packet) throws InterruptedException {
        packetBuffer.enqueue(packet);
    }

    public Object getPacketFromBuffer() throws InterruptedException {
        return packetBuffer.dequeue();
    }

    @Override
    public void run() {
        while (true) {
            try {
                QuicPacket quicPacket = (QuicPacket) packetBuffer.dequeue();
                if (quicPacket instanceof QuicInitialPacket) {
                    if (Arrays.equals(quicPacket.getDcID(), Client.getSourceAdrees())) {
                        Set<QuicFrame> frames = quicPacket.getFrames();
                        Iterator<QuicFrame> frameIterator = frames.iterator();
                        QuicFrame quicFrame = frameIterator.next();
                        if(!(quicFrame instanceof QuicConnectionCloseFrame)) {
                            Client.setDestinationAdress(((QuicInitialPacket) quicPacket).getScID());
                            Client.getFileDownloader().setFlag(2);
                        }else{
                            System.out.println(((QuicConnectionCloseFrame)quicFrame).getReasonPhrase());
                            Client.getFileDownloader().setFlag(1);
                        }
                    } else {
                        QuicFrame connectionCloseFrame = new QuicConnectionCloseFrame(10, 0, "Invalid Destination Adress");
                        Set<QuicFrame> temp = new HashSet<>();
                        temp.add(connectionCloseFrame);
                        QuicPacket initialPacket = new QuicInitialPacket(((QuicInitialPacket) quicPacket).getScID(), 0, 0xff000019L, "0".getBytes(), temp);
                        Client.getSender().addPacketToSend(initialPacket);
                    }
                }
                else if(quicPacket instanceof QuicShortHeaderPacket){
                    Set<QuicFrame> frames = quicPacket.getFrames();
                    Iterator<QuicFrame> frameIterator = frames.iterator();
                    QuicFrame quicFrame = frameIterator.next();
                    if(quicFrame instanceof QuicStreamFrame){
                        if(((QuicStreamFrame) quicFrame).getData().length==0 &&((QuicStreamFrame) quicFrame).isEndOfStream()){
                            System.out.println("File : "+Client.getIncomingStreamHandler().getFileStreamIdMap().get(((QuicStreamFrame) quicFrame).getStreamId())+" Not found");
                        }else{
                            Client.getIncomingStreamHandler().addNewStreamFrame(quicFrame);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
