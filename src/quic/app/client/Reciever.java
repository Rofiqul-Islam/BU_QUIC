package quic.app.client;

import quic.serialization.exception.QuicException;
import quic.serialization.packet.QuicPacket;
import quic.serialization.util.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * @author Md Rofiqul Islam
 */
public class Reciever implements Runnable {

    public Reciever() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            byte[] b1 = new byte[2048];
            DatagramPacket DpRecv = new DatagramPacket(b1, b1.length);
            try {
                Client.getDs().receive(DpRecv);
                QuicPacket quicPacket = QuicPacket.decode(b1);
                System.out.println("Recieveing : "+quicPacket.toString());
                Client.getIncomingPacketHandler().addNewPacketToBuffer(quicPacket);

            } catch (IOException | QuicException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
