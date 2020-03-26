package quic.frame;

import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a QUIC CONNECTION_CLOSE frame. This frame is sent when a
 * connection error is detected.
 *
 * @version 1.0
 * @author Md Rofiqul Islam
 */
public class QuicMaxStreamDataFrame extends QuicFrame{

    /** Quic MAX_STREAM_DATA frames have a type of 0x11 */
    public static int FRAME_TYPE = 17;

    /**
     * Stream ID:  The stream ID of the stream that is affected encoded as a
     * variable-length integer.
     **/
    private long streamId;

    /**
     * Maximum Stream Data:  A variable-length integer indicating the
     * maximum amount of data that can be sent on the identified stream,
     * in units of bytes.
     */
    private long maximumStreamData;

    /**
     * Values
     * @param streamId // variable length stream ID
     * @param maximumStreamData // variable length maximum amount of data
     *                          in the stream
     */
    public QuicMaxStreamDataFrame(long streamId, long maximumStreamData) {
        this.setStreamId(streamId);
        this.setMaximumStreamData(maximumStreamData);
    }

    /**
     * Setter for StreamId
     * @param streamId long streamId
     */
    public void setStreamId(long streamId) {
        if(streamId>=0 && streamId<(long)Math.pow(2,62)){
            this.streamId = streamId;
        }else{
            throw new IllegalArgumentException();
        }

    }

    /**
     * getter for maximumStreamData
     * @param maximumStreamData long maximumStreamData
     */
    public void setMaximumStreamData(long maximumStreamData) {
        if(maximumStreamData>=0 && maximumStreamData<(long)Math.pow(2,62)) {
            this.maximumStreamData = maximumStreamData;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for stream ID
     * @return // variable length stream ID
     */
    public long getStreamId() {
        return streamId;
    }

    /**
     * Getter for maximum stream data
     * @return // variable length maximum amount of data in the stream
     */
    public long getMaximumStreamData() {
        return maximumStreamData;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        encoding.write(FRAME_TYPE);  // appending header byte
        encoding.write(Util.generateVariableLengthInteger(this.getStreamId()));  // appending Stream id as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getMaximumStreamData()));  // appending Maximum Stream data as a variable length integer
        byte [] data = encoding.toByteArray();

        return data;
    }

    @Override
    public String toString() {
        return "QuicMaxStreamDataFrame{" +
                "streamId=" + this.getStreamId() +
                ", maximumStreamData=" + this.getMaximumStreamData() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicMaxStreamDataFrame)) return false;
        QuicMaxStreamDataFrame that = (QuicMaxStreamDataFrame) o;
        return getStreamId() == that.getStreamId() &&
                getMaximumStreamData() == that.getMaximumStreamData();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStreamId(), getMaximumStreamData());
    }
}
