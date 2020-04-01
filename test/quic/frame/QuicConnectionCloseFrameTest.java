package quic.frame;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import quic.serialization.exception.QuicException;
import quic.serialization.frame.QuicConnectionCloseFrame;
import quic.serialization.frame.QuicFrame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for the QuicConnectionCloseFrame class
 *
 * @author Denton Wood
 */
public class QuicConnectionCloseFrameTest {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    // Error codes range from 0x0 to 0x1FF (8191) except 0xC, 0xE, and 0xF
    Stream<Long> getValidErrorCodes() {
        return Stream.of(0L, 1L, 11L, 13L, 16L, 234L, 8191L);
    }

    Stream<Long> getInvalidQuicErrorCodes() {
        return Stream.of(-1L, 12L, 14L, 15L, 8192L, Long.MIN_VALUE,
                Long.MAX_VALUE);
    }

    // The valid QUIC frame types range from 0x0 to 0x1E (30)
    Stream<Long> getValidFrameTypes() {
        return Stream.of(0L, 1L, 3L, 23L, 30L);
    }

    Stream<Long> getInvalidFrameTypes() {
        return Stream.of(-1L, 31L, 234634L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    Stream<String> getValidPhrases() {
        return Stream.of("", "e", "error", "this is an error message",
                "$pec!@l čh@rĀct#rs");
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValidErrors() {
            return getValidErrorCodes().flatMap(errorCode
                    -> getValidFrameTypes().flatMap(frameType
                    -> getValidPhrases().map(phrase ->
                    dynamicTest("error code: " + errorCode
                            + ", frame type = " + frameType
                            + ", phrase = " + phrase, () -> {
                        QuicConnectionCloseFrame frame =
                                new QuicConnectionCloseFrame(errorCode,
                                        frameType, phrase);
                        assertEquals(errorCode.longValue(),
                                frame.getErrorCode());
                        assertEquals(frameType.longValue(),
                                frame.getFrameType());
                        assertEquals(phrase, frame.getReasonPhrase());
                    }))));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidErrorCode() {
            return getInvalidQuicErrorCodes().map(errorCode ->
                    dynamicTest("error code: " + errorCode, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicConnectionCloseFrame frame =
                                    new QuicConnectionCloseFrame(errorCode,
                                            0, "message");
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidFrameType() {
            return getInvalidFrameTypes().map(frameType ->
                    dynamicTest("frame type: " + frameType, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicConnectionCloseFrame frame =
                                    new QuicConnectionCloseFrame(0,
                                            frameType, "message");
                        });
                    }));
        }
    }

    @Nested
    public class GettersAndSettersTest {
        private QuicConnectionCloseFrame frame;

        @BeforeEach
        public void init() {
            this.frame = new QuicConnectionCloseFrame(0, 0, "message");
        }

        @TestFactory
        public Stream<DynamicTest> testValidQuicErrorCodes() {
            return getValidErrorCodes().map(errorCode -> dynamicTest(
                    "error code: " + errorCode, () -> {
                        this.frame.setErrorCode(errorCode);
                        assertEquals(errorCode.longValue(),
                                this.frame.getErrorCode());
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidQuicErrorCodes() {
            return getInvalidQuicErrorCodes().map(errorCode
                    -> dynamicTest("error code: " + errorCode,
                    () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            this.frame.setErrorCode(errorCode);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidFrameTypes() {
            return getValidFrameTypes().map(frameType
                    -> dynamicTest("frame type: " + frameType,
                    () -> {
                        this.frame.setFrameType(frameType);
                        assertEquals(frameType.longValue(),
                                this.frame.getFrameType());
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidFrameTypes() {
            return getInvalidFrameTypes().map(frameType
                    -> dynamicTest("frame type: " + frameType,
                    () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            this.frame.setFrameType(frameType);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidPhrases() {
            return getValidPhrases().map(phrase
                    -> dynamicTest("phrase: " + phrase,
                    () -> {
                        this.frame.setReasonPhrase(phrase);
                        assertEquals(phrase, this.frame.getReasonPhrase());
                    }));
        }
    }

    @Nested
    public class EncodeTest {
        @Test
        public void testOneByteFrame() throws IOException {
            int errorCode = 23;
            int frameCode = 22;
            String reason = "reason";
            QuicConnectionCloseFrame frame =
                    new QuicConnectionCloseFrame(errorCode, frameCode, reason);

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());
            assertArrayEquals(frame.encode(), out.toByteArray());
        }

        @Test
        public void testMultipleByteFrame() throws IOException {
            int errorCode = 8191;
            int frameCode = 30;
            String reason = "This is a long reason for tÉsting purposes, "
                    + "but not too long";
            QuicConnectionCloseFrame frame =
                    new QuicConnectionCloseFrame(errorCode, frameCode, reason);

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[2] = (byte) errorCode;
            // Set the first bit
            bytes[1] = (byte) ((errorCode >> Byte.SIZE) + 64);
            bytes[3] = (byte) frameCode;
            bytes[4] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes(CHARSET));
            byte[] actualBytes = frame.encode();
            assertArrayEquals(out.toByteArray(), frame.encode());
        }
    }

    @Nested
    public class DecodeTest {
        @Test
        public void testOneByteFrame() throws IOException, QuicException {
            long errorCode = 5;
            long frameCode = 11;
            String reason = "reason";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            QuicFrame frame = QuicFrame.decode(out.toByteArray());
            assertTrue(frame instanceof QuicConnectionCloseFrame);

            QuicConnectionCloseFrame connectionCloseFrame =
                    (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @Test
        public void testMultipleByteFrame() throws IOException, QuicException {
            long errorCode = 2345;
            long frameCode = 28;
            String reason = "This is my reason and I will shout it, "
                    + "because it is good.";

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[2] = (byte) errorCode;
            // Set the second bit
            bytes[1] = (byte) ((errorCode >> Byte.SIZE) + 64);
            bytes[3] = (byte) frameCode;
            bytes[4] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            QuicFrame frame = QuicFrame.decode(out.toByteArray());
            assertTrue(frame instanceof QuicConnectionCloseFrame);

            QuicConnectionCloseFrame connectionCloseFrame =
                    (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @Test
        public void testNullLengthString() throws IOException, QuicException {
            long errorCode = 11;
            long frameCode = 4;
            String reason = "";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            QuicFrame frame = QuicFrame.decode(out.toByteArray());
            assertTrue(frame instanceof QuicConnectionCloseFrame);

            QuicConnectionCloseFrame connectionCloseFrame =
                    (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @ParameterizedTest
        @ValueSource(longs = {-1, 12, 14, 15})
        public void testInvalidErrorCode(long errorCode) throws IOException {
            long frameCode = 24;
            String reason = "Another reason";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            assertThrows(QuicException.class,
                    () -> QuicFrame.decode(out.toByteArray()));
        }

        @ParameterizedTest
        @ValueSource(longs = {-1, 31, 62})
        public void testInvalidFrameCode(long frameCode) throws IOException {
            long errorCode = 3;
            String reason = "Yet another reason";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            assertThrows(QuicException.class,
                    () -> QuicFrame.decode(out.toByteArray()));
        }
    }

    @TestFactory
    public Stream<DynamicTest> testEqualsAndHashcode() {
        return getValidErrorCodes()
                .flatMap(errorCode -> getValidFrameTypes().flatMap(frameType ->
                        getValidPhrases().map(phrase -> dynamicTest("code: "
                                + errorCode + ", frame type: " + frameType
                                + ", phrase: " + phrase, () -> {
                            QuicConnectionCloseFrame frame1 =
                                    new QuicConnectionCloseFrame(errorCode,
                                            frameType, phrase);
                            QuicConnectionCloseFrame frame2 =
                                    new QuicConnectionCloseFrame(errorCode,
                                            frameType, phrase);
                            assertEquals(frame1, frame2);
                            assertEquals(frame1.hashCode(), frame2.hashCode());
                        }))));
    }

    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidErrorCodes()
                .flatMap(errorCode -> getValidFrameTypes().flatMap(frameType ->
                        getValidPhrases().map(phrase
                                -> dynamicTest("code: " + errorCode
                                + ", frame type: " + frameType + ", phrase: "
                                + phrase, () -> {
                            QuicConnectionCloseFrame frame =
                                    new QuicConnectionCloseFrame(errorCode,
                                            frameType, phrase);
                            assertEquals("QuicConnectionCloseFrame"
                                    + "{errorCode=" + errorCode + ", frameType="
                                    + frameType + ", reasonPhrase='" + phrase
                                    + "'}", frame.toString());
                        }))));
    }
}
