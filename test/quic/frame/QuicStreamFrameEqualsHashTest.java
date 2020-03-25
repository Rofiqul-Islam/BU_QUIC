package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicStreamFrameEqualsHashTest {

    @DisplayName("Test hash code and equals of QuicStreamFrame")
    @Test
    public void quicStreamFrameHashEqualsTest() {
        assertEquals(new QuicStreamFrame(2, 0, true, "0400".getBytes()), new QuicStreamFrame(2, 0, true, "0400".getBytes()));
        assertNotEquals(new QuicStreamFrame(2, 0, false, "0400".getBytes()), new QuicStreamFrame(2, 0, true, "040000".getBytes()));
    }
}
