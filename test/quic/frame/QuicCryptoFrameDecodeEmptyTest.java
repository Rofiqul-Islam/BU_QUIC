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
public class QuicCryptoFrameDecodeEmptyTest {

    private static final String encodedCryptoPacketNoDataNoOffset = "06000008";

    @DisplayName("Decode crypto frame without offset and null data")
    @Test
    void shouldDecodeCryptoFrameNullDataNoOffsetTest() throws QuicException {
        QuicCryptoFrame expectedCryptoFrame = new QuicCryptoFrame(0, null);
        QuicFrame decodedCryptoFrame = QuicFrame.decode(encodedCryptoPacketNoDataNoOffset.getBytes());
        assertEquals(expectedCryptoFrame, decodedCryptoFrame);
    }


    @DisplayName("Decode crypto frame without offset and empty data")
    @Test
    void shouldDecodeCryptoFrameEmptyDataNoOffsetTest() throws QuicException {
        QuicCryptoFrame expectedCryptoFrame = new QuicCryptoFrame(0, "".getBytes());
        QuicFrame decodedCryptoFrame = QuicFrame.decode(encodedCryptoPacketNoDataNoOffset.getBytes());
        assertEquals(expectedCryptoFrame, decodedCryptoFrame);
    }


    @DisplayName("Decode crypto frame with negative offset and empty data")
    @Test
    void shouldDecodeCryptoFrameEmptyDataNegativeOffsetTest() throws QuicException {
        QuicCryptoFrame expectedCryptoFrame = new QuicCryptoFrame(-2, "".getBytes());
        QuicFrame decodedCryptoFrame = QuicFrame.decode(encodedCryptoPacketNoDataNoOffset.getBytes());
        assertEquals(expectedCryptoFrame, decodedCryptoFrame);
    }

}
