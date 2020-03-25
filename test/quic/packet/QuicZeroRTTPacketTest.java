package quic.packet;

import org.junit.jupiter.api.*;
import quic.exception.QuicException;
import quic.frame.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Tests for the QuicZeroRTTPacket class
 *
 * @author Denton Wood
 */
public class QuicZeroRTTPacketTest extends QuicPacketTest {
    public static final int BASE_HEADER_BYTE = 208;

    public Set<QuicFrame> frames;

    @BeforeEach
    public void init() {
        this.frames = new HashSet<>();
        this.frames.add(new QuicConnectionCloseFrame(0, 0, "reason"));
    }

    @Nested
    public class ConstructorTest {
        @TestFactory
        public Stream<DynamicTest> testValid() {
            return getValidConnectionIds().flatMap(dcId -> getValidPacketNumbers()
                    .flatMap(packetNumber -> getValidVersions().flatMap(version ->
                            getValidConnectionIds().map(scId -> dynamicTest(
                                    "dcid = " + dcId + ", packet # = " + packetNumber + ", version = "
                                            + version + ", scid = " + scId, () -> {
                                        QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, packetNumber, version, scId, frames);
                                        assertArrayEquals(dcId, packet.getDcID());
                                        assertEquals(packetNumber, packet.getPacketNumber());
                                        assertEquals(version, packet.getVersion());
                                        assertArrayEquals(scId, packet.getScID());
                                        assertEquals(1, packet.getFrames().size());
                                    })))));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidDestinationId() {
            return getInvalidConnectionIds().map(dcId -> dynamicTest("dcId = " + dcId, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, 1, CURRENT_VERSION, "a".getBytes(CHARSET), frames);
                });
            }));
        }

        @Test
        public void testNullDestinationId() {
            assertThrows(NullPointerException.class, () -> {
                QuicZeroRTTPacket packet = new QuicZeroRTTPacket(null, 1, CURRENT_VERSION, "a".getBytes(CHARSET), frames);
            });
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidSourceId() {
            return getInvalidConnectionIds().map(scId -> dynamicTest("scId = " + scId, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicZeroRTTPacket packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), 1, CURRENT_VERSION, scId, frames);
                });
            }));
        }

        @Test
        public void testNullSourceId() {
            assertThrows(NullPointerException.class, () -> {
                QuicZeroRTTPacket packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), 1, CURRENT_VERSION, null, frames);
            });
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidPacketNumber() {
            return getInvalidPacketNumbers().map(packetNum -> dynamicTest("packetNum = " + packetNum, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicZeroRTTPacket packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), packetNum, CURRENT_VERSION, "b".getBytes(CHARSET), frames);
                });
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidVersion() {
            return getInvalidVersions().map(version -> dynamicTest("version = " + version, () -> {
                assertThrows(IllegalArgumentException.class, () -> {
                    QuicZeroRTTPacket packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), 1, version, "b".getBytes(CHARSET), frames);
                });
            }));
        }
    }

    @Nested
    public class GettersAndSettersTest {
        private QuicZeroRTTPacket packet;

        @BeforeEach
        public void init() {
            this.packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), 0, 0, "b".getBytes(CHARSET), frames);
        }

        @TestFactory
        public Stream<DynamicTest> testValidDestinationIds() {
            return getValidConnectionIds().map(dcId -> dynamicTest("dcId = " + dcId, () -> {
                this.packet.setDcID(dcId);
                assertArrayEquals(dcId, this.packet.getDcID());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidDestinationIds() {
            return getInvalidConnectionIds().map(dcId -> dynamicTest("dcId = " + dcId, () -> {
                assertThrows(IllegalArgumentException.class, () -> this.packet.setDcID(dcId));
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidSourceIds() {
            return getValidConnectionIds().map(scId -> dynamicTest("scId = " + scId, () -> {
                this.packet.setScID(scId);
                assertArrayEquals(scId, packet.getScID());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidSourceIds() {
            return getInvalidConnectionIds().map(scId -> dynamicTest("scId = " + scId, () -> {
                assertThrows(IllegalArgumentException.class, () -> this.packet.setScID(scId));
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidVersions() {
            return getValidVersions().map(version -> dynamicTest("version = " + version, () -> {
                this.packet.setVersion(version);
                assertEquals(version, packet.getVersion());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidVersions() {
            return getInvalidVersions().map(version -> dynamicTest("version = " + version, () -> {
                assertThrows(IllegalArgumentException.class, () -> this.packet.setVersion(version));
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testValidPacketNumbers() {
            return getValidPacketNumbers().map(packetNum -> dynamicTest("packet # = " + packetNum, () -> {
                this.packet.setPacketNumber(packetNum);
                assertEquals(packetNum, packet.getPacketNumber());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidPacketNumbers() {
            return getInvalidPacketNumbers().map(packetNum -> dynamicTest("packet # = " + packetNum, () -> {
                assertThrows(IllegalArgumentException.class, () -> this.packet.setPacketNumber(packetNum));
            }));
        }

        @Test
        public void testAddingNullFrameList() {
            assertThrows(NullPointerException.class, () -> {
                new QuicZeroRTTPacket(new byte[1], 1L, 1L, new byte[1], null);
            });
        }

        @Test
        public void testAddingEmptyFrameList() {
            assertThrows(IllegalArgumentException.class, () -> {
                new QuicZeroRTTPacket(new byte[1], 1L, 1L, new byte[1], new HashSet<>());
            });
        }

        @TestFactory
        public Stream<DynamicTest> testAddingFrames() {
            return Stream.of(1, 3, 17, 27, 1004).map(numFrames -> dynamicTest("num frames = " + numFrames, () -> {
                Set<QuicFrame> frameSet = new HashSet<>();
                frameSet.add(new QuicConnectionCloseFrame(0, 0, "message"));
                this.packet = new QuicZeroRTTPacket("a".getBytes(CHARSET), 0, 0, "b".getBytes(CHARSET), frameSet);
                for (int i = 0; i < numFrames; i++) {
                    QuicFrame frame = null;
                    if (i % 3 == 0) {
                        frame = new QuicConnectionCloseFrame(i + 100, i % 30, "reason");
                    } else if (i % 3 == 1) {
                        frame = new QuicCryptoFrame(i, "data".getBytes(CHARSET));
                    } else {
                        frame = new QuicStreamFrame(i, i, false, "data".getBytes(CHARSET));
                    }
                    frameSet.add(frame);
                    this.packet.addFrame(frame);
                }
                assertArrayEquals(frameSet.toArray(), this.packet.getFrames().toArray());
            }));
        }
    }

    public void writeVariableLengthNumber(long number, ByteArrayOutputStream out) {
        if (number > 1073741823) {
            // The prefix is too big to hold in a long
            out.write((int) (number >> 56) + 128 + 64);
            out.write((int) (number >> 48));
            out.write((int) (number >> 40));
            out.write((int) (number >> 32));
        }
        if (number > 16383) {
            if (number < 1073741823) {
                number += Math.pow(2, 31);
            }
            out.write((int) (number >> 24));
            out.write((int) (number >> 16));
        }
        if (number > 63) {
            if (number < 16383) {
                number += Math.pow(2, 14);
            }
            out.write((int) (number >> 8));
        }
        out.write((int) number);
    }

    public byte[] writeBytes(int headerByte, long version, int dcIdLen, byte[] dcId, int scIdLen,
                             byte[] scId, long packetNum, Set<QuicFrame> frames) throws IOException {
        ByteArrayOutputStream encoding = new ByteArrayOutputStream();
        // Write header byte (packet number of 0)
        encoding.write(headerByte);

        // Write all four version bytes
        encoding.write((int) version >> 24);
        encoding.write((int) version >> 16);
        encoding.write((int) version >> 8);
        encoding.write((int) version);

        // Write source and destination IDs
        encoding.write(dcIdLen);
        encoding.write(dcId);
        encoding.write(scIdLen);
        encoding.write(scId);

        int len = 0;

        // Write the frames to get the length
        ByteArrayOutputStream frameOut = new ByteArrayOutputStream();
        Iterator<QuicFrame> frameIter = frames.iterator();
        while (frameIter.hasNext()) {
            frameOut.write(frameIter.next().encode());
        }
        len += frameOut.size();

        // Write the packet number to get the length
        ByteArrayOutputStream packetOut = new ByteArrayOutputStream();
        int prefix = headerByte & 0x3;
        for (int i = prefix; i >= 0; i--) {
            packetOut.write((int) packetNum >> 8 * i);
        }
        len += packetOut.size();

        // Write length of packet using variable-length encoding
        writeVariableLengthNumber(len, encoding);

        // Write the packet number using a variable-length encoding of 0 bytes
        encoding.write(packetOut.toByteArray());

        // Write the frames (for real this time)
        encoding.write(frameOut.toByteArray());

        return encoding.toByteArray();
    }

    @Nested
    public class EncodeTest {
        @TestFactory
        public Stream<DynamicTest> testValidVersions() {
            return getValidVersions().map(version -> dynamicTest("version = " + version, () -> {
                QuicZeroRTTPacket packet = new QuicZeroRTTPacket("1".getBytes(CHARSET), 1, version, "1".getBytes(CHARSET), frames);
                byte[] encoding = writeBytes(BASE_HEADER_BYTE, version, 1, "1".getBytes(CHARSET), 1, "1".getBytes(CHARSET), 1, frames);
                assertArrayEquals(encoding, packet.encode());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testWithFrames() {
            return Stream.of(1, 3, 5, 7, 10).map(numFrames -> dynamicTest("num frames = " + numFrames, () -> {
                byte[] dcId = "1".getBytes(CHARSET);
                byte[] scId = "1".getBytes(CHARSET);
                long packetNumber = 1;
                QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, packetNumber, CURRENT_VERSION, scId, frames);
                Set<QuicFrame> frameSet = new HashSet<>(frames);
                for (int i = 0; i < numFrames; i++) {
                    QuicFrame frame = null;
                    if (i % 3 == 0) {
                        frame = new QuicStreamFrame(i, i, false, "data".getBytes());
                    } else if (i % 3 == 1) {
                        frame = new QuicCryptoFrame(i, "data".getBytes(CHARSET));
                    } else {
                        frame = new QuicConnectionCloseFrame(i, i, "reason");
                    }
                    packet.addFrame(frame);
                    frameSet.add(frame);
                }
                byte[] encoding = writeBytes(BASE_HEADER_BYTE, CURRENT_VERSION, 1, dcId, 1, scId, packetNumber, frameSet);
                assertArrayEquals(encoding, packet.encode());
            }));
        }

        @Test
        public void testLongIds() throws IOException {
            byte[] dcId = "aaaaaaaaaaaaaaaaaaaa".getBytes(CHARSET);
            byte[] scId = "88888888888888888888".getBytes(CHARSET);
            long packetNumber = 27;
            QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, packetNumber, CURRENT_VERSION, scId, frames);
            byte[] encoding = writeBytes(BASE_HEADER_BYTE, CURRENT_VERSION, 20, dcId, 20, scId, packetNumber, frames);
            assertArrayEquals(encoding, packet.encode());
        }

        @TestFactory
        public Stream<DynamicTest> testPacketNumbersWithHeader() {
            return Stream.of(0, 1, 2, 3).map(prefix -> dynamicTest("prefix = " + prefix, () -> {
                byte[] dcId = "1".getBytes(CHARSET);
                byte[] scId = "1".getBytes(CHARSET);
                long packetNum = (long) Math.pow(256, prefix + 1) - 1;
                int headerByte = BASE_HEADER_BYTE + prefix;
                QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, packetNum, CURRENT_VERSION, scId, frames);
                byte[] encoding = writeBytes(headerByte, CURRENT_VERSION, 1, dcId, 1, scId, packetNum, frames);
                assertArrayEquals(encoding, packet.encode());
            }));
        }
    }

    @Nested
    public class DecodeTest {
        @TestFactory
        public Stream<DynamicTest> testValidVersions() {
            return getValidVersions().map(version -> dynamicTest("version = " + version, () -> {
                byte[] dcId = "1".getBytes(CHARSET);
                byte[] scId = "1".getBytes(CHARSET);
                byte[] encoding = writeBytes(BASE_HEADER_BYTE, version, 1, dcId, 1, scId,1, frames);
                QuicZeroRTTPacket packet = (QuicZeroRTTPacket) QuicPacket.decode(encoding);
                assertArrayEquals(dcId, packet.getDcID());
                assertArrayEquals(scId, packet.getScID());
                assertEquals(1, packet.getPacketNumber());
                assertEquals(version, packet.getVersion());
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testInvalidHeaderBytes() {
            return Stream.of(0, 100, 223, 250, 255).map(headerByte -> dynamicTest("headerByte = " + headerByte, () -> {
                assertThrows(QuicException.class, () -> {
                    byte[] encoding = writeBytes(headerByte, CURRENT_VERSION, 1, "1".getBytes(CHARSET), 1, "1".getBytes(CHARSET), 1, frames);
                    QuicPacket packet = QuicPacket.decode(encoding);
                });
            }));
        }

        @TestFactory
        public Stream<DynamicTest> testRandomStrings() {
            return Stream.of("", "abc123", "1234567890", "this is a long random string").map(str -> dynamicTest("str = " + str, () -> {
                assertThrows(QuicException.class, () -> {
                    byte[] encoding = str.getBytes(CHARSET);
                    QuicPacket packet = QuicPacket.decode(encoding);
                });
            }));
        }
    }

    @TestFactory
    public Stream<DynamicTest> testEqualsAndHashcode() {
        return getValidConnectionIds().flatMap(dcId -> getValidPacketNumbers()
                .flatMap(packetNumber -> getValidVersions().flatMap(version ->
                        getValidConnectionIds().map(scId -> dynamicTest(
                                "dcid = " + dcId + ", packet # = " + packetNumber + ", version = "
                                        + version + ", scid = " + scId, () -> {
                                    QuicZeroRTTPacket packet1 = new QuicZeroRTTPacket(dcId, packetNumber, version, scId, frames);
                                    QuicZeroRTTPacket packet2 = new QuicZeroRTTPacket(dcId, packetNumber, version, scId, frames);
                                    assertEquals(packet1, packet2);
                                    assertEquals(packet1.hashCode(), packet2.hashCode());
                                })))));
    }

    @TestFactory
    public Stream<DynamicTest> testToString() {
        return getValidConnectionIds().flatMap(dcId -> getValidPacketNumbers()
                .flatMap(packetNumber -> getValidVersions().flatMap(version ->
                        getValidConnectionIds().map(scId -> dynamicTest(
                                "dcid = " + dcId + ", packet # = " + packetNumber + ", version = "
                                        + version + ", scid = " + scId, () -> {
                                    QuicZeroRTTPacket packet = new QuicZeroRTTPacket(dcId, packetNumber, version, scId, frames);
                                    StringBuilder builder = new StringBuilder();
                                    for (QuicFrame frame: frames) {
                                        builder.append(frame.toString());
                                    }
                                    assertEquals("QuicZeroRTTPacket{version=" + version + ", scID=" + printConnectionId(scId) + ", dcID=" + printConnectionId(dcId) + ", packetNumber=" + packetNumber + ", frames=[" + builder.toString() + "]}", packet.toString());
                                })))));
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
