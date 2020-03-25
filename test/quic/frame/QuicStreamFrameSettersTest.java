package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicStreamFrameSettersTest extends QuicBaseStreamFrameTest{

    @DisplayName("Test setters from QuicStreamFrame")
    @Test
    public void dataSetterSomeDataTest() {
        int streamId = 2;
        int offset = 1;
        boolean endOfStream = true;
        String data = "0400";

        QuicStreamFrame qsf = new QuicStreamFrame(0, 0, false, "".getBytes());
        qsf.setStreamId(streamId);
        qsf.setOffset(offset);
        qsf.setEndOfStream(endOfStream);
        qsf.setData(data.getBytes());

        assertEquals(streamId, getDataFromDataField(qsf, "streamId"));
        assertEquals(data, getDataFromDataField(qsf, "data"));
        assertEquals(endOfStream, getDataFromDataField(qsf, "endOfStream"));
        assertEquals(offset, getDataFromDataField(qsf, "offset"));

    }
}
