package quic.frame;


import quic.exception.QuicException;
import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a QUIC ACK frame.
 * A QUIC ACK frame to inform senders of packets they have received and processed.
 *
 * @version 1.2
 * @author Md Rofiqul Islam
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
     * Represent contiguous unacknowledged packets preceding the packet number one lower than
     * the smallest in the preceding ACK range
     */
    private List<Long> gaps;

    /**
     * Represent additional ranges of ACKed packets
     */
    private List<Long> acks;

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
        this.acks = new ArrayList<>();
        this.gaps = new ArrayList<>();
        if(largestAck<firstAckRange) {         // largest ACK should be less than first ACK range
            this.setLargestAck(largestAck);
            this.setDelay(delay);
            this.setRangeCount(rangeCount);
            this.setFirstAckRange(firstAckRange);
        }else {
            throw new IllegalArgumentException();
        }

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
        else{
            throw new IllegalArgumentException();
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
        else{
            throw new IllegalArgumentException();
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
        else{
            throw new IllegalArgumentException();
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
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for gaps in the frame
     *
     * @return the gaps
     */
    public List<Long> getGaps() {
        return gaps;
    }

    /**
     * Getter for acks in the frame
     *
     * @return the acks
     */
    public List<Long> getAcks() {
        return acks;
    }

    /**
     * Setter for gaps and acks in the frame. They must always be paired.
     *
     * @param gap the gap to set
     * @param ack the ack to set
     */
    public void addGapAndAck(long gap, long ack) {
        this.gaps.add(gap);
        this.acks.add(ack);
    }


    @Override
    public byte[] encode() throws IOException{
    	
    	ByteArrayOutputStream encoding = new ByteArrayOutputStream();
    	encoding.write(Util.hexStringToByteArray("2",1));  // appending header
        encoding.write(Util.generateVariableLengthInteger(this.getLargestAck())); // appending largest Ack as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getDelay()));  // appending Ack Delay as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getRangeCount())); // appending Range Count as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getFirstAckRange())); // appending First Ack Range as a variable length integer

        Iterator<Long> gapIterator = this.getGaps().iterator();
        Iterator<Long> ackIterator  = this.getAcks().iterator();
        while(gapIterator.hasNext() && ackIterator.hasNext()){
            encoding.write(Util.generateVariableLengthInteger(gapIterator.next()));  // appending GAP as a variable length integer
            encoding.write(Util.generateVariableLengthInteger(ackIterator.next()));  // appending Ack as a variable length integer
        }
        return encoding.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicAckFrame)) return false;
        QuicAckFrame that = (QuicAckFrame) o;
        return getLargestAck() == that.getLargestAck() &&
                getDelay() == that.getDelay() &&
                getRangeCount() == that.getRangeCount() &&
                getFirstAckRange() == that.getFirstAckRange() &&
                Objects.equals(getGaps(), that.getGaps()) &&
                Objects.equals(getAcks(), that.getAcks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLargestAck(), getDelay(), getRangeCount(), getFirstAckRange(), getGaps(), getAcks());
    }
}

