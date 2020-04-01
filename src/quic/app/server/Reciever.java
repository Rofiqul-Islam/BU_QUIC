package quic.app.server;

import quic.serialization.exception.QuicException;
import quic.serialization.packet.QuicInitialPacket;
import quic.serialization.packet.QuicPacket;
import quic.serialization.util.Util;

import java.io.IOException;
import java.net.DatagramPacket;

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
            byte[] b1 = new byte[1300];
            DatagramPacket DpRecv = new DatagramPacket(b1, b1.length);
            try {
                Server.getDs().receive(DpRecv);
                Server.setClientIp(DpRecv.getAddress());
                Server.setClientPort(DpRecv.getPort());
                QuicPacket quicPacket = QuicPacket.decode(b1);
                if(quicPacket instanceof QuicInitialPacket){
                    Server.setClientIp(DpRecv.getAddress());
                    Server.setClientPort(DpRecv.getPort());
                }
                System.out.println("Recieveing : "+quicPacket.toString());
                Server.getIncomingPacketHandler().addNewPacketToBuffer(quicPacket);
            } catch (IOException | QuicException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
