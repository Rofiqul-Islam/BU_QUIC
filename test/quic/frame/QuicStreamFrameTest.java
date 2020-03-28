package quic.frame;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
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
    private static final int INITIAL_HEADER_BYTE = 0x8;

    // valid stream IDs
    private Stream<Long> getValidStreamIds() {
        return Stream.of(0L, 1L, 27L, (long) Integer.MAX_VALUE,
                4611686018427387800L);
    }

    // invalid stream IDs
    private Stream<Long> getInvalidStreamIds() {
        return Stream.of(-1L, -27L,
                4611686018427387903L + 1, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private Stream<Long> getValidOneByteNumbers() {
        return Stream.of(1L, 9L, 17L, 45L, 63L);
    }

    private Stream<Long> getValidTwoByteNumbers() {
        return Stream.of(64L, 1000L, 10000L, 16383L);
    }

    private Stream<Long> getValidFourByteNumbers() {
        return Stream.of(16384L, 100000L, 1073741823L);
    }

    private Stream<Long> getValidEightByteNumbers() {
        return Stream.of(1073741824L, 10000000000000L, 4611686018427387L);
    }

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
        @Nested
        public class OneByteTest {
            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidOneByteNumbers().flatMap(streamId
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", data = " + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, 0,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte = INITIAL_HEADER_BYTE;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    out.write(streamId.intValue());
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidOneByteNumbers().flatMap(
                        streamId -> getValidData().map(data -> dynamicTest(
                                "streamId = " + streamId + ", data = " + data, () -> {
                                    QuicStreamFrame frame =
                                            new QuicStreamFrame(streamId, 0,
                                                    true, data);
                                    ByteArrayOutputStream out =
                                            new ByteArrayOutputStream();
                                    int initialHeaderByte =
                                            INITIAL_HEADER_BYTE + 1;
                                    boolean writeData = false;
                                    if (data.length > 0) {
                                        initialHeaderByte += 2;
                                        writeData = true;
                                    }
                                    out.write(initialHeaderByte);
                                    out.write(streamId.intValue());
                                    if (writeData) {
                                        out.write(data.length);
                                        out.write(data);
                                    }
                                    assertArrayEquals(out.toByteArray(),
                                            frame.encode());
                                })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidOneByteNumbers().flatMap(streamId
                        -> getValidOneByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, offset,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    out.write(streamId.intValue());
                    out.write(offset.intValue());
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidOneByteNumbers().flatMap(streamId
                        -> getValidOneByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest(
                        "streamId = " + streamId
                                + ", offset = " + offset + ", data = "
                                + data, () -> {
                            QuicStreamFrame frame =
                                    new QuicStreamFrame(streamId,
                                            offset,
                                            true, data);
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 5;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            out.write(streamId.intValue());
                            out.write(offset.intValue());
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }
                            assertArrayEquals(out.toByteArray(),
                                    frame.encode());
                        }))));
            }
        }

        @Nested
        public class TwoByteTest {
            private void writeTwoByteVLNumber(long number,
                                              ByteArrayOutputStream out) {
                out.write((int) (number >> 8) + 0x40);
                out.write((int) (number & 0xFF));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidTwoByteNumbers().flatMap(streamId
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", data = " + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, 0,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte = INITIAL_HEADER_BYTE;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeTwoByteVLNumber(streamId, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidTwoByteNumbers().flatMap(
                        streamId -> getValidData().map(data -> dynamicTest(
                                "streamId = " + streamId + ", data = " + data, () -> {
                                    QuicStreamFrame frame =
                                            new QuicStreamFrame(streamId, 0,
                                                    true, data);
                                    ByteArrayOutputStream out =
                                            new ByteArrayOutputStream();
                                    int initialHeaderByte =
                                            INITIAL_HEADER_BYTE + 1;
                                    boolean writeData = false;
                                    if (data.length > 0) {
                                        initialHeaderByte += 2;
                                        writeData = true;
                                    }
                                    out.write(initialHeaderByte);
                                    writeTwoByteVLNumber(streamId, out);
                                    if (writeData) {
                                        out.write(data.length);
                                        out.write(data);
                                    }
                                    assertArrayEquals(out.toByteArray(),
                                            frame.encode());
                                })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidTwoByteNumbers().flatMap(streamId
                        -> getValidTwoByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, offset,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeTwoByteVLNumber(streamId, out);
                    writeTwoByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidTwoByteNumbers().flatMap(streamId
                        -> getValidTwoByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest(
                        "streamId = " + streamId
                                + ", offset = " + offset + ", data = "
                                + data, () -> {
                            QuicStreamFrame frame =
                                    new QuicStreamFrame(streamId,
                                            offset,
                                            true, data);
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 5;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeTwoByteVLNumber(streamId, out);
                            writeTwoByteVLNumber(offset, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }
                            assertArrayEquals(out.toByteArray(),
                                    frame.encode());
                        }))));
            }
        }

        @Nested
        public class FourByteTest {
            private void writeFourByteVLNumber(long number,
                                               ByteArrayOutputStream out) {
                out.write((int) (number >> 24) + 0x80);
                for (int i = 16; i >= 0; i -= 8) {
                    out.write((int) (number >> i) & 0xFF);
                }
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidFourByteNumbers().flatMap(streamId
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", data = " + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, 0,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte = INITIAL_HEADER_BYTE;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeFourByteVLNumber(streamId, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidFourByteNumbers().flatMap(
                        streamId -> getValidData().map(data -> dynamicTest(
                                "streamId = " + streamId + ", data = " + data, () -> {
                                    QuicStreamFrame frame =
                                            new QuicStreamFrame(streamId, 0,
                                                    true, data);
                                    ByteArrayOutputStream out =
                                            new ByteArrayOutputStream();
                                    int initialHeaderByte =
                                            INITIAL_HEADER_BYTE + 1;
                                    boolean writeData = false;
                                    if (data.length > 0) {
                                        initialHeaderByte += 2;
                                        writeData = true;
                                    }
                                    out.write(initialHeaderByte);
                                    writeFourByteVLNumber(streamId, out);
                                    if (writeData) {
                                        out.write(data.length);
                                        out.write(data);
                                    }
                                    assertArrayEquals(out.toByteArray(),
                                            frame.encode());
                                })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidFourByteNumbers().flatMap(streamId
                        -> getValidFourByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, offset,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeFourByteVLNumber(streamId, out);
                    writeFourByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidFourByteNumbers().flatMap(streamId
                        -> getValidFourByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest(
                        "streamId = " + streamId
                                + ", offset = " + offset + ", data = "
                                + data, () -> {
                            QuicStreamFrame frame =
                                    new QuicStreamFrame(streamId,
                                            offset,
                                            true, data);
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 5;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeFourByteVLNumber(streamId, out);
                            writeFourByteVLNumber(offset, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }
                            assertArrayEquals(out.toByteArray(),
                                    frame.encode());
                        }))));
            }
        }

        @Nested
        public class EightByteTest {
            private void writeEightByteVLNumber(long number,
                                                ByteArrayOutputStream out) {
                out.write((int) (number >> 56) + 0xC0);
                for (int i = 48; i >= 0; i -= 8) {
                    out.write((int) (number >> i) & 0xFF);
                }
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidEightByteNumbers().flatMap(streamId
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", data = " + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, 0,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte = INITIAL_HEADER_BYTE;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeEightByteVLNumber(streamId, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidEightByteNumbers().flatMap(
                        streamId -> getValidData().map(data -> dynamicTest(
                                "streamId = " + streamId + ", data = " + data, () -> {
                                    QuicStreamFrame frame =
                                            new QuicStreamFrame(streamId, 0,
                                                    true, data);
                                    ByteArrayOutputStream out =
                                            new ByteArrayOutputStream();
                                    int initialHeaderByte =
                                            INITIAL_HEADER_BYTE + 1;
                                    boolean writeData = false;
                                    if (data.length > 0) {
                                        initialHeaderByte += 2;
                                        writeData = true;
                                    }
                                    out.write(initialHeaderByte);
                                    writeEightByteVLNumber(streamId, out);
                                    if (writeData) {
                                        out.write(data.length);
                                        out.write(data);
                                    }
                                    assertArrayEquals(out.toByteArray(),
                                            frame.encode());
                                })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidEightByteNumbers().flatMap(streamId
                        -> getValidEightByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    QuicStreamFrame frame =
                            new QuicStreamFrame(streamId, offset,
                                    false, data);
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeEightByteVLNumber(streamId, out);
                    writeEightByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }
                    assertArrayEquals(out.toByteArray(),
                            frame.encode());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidEightByteNumbers().flatMap(streamId
                        -> getValidEightByteNumbers().flatMap(offset
                        -> getValidData().map(data -> dynamicTest(
                        "streamId = " + streamId
                                + ", offset = " + offset + ", data = "
                                + data, () -> {
                            QuicStreamFrame frame =
                                    new QuicStreamFrame(streamId,
                                            offset,
                                            true, data);
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 5;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeEightByteVLNumber(streamId, out);
                            writeEightByteVLNumber(offset, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }
                            assertArrayEquals(out.toByteArray(),
                                    frame.encode());
                        }))));
            }
        }
    }

    @Nested
    public class DecodeTest {
        @Nested
        public class OneByteTest {
            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidOneByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte = INITIAL_HEADER_BYTE;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            out.write(streamId.intValue());
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertFalse(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidOneByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 1;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            out.write(streamId.intValue());
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertTrue(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidOneByteNumbers().flatMap(streamId
                        -> getValidOneByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", offset = " + offset + ", data = " + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    out.write(streamId.intValue());
                    out.write(offset.intValue());
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertFalse(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidOneByteNumbers().flatMap(streamId
                        -> getValidOneByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 5;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    out.write(streamId.intValue());
                    out.write(offset.intValue());
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertTrue(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }
        }

        @Nested
        public class TwoByteTest {
            private void writeTwoByteVLNumber(long number,
                                              ByteArrayOutputStream out) {
                out.write((int) (number >> 8) + 0x40);
                out.write((int) (number & 0xFF));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidTwoByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte = INITIAL_HEADER_BYTE;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeTwoByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertFalse(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidTwoByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 1;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeTwoByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertTrue(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidTwoByteNumbers().flatMap(streamId
                        -> getValidTwoByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", offset = " + offset + ", data = " + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeTwoByteVLNumber(streamId, out);
                    writeTwoByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertFalse(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidTwoByteNumbers().flatMap(streamId
                        -> getValidTwoByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 5;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeTwoByteVLNumber(streamId, out);
                    writeTwoByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertTrue(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }
        }

        @Nested
        public class FourByteTest {
            private void writeFourByteVLNumber(long number,
                                               ByteArrayOutputStream out) {
                out.write((int) (number >> 24) + 0x80);
                for (int i = 16; i >= 0; i -= 8) {
                    out.write((int) (number >> i) & 0xFF);
                }
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidFourByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte = INITIAL_HEADER_BYTE;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeFourByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertFalse(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidFourByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 1;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeFourByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertTrue(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidFourByteNumbers().flatMap(streamId
                        -> getValidFourByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", offset = " + offset + ", data = " + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeFourByteVLNumber(streamId, out);
                    writeFourByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertFalse(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidFourByteNumbers().flatMap(streamId
                        -> getValidFourByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 5;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeFourByteVLNumber(streamId, out);
                    writeFourByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertTrue(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }
        }

        @Nested
        public class EightByteTest {
            private void writeEightByteVLNumber(long number,
                                                ByteArrayOutputStream out) {
                out.write((int) (number >> 56) + 0xC0);
                for (int i = 48; i >= 0; i -= 8) {
                    out.write((int) (number >> i) & 0xFF);
                }
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetNoFin() {
                return getValidEightByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte = INITIAL_HEADER_BYTE;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeEightByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertFalse(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testNoOffsetFin() {
                return getValidEightByteNumbers().flatMap(
                        streamId -> getValidData().map(data
                                -> dynamicTest("streamId = "
                                + streamId + ", data = " + data, () -> {
                            ByteArrayOutputStream out =
                                    new ByteArrayOutputStream();
                            int initialHeaderByte =
                                    INITIAL_HEADER_BYTE + 1;
                            boolean writeData = false;
                            if (data.length > 0) {
                                initialHeaderByte += 2;
                                writeData = true;
                            }
                            out.write(initialHeaderByte);
                            writeEightByteVLNumber(streamId, out);
                            if (writeData) {
                                out.write(data.length);
                                out.write(data);
                            }

                            QuicStreamFrame frame = (QuicStreamFrame)
                                    QuicFrame.decode(out.toByteArray());
                            assertEquals(streamId, frame.getStreamId());
                            assertEquals(0, frame.getOffset());
                            assertTrue(frame.isEndOfStream());
                            assertArrayEquals(data, frame.getData());
                        })));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetNoFin() {
                return getValidEightByteNumbers().flatMap(streamId
                        -> getValidEightByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = " + streamId
                        + ", offset = " + offset + ", data = " + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 4;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeEightByteVLNumber(streamId, out);
                    writeEightByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertFalse(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }

            @TestFactory
            public Stream<DynamicTest> testOffsetFin() {
                return getValidEightByteNumbers().flatMap(streamId
                        -> getValidEightByteNumbers().flatMap(offset
                        -> getValidData().map(data
                        -> dynamicTest("streamId = "
                        + streamId + ", offset = " + offset + ", data = "
                        + data, () -> {
                    ByteArrayOutputStream out =
                            new ByteArrayOutputStream();
                    int initialHeaderByte =
                            INITIAL_HEADER_BYTE + 5;
                    boolean writeData = false;
                    if (data.length > 0) {
                        initialHeaderByte += 2;
                        writeData = true;
                    }
                    out.write(initialHeaderByte);
                    writeEightByteVLNumber(streamId, out);
                    writeEightByteVLNumber(offset, out);
                    if (writeData) {
                        out.write(data.length);
                        out.write(data);
                    }

                    QuicStreamFrame frame =
                            (QuicStreamFrame) QuicFrame
                                    .decode(out.toByteArray());
                    assertEquals(streamId, frame.getStreamId());
                    assertEquals(offset, frame.getOffset());
                    assertTrue(frame.isEndOfStream());
                    assertArrayEquals(data, frame.getData());
                }))));
            }
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
