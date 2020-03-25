package quic.main;

import quic.packet.QuicPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static quic.main.Client.ds;

public class Sender implements Runnable {
    private InetAddress ip;
    private int port;
    private byte[] data;

    public Sender(InetAddress ip, int port, byte[] data) {
        this.ip = ip;
        this.port = port;
        this.data = data;
    }

    private void SendData(){
        DatagramPacket DpSend = new DatagramPacket(data, data.length,ip, port);
        try {
            ds.send(DpSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Packet sent");
    }

    @Override
    public void run() {
        SendData();
    }
}
