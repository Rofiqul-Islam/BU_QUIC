package quic.app.server;

import quic.serialization.frame.QuicAckFrame;
import quic.serialization.frame.QuicConnectionCloseFrame;
import quic.serialization.frame.QuicFrame;
import quic.serialization.frame.QuicStreamFrame;
import quic.serialization.packet.QuicInitialPacket;
import quic.serialization.packet.QuicPacket;
import quic.serialization.packet.QuicShortHeaderPacket;
import quic.serialization.util.Util;
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
                    if (Arrays.equals(quicPacket.getDcID(), "0".getBytes())) {
                        Server.setDestinationAdress(((QuicInitialPacket) quicPacket).getScID());
                        QuicPacket.setDcIdSize(Server.getSourceAdrees().length);
                        QuicFrame ackFrame = new QuicAckFrame(0, 0, 0, 1);
                        Set<QuicFrame> temp = new HashSet<>();
                        temp.add(ackFrame);
                        QuicPacket initialReply = new QuicInitialPacket(Server.getDestinationAdress(),0,Server.getVersion(),Server.getSourceAdrees(),temp);
                        Server.getSender().addPacketToSend(initialReply);
                    } else {
                        QuicFrame connectionCloseFrame = new QuicConnectionCloseFrame(10, 0, "Invalid Destination Adress");
                        Set<QuicFrame> temp = new HashSet<>();
                        temp.add(connectionCloseFrame);
                        QuicPacket initialPacket = new QuicInitialPacket(((QuicInitialPacket) quicPacket).getScID(), 0, 0xff000019L, "0".getBytes(), temp);
                        Server.getSender().addPacketToSend(initialPacket);
                    }
                }
                else if(quicPacket instanceof QuicShortHeaderPacket){
                    Set<QuicFrame> frames = quicPacket.getFrames();
                    Iterator<QuicFrame> frameIterator = frames.iterator();
                    while(frameIterator.hasNext()){
                        QuicFrame quicFrame = frameIterator.next();
                        if(quicFrame instanceof QuicStreamFrame){
                            Server.getFileUploader().addFileToUpload((QuicStreamFrame)quicFrame);
                        }else if(quicFrame instanceof QuicAckFrame){
                            Server.getFileUploader().addNewAck(new IncomingAck(quicPacket.getPacketNumber(),((QuicAckFrame) quicFrame).getFirstAckRange()));
                        }else if(quicFrame instanceof QuicConnectionCloseFrame){

                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
