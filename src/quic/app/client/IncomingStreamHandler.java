package quic.app.client;

import quic.app.util.BlockingQueue;
import quic.serialization.frame.QuicAckFrame;
import quic.serialization.frame.QuicFrame;
import quic.serialization.frame.QuicStreamFrame;
import quic.serialization.packet.QuicPacket;
import quic.serialization.packet.QuicShortHeaderPacket;

import java.util.*;

public class IncomingStreamHandler implements Runnable {
    BlockingQueue incomingStreamQueue = new BlockingQueue(1200);
    Map<Long,String> fileStreamIdMap = new HashMap<>();
    Random rand = new Random();

    public IncomingStreamHandler() {
        Thread t = new Thread(this);
        t.start();
    }

    public long getValidStreamId(){
        long x;
        while(true) {
            x = rand.nextLong() % 1073741820L;
            if (!fileStreamIdMap.containsKey(x) && x>0) {
                break;
            }
        }
        return x;
    }
    public void addNewStreamFrame(QuicFrame frame) throws InterruptedException {
        incomingStreamQueue.enqueue(frame);
    }

    public Object getStreamFrameQueue() throws InterruptedException {
        return incomingStreamQueue.dequeue();
    }

    public Map<Long, String> getFileStreamIdMap() {
        return fileStreamIdMap;
    }

    public void setFileStreamIdMap(Map<Long, String> fileStreamIdMap) {
        this.fileStreamIdMap = fileStreamIdMap;
    }

    @Override
    public void run() {
        Set<QuicFrame> temp = new HashSet<>();
        while(true){
            try {
                QuicStreamFrame quicStreamFrame = (QuicStreamFrame) getStreamFrameQueue();
                temp.clear();
                temp.add(new QuicAckFrame(0,0,0,quicStreamFrame.getOffset()+1));
                Client.getSender().addPacketToSend(new QuicShortHeaderPacket(Client.getDestinationAdress(),quicStreamFrame.getStreamId(),temp));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
