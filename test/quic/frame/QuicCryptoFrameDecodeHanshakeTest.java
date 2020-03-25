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
public class QuicCryptoFrameDecodeHanshakeTest {

    private static final String handshakeCryptoFrame = "4cf040d6aa0eda11ac3474e8d17ab654" +
            "2599ae3e8db8fbfc9d9219298f12e667" +
            "82954335dff937e7769d2eb3299cd32c" +
            "66023fb1464fcff402e53e12ab66b1e6" +
            "d05258e2e55bc50aa4719abb2be5ef9a" +
            "9dcfa773522d2b57378676d3cd6e50e5" +
            "4578f427b19bd78f30076918a6cad794" +
            "f16f2579b6b49be0c6737cf3f962e0cb" +
            "4b8dde60ae78330d197d1c35a3848e77" +
            "4bd18d44e3a35c2f9c9dbea59cab0577" +
            "321ae0cb13291400003025d73982d1c7" +
            "d1489f0f0c840529033e675282bab872" +
            "cd56c5b2ee9b3e78828b521c1ec70554" +
            "169a63381e46fcb8ea05";

    private static final String handshakeCryptoFrameData = "aa0eda11ac3474e8d17ab654" +
            "2599ae3e8db8fbfc9d9219298f12e667" +
            "82954335dff937e7769d2eb3299cd32c" +
            "66023fb1464fcff402e53e12ab66b1e6" +
            "d05258e2e55bc50aa4719abb2be5ef9a" +
            "9dcfa773522d2b57378676d3cd6e50e5" +
            "4578f427b19bd78f30076918a6cad794" +
            "f16f2579b6b49be0c6737cf3f962e0cb" +
            "4b8dde60ae78330d197d1c35a3848e77" +
            "4bd18d44e3a35c2f9c9dbea59cab0577" +
            "321ae0cb13291400003025d73982d1c7" +
            "d1489f0f0c840529033e675282bab872" +
            "cd56c5b2ee9b3e78828b521c1ec70554" +
            "169a63381e46fcb8ea05";

    private static final int handshakeCryptoFrameOffset = 3312;

    @DisplayName("Decode crypto frame sent from the server with offset in 0-RTT Handshake Packet")
    @Test
    void shouldDecodeCryptoFrameWithOffsetTest() throws QuicException {
        QuicCryptoFrame expectedCryptoFrame = new QuicCryptoFrame(handshakeCryptoFrameOffset, handshakeCryptoFrameData.getBytes());
        QuicFrame decodedCryptoFrame = QuicFrame.decode(handshakeCryptoFrame.getBytes());
        assertEquals(expectedCryptoFrame, decodedCryptoFrame);
    }
}
