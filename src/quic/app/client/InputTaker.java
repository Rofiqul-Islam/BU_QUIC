package quic.app.client;

import java.util.Scanner;

public class InputTaker implements Runnable {
    Scanner s = new Scanner(System.in);

    public InputTaker() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(true){
            if(s.hasNext()) {
                String fileName = s.nextLine();
                System.out.println(fileName);
                try {
                    Client.getFileDownloader().addFileToDownloadList(fileName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //System.out.print("File name  = ");
        }
    }
}
