package quic.packet;

import org.junit.jupiter.api.*;
import quic.serialization.exception.QuicException;
import quic.serialization.frame.*;
import quic.serialization.packet.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for the QuicShortHeaderPacket class
 *
 * @author Md Rofiqul Islam
 */
public class QuicShortHeaderPacketTest extends QuicPacketTest {
    public static int BASE_HEADER_BYTE = 64;

    public Set<QuicFrame> frames;

    @BeforeEach
    public void init() {
        this.frames = new HashSet<>();
        this.frames.add(new QuicStreamFrame(1,0,true,
                "Hello world".getBytes()));
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValid() {
            return getValidConnectionIds().flatMap(dcId
                    -> getValidPacketNumbers().map(packetNumber
                    -> dynamicTest("dcid = " + dcId + ", packet # = "
                    + packetNumber , () -> {
                        QuicShortHeaderPacket packet =
                                new QuicShortHeaderPacket(dcId,packetNumber,
                                        frames);
                        assertArrayEquals(dcId, packet.getDcID());
                        assertEquals(packetNumber, packet.getPacketNumber());
                        assertEquals(1, packet.getFrames().size());
                    })));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidDestinationId() {
            return getInvalidConnectionIds().map(dcId
                    -> dynamicTest("dcId = " + dcId, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicShortHeaderPacket packet =
                                    new QuicShortHeaderPacket(dcId,1, frames);
                });
            }));
        }

        @Test
        public void testNullDestinationId() {
            assertThrows(NullPointerException.class, () -> {
                QuicShortHeaderPacket packet = new QuicShortHeaderPacket(null,
                        1, frames);
            });
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidPacketNumber() {
            return getInvalidPacketNumbers().map(packetNum
                    -> dynamicTest("packetNum = " + packetNum, () -> {
                        assertThrows(IllegalArgumentException.class, () -> {
                            QuicShortHeaderPacket packet =
                                    new QuicShortHeaderPacket("a"
                                            .getBytes(CHARSET), packetNum,
                                            frames);
                });
            }));
        }

    }

    @Nested
    public class GettersAndSettersTest {
        private QuicShortHeaderPacket packet;

        @BeforeEach
        public void init() {
            this.packet = new QuicShortHeaderPacket("a".getBytes(CHARSET),
                    0, frames);
        }

        @TestFactory
        public Stream<DynamicTest> testValidDestinationIds() {
            return getValidConnectionIds().map(dcId
                    -> dynamicTest("dcId = " + dcId, () -> {
                        packet.setDcID(dcId);
                        assertArrayEquals(dcId, packet.getDcID());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidDestinationIds() {
            return getInvalidConnectionIds().map(dcId
                    -> dynamicTest("dcId = " + dcId, () -> {
                        assertThrows(IllegalArgumentException.class,
                                () -> packet.setDcID(dcId));
            }));
        }




        @TestFactory
        public Stream<DynamicTest> testValidPacketNumbers() {
            return getValidPacketNumbers().map(packetNum
                    -> dynamicTest("packet # = " + packetNum, () -> {
                packet.setPacketNumber(packetNum);
                assertEquals(packetNum, packet.getPacketNumber());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidPacketNumbers() {
            return getInvalidPacketNumbers().map(packetNum
                    -> dynamicTest("packet # = " + packetNum, () -> {
                        assertThrows(IllegalArgumentException.class,
                                () -> packet.setPacketNumber(packetNum));
            }));
        }

        @Test
        public void testAddingEmptyFrameList() {
            assertThrows(IllegalArgumentException.class, () -> {
                new QuicShortHeaderPacket(new byte[1], 1L, new HashSet<>());
            });
        }

        @Test
        public void testAddingNullFrameList() {
            assertThrows(NullPointerException.class, () -> {
                new QuicShortHeaderPacket(new byte[1], 1L, null);
            });
        }

        @TestFactory
        public Stream<DynamicTest> testAddingFrames() {
            return Stream.of(0, 1, 3, 17, 27, 1004).map(numFrames
                    -> dynamicTest("num frames = " + numFrames, () -> {
                Set<QuicFrame> frameSet = new HashSet<>();
                frameSet.add(new QuicStreamFrame(1,0,true,
                        "Hello world".getBytes()));
                this.packet = new QuicShortHeaderPacket("a".getBytes(CHARSET),
                        0, frameSet);
                for (int i = 0; i < numFrames; i++) {
                    QuicFrame frame = null;
                    if (i % 3 == 0) {
                        frame = new QuicStreamFrame(i,i,false,
                                "Hello world".getBytes());
                    } else if (i % 3 == 1) {
                        frame = new QuicStreamFrame(1,0,true,
                                "Hello world".getBytes());
                    } else {
                        frame = new QuicConnectionCloseFrame(i + 100, i % 30,
                                "reason");
                    }
                    frameSet.add(frame);
                    packet.addFrame(frame);
                }
                assertArrayEquals(frameSet.toArray(), packet.getFrames().toArray());
            }));
        }
    }

    public byte[] writeBytes(int headerByte, byte[] dcId, long packetNum,
                             Set<QuicFrame> frames) throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        // Write header byte (packet number of 0)
        encoding.write(headerByte);
        encoding.write(dcId);

        // Write the packet number to get the length
        ByteArrayOutputStream packetOut = new ByteArrayOutputStream();
        int prefix = headerByte & 0x3;
        for (int i = prefix; i >= 0; i--) {
            packetOut.write((int) packetNum >> 8 * i);
        }
        ByteArrayOutputStream frameOut = new ByteArrayOutputStream();
        encoding.write(packetOut.toByteArray());
        Iterator<QuicFrame> frameIter = frames.iterator();
        while (frameIter.hasNext()) {
            frameOut.write(frameIter.next().encode());
        }

        // Write the frames (for real this time)
        encoding.write(frameOut.toByteArray());

        return encoding.toByteArray();
    }

    @Nested
    public class EncodeTest {
        @TestFactory
        public Stream<DynamicTest> testWithFrames() {
            return Stream.of(0, 1, 3, 5, 7, 10).map(numFrames
                    -> dynamicTest("num frames = " + numFrames, () -> {
                byte[] dcId = "1".getBytes(CHARSET);
                byte[] scId = "1".getBytes(CHARSET);
                long packetNumber = 1;
                QuicShortHeaderPacket packet = new QuicShortHeaderPacket(dcId,
                        1, frames);
                Set<QuicFrame> frameSet = new HashSet<>(frames);
                for (int i = 0; i < numFrames; i++) {
                    QuicFrame frame;
                    if (i % 3 == 0) {
                        frame = new QuicStreamFrame(i,i,false,
                                "Hello world".getBytes());
                    } else if (i % 3 == 1) {
                        frame =new QuicStreamFrame(i,i,true,
                                "Quic Stream frame".getBytes());
                    } else {
                        frame = new QuicConnectionCloseFrame(i, i, "reason");
                    }
                    packet.addFrame(frame);
                    frameSet.add(frame);
                }
                byte[] encoding = writeBytes(BASE_HEADER_BYTE, dcId,
                        packetNumber, frameSet);
                assertArrayEquals(encoding, packet.encode());
            }));
        }

        @Test
        public void testLongIds() throws IOException, QuicException {
            byte[] dcId = "aaaaaaaaaaaaaaaaaaaa".getBytes(CHARSET);
            long packetNumber = 27;
            QuicShortHeaderPacket packet = new QuicShortHeaderPacket(dcId,
                    packetNumber, frames);
            byte[] encoding = writeBytes(BASE_HEADER_BYTE, dcId,packetNumber,
                    frames);
            assertArrayEquals(encoding, packet.encode());
        }

        @TestFactory
        public Stream<DynamicTest> testPacketNumbersWithHeader() {
            return Stream.of(0, 1, 2, 3).map(prefix
                    -> dynamicTest("prefix = " + prefix, () -> {
                byte[] dcId = "1".getBytes(CHARSET);
                long packetNum = (long) Math.pow(256, prefix + 1) - 1;
                int headerByte = BASE_HEADER_BYTE + prefix;
                QuicShortHeaderPacket packet = new QuicShortHeaderPacket(dcId,
                        packetNum, frames);
                byte[] encoding = writeBytes(headerByte,  dcId, packetNum,
                        frames);
                assertArrayEquals(encoding, packet.encode());
            }));
        }
    }

    @Nested
    public class DecodeTest {

        @TestFactory
        public Stream<DynamicTest> testRandomStrings() throws QuicException {
            return Stream.of("", "1234567890", "this is a long random string")
                    .map(str -> dynamicTest("str = " + str, () -> {
                assertThrows(QuicException.class, () -> {
                    byte[] encoding = str.getBytes(CHARSET);
                    QuicPacket packet = QuicPacket.decode(encoding);
                });
            }));
        }
    }
    @TestFactory
    public Stream<DynamicTest> testInvalidHeaderBytes() {
        return Stream.of(0, 100, 223, 250, 255).map(headerByte -> dynamicTest("headerByte = " + headerByte, () -> {
            assertThrows(QuicException.class, () -> {
                byte[] encoding = writeBytes(headerByte, "1".getBytes(CHARSET), 1, frames);
                QuicPacket packet = QuicPacket.decode(encoding);
            });
        }));
    }

    @TestFactory
    public Stream<DynamicTest> testEqualsAndHashcode() {
        return getValidConnectionIds().flatMap(dcId
                -> getValidPacketNumbers().map(packetNumber
                -> dynamicTest("dcid = " + dcId + ", packet # = "
                + packetNumber, () -> {
                    QuicShortHeaderPacket packet1 = new QuicShortHeaderPacket(dcId, packetNumber,frames);
                    QuicShortHeaderPacket packet2 = new QuicShortHeaderPacket(dcId, packetNumber,frames);
                    assertEquals(packet1, packet2);
                    assertEquals(packet1.hashCode(), packet2.hashCode());
                })));
    }


    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidConnectionIds().flatMap(dcId
                -> getValidPacketNumbers().map(packetNumber
                -> dynamicTest("dcid = " + dcId + ", packet # = "
                + packetNumber , () -> {
                    QuicShortHeaderPacket packet =
                            new QuicShortHeaderPacket(dcId, packetNumber,
                                    frames);
                    StringBuilder builder = new StringBuilder();
                    for (QuicFrame frame: frames) {
                        builder.append(frame.toString());
                    }
                    assertEquals("QuicShortHeaderPacket{dcID="
                            + printConnectionId(dcId) + ", packetNumber="
                            + packetNumber + ", frames=[" + builder.toString()
                            + "]}", packet.toString());
                })));
    }

    /**
     * Prints the hex-digit code representing the connection ID
     *
     * @param connectionId the ID to print
     * @return the hexadecimal string representing the connection ID
     */
    public String printConnectionId(byte[] connectionId) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < connectionId.length; i++) {
            // Only print the last two digits, but add a 0 if we need one
            String b = "0" + Integer.toHexString(connectionId[i]);
            builder.append(b.substring(b.lastIndexOf("") - 2));
        }
        return builder.toString();
    }
}
