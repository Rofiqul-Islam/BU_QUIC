package quic.app.server;


import java.net.*;


/**
 * @author Rofiqul Islam
 */

public class Server {
    private static int serverPort = 443;
    private static int clientPort;
    private static DatagramSocket ds;
    private static Sender sender ;
    private static Reciever reciever;
    private static InetAddress clientIp;
    private static byte[] sourceAdrees = "Server".getBytes();
    private static byte[] destinationAdress;
    private static IncomingPacketHandler incomingPacketHandler;
    private static long version  = 0xff000019L;
    private static int maxRate = 1500;
    private static int packetSize = 500;
    private static int timout = 100;
    private static FileUploader fileUploader;


    public static void main(String args[]) throws SocketException, UnknownHostException {
        ds = new DatagramSocket(serverPort);
        sender = new Sender();
        reciever = new Reciever();
        incomingPacketHandler = new IncomingPacketHandler();
        fileUploader = new FileUploader();
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        Server.serverPort = serverPort;
    }

    public static int getClientPort() {
        return clientPort;
    }

    public static void setClientPort(int clientPort) {
        Server.clientPort = clientPort;
    }

    public static DatagramSocket getDs() {
        return ds;
    }

    public static void setDs(DatagramSocket ds) {
        Server.ds = ds;
    }

    public static Sender getSender() {
        return sender;
    }

    public static void setSender(Sender sender) {
        Server.sender = sender;
    }

    public static InetAddress getClientIp() {
        return clientIp;
    }

    public static void setClientIp(InetAddress clientIp) {
        Server.clientIp = clientIp;
    }

    public static byte[] getSourceAdrees() {
        return sourceAdrees;
    }

    public static void setSourceAdrees(byte[] sourceAdrees) {
        Server.sourceAdrees = sourceAdrees;
    }

    public static byte[] getDestinationAdress() {
        return destinationAdress;
    }

    public static void setDestinationAdress(byte[] destinationAdress) {
        Server.destinationAdress = destinationAdress;
    }

    public static Reciever getReciever() {
        return reciever;
    }

    public static void setReciever(Reciever reciever) {
        Server.reciever = reciever;
    }

    public static IncomingPacketHandler getIncomingPacketHandler() {
        return incomingPacketHandler;
    }

    public static void setIncomingPacketHandler(IncomingPacketHandler incomingPacketHandler) {
        Server.incomingPacketHandler = incomingPacketHandler;
    }

    public static long getVersion() {
        return version;
    }

    public static void setVersion(long version) {
        Server.version = version;
    }

    public static FileUploader getFileUploader() {
        return fileUploader;
    }

    public static void setFileUploader(FileUploader fileUploader) {
        Server.fileUploader = fileUploader;
    }

    public static int getPacketSize() {
        return packetSize;
    }

    public static void setPacketSize(int packetSize) {
        Server.packetSize = packetSize;
    }
}
