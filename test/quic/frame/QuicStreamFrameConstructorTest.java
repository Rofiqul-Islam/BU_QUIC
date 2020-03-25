package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicStreamFrameConstructorTest {

    @DisplayName("Quic Stream Frame Constructor")
    @Test
    void shouldCreateStreamFrameFromConstructor() {
        int streamId = 2;
        int offset = 0;
        boolean endOfStream = true;
        String data = "0400";
        QuicStreamFrame qsf = new QuicStreamFrame(streamId, offset, endOfStream, data.getBytes());
        assertEquals(qsf.getData(), data);
        assertEquals(qsf.getOffset(), offset);
        assertEquals(qsf.getStreamId(), streamId);
        assertEquals(qsf.isEndOfStream(), endOfStream);

    }

}
