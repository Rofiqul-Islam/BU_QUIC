package quic.extension;

public class TlsTicket {
    private int handshakeType;
    private long length;
    private byte[] ticketData;

    public TlsTicket(int handshakeType, long length, byte[] ticketData) {
        this.handshakeType = handshakeType;
        this.length = length;
        this.ticketData = ticketData;
    }

    public int getHandshakeType() {
        return handshakeType;
    }

    public void setHandshakeType(int handshakeType) {
        this.handshakeType = handshakeType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public byte[] getTicketData() {
        return ticketData;
    }

    public void setTicketData(byte[] ticketData) {
        this.ticketData = ticketData;
    }
}
