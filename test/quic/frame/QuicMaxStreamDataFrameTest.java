package quic.frame;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for the QuicMaxStreamDataFrame class
 *
 * @author Denton Wood
 */
public class QuicMaxStreamDataFrameTest {
    public Stream<Long> getValidVariableLengthNumbers() {
        return Stream.of(0L, 1L, 43L, 37435L, (long) Integer.MAX_VALUE,
                4611686018427387903L);
    }

    public Stream<Long> getInvalidVariableLengthNumbers() {
        return Stream.of(-1L, -6344L, Long.MIN_VALUE,
                4611686018427387904L, Long.MAX_VALUE);
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValid() {
            return getValidVariableLengthNumbers().flatMap(streamId
                    -> getValidVariableLengthNumbers().map(maxData
                    -> dynamicTest("streamId = " + streamId + ", max data = "
                    + maxData, () -> {
                        QuicMaxStreamDataFrame frame =
                                new QuicMaxStreamDataFrame(streamId, maxData);
                        assertEquals(streamId, frame.getStreamId());
                        assertEquals(maxData, frame.getMaximumStreamData());
            })));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidStreamId() {
            return getInvalidVariableLengthNumbers().map(streamId
                    -> dynamicTest("streamId = " + streamId, () -> {
                        assertThrows(IllegalArgumentException.class,
                                () -> new QuicMaxStreamDataFrame(streamId,
                                        1L));
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidMaxData() {
            return getInvalidVariableLengthNumbers().map(maxData
                    -> dynamicTest("max data = " + maxData, () -> {
                        assertThrows(IllegalArgumentException.class,
                                () -> new QuicMaxStreamDataFrame(1, maxData));
            }));
        }
    }

    public void writeVariableLengthNumber(long number,
                                          ByteArrayOutputStream out) {
        int prefix = 0;
        int numBytes = 1;
        if (number > 1073741823) {
            prefix = 0xC0;
            numBytes = 8;
        } else if (number > 16383) {
            prefix = 0x80;
            numBytes = 4;
        } else if (number > 63) {
            prefix = 0x40;
            numBytes = 2;
        }

        for (int i = numBytes - 1; i >= 0; i--) {
            int val = (int) (number >> (8 * i));
            if (i == (numBytes - 1)) {
                val += prefix;
            }
            out.write(val);
        }
    }

    @TestFactory
    public Stream<DynamicTest> testEncode() {
        return getValidVariableLengthNumbers().flatMap(streamId
                -> getValidVariableLengthNumbers().map(maxData
                -> dynamicTest("streamId = " + streamId + ", max data = "
                + maxData, () -> {
            QuicMaxStreamDataFrame frame =
                    new QuicMaxStreamDataFrame(streamId, maxData);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(QuicMaxStreamDataFrame.FRAME_TYPE);
            writeVariableLengthNumber(streamId, out);
            writeVariableLengthNumber(maxData, out);
            assertArrayEquals(out.toByteArray(), frame.encode());
        })));
    }

    @TestFactory
    public Stream<DynamicTest> testDecode() {
        return getValidVariableLengthNumbers().flatMap(streamId
                -> getValidVariableLengthNumbers().map(maxData
                -> dynamicTest("streamId = " + streamId + ", max data = "
                + maxData, () -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(QuicMaxStreamDataFrame.FRAME_TYPE);
            writeVariableLengthNumber(streamId, out);
            writeVariableLengthNumber(maxData, out);

            QuicMaxStreamDataFrame frame = (QuicMaxStreamDataFrame)
                    QuicFrame.decode(out.toByteArray());
            assertEquals(streamId, frame.getStreamId());
            assertEquals(maxData, frame.getMaximumStreamData());
        })));
    }

    @TestFactory
    public Stream<DynamicTest> equalsAndHashCode() {
        return getValidVariableLengthNumbers().flatMap(streamId
                -> getValidVariableLengthNumbers().map(maxData
                -> dynamicTest("streamId = " + streamId + ", max data = "
                + maxData, () -> {
            QuicMaxStreamDataFrame frame1 =
                    new QuicMaxStreamDataFrame(streamId, maxData);
            QuicMaxStreamDataFrame frame2 =
                    new QuicMaxStreamDataFrame(streamId, maxData);
            assertEquals(frame1, frame2);
            assertEquals(frame1.hashCode(), frame2.hashCode());
        })));
    }

    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidVariableLengthNumbers().flatMap(streamId
                -> getValidVariableLengthNumbers().map(maxData
                -> dynamicTest("streamId = " + streamId + ", max data = "
                + maxData, () -> {
            QuicMaxStreamDataFrame frame =
                    new QuicMaxStreamDataFrame(streamId, maxData);
            assertEquals("QuicMaxStreamDataFrame{streamId=" + streamId + ", "
                    + "maximumStreamData=" + maxData + "}", frame.toString());
        })));
    }
}
