package quic.frame;

/**
 * Tests for the QuicAckFrameTest class
 *
 * @author Rafi
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import quic.serialization.exception.QuicException;
import quic.serialization.frame.QuicAckFrame;
import quic.serialization.frame.QuicFrame;


public class QuicAckFrameTest {


    // Largest Acknowledged
    public static Stream<Long> getValidLargestAck() {
        return Stream.of(0L, 1L, 11L, 13L, 16L, 234L, 4611686018427387903L);
    }

    public static Stream<Long> getInvalidLargestAck() {
        return Stream.of(-1L, -1L, Long.MIN_VALUE, 4611686018427387904L,
                Long.MAX_VALUE);
    }

    // ACK Delay
    Stream<Long> getValidAckDelay() {
        return Stream.of(0L, 1L, 3L, 23L, 30L, 4611686018427387903L);
    }

    Stream<Long> getInvalidAckDelay() {
        return Stream.of(-1L, -31L, Long.MIN_VALUE, 4611686018427387904L,
                Long.MAX_VALUE);
    }


    // ACK Range Count
    Stream<Integer> getValidAckRangeCount() {
        return Stream.of(12, 1, 31, 32, 1000);
    }

    Stream<Integer> getInvalidAckRangeCount() {
        return Stream.of(-1, -31, -234634, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
    }

    // First ACK Range
    Stream<Long> getValidFirstAckRange() {
        return Stream.of(0L, 1L, 3L, 2L, 4L);
    }

    Stream<Long> getInvalidFirstAckRange() {
        return Stream.of(-1L, -31L, -234634L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Nested
    public class ConstructorTest {

        @TestFactory
        public Stream<DynamicTest> testValid() {
            return getValidLargestAck().flatMap(largestAck
                    -> getValidAckDelay().flatMap(ackDelay
                    -> getValidAckRangeCount().flatMap(rangeCount
                    -> getValidFirstAckRange().map(firstAckRange
                    -> dynamicTest("largest Acknowledgement = "
                            + largestAck + ", ACK Delay = " + ackDelay
                            + ", ACK Range Count = " + rangeCount
                            + ", First ACK Range = " + firstAckRange,
                    () -> {
                        

                        if (largestAck < firstAckRange) {
                            QuicAckFrame frame =
                                    new QuicAckFrame(largestAck, ackDelay,
                                            rangeCount, firstAckRange);
                            
                            assertEquals(largestAck, frame.getLargestAck());
                            assertEquals(ackDelay, frame.getDelay());
                            assertEquals((long) rangeCount,
                                    frame.getRangeCount());
                            assertEquals(firstAckRange,
                                    frame.getFirstAckRange());
                         
                        }
                    })))));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidLargestAck() {
            return getInvalidLargestAck().map(largestAck ->
                    dynamicTest("largest Acknowledgement: "
                            + largestAck, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                        	
                            QuicAckFrame frame = new QuicAckFrame(-1, 1, 1, 1);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidAckDelay() {
            return getInvalidAckDelay()
                    .map(ackDelay -> dynamicTest("ACK Delay: " + ackDelay, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                        	
                            QuicAckFrame frame = new QuicAckFrame(1, -1, 1, 1);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidAckRangeCount() {
            return getInvalidAckRangeCount().map(ackRangeCount
                    -> dynamicTest("ACK Range Count" + ackRangeCount,
                    () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                        	
                            QuicAckFrame frame = new QuicAckFrame(1, 1, -1, 1);
                        });
                    }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidFirstAckRange() {
            return getInvalidFirstAckRange().map(firstAckRange
                    -> dynamicTest("First ACK Range" + firstAckRange, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                	
                    QuicAckFrame frame = new QuicAckFrame(1, 1, 1, -1);
                });
            }));
        }
    }
    
    @Nested
    public class EncodeTest {
        @Test
        public void encodeTest() throws IOException {
        	
        	long largestAck = 5L;
            long delay = 14L;
            int rangeCount = 0;
            long firstAckRange = 6L;
           
            
            QuicAckFrame frame =
                    new QuicAckFrame(largestAck, delay, rangeCount, firstAckRange);

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicAckFrame.FRAME_TYPE;
            bytes[1] = (byte) largestAck;
            bytes[2] = (byte) delay;
            bytes[3] = (byte) rangeCount;
            bytes[4] = (byte) firstAckRange;
           

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
          
            
            assertArrayEquals(frame.encode(), out.toByteArray());
        }

    }

    @Nested
    public class DecodeTest {
        @Test
        public void decodeTest() throws IOException, QuicException {
            
            long largestAck = 5L;
            long delay = 14L;
            int rangeCount = 0;
            long firstAckRange = 6L;
            

            byte[] bytes = new byte[5];
            bytes[0] = (byte) QuicAckFrame.FRAME_TYPE;
            bytes[1] = (byte) largestAck;
            bytes[2] = (byte) delay;
            bytes[3] = (byte) rangeCount;
            bytes[4] = (byte) firstAckRange;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(bytes);
           

            QuicFrame frame = QuicFrame.decode(out.toByteArray());
            assertTrue(frame instanceof QuicAckFrame);

            QuicAckFrame quicAckFrame =
                    (QuicAckFrame) frame;
            assertEquals(largestAck, quicAckFrame.getLargestAck());
            assertEquals(delay, quicAckFrame.getDelay());
            assertEquals(rangeCount, quicAckFrame.getRangeCount());
            assertEquals(firstAckRange, quicAckFrame.getFirstAckRange());
            assertEquals(rangeCount, quicAckFrame.getRangeCount());
           
            
        }

    }
}
