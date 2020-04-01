package quic.app.client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Rofiqul Islam
 */

public class Client {
    private static String connectionId = "127.0.0.1";
    private static int port = 443;
    private static DatagramSocket ds;
    private static Sender sender ;
    private static Reciever reciever;
    private static InetAddress ip;
    private static byte[] sourceAdrees = "client".getBytes();
    private static byte[] destinationAdress = "0".getBytes();
    private static FileDownloader fileDownloader;
    private static long version = 0xff000019L;
    private static IncomingPacketHandler incomingPacketHandler;
    private static InputTaker inputTaker;
    private static int timeout = 500;
    private static IncomingStreamHandler incomingStreamHandler;


    public static void main(String args[]) throws SocketException, UnknownHostException {
        ip = InetAddress.getByName(connectionId);
        ds = new DatagramSocket();
        fileDownloader = new FileDownloader();
        sender = new Sender();
        reciever = new Reciever();
        inputTaker = new InputTaker();
        incomingPacketHandler = new IncomingPacketHandler();
        incomingStreamHandler = new IncomingStreamHandler();

        System.out.print("File name: ");


    }

    public static String getConnectionId() {
        return connectionId;
    }

    public static void setConnectionId(String connectionId) {
        Client.connectionId = connectionId;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Client.port = port;
    }

    public static DatagramSocket getDs() {
        return ds;
    }

    public static void setDs(DatagramSocket ds) {
        Client.ds = ds;
    }

    public static Sender getSender() {
        return sender;
    }

    public static void setSender(Sender sender) {
        Client.sender = sender;
    }

    public static InetAddress getIp() {
        return ip;
    }

    public static void setIp(InetAddress ip) {
        Client.ip = ip;
    }

    public static Reciever getReciever() {
        return reciever;
    }

    public static void setReciever(Reciever reciever) {
        Client.reciever = reciever;
    }

    public static byte[] getSourceAdrees() {
        return sourceAdrees;
    }

    public static void setSourceAdrees(byte[] sourceAdrees) {
        Client.sourceAdrees = sourceAdrees;
    }

    public static byte[] getDestinationAdress() {
        return destinationAdress;
    }

    public static void setDestinationAdress(byte[] destinationAdress) {
        Client.destinationAdress = destinationAdress;
    }

    public static FileDownloader getFileDownloader() {
        return fileDownloader;
    }

    public static void setFileDownloader(FileDownloader fileDownloader) {
        Client.fileDownloader = fileDownloader;
    }

    public static long getVersion() {
        return version;
    }

    public static void setVersion(long version) {
        Client.version = version;
    }

    public static IncomingPacketHandler getIncomingPacketHandler() {
        return incomingPacketHandler;
    }

    public static void setIncomingPacketHandler(IncomingPacketHandler incomingPacketHandler) {
        Client.incomingPacketHandler = incomingPacketHandler;
    }

    public static InputTaker getInputTaker() {
        return inputTaker;
    }

    public static void setInputTaker(InputTaker inputTaker) {
        Client.inputTaker = inputTaker;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        Client.timeout = timeout;
    }

    public static IncomingStreamHandler getIncomingStreamHandler() {
        return incomingStreamHandler;
    }

    public static void setIncomingStreamHandler(IncomingStreamHandler incomingStreamHandler) {
        Client.incomingStreamHandler = incomingStreamHandler;
    }
}
