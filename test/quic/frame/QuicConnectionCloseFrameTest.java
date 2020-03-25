package quic.frame;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import quic.exception.QuicException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for the QuicConnectionCloseFrame class
 *
 * @author Denton Wood
 */
public class QuicConnectionCloseFrameTest {

    // QUIC error codes range from 0x0 to 0x1FF (8191), not including 0xC, 0xE, and 0xF
    Stream<Integer> getValidErrorCodes() {
        return Stream.of(0, 1, 11, 13, 16, 234, 8191);
    }

    Stream<Integer> getInvalidQuicErrorCodes() {
        return Stream.of(-1, 12, 14, 15, 8192, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    // The valid QUIC frame types range from 0x0 to 0x1E (30)
    Stream<Integer> getValidFrameTypes() {
        return Stream.of(0, 1, 3, 23, 30);
    }

    Stream<Integer> getInvalidFrameTypes() {
        return Stream.of(-1, 31, 234634, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    Stream<String> getValidPhrases() {
        return Stream.of("", "e", "error", "this is an error message", "$pec!@l čh@rĀct#rs");
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValidErrors() {
            return getValidErrorCodes().flatMap(errorCode -> getValidFrameTypes()
                    .flatMap(frameType -> getValidPhrases().map(phrase ->
                            dynamicTest("error code: " + errorCode + ", frame type = " +
                                    frameType + ", phrase = " + phrase, () -> {
                                QuicConnectionCloseFrame frame =
                                        new QuicConnectionCloseFrame(errorCode, frameType, phrase);
                                assertEquals(errorCode.longValue(), frame.getErrorCode());
                                assertEquals(frameType.longValue(), frame.getFrameType());
                                assertEquals(phrase, frame.getReasonPhrase());
                            }))));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidErrorCode() {
            return getInvalidQuicErrorCodes().map(errorCode ->
                    dynamicTest("error code: " + errorCode, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicConnectionCloseFrame frame = new QuicConnectionCloseFrame(errorCode, 0, "message");
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidFrameType() {
            return getInvalidFrameTypes().map(frameType ->
                    dynamicTest("frame type: " + frameType, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicConnectionCloseFrame frame = new QuicConnectionCloseFrame(0, frameType, "message");
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
            return getValidErrorCodes().map(errorCode -> dynamicTest("error code: " + errorCode,
                    () -> {
                        this.frame.setErrorCode(errorCode);
                        assertEquals(errorCode.longValue(), this.frame.getErrorCode());
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidQuicErrorCodes() {
            return getInvalidQuicErrorCodes().map(errorCode -> dynamicTest("error code: " + errorCode,
                    () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            this.frame.setErrorCode(errorCode);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidFrameTypes() {
            return getValidFrameTypes().map(frameType -> dynamicTest("frame type: " + frameType,
                    () -> {
                        this.frame.setFrameType(frameType);
                        assertEquals(frameType.longValue(), this.frame.getFrameType());
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidFrameTypes() {
            return getInvalidFrameTypes().map(frameType -> dynamicTest("frame type: " + frameType,
                    () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            this.frame.setFrameType(frameType);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidPhrases() {
            return getValidPhrases().map(phrase -> dynamicTest("phrase: " + phrase,
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
            int frameCode = 34;
            String reason = "reason";
            QuicConnectionCloseFrame frame = new QuicConnectionCloseFrame(errorCode, frameCode, reason);

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());
            assertEquals(frame.encode(), out.toByteArray());
        }

        @Test
        public void testMultipleByteFrame() throws IOException {
            int errorCode = 8191;
            int frameCode = 30;
            String reason = "This is a really long reason for tÉsting purposes. Look at how long it is!";
            QuicConnectionCloseFrame frame = new QuicConnectionCloseFrame(errorCode, frameCode, reason);

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            errorCode = Integer.rotateLeft(errorCode, 8);
            // Set the first bit
            bytes[2] = (byte) (errorCode + 128);
            bytes[3] = (byte) frameCode;
            bytes[4] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());
            assertEquals(out.toByteArray(), frame.encode());
        }
    }

    @Nested
    public class DecodeTest {
        @Test
        public void testOneByteFrame() throws IOException, QuicException {
            int errorCode = 5;
            int frameCode = 33;
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

            QuicConnectionCloseFrame connectionCloseFrame = (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @Test
        public void testMultipleByteFrame() throws IOException, QuicException {
            int errorCode = 2345;
            int frameCode = 28;
            String reason = "This is my reason and I will shout it, because it is my reason.";

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            errorCode = Integer.rotateLeft(errorCode, 8);
            // Set the first bit
            bytes[2] = (byte) (errorCode + 128);
            bytes[3] = (byte) frameCode;
            bytes[4] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            QuicFrame frame = QuicFrame.decode(out.toByteArray());
            assertTrue(frame instanceof QuicConnectionCloseFrame);

            QuicConnectionCloseFrame connectionCloseFrame = (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @Test
        public void testNullLengthString() throws IOException, QuicException {
            int errorCode = 11;
            int frameCode = 4;
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

            QuicConnectionCloseFrame connectionCloseFrame = (QuicConnectionCloseFrame) frame;
            assertEquals(errorCode, connectionCloseFrame.getErrorCode());
            assertEquals(frameCode, connectionCloseFrame.getFrameType());
            assertEquals(reason, connectionCloseFrame.getReasonPhrase());
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 12, 14})
        public void testInvalidErrorCode(int errorCode) throws IOException {
            int frameCode = 24;
            String reason = "Another reason";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            assertThrows(QuicException.class, () -> QuicFrame.decode(out.toByteArray()));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 31, 62})
        public void testInvalidFrameCode(int frameCode) throws IOException {
            int errorCode = 3;
            String reason = "Yet another reason";

            byte[] bytes = new byte[4];
            bytes[0] = (byte) QuicConnectionCloseFrame.FRAME_TYPE;
            bytes[1] = (byte) errorCode;
            bytes[2] = (byte) frameCode;
            bytes[3] = (byte) reason.length();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
            out.write(reason.getBytes());

            assertThrows(QuicException.class, () -> QuicFrame.decode(out.toByteArray()));
        }
    }

    @TestFactory
    public Stream<DynamicTest> testEqualsAndHashcode() {
        return getValidErrorCodes()
                .flatMap(errorCode -> getValidFrameTypes().flatMap(frameType ->
                        getValidPhrases().map(phrase -> dynamicTest("code: "
                                + errorCode + ", frame type: " + frameType + ", phrase: " + phrase, () -> {
                            QuicConnectionCloseFrame frame1 = new QuicConnectionCloseFrame(errorCode, frameType, phrase);
                            QuicConnectionCloseFrame frame2 = new QuicConnectionCloseFrame(errorCode, frameType, phrase);
                            assertEquals(frame1, frame2);
                            assertEquals(frame1.hashCode(), frame2.hashCode());
                        }))));
    }

    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidErrorCodes()
                .flatMap(errorCode -> getValidFrameTypes().flatMap(frameType ->
                        getValidPhrases().map(phrase -> dynamicTest("code: "
                                + errorCode + ", frame type: " + frameType + ", phrase: " + phrase, () -> {
                            QuicConnectionCloseFrame frame = new QuicConnectionCloseFrame(errorCode, frameType, phrase);
                            assertEquals("QuicConnectionCloseFrame{errorCode=" + errorCode + ", frameType=" + frameType + ", reasonPhrase='" + phrase + "'}", frame.toString());
                        }))));
    }
}
