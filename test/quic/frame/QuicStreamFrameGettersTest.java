package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicStreamFrameGettersTest extends QuicBaseStreamFrameTest {


    @DisplayName("Test getter data QuicStreamFrame")
    @Test
    public void dataGetterDataTest() {
        String data = "06000008";
        byte[] result = getDataFromObject(data);
        assertEquals(data, result);
    }

    @DisplayName("Test getter endOfStream QuicStreamFrame")
    @Test
    public void getterEndOfStreamTest() {
        boolean endOfStream = true;
        boolean result = getEndOfStreamFromObject(endOfStream);
        assertEquals(endOfStream, result);
    }


    @DisplayName("Test getter streamID QuicStreamFrame")
    @Test
    public void getterStreamIdTest() {
        int sID = 0;
        long result = getStreamIdFromObject(sID);
        assertEquals(sID, result);
    }


    @DisplayName("Test getter offset QuicStreamFrame")
    @Test
    public void getterOffsetTest() {
        int data = 1;
        long result = getOffsetFromObject(data);
        assertEquals(data, result);
    }






}
