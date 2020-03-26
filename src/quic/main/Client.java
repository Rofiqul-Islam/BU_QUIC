package quic.main;

import quic.exception.QuicException;
import java.io.IOException;
import java.net.*;






/**
 *
 *
 * @author Rofiqul Islam
 */

public class Client {
    private static String applicationProtocol = null;
    public static String connectionId="104.17.209.9";
    // public static String connectionId="185.92.221.97";
    public static int port = 443;
    public static String destianation = "bd22d8d0e964c1add9f75fb9303567b3002b654b";
    public static DatagramSocket ds;
    public static void main(String args[]) throws IOException, QuicException {

        try {
            ds = new DatagramSocket();
        } catch (Exception e) {
            throw new QuicException(0,0,"Runtime exception");
        }

    }


}
