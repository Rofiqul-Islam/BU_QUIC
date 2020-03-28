package quic.packet;

import quic.exception.QuicException;
import quic.frame.QuicFrame;
import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;


/**
 * Represents a QUIC Initial Packet. It carries the first CRYPTO frames sent
 * by the client and server to perform key exchange, and carries ACKs in either direction.
 *
 * @author Md Rofiqul Islam
 * @version 1.1
 */
public class QuicInitialPacket extends QuicLongHeaderPacket {

    byte headerByte;
    int packetNumberLength;
    int tokenLength;

    /**
     * Value constructor for QuicInitialPacket class
     *
     * @param dcID         destination connection ID
     * @param packetNumber number of the packet
     * @param version      version of quic
     * @param scID         source connections ID
     */
    public QuicInitialPacket(byte[] dcID, long packetNumber, long version, byte[] scID, Set<QuicFrame> frames) {
        super(dcID, packetNumber, version, scID, frames);
        this.setHeaderByte(packetNumber);
        this.setTokenLength(0);

    }

    /**
     * getter of header byte
     *
     * @return header byte
     */
    public int getHeaderByte() {
        return headerByte;
    }

    /**
     * Setter of header byte
     *
     * @param packetNumber , packet number affects the header byte
     */
    public void setHeaderByte(Long packetNumber) {
        if (packetNumber < Math.pow(2, 8)) {    // packet number can be different sizes
            this.headerByte = (byte) 192;   // when packet number is one byte long , the last two bit of header is 00
            this.packetNumberLength = 1;
        } else if (packetNumber < Math.pow(2, 16)) { // when packet number is two byte long , the last two bit of header is 01
            this.headerByte = (byte) 193;
            this.packetNumberLength = 2;
        } else if (packetNumber < Math.pow(2, 24)) { // when packet number is three byte long , the last two bit of header is 10
            this.headerByte = (byte) 194;
            this.packetNumberLength = 3;
        } else if (packetNumber < Math.pow(2, 32)) { // when packet number is four byte long , the last two bit of header is 11
            this.headerByte = (byte) 195;
            this.packetNumberLength = 4;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Getter for token length
     *
     * @return int token length
     */
    public int getTokenLength() {
        return tokenLength;
    }

    /**
     * Setter for token length
     *
     * @param tokenLength
     */
    public void setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
    }

    /**
     * Encodes initial packet
     *
     * @return encoded byte array
     */
    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();

        encoding.write(Util.hexStringToByteArray(Util.byteToHex(headerByte), 1));  // header is appended
        encoding.write(Util.hexStringToByteArray(Long.toHexString(this.getVersion()), 4));  // version is appended
        encoding.write(Util.hexStringToByteArray((Util.byteToHex((byte) this.getDcID().length)), 1)); // Destination ID length is appended
        encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getDcID()), this.getDcID().length));  // Destiantion ID is appended
        encoding.write(Util.hexStringToByteArray((Util.byteToHex((byte) this.getScID().length)), 1));  // Source ID length is appended
        encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getScID()), this.getScID().length));   // Source ID is appended
        encoding.write(Util.hexStringToByteArray(Util.byteToHex((byte) this.getTokenLength()), 1));    // Token Length is appended

        long frameSize = 0;
        Iterator<QuicFrame> iterator1 = this.getFrames().iterator();
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        while (iterator1.hasNext()) {
            QuicFrame f = iterator1.next();
            frameSize += f.encode().length;      // calculating payload length
            temp.write(f.encode());
        }
        encoding.write(Util.generateVariableLengthInteger(packetNumberLength + frameSize)); // length  = payload length+ packet numebr length
        encoding.write(Util.hexStringToByteArray((Long.toHexString(this.getPacketNumber())), packetNumberLength)); // appending packet number
        encoding.write(temp.toByteArray()); // appending the payload

        return encoding.toByteArray();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<QuicFrame> temp = showFrames();
        for (QuicFrame frame : temp) {
            builder.append(frame.toString());
        }
        return "QuicInitialPacket{version=" + this.getVersion() + ", scID=" + Util.byteToString(this.getScID()) + ", dcID=" + Util.byteToString(this.getDcID()) + ", packetNumber=" + this.getPacketNumber() + ", frames=[" + builder.toString() + "]}";
    }


}
