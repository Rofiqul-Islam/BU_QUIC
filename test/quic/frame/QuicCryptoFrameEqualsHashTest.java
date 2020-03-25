package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicCryptoFrameEqualsHashTest {

    @DisplayName("Test hash code and equals of QuicCryptoFrame")
    @Test
    public void quicCryptoFrameHashEqualsTest() {
        assertEquals(new QuicCryptoFrame(234, "0600008".getBytes()), new QuicCryptoFrame(234, "0600008".getBytes()));
        assertNotEquals(new QuicCryptoFrame(0, "0600008".getBytes()), new QuicCryptoFrame(234, "06000008".getBytes()));
    }
}
