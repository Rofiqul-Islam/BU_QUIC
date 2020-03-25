package quic.crypto;

import java.util.List;

public class CryptoData {

    private byte[] random;

    private byte[] sessionId;

    private byte[] cipherSuites;

    private int handshakeType;

    private long length;

    private byte[] data;

    private List<Extension> extensions;

    public CryptoData(int handshakeType, long length, byte[] data, List<Extension> extensions) {
        this.handshakeType = handshakeType;
        this.length = length;
        this.data = data;
        this.extensions = extensions;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public byte[] getRandom() {
        return random;
    }

    public void setRandom(byte[] random) {
        this.random = random;
    }

    public byte[] getSessionId() {
        return sessionId;
    }

    public void setSessionId(byte[] sessionId) {
        this.sessionId = sessionId;
    }

    public byte[] getCipherSuites() {
        return cipherSuites;
    }

    public void setCipherSuites(byte[] cipherSuites) {
        this.cipherSuites = cipherSuites;
    }
}
