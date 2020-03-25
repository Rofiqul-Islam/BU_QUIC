package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import quic.exception.QuicException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicCryptoFrameDecodeOneRttTest {

    private static final String oneRTTCryptoFrame = "06004059040000550001518013294ac5209248" +
                    "11b0d8c60730336e295aca328352b9757" +
                    "ccec50a58b557bb02e6624d2184002058" +
                    "4c07040ecfd1b531495b0b7904e07310b" +
                    "b371765ca0f44e66cd4debd35bbf30008" +
                    "002a0004ffffffff";

    private static final String oneRTTCryptoFrameData = "40000550001518013294ac520924811b0d8c6073" +
            "0336e295aca328352b9757ccec50a5" +
            "8b557bb02e6624d21840020584c070" +
            "40ecfd1b531495b0b7904e07310bb3" +
            "71765ca0f44e66cd4debd35bbf3000" +
            "8002a0004ffffffff";

    private static final int oneRTTCryptoFrameOffset = 0;


    @DisplayName("Decode crypto frame sent from the server without offset in 1-RTT packet")
    @Test
    void shouldDecodeCryptoFrameWithoutOffsetTest() throws QuicException {
        QuicCryptoFrame expectedCryptoFrame = new QuicCryptoFrame(oneRTTCryptoFrameOffset, oneRTTCryptoFrameData.getBytes());
        QuicFrame decodedCryptoFrame = QuicFrame.decode(oneRTTCryptoFrame.getBytes());
        assertEquals(expectedCryptoFrame, decodedCryptoFrame);
    }

}
