package quic.frame;

import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a QUIC STREAM frame. The STREAM frame manages a data stream. It
 * can create a stream and carry data.
 *
 * @version 1.1
 * @author Md Rofiqul Islam
 */
public class QuicStreamFrame extends QuicFrame {
    byte header;
    /**
     * The ID of the stream
     */
    private long streamId;
    /**
     * The byte offset within the stream for the data within this packet
     */
    private long offset;
    /**
     * Flag marking the end of the stream when set to true
     */
    private boolean endOfStream;
    /**
     * The data being delivered by the frame
     */
    private byte[] data;

    /**
     * Values constructor for the STREAM frame.
     *
     * @param streamId the ID of the stream
     * @param offset the byte offset of the data within the stream
     * @param endOfStream flag marking the end of the stream
     * @param data the frame's data
     */
    public QuicStreamFrame(long streamId, long offset, boolean endOfStream, byte[] data) {
        this.setStreamId(streamId);
        this.setOffset(offset);
        this.setEndOfStream(endOfStream);
        this.setData(data);
        this.setHeader((byte)8);
    }

    /**
     * Getter for the stream ID
     *
     * @return the ID of the stream
     */
    public long getStreamId() {

        return this.streamId;
    }

    /**
     * Getter of header byte
     * @return
     */
    public byte getHeader() {
        return header;
    }

    /**
     * Setter of header byte
     * @param header
     */
    public void setHeader(byte header) {
        this.header = header;
        if(this.getOffset()>0){   // offset bit is set when offset is greater than 0
            this.header = (byte) (this.header | 4);
        }

        this.header = (byte) (this.header | 2);    // always setting len bit

        if(this.isEndOfStream()){    // fin bit is set when it is the last frame of data
            this.header = (byte)(this.header | 1);
        }
    }

    /**
     * Setter for the stream ID
     *
     * @param streamId the ID to set
     */
    public void setStreamId(long streamId) {
        if(streamId>=0 && streamId<(long)Math.pow(2,62)) {
            this.streamId = streamId;
        }else{
            throw new IllegalArgumentException();
        }

    }

    /**
     * Getter for the byte offset of the data
     *
     * @return the offset
     */
    public long getOffset() {
        return this.offset;
    }

    /**
     * Setter for the byte offset of the data
     *
     * @param offset the offset to set
     */
    public void setOffset(long offset) {
        if(offset>=0 && offset<(long)Math.pow(2,62)) {
            this.offset = offset;
        }else{
            throw new IllegalArgumentException();
        }

    }

    /**
     * Getter for the end-of-stream flag
     *
     * @return true if this is the last frame in the stream, otherwise false
     */
    public boolean isEndOfStream() {
        return this.endOfStream;
    }

    /**
     * Setter for the end-of-stream flag
     *
     * @param endOfStream the value to set
     */
    public void setEndOfStream(boolean endOfStream) {
        this.endOfStream = endOfStream;
    }

    /**
     * Getter for the data in the frame
     *
     * @return the stream data
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Setter for the data in the frame
     *
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }



    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        encoding.write(this.getHeader()); // appending header byte
        encoding.write(Util.generateVariableLengthInteger(this.getStreamId())); // appending Stream id as a variable length integer
        if(this.getOffset()>0) {
            encoding.write(Util.generateVariableLengthInteger(this.getOffset())); // appending Offset as a variable length integer
        }
        encoding.write(Util.generateVariableLengthInteger((long)this.getData().length));  // appending length as a variable length integer
        encoding.write(this.getData());


        byte [] data = encoding.toByteArray();
        return data;
    }

    @Override
    public String toString() {
        return "QuicStreamFrame{" +
                "streamId=" + this.getStreamId() +
                ", offset=" +this.getOffset()+
                ", endOfStream=" + this.isEndOfStream() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicStreamFrame)) return false;
        QuicStreamFrame that = (QuicStreamFrame) o;
        return getHeader() == that.getHeader() &&
                getStreamId() == that.getStreamId() &&
                getOffset() == that.getOffset() &&
                isEndOfStream() == that.isEndOfStream() &&
                Arrays.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getHeader(), getStreamId(), getOffset(), isEndOfStream());
        result = 31 * result + Arrays.hashCode(getData());
        return result;
    }
}
