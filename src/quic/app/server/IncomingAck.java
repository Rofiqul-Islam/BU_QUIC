package quic.app.server;

public class IncomingAck {
    private long packetNumer;
    private long offset;

    public IncomingAck(long packetNumer, long offset) {
        this.packetNumer = packetNumer;
        this.offset = offset;
    }

    public long getPacketNumer() {
        return packetNumer;
    }

    public void setPacketNumer(long packetNumer) {
        this.packetNumer = packetNumer;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
