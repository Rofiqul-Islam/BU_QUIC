package quic.packet;

import quic.frame.QuicFrame;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Represents IETF-QUIC Packet
 * Chapter 17.2
 *
 * @version 1.1
 */
public abstract class QuicLongHeaderPacket extends QuicPacket{
    /**
     * The QUIC Version indicates which version of QUIC is in use and
     * determines how the rest of the protocol fields are interpreted.
     */
    private long version;

    /**
     * The Source Connection ID field identifies the ID of the source
     */
    private byte[] scID;

    /**
     * Value constructor for packet.QuicLongHeaderPacket
     *
     * @param dcID destination connection ID
     * @param packetNumber number of the packet
     * @param version version of QUIC
     * @param scID source connections ID
     */
    public QuicLongHeaderPacket(byte[] dcID, long packetNumber, long version, byte[] scID, Set<QuicFrame> frames) {
        super(dcID, packetNumber,frames);
        this.setVersion(version);
        this.setScID(scID);
    }

    /**
     * Get IETF-QUIC version of this packet
     * @return IETF-QUIC version
     */
    public long getVersion() {
        return (version);
    }


    /**
     * Set IETF-QUIC version of this packet
     * @param version IETF-QUIC version
     */
    public void setVersion(long version) {
        if(version>=0 && version<=4294967295L) {
            this.version = version;
        }else{
            throw new IllegalArgumentException();
        }


    }

    /**
     * Get Source Connection ID
     * @return scID
     */
    public byte[] getScID() {
        return scID;
    }

    /**
     * Set Source Connection ID
     * @param scID source connection ID
     */
    public void setScID(byte[] scID) {
        if(scID!=null && scID.length <=20) {
            this.scID = scID;
        }else if(scID ==  null){
            throw new NullPointerException();
        }else{
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuicLongHeaderPacket)) return false;
        QuicLongHeaderPacket that = (QuicLongHeaderPacket) o;
        return getVersion() == that.getVersion() &&
                Arrays.equals(getScID(), that.getScID());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getVersion());
        result = 31 * result + Arrays.hashCode(getScID());
        return result;
    }
}
