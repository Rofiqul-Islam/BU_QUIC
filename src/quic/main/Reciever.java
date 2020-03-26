package quic.main;

import quic.exception.QuicException;
import quic.packet.QuicPacket;
import quic.util.Util;

import java.io.IOException;
import java.net.DatagramPacket;

import static quic.main.Client.ds;

/**
 * @author Md Rofiqul Islam
 */
public class Reciever implements Runnable {

    public Reciever() {
    }

    @Override
    public void run() {
        int counter = 0;
        while (counter<10) {
            byte[] b1 = new byte[2048];
            DatagramPacket DpRecv = new DatagramPacket(b1, b1.length);
            try {
                ds.receive(DpRecv);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("--------------------------------------");

            try {
                    QuicPacket quicPacket = QuicPacket.decode(b1);
            } catch (QuicException e) {
                e.printStackTrace();
            }
            counter++;
        }
    }
}
