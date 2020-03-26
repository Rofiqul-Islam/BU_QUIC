package quic.frame;

import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * Represents a QUIC CONNECTION_CLOSE frame. This frame is sent when a
 * connection error is detected.
 *
 * @version 1.0
 * @author Md Rofiqul Islam
 */
public class QuicConnectionCloseFrame extends QuicFrame {
    /** Quic CONNECTION_CLOSE frames have a type of 0x1c */
    public static byte FRAME_TYPE = 28;

    /** Code denoting the error */
    private long errorCode;
    /** The type of frame which triggered the error */
    private long frameType;
    /** The human-readable reason for the error */
    private String reasonPhrase;

    /**
     * Values constructor for the frame
     *
     * @param errorCode the code corresponding to the error
     * @param frameType the type of frame causing the error
     * @param reasonPhrase the reason for the error
     */
    public QuicConnectionCloseFrame(long errorCode, long frameType, String reasonPhrase) {
        this.setErrorCode(errorCode);
        this.setFrameType(frameType);
        this.setReasonPhrase(reasonPhrase);
    }

    /**
     * Getter for the error code
     * @return the error code
     */
    public long getErrorCode() {
        return this.errorCode;
    }

    /**
     * Setter for the error code
     * @param errorCode the error code
     */
    public void setErrorCode(long errorCode) {
        if(errorCode>=0 && errorCode<= 8191 && errorCode !=12 && errorCode != 14 && errorCode!=15) {
            this.errorCode = errorCode;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for the type of frame which caused the error
     * @return the frame type
     */
    public long getFrameType() {
        return this.frameType;
    }

    /**
     * Setter for the type of frame which caused the error
     * @param frameType the frame type
     */
    public void setFrameType(long frameType) {
        if(frameType>=0 && frameType<=30) {
            this.frameType = frameType;
        }else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for the reason for the error
     * @return the reason phrase
     */
    public String getReasonPhrase() {
        try {
            return new String(this.reasonPhrase.getBytes(), "UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Setter for the reason for the error
     * @param reasonPhrase the reason phrase
     */
    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        encoding.write(FRAME_TYPE);        // appending header byte
        encoding.write(Util.generateVariableLengthInteger(this.getErrorCode()));      // appending Error code as a variable length integer
        encoding.write(Util.generateVariableLengthInteger(this.getFrameType()));      // appending Frame type as a variable length integer
        encoding.write(Util.generateVariableLengthInteger((long)(this.getReasonPhrase().length())));  // appending Reason Phrase length as a variable length integer
        encoding.write(this.getReasonPhrase().getBytes());   // Appending reason phrase

        byte [] data = encoding.toByteArray();

        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicConnectionCloseFrame)) return false;
        QuicConnectionCloseFrame that = (QuicConnectionCloseFrame) o;
        return getErrorCode() == that.getErrorCode() &&
                getFrameType() == that.getFrameType() &&
                getReasonPhrase().equals(that.getReasonPhrase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorCode(), getFrameType(), getReasonPhrase());
    }

    @Override
    public String toString() {
        return "QuicConnectionCloseFrame{" +
                "errorCode=" + this.getErrorCode() +
                ", frameType=" +this.getFrameType() +
                ", reasonPhrase='" + this.reasonPhrase + '\'' +
                '}';
    }

}
