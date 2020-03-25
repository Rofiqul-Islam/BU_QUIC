package quic.util;

import quic.frame.QuicFrame;

public class DecodedFrame {
    QuicFrame quicFrame;
    int inderx;

    public DecodedFrame(QuicFrame quicFrame, int inderx) {
        this.quicFrame = quicFrame;
        this.inderx = inderx;
    }

    public QuicFrame getQuicFrame() {
        return quicFrame;
    }

    public void setQuicFrame(QuicFrame quicFrame) {
        this.quicFrame = quicFrame;
    }

    public int getInderx() {
        return inderx;
    }

    public void setInderx(int inderx) {
        this.inderx = inderx;
    }
}
