package quic.frame;

import java.io.Serializable;

/**
 * Represents a range of ACKs in a QUIC ACK Frame.
 *
 * @version 1.1
 */
public class QuicAckRange implements Serializable {
    /**
     * Number of contiguous unacknowledged packets
     */
    private long gap;

    /**
     * Indicates the number of contiguous acknowledged packets preceding the largest
     * packet number, as determined by the preceding Gap.
     */
    private long ackRange;

    /**
     * Values constructor for QuicAckRange
     *
     * @param gap the gap
     * @param ackRange the range
     */
    public QuicAckRange(long gap, long ackRange) {
        this.setGap(gap);
        this.setAckRange(ackRange);
    }

    /**
     * Getter for gap in the ackRange.
     *
     * @return the gap
     */
    public long getGap() {
        return this.gap;
    }

    /**
     * Setter for gap in the ackRange.
     *
     * @param gap gap of the previous ackRange
     */
    public void setGap(long gap) {

        this.gap=gap;
    }

    /**
     * Getter for ackRange in the ackRange.
     *
     * @return the ackRange; range from previous largest acknowledged
     */
    public long getAckRange() {

        return this.ackRange;
    }

    /**
     * Setter for ackRange in the ackRange.
     *
     * @param ackRange the range of the ack
     */
    public void setAckRange(long ackRange) {
        this.ackRange=ackRange;
    }
}
