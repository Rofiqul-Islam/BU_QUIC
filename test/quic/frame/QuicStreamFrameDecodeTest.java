package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import quic.exception.QuicException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicStreamFrameDecodeTest {

    private static final String streamFrame = "0b0040cf3c21444f43545950" +
            "452068746d6c3e0a3c68746d6c206c61" +
            "6e673d22656e223e0a3c686561643e0a" +
            "20203c7469746c653e51554943207465" +
            "737420706167653c2f7469746c653e0a" +
            "3c2f686561643e0a3c626f64793e0a20" +
            "203c68313e48656c6c6f20576f726c64" +
            "213c2f68313e0a20203c612068726566" +
            "3d22384d223e384d6942206f66207261" +
            "6e646f6d20646174613c2f613e0a2020" +
            "3c6120687265663d2233324d223e3332" +
            "4d6942206f662072616e646f6d206461" +
            "74613c2f613e0a3c2f626f64793e0a3c" +
            "2f68746d6c3e0a";

    private static final String streamFrameData = "3c21444f43545950" +
            "452068746d6c3e0a3c68746d6c206c61" +
            "6e673d22656e223e0a3c686561643e0a" +
            "20203c7469746c653e51554943207465" +
            "737420706167653c2f7469746c653e0a" +
            "3c2f686561643e0a3c626f64793e0a20" +
            "203c68313e48656c6c6f20576f726c64" +
            "213c2f68313e0a20203c612068726566" +
            "3d22384d223e384d6942206f66207261" +
            "6e646f6d20646174613c2f613e0a2020" +
            "3c6120687265663d2233324d223e3332" +
            "4d6942206f662072616e646f6d206461" +
            "74613c2f613e0a3c2f626f64793e0a3c" +
            "2f68746d6c3e0a";

    private static final int streamId = 0;

    private static final int offset = 0;

    private static final boolean endOfStream = true;


    @DisplayName("Decode stream packet")
    @Test
    void shouldDecodeStreamPacketTest() throws QuicException {
        QuicStreamFrame qsf = new QuicStreamFrame(streamId, offset, endOfStream, streamFrameData.getBytes());
        QuicFrame decodedQsf = QuicFrame.decode(streamFrame.getBytes());
        assertEquals(qsf, decodedQsf);
    }

}
