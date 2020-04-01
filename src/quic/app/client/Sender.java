package quic.app.client;


import quic.serialization.packet.QuicPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import quic.app.util.*;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Md Rofiqul Islam
 */
public class Sender implements Runnable {
    BlockingQueue packetToSend = new BlockingQueue(1200);

    public Sender() {
        Thread t = new Thread(this);
        t.start();
    }

    public void addPacketToSend(QuicPacket packet) throws InterruptedException {
        packetToSend.enqueue(packet);
    }

    public void sendData(byte[] data) {
        DatagramPacket DpSend = new DatagramPacket(data, data.length, Client.getIp(), Client.getPort());
        try {
            Client.getDs().send(DpSend);
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

