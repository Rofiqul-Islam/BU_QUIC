package quic.frame;

import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Represents a QUIC STREAM frame. The STREAM frame manages a data stream. It
 * can create a stream and carry data.
 *
 * @version 1.1
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

    public byte getHeader() {
        return header;
    }

    public void setHeader(byte header) {
        this.header = header;
        if(this.getOffset()>0){
            this.header = (byte) (this.header | 4);
        }

        this.header = (byte) (this.header | 2);    // always setting len bit

        if(this.isEndOfStream()){
            this.header = (byte)(this.header | 1);
        }
    }

    /**
     * Setter for the stream ID
     *
     * @param streamId the ID to set
     */
    public void setStreamId(long streamId) {
        this.streamId = streamId;
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
        this.offset = offset;
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
        ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferOutputStream);

        try {
            objectOutputStream.writeObject(this.getHeader());
            objectOutputStream.writeObject(Util.generateVariableLengthInteger(this.getStreamId()));
            if(this.getOffset()>0) {
                objectOutputStream.writeObject(Util.generateVariableLengthInteger(this.getOffset()));
            }
            objectOutputStream.writeObject(Util.generateVariableLengthInteger((long)this.getData().length));
            objectOutputStream.writeObject(this.getData());
            objectOutputStream.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }

        byte [] data = bufferOutputStream.toByteArray();

        return data;
    }
}
