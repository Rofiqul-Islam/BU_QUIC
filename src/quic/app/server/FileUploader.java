package quic.app.server;

import quic.serialization.frame.QuicAckFrame;
import quic.serialization.frame.QuicConnectionCloseFrame;
import quic.serialization.frame.QuicFrame;
import quic.serialization.frame.QuicStreamFrame;
import quic.serialization.packet.QuicPacket;
import quic.serialization.packet.QuicShortHeaderPacket;
import quic.serialization.util.Util;
import quic.app.util.*;

import java.io.*;
import java.sql.ClientInfoStatus;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class FileUploader implements Runnable {
    private BlockingQueue fileToUpload = new BlockingQueue(1200);
    private BlockingQueue incomingAckBuffer = new BlockingQueue(1200);

    public FileUploader() {
        Thread t = new Thread(this);
        t.start();
    }

    public void addFileToUpload(QuicFrame frame) throws InterruptedException {
        fileToUpload.enqueue(frame);
    }

    public Object retrieveFileToUpload() throws InterruptedException {
        return fileToUpload.dequeue();
    }

    public void addNewAck(IncomingAck ack) throws InterruptedException {
        incomingAckBuffer.enqueue(ack);
    }

    public Object retrieveAckFrame() throws InterruptedException {
        return incomingAckBuffer.dequeue();
    }


    @Override
    public void run() {
        QuicStreamFrame quicStreamFrame = null;
        InputStream is = null;
        Set<QuicFrame> temp = new HashSet<>();
        while (true) {
            try {
                quicStreamFrame = (QuicStreamFrame) retrieveFileToUpload();
                String fileName = "C:\\Datacom\\qq\\BU_QUIC_SERVER\\resources\\" + new String(quicStreamFrame.getData());
                is = new FileInputStream(fileName);
                int c = 0;
                int dataSize = is.available();
                while (true) {
                    if (is.available() == 0) {
                        break;
                    }
                    byte[] data;
                    if (is.available() > Server.getPacketSize()) {
                        data = new byte[Server.getPacketSize()];
                    } else {
                        data = new byte[is.available()];
                    }
                    c += is.read(data);
                    temp.clear();
                    if(c==dataSize){
                        temp.add(new QuicStreamFrame(quicStreamFrame.getStreamId(), c, true, data));
                    }else {
                        temp.add(new QuicStreamFrame(quicStreamFrame.getStreamId(), c, false, data));
                    }
                    QuicShortHeaderPacket quicShortHeaderPacket = new QuicShortHeaderPacket(Server.getDestinationAdress(), 0, temp);
                    Server.getSender().addPacketToSend(quicShortHeaderPacket);
                    ////////
                    IncomingAck ack = (IncomingAck) retrieveAckFrame();
                    while (ack.getPacketNumer() != quicStreamFrame.getStreamId() || ack.getOffset() != c + 1) {

                    }

                }
            } catch (FileNotFoundException e) {
                temp.clear();
                temp.add(new QuicStreamFrame(quicStreamFrame.getStreamId(), 0, true, new byte[0]));
                try {
                    Server.getSender().addPacketToSend(new QuicShortHeaderPacket(Server.getDestinationAdress(), 0, temp));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}

