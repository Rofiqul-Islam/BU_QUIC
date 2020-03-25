package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 */
public class QuicCryptoFrameEncodeEmptyTest {

    private static final String encodedCryptoPacketNoDataExpected = "06e30008";

    /**
     * Data: null
     * Offset: 227
     * Should return: offset 227, data empty, calculate length, length 8
     */
    @DisplayName("Encodes crypto packet with offset, null data")
    @Test
    void shouldEncodeCryptoPacketNullDataOffsetTest() throws IOException {
        QuicCryptoFrame initCryptoFrame = new QuicCryptoFrame(227, null);
        byte[] quicCryptoFrameBytes = initCryptoFrame.encode();
        assertEquals(encodedCryptoPacketNoDataExpected, quicCryptoFrameBytes);
    }

    private static final String encodedCryptoPacketNoDataNoOffsetExpected = "06000008";

    /**
     * Data: ""
     * Offset: -2
     * Should return: offset 0, no data
     */
    @DisplayName("Encodes crypto packet with no offset, no data")
    @Test
    void shouldEncodeCryptoPacketEmptyDataNegativeOffsetTest() throws IOException {
        QuicCryptoFrame initCryptoFrame = new QuicCryptoFrame(-2, "".getBytes());
        byte[] quicCryptoFrameBytes = initCryptoFrame.encode();
        assertEquals(encodedCryptoPacketNoDataNoOffsetExpected, quicCryptoFrameBytes);
    }

}
