package quic.exception;

/**
 * Thrown when QUIC encounters an error. Contains the appropriate information
 * to construct a frame to send to the other party if necessary.
 *
 * @version 1.0
 */
public class QuicException extends Exception {
    /** The QUIC error code */
    private int errorCode;
    /** The QUIC code for the frame */
    private int frameType;
    /** The error message */
    private String message;

    /**
     * Values constructor for the exception.
     *
     * @param errorCode The QUIC error code
     * @param frameType The QUIC frame type for the frame which caused the error
     * @param message The error message
     */
    public QuicException(int errorCode, int frameType, String message) {

    }

    /**
     * Getter for the QUIC error code
     * @return the error code
     */
    public int getErrorCode() {
        return 0;
    }

    /**
     * Setter for the QUIC error code
     * @param errorCode the error code to set
     */
    public void setErrorCode(int errorCode) {

    }

    /**
     * Getter for the type of QUIC frame
     * @return the frame type
     */
    public int getFrameType() {
        return 0;
    }

    /**
     * Setter for the frame type
     * @param frameType the frame type to set
     */
    public void setFrameType(int frameType) {

    }

    @Override
    public String getMessage() {
        return null;
    }

    /**
     * Setter for the error message
     * @param message the message to set
     */
    public void setMessage(String message) {

    }
}
