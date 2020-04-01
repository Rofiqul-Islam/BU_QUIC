package quic.serialization.frame;


import quic.serialization.exception.QuicException;
import quic.serialization.util.Util;

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
 * @author Md Rofiqul Islam
 * @version 1.2
 */
public class QuicAckFrame extends QuicFrame implements Serializable {
    /**
     * The frame type of ACK frame
     */
    public static final int FRAME_TYPE = 2;
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
     * Value constructor for the frame.QuicACKFrame class. Specifies the largestAck, delay, rangeCount
     * firstAckRange and ackRanges
     *
     * @param largestAck    The largest packet number the peer is acknowledging
     * @param delay         Time delta between ACK sent and largest acknowledged packet
     * @param rangeCount    Number of gap and ack range fields in the frame
     * @param firstAckRange Indicates the number of contiguous packets preceding the largest acknowledged
     */
    public QuicAckFrame(long largestAck, long delay, long rangeCount, long firstAckRange) {
        if (largestAck < firstAckRange) {         // largest ACK should be less than first ACK range
            this.setLargestAck(largestAck);
            this.setDelay(delay);
            this.setRangeCount(rangeCount);
            this.setFirstAckRange(firstAckRange);
        } else {
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
        if (largestAck >= 0) {
            this.largestAck = largestAck;
        } else {
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
        if (delay >= 0) {
            this.delay = delay;
        } else {
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
        if (rangeCount >= 0) {
            this.rangeCount = rangeCount;
        } else {
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
        if (firstAckRange >= 0) {
            this.firstAckRange = firstAckRange;
        } else {
            throw new IllegalArgumentException();
        }
    }


    @Override
    public byte[] encode() throws IOException {

        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        encoding.write(Util.hexStringToByteArray("2", 1));  // appending header
        encoding.write(Util.generateVariableLengthInteger(this.getLargestAck())); // appending largest Ack as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getDelay()));  // appending Ack Delay as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getRangeCount())); // appending Range Count as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getFirstAckRange())); // appending First Ack Range as a variable length integer

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
                getFirstAckRange() == that.getFirstAckRange();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLargestAck(), getDelay(), getRangeCount(), getFirstAckRange());
    }

    @Override
    public String toString() {
        return "QuicAckFrame{" +
                "largestAck=" + this.getLargestAck() +
                ", delay=" + this.getDelay() +
                ", rangeCount=" + this.getRangeCount() +
                ", firstAckRange=" + this.getFirstAckRange() +
                '}';
    }
}

