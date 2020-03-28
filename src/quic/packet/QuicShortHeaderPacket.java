package quic.packet;


import quic.exception.QuicException;
import quic.frame.QuicFrame;
import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a QUIC Short Header Packet.
 * A Short Header Packet can be used after the version and 1-RTT
 * keys are negotiated.
 *
 * @author Md Rofiqul Islam
 * @version 1.1
 */
public class QuicShortHeaderPacket extends QuicPacket {

    byte headerByte;
    int packetNumberLength;

    /**
     * Value constructor for QuicShortHeaderPacket class
     *
     * @param dcID         destination connection ID
     * @param packetNumber number of the packet
     */
    public QuicShortHeaderPacket(byte[] dcID, long packetNumber, Set<QuicFrame> frames) {
        super(dcID, packetNumber, frames);
        this.setHeaderByte(packetNumber);
    }

    /**
     * Getter of header byte
     *
     * @return
     */
    public byte getHeaderByte() {
        return headerByte;
    }

    /**
     * Setter of header byte
     *
     * @param packetNumber
     */
    public void setHeaderByte(Long packetNumber) { // packet number can be different sizes
        if (packetNumber < Math.pow(2, 8)) {   // when packet number is one byte long , the last two bit of header is 00
            this.headerByte = 64;
            this.packetNumberLength = 1;
        } else if (packetNumber < Math.pow(2, 16)) { // when packet number is two byte long , the last two bit of header is 01
            this.headerByte = 65;
            this.packetNumberLength = 2;
        } else if (packetNumber < Math.pow(2, 24)) {  // when packet number is three byte long , the last two bit of header is 10
            this.headerByte = 66;
            this.packetNumberLength = 3;
        } else if (packetNumber < Math.pow(2, 32)) {  // when packet number is four byte long , the last two bit of header is 11
            this.headerByte = 67;
            this.packetNumberLength = 4;
        }
    }

    /**
     * Encodes short header packet
     *
     * @return encoded byte array
     */
    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();

        encoding.write(Util.hexStringToByteArray(Util.byteToHex(this.getHeaderByte()), 1));  // appending the header byte
        encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getDcID()), this.getDcID().length));  // appending the Destiantion Id
        Iterator<QuicFrame> iterator1 = this.getFrames().iterator();
        ByteArrayOutputStream temp = new ByteArrayOutputStream();     // payload array
        while (iterator1.hasNext()) {
            QuicFrame f = iterator1.next();
            temp.write(f.encode());
        }
        encoding.write(Util.hexStringToByteArray((Long.toHexString(this.getPacketNumber())), packetNumberLength));  // appending packet number
        encoding.write(temp.toByteArray());   // appending payload

        return encoding.toByteArray();
    }

    /**
     * To string method
     *
     * @return String representation of this class
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<QuicFrame> temp = showFrames();
        for (QuicFrame frame : temp) {
            builder.append(frame.toString());
        }
        return "QuicShortHeaderPacket{dcID=" + Util.byteToString(this.getDcID()) + ", packetNumber=" + this.getPacketNumber() + ", frames=[" + builder.toString() + "]}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicShortHeaderPacket)) return false;
        if (!super.equals(o)) return false;
        QuicShortHeaderPacket that = (QuicShortHeaderPacket) o;
        return getHeaderByte() == that.getHeaderByte() &&
                packetNumberLength == that.packetNumberLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getHeaderByte(), packetNumberLength);
    }
}
