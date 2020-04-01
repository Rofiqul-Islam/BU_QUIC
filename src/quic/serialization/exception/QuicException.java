package quic.serialization.exception;

import java.util.Objects;

/**
 * Thrown when QUIC encounters an error. Contains the appropriate information
 * to construct a frame to send to the other party if necessary.
 *
 * @version 1.0
 */
public class QuicException extends Exception {
    /** The QUIC error code */
    private long errorCode;
    /** The QUIC code for the frame */
    private long frameType;
    /** The error message */
    private String message;

    /**
     * Values constructor for the exception.
     *
     * @param errorCode The QUIC error code
     * @param frameType The QUIC frame type for the frame which caused the error
     * @param message The error message
     */
    public QuicException(long errorCode, long frameType, String message) {
        this.setErrorCode(errorCode);
        this.setFrameType(frameType);
        this.setMessage(message);
    }

    /**
     * Getter for the QUIC error code
     * @return the error code
     */
    public long getErrorCode() {
        return errorCode;
    }

    /**
     * Setter for the QUIC error code
     * @param errorCode the error code to set
     */
    public void setErrorCode(long errorCode) {
        if(errorCode>=0 && errorCode<=8191L && errorCode!=12 && errorCode!=14 && errorCode!=15) {
            this.errorCode = errorCode;
        }else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for the type of QUIC frame
     * @return the frame type
     */
    public long getFrameType() {
        return frameType;
    }

    /**
     * Setter for the frame type
     * @param frameType the frame type to set
     */
    public void setFrameType(long frameType) {
        if(frameType>=0 && frameType<=30) {
            this.frameType = frameType;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for the type of QUIC message
     * @return the error message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for the error message
     * @param message the message to set
     */
    public void setMessage(String message) {
        if(message !=null){
            this.message = message;
        }else {
            throw new NullPointerException();
        }

    }

    @Override
    public String toString() {
        return "QuicException [" +
                "errorCode: " + this.getErrorCode() +
                ", frameType: " + this.getFrameType() +
                ", message: '" + this.getMessage() + '\'' +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicException)) return false;
        QuicException that = (QuicException) o;
        return getErrorCode() == that.getErrorCode() &&
                getFrameType() == that.getFrameType() &&
                getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrorCode(), getFrameType(), getMessage());
    }
}
