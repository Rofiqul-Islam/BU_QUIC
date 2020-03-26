package quic.frame;

/**
 * Tests for the QuicAckFrameTest class
 *
 * @author Rafi
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestFactory;

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
        return Stream.of(0, 1, 31, 32, 1000);
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
                        long[] gaps = new long[rangeCount];
                        long[] acks = new long[rangeCount];

                        if (largestAck < firstAckRange) {
                            QuicAckFrame frame =
                                    new QuicAckFrame(largestAck, ackDelay,
                                            rangeCount, firstAckRange);
                            for (int i = 0; i < rangeCount; i++) {
                                frame.addGapAndAck(gaps[i], acks[i]);
                            }
                            assertEquals(largestAck, frame.getLargestAck());
                            assertEquals(ackDelay, frame.getDelay());
                            assertEquals((long) rangeCount,
                                    frame.getRangeCount());
                            assertEquals(firstAckRange,
                                    frame.getFirstAckRange());
                            assertEquals(rangeCount, frame.getGaps().size());
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
}