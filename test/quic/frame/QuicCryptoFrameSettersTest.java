package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicCryptoFrameSettersTest extends QuicBaseCryptoFrameTest {

    @DisplayName("Test setter of Data field in Quic Crypto frame with some data")
    @Test
    public void dataSetterSomeDataTest() {
        byte[] expected = new byte[0];
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setData(expected);
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, dataFieldName));
    }

    @DisplayName("Test setter of Data field in Quic Crypto frame with some data")
    @Test
    public void dataSetterEmptyDataTest() {
        byte[] expected = new byte[1000];
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setData(expected);
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, dataFieldName));
    }

    @DisplayName("Test setter of Data field in Quic Crypto frame with no data")
    @Test
    public void dataSetterNoDataTest() {
        byte[] expected = null;
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setData(expected);
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, dataFieldName));
    }

    @DisplayName("Test setter of Data field in Quic Crypto frame with some data")
    @Test
    public void offsetSetterSomeOffsetTest() {
        int expected = 234;
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setOffset(expected);
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, offsetFieldName));
    }

    @DisplayName("Test setter of Data field in Quic Crypto frame with some data")
    @Test
    public void offsetSetterZeroOffsetTest() {
        int expected = 0;
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setOffset(expected);
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, offsetFieldName));
    }

    @DisplayName("Test setter of Offset field in Quic Crypto frame with no data")
    @Test
    public void offsetSetterNegativeOffsetTest() {
        int value = -9;
        QuicCryptoFrame quicCryptoFrame = new QuicCryptoFrame(0, new byte[0]);
        quicCryptoFrame.setOffset(value);
        int expected = 0;
        assertEquals(expected, getDataFromDataField(quicCryptoFrame, offsetFieldName));
    }

}
