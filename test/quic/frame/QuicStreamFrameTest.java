package quic.frame;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for QuicStreamFrame class
 *
 * @author Jan Svacina
 */
public class QuicStreamFrameTest {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    // valid stream IDs
    private Stream<Long> getValidStreamIds() {
        return Stream.of(0L, 1L, 27L, (long) Integer.MAX_VALUE,
                4611686018427387800L);
    }

    // invalid stream IDs
    private Stream<Long> getInvalidStreamIds() {return Stream.of(-1L, -27L,
            4611686018427387903L + 1, Long.MIN_VALUE, Long.MAX_VALUE);}

    // valid offset
    public static Stream<Long> getValidOffset() {
        return Stream.of(0L, 1L, 27L, (long) Integer.MAX_VALUE,
                4611686018427387800L);
    }

    // invalid offset
    public static Stream<Long> getInvalidOffset() {
        return Stream.of(-1L, -27L, 4611686018427387903L + 1, Long.MIN_VALUE,
                Long.MAX_VALUE);
    }

    // valid end of stream
    private Stream<Boolean> getValidEndOfStream() {
        return Stream.of(true, false);
    }

    // valid data
    public static Stream<byte[]> getValidData() {
        return Stream.of(new byte[0], "aaaaa".getBytes(CHARSET));
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValid() {
            return getValidStreamIds().flatMap(streamId
                    -> getValidOffset().flatMap(offset
                    -> getValidEndOfStream().flatMap(endOfStream
                    -> getValidData().map(data -> dynamicTest("streamId = "
                    + streamId + ", offset # = " + offset + ", endOfStream = "
                    + endOfStream + ", data = " + Arrays.toString(data), () -> {
                QuicStreamFrame f = new QuicStreamFrame(streamId,
                        offset, endOfStream, data);
                assertEquals(streamId, f.getStreamId());
                assertEquals(offset, f.getOffset());
                assertEquals(endOfStream, f.isEndOfStream());
                assertArrayEquals(data, f.getData());
            })))));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidOffset() {
            return getInvalidOffset().map(offset -> dynamicTest("offset = "
                    + offset, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicStreamFrame f = new QuicStreamFrame(1L, offset,
                            false, new byte[0]);
                });
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidStreamId() {
            return getInvalidStreamIds().map(streamId
                    -> dynamicTest("streamId = " + streamId, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicStreamFrame f = new QuicStreamFrame(streamId, 2L,
                            false, new byte[0]);
                });
            }));
        }
    }

    @Nested
    public class GettersAndSettersTest {
        private QuicStreamFrame frame;

        @BeforeEach
        public void init() {
            this.frame = new QuicStreamFrame(1L, 2L, true,
                    "aaaa".getBytes());
        }

        @TestFactory
        public Stream<DynamicTest> testValidStreamIds() {
            return getValidStreamIds().map(streamId
                    -> dynamicTest("streamId = " + streamId, () -> {
                frame.setStreamId(streamId);
                assertEquals(streamId, frame.getStreamId());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidDestinationIds() {
            return getInvalidOffset().map(streamId
                    -> dynamicTest("streamId = " + streamId, () -> {
                assertThrows(IllegalArgumentException.class,
                        () -> frame.setStreamId(streamId));
            }));
        }
    }

    @Nested
    public class EncodeTest {
        private int streamId = 2;
        private int offset = 0;
        private boolean endOfStream = true;
        private byte[] data = new byte[]{(byte)0x04, (byte)0x00};

        @DisplayName("Encode stream packet")
        @Test
        void shouldEncodeStreamPacket() throws IOException {
            QuicStreamFrame qsf = new QuicStreamFrame(streamId, offset,
                    endOfStream, data);
            byte[] qsfBytes = qsf.encode();
            byte[] streamFrame = new byte[] { 0xB, 0x2, 0x2, 0x4, 0x0 };
            assertArrayEquals(qsfBytes, streamFrame);
        }
    }

    @Nested
    public class DecodeTest {
        @DisplayName("Decode stream packet")
        @Test
        void shouldDecodeStreamPacket() throws IOException {
            byte[] encodedFrame = new byte[] { 0xB, 0x0, 0x1, 0x3C };
            QuicStreamFrame qsf = new QuicStreamFrame(0L, 0L,
                    true, new byte[] { 0x3c } );
            assertArrayEquals(qsf.encode(), encodedFrame);
        }
    }

    @TestFactory
    public Stream<DynamicTest> testEqualsAndHashcode() {
        return getValidStreamIds().flatMap(streamId
                -> getValidOffset().flatMap(offset
                -> getValidEndOfStream().flatMap(endOfStream
                -> getValidData().map(data -> dynamicTest("streamId = "
                + streamId + ", offset # = " + offset + ", endOfStream = "
                + endOfStream + ", data = " + Arrays.toString(data), () -> {
            QuicStreamFrame f1 = new QuicStreamFrame(streamId, offset,
                    endOfStream, data);
            QuicStreamFrame f2 = new QuicStreamFrame(streamId, offset,
                    endOfStream, data);
            assertEquals(f1, f2);
            assertEquals(f1.hashCode(), f2.hashCode());
        })))));
    }


    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidStreamIds().flatMap(streamId
                -> getValidOffset().flatMap(offset
                -> getValidEndOfStream().flatMap(endOfStream
                -> getValidData().map(data -> dynamicTest("streamId = "
                + streamId + ", offset # = " + offset + ", endOfStream = "
                + endOfStream + ", data = " + Arrays.toString(data), () -> {
            QuicStreamFrame frame = new QuicStreamFrame(streamId, offset,
                    endOfStream, data);
            assertEquals("QuicStreamFrame{" + "streamId="
                    + streamId + ", offset=" + offset + ", endOfStream="
                    + endOfStream + "}", frame.toString());
        })))));
    }

}
