package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicCryptoFrameConstructorTest {

    @DisplayName("Crypto Frame Constructor Null Values")
    @Test
    void shouldSetFieldsFromConstructorNullValues() {
        QuicCryptoFrame qcf = new QuicCryptoFrame(0, null);
        assertEquals(qcf.getData(), "");
        assertEquals(qcf.getOffset(), 0);
    }

    @DisplayName("Crypto Frame Constructor Some Values")
    @Test
    void shouldSetFieldsFromConstructorSomeValues() {
        QuicCryptoFrame qcf = new QuicCryptoFrame(235, "0600008".getBytes());
        assertEquals(qcf.getData(), "0600008");
        assertEquals(qcf.getOffset(), 235);
    }

    @DisplayName("Crypto Frame Constructor Negative Value")
    @Test
    void shouldSetFieldsFromConstructorNegativeValue() {
        QuicCryptoFrame qcf = new QuicCryptoFrame(-9, "0600008".getBytes());
        assertEquals(qcf.getData(), "0600008");
        assertEquals(qcf.getOffset(), 0);
    }

}
