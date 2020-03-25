package quic.crypto;

import java.util.List;

public class TlsExtension {

    private int handshakeType;

    private long length;

    private List<TlsExtension> tlsExtensions;

    public TlsExtension(int handshakeType, long length, List<TlsExtension> tlsExtensions) {
        this.handshakeType = handshakeType;
        this.length = length;
        this.tlsExtensions = tlsExtensions;
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

    public List<TlsExtension> getTlsExtensions() {
        return tlsExtensions;
    }

    public void setTlsExtensions(List<TlsExtension> tlsExtensions) {
        this.tlsExtensions = tlsExtensions;
    }
}
