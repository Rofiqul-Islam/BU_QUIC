package quic.frame;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Quic Padding frame
 * @author Md Rofiqul Islam
 */
public class QuicPaddingFrame extends QuicFrame {

    private int length;


    public QuicPaddingFrame() {
    }

    public QuicPaddingFrame(int paddingSize) {
        length = paddingSize;
    }

    /**
     * Strictly speaking, a padding frame consists of one single byte. For convenience, here all subsequent padding
     * bytes are collected in one padding object.
     * @param buffer
     * @return
     */
    public QuicPaddingFrame parse(ByteBuffer buffer) {
        while (buffer.position() < buffer.limit() && buffer.get() == 0)
            length++;

        if (buffer.position() < buffer.limit()) {
            // Set back one position
            buffer.position(buffer.position() - 1);
        }

        return this;
    }


    public byte[] getBytes() {
        return new byte[length];
    }

    public boolean isAckEliciting() {
        return false;
    }

    @Override
    public String toString() {
        return "Padding(" + length + ")";
    }

    public int getLength() {
        return length;
    }

    @Override
    public byte[] encode() throws IOException {
        return new byte[0];
    }
}
