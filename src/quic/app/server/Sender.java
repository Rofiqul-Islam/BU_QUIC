package quic.app.server;



import quic.serialization.exception.QuicException;
import quic.serialization.packet.QuicPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import quic.app.util.*;

/**
 * @author Md Rofiqul Islam
 */
public class Sender implements Runnable {
    private BlockingQueue packetToSend = new BlockingQueue(1200);

    public Sender() {
        Thread t = new Thread(this);
        t.start();
    }

    public void addPacketToSend(QuicPacket packet) throws InterruptedException {
        packetToSend.enqueue(packet);
    }
    public Object retrievePacketToSend() throws InterruptedException {
        return packetToSend.dequeue();
    }

    public void sendData(byte[] data){
        DatagramPacket DpSend = new DatagramPacket(data, data.length,Server.getClientIp(), Server.getClientPort());
        try {
            Server.getDs().send(DpSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                QuicPacket quicPacket = (QuicPacket) packetToSend.dequeue();
                sendData(quicPacket.encode());
                System.out.println("Sending : "+quicPacket.toString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
