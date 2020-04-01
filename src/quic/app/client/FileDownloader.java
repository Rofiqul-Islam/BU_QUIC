package quic.app.client;

import quic.serialization.frame.QuicStreamFrame;
import quic.serialization.frame.QuicAckFrame;
import quic.serialization.frame.QuicFrame;
import quic.serialization.packet.QuicInitialPacket;
import quic.serialization.packet.QuicPacket;
import quic.serialization.packet.QuicShortHeaderPacket;
import quic.app.util.*;

import java.util.*;


public class FileDownloader implements Runnable {
    BlockingQueue fileToDownloadQueue = new BlockingQueue(1200);
    static int flag = 0;


    public FileDownloader() {
        Thread t = new Thread(this);
        t.start();
    }


    public static int getFlag() {
        return flag;
    }

    public static void setFlag(int flag) {
        FileDownloader.flag = flag;
    }

    public Object getNextFileToDownload() throws InterruptedException {
        return fileToDownloadQueue.dequeue();
    }

    public void addFileToDownloadList(String fileName) throws InterruptedException {
        fileToDownloadQueue.enqueue(fileName);
    }


    @Override
    public void run() {
        while (true) {
            String fileName=null;
            Set<QuicFrame> temp = new HashSet<>();
            try {
                fileName = getNextFileToDownload()+"";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Arrays.equals(Client.getDestinationAdress(),"0".getBytes())) {
                QuicFrame ackFrame = new QuicAckFrame(0, 0, 0, 1);
                temp.clear();
                temp.add(ackFrame);
                QuicPacket.setDcIdSize(Client.getSourceAdrees().length);
                QuicPacket initialPacket = new QuicInitialPacket(Client.getDestinationAdress(), 0, 0xff000019L, Client.getSourceAdrees(), temp);
                setFlag(0);
                try {
                    Client.getSender().addPacketToSend(initialPacket);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int timer = 0;
                while (true) {
                    if (flag != 0) {
                        break;
                    }
                    try {
                        Thread.sleep(100);
                        timer += 100;
                        if (timer >= Client.getTimeout()) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(flag == 0){
                    System.out.println("Server not found");
            }else if(flag  == 1){
                Client.setDestinationAdress("0".getBytes());
                System.out.println("Invalid destiantion id");
                continue;           // file not found or invalid destinatin
            }else if(flag ==2){
                long x = Client.getIncomingStreamHandler().getValidStreamId()<<2;
                QuicFrame quicStreamFrame = new QuicStreamFrame(x,0,true,(fileName).getBytes());
                temp.clear();
                temp.add(quicStreamFrame);
                QuicPacket shortHeaderPacket = new QuicShortHeaderPacket(Client.getDestinationAdress(),1,temp);
                Client.getIncomingStreamHandler().getFileStreamIdMap().put(x,fileName);
                try {
                    Client.getSender().addPacketToSend(shortHeaderPacket);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}

