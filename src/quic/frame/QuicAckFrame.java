package quic.frame;

import quic.exception.QuicException;
import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a QUIC ACK frame.
 * A QUIC ACK frame to inform senders of packets they have received and processed.
 *
 * @version 1.1
 */
public class QuicAckFrame extends QuicFrame implements Serializable {
    /**
     * The largest packet number the peer is acknowledging
     */
    private long largestAck;

    /**
     * Time delta between ACK sent and largest acknowledged packet
     */
    private long delay;

    /**
     * Number of gap and ack range fields in the frame
     */
    private long rangeCount;

    /**
     * Indicates the number of contiguous packets preceding the largest acknowledged
     */
    private long firstAckRange;

    /**
     * Additional ranges of packets which are alternately gap and ack range
     */
    private ArrayList<QuicAckRange> ackRanges;

    /**
     * Value constructor for the frame.QuicACKFrame class. Specifies the largestAck, delay, rangeCount
     * firstAckRange and ackRanges
     *
     * @param largestAck The largest packet number the peer is acknowledging
     * @param delay Time delta between ACK sent and largest acknowledged packet
     * @param rangeCount Number of gap and ack range fields in the frame
     * @param firstAckRange Indicates the number of contiguous packets preceding the largest acknowledged
     */
    public QuicAckFrame(long largestAck, long delay, long rangeCount, long firstAckRange) {
    	
    	this.setLargestAck(largestAck);
    	this.setDelay(delay);
    	this.setRangeCount(rangeCount);
    	this.setFirstAckRange(firstAckRange);
    	this.ackRanges= new ArrayList<>();
    }

    @Override
    public byte[] encode() throws IOException{
    	
    	ByteArrayOutputStream encoding = new ByteArrayOutputStream();

		
	    try {
	    	encoding.write(Util.hexStringToByteArray("2",1));
	    	encoding.write(Util.generateVariableLengthInteger(this.getLargestAck()));
	    	encoding.write(Util.generateVariableLengthInteger(this.getDelay()));
	    	encoding.write(Util.generateVariableLengthInteger(this.getRangeCount()));
	    	encoding.write(Util.generateVariableLengthInteger(this.getFirstAckRange()));

            Iterator<QuicAckRange> x = this.getAckRanges().iterator();
            while(x.hasNext()){
                QuicAckRange quicAckRange = x.next();
                encoding.write(Util.generateVariableLengthInteger(quicAckRange.getGap()));
                encoding.write(Util.generateVariableLengthInteger(quicAckRange.getAckRange()));
            }

	    	
		} catch (Exception e) {

		}

	    
        return encoding.toByteArray();
    }




    /**
     * Getter for largestAck in the frame.
     *
     * @return the largestAck
     */
    public long getLargestAck() {
        return this.largestAck;
    }

    /**
     * Setter for largestAck in the frame.
     *
     * @param largestAck The largest packet number the peer is acknowledging
     */
    public void setLargestAck(long largestAck) {
    	if(largestAck>=0) {
            this.largestAck = largestAck;
        }
    }

    /**
     * Getter for delay in the frame..
     *
     * @return the delay
     */
    public long getDelay() {
        return this.delay;
    }

    /**
     * Setter for delay in the frame.
     *
     * @param delay Time delta between ACK sent and largest acknowledged packet
     */
    public void setDelay(long delay) {
        if(delay>=0) {
            this.delay = delay;
        }
    }

    /**
     * Getter for rangeCount in the frame.
     *
     * @return the rangeCount
     */
    public long getRangeCount() {
        return this.rangeCount;
    }

    /**
     * Setter for rangeCount in the frame.
     *
     * @param rangeCount Number of gap and ack range fields in the frame
     */
    public void setRangeCount(long rangeCount) {
        if(rangeCount>=0) {
            this.rangeCount = rangeCount;
        }
    }

    /**
     * Getter for firstAckRange in the frame
     *
     * @return the firstAckRange
     */
    public long getFirstAckRange() {
        return this.firstAckRange;
    }

    /**
     * Setter for firstAckRange in the frame
     *
     * @param firstAckRange Indicates the number of contiguous packets preceding the largest acknowledged
     */
    public void setFirstAckRange(long firstAckRange) {
        if(firstAckRange>=0) {
            this.firstAckRange = firstAckRange;
        }
    }


    /**
     * Getter for ackRanges in the frame
     *
     * @return the ackRanges
     */
    public List<QuicAckRange> getAckRanges() {
        if(this.ackRanges.size() <1){
            this.ackRanges.add(new QuicAckRange(0,0));
        }
        ArrayList<QuicAckRange> temp =new ArrayList<>();
        for(QuicAckRange x : this.ackRanges){
           temp.add(x);

        }
        this.ackRanges.removeAll(temp);
        return temp;
    }

    /**
     * Adds an AckRange to the given list
     *
     * @param ackRange the range to add
     */
    public void addAckRange(QuicAckRange ackRange) {
        this.ackRanges.add(ackRange);
    }
}

