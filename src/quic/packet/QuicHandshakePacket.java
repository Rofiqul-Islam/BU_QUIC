package quic.packet;

import quic.exception.QuicException;
import quic.frame.QuicFrame;
import quic.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a QUIC Handshake packet.
 * A Handshake packet carries acknowledgement and cryptographic handshake messages.
 *
 * @version 1.1
 */

public class QuicHandshakePacket extends QuicLongHeaderPacket {

    int headerByte;
    byte[] payload = new byte[1000];
    int packetNumberLength;
    /**
     * Value constructor for QUICHandshakePacket
     *  @param dcID Destination Connection ID
     * @param packetNumber number of the packet
     * @param version version of quic
     * @param scID source connections ID
     * @param frames
     */
    public QuicHandshakePacket(byte[] dcID, long packetNumber, long version, byte[] scID, Set<QuicFrame> frames) {
        super(dcID, packetNumber, version, scID,frames);
        this.setHeaderByte(packetNumber);
    }

    public int getHeaderByte() {
        return headerByte;
    }

    public void setHeaderByte(Long packetNumber) {
        if(packetNumber<Math.pow(2,8)) {
            this.headerByte = 224;
            this.packetNumberLength=1;
        }
        else if(packetNumber<Math.pow(2,16)){
            this.headerByte = 225;
            this.packetNumberLength=2;
        }
        else if(packetNumber<Math.pow(2,24)) {
            this.headerByte = 226;
            this.packetNumberLength=3;
        }
        else if(packetNumber<Math.pow(2,32)){
            this.headerByte = 227;
            this.packetNumberLength=4;
        }

    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<QuicFrame>temp = showFrames();
        for (QuicFrame frame: temp) {
            builder.append(frame.toString());
        }
        return "QuicHandshakePacket{version=" + this.getVersion()+ ", scID=" + Util.printConnectionId(this.getScID()) + ", dcID=" + Util.printConnectionId(this.getDcID()) + ", packetNumber=" + this.getPacketNumber() + ", frames=[" + builder.toString() + "]}";
    }

    @Override
    public void addFrame(QuicFrame frame) {
        super.addFrame(frame);
    }

    @Override
    public Set<QuicFrame> getFrames() {
        return super.getFrames();
    }

    /**
     * Encodes Handshake packet
     * @return encoded byte array
     */

    @Override
    public byte[] encode() {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        try {
            encoding.write(Util.hexStringToByteArray(Util.byteToHex((byte)headerByte),1));
            encoding.write(Util.hexStringToByteArray(Long.toHexString(this.getVersion()),4));
            encoding.write(Util.hexStringToByteArray((Util.byteToHex((byte)this.getDcID().length)),1));
            encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getDcID()),this.getDcID().length));
            encoding.write(Util.hexStringToByteArray((Util.byteToHex((byte)this.getScID().length)),1));
            encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getScID()),this.getScID().length));

            long frameSize = 0;
            Iterator<QuicFrame> iterator1 = this.getFrames().iterator();
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            while (iterator1.hasNext()) {
                QuicFrame f = iterator1.next();
                frameSize += f.encode().length;
                temp.write(f.encode());
            }
            encoding.write(Util.generateVariableLengthInteger(packetNumberLength + frameSize));
            encoding.write(Util.hexStringToByteArray((Long.toHexString(this.getPacketNumber())),packetNumberLength));
            encoding.write(temp.toByteArray());


        } catch (IOException e) {
            System.out.println(e);
        }
        return encoding.toByteArray();
    }



}
