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
 * Represents a QUIC Short Header Packet.
 * A Short Header Packet can be used after the version and 1-RTT
 * keys are negotiated.
 *
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
        super(dcID, packetNumber,frames);
        this.setHeaderByte(packetNumber);
    }

    public byte getHeaderByte() {
        return headerByte;
    }

    public void setHeaderByte(Long packetNumber) {
        if(packetNumber<Math.pow(2,8)) {
            this.headerByte = 64;
            this.packetNumberLength = 1;
        }
        else if(packetNumber<Math.pow(2,16)){
            this.headerByte = 65;
            this.packetNumberLength = 2;
        }
        else if(packetNumber<Math.pow(2,24)) {
            this.headerByte = 66;
            this.packetNumberLength = 3;
        }
        else if(packetNumber<Math.pow(2,32)){
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
    public byte[] encode() {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        try {
            encoding.write(Util.hexStringToByteArray(Util.byteToHex(this.getHeaderByte()),1));
            encoding.write(Util.hexStringToByteArray(Util.bytesArrayToHex(this.getDcID()),this.getDcID().length));


            Iterator<QuicFrame> iterator1 = this.getFrames().iterator();
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            while (iterator1.hasNext()) {
                QuicFrame f = iterator1.next();
                temp.write(f.encode());
            }
            encoding.write(Util.hexStringToByteArray((Long.toHexString(this.getPacketNumber())),packetNumberLength));
            encoding.write(temp.toByteArray());


        } catch (IOException e) {
            System.out.println(e);
        }
        return encoding.toByteArray();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Set<QuicFrame>temp = showFrames();
        for (QuicFrame frame: temp) {
            builder.append(frame.toString());
        }
        return "QuicShortHeaderPacket{dcID=" + Util.printConnectionId(this.getDcID()) + ", packetNumber=" + this.getPacketNumber() + ", frames=[" + builder.toString() + "]}";
    }
}
