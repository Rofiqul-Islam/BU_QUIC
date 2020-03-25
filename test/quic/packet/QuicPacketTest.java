package quic.packet;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class QuicPacketTest {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static Stream<byte[]> getValidConnectionIds() {
        return Stream.of(new byte[0], "a".getBytes(CHARSET), "abc".getBytes(CHARSET), "#$%^&*".getBytes(CHARSET), "0".getBytes(CHARSET), "287340932".getBytes(CHARSET), "1234567890".getBytes(CHARSET));
    }

    public static Stream<byte[]> getInvalidConnectionIds() {
        return Stream.of(new byte[21], "asdfgasdfghjklhjklaslkdjf".getBytes(CHARSET), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes(CHARSET), "DYFUGHIJH34567890DTYUIKslerj".getBytes(CHARSET));
    }

    public static Stream<Long> getValidPacketNumbers() {
        return Stream.of(0L, 1L, 27L, (long) Integer.MAX_VALUE, (long) Math.pow(2, 32) - 1);
    }

    public static Stream<Long> getInvalidPacketNumbers() {
        return Stream.of(-1L, -27L, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static long CURRENT_VERSION = 25;

    public static Stream<Long> getValidVersions() {
        return Stream.of(0L, 1L, 23L, 24L, 25L, 26L, (long) Integer.MAX_VALUE);
    }

    public static Stream<Long> getInvalidVersions() {
        return Stream.of(-1L, -62L, Long.MIN_VALUE, Long.MAX_VALUE);
    }
}
