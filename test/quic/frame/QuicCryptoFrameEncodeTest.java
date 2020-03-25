package quic.frame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicCryptoFrameEncodeTest {
    
    private static final String initCryptoFrameExpected = "0600411601" +
            "00011203034f2692794ebe373bcf90fb" +
            "c0266b1919a4e1161c924903b42237fa" +
            "33c1be286b20b5f69c213ccbba1f2e29" +
            "645d36a0dd6e4fafda84f296fffe58b0" +
            "7f8c753276ae00061302130113030100" +
            "00c3003300470045001700410494d178" +
            "05901ecf8e007cf0e796de756b33a9c2" +
            "24de02ab0911083328032a1ecc9b9443" +
            "2f179186aa726942e415032ab1437a9d" +
            "24bcc9e59718a9ebd36576407c000000" +
            "0f000d00000a72616c6974682e636f6d" +
            "0010000800060568712d3234002b0003" +
            "020304000d000a000808040403040102" +
            "01000a000600040017001dffa5002f00" +
            "2d000500048000400000070004800040" +
            "00000400048000800000080001010009" +
            "000103000100026710000e000104002d" +
            "0003020001";
    
    private static final String initCryptoFrameData = "01" +
            "00011203034f2692794ebe373bcf90fb" +
            "c0266b1919a4e1161c924903b42237fa" +
            "33c1be286b20b5f69c213ccbba1f2e29" +
            "645d36a0dd6e4fafda84f296fffe58b0" +
            "7f8c753276ae00061302130113030100" +
            "00c3003300470045001700410494d178" +
            "05901ecf8e007cf0e796de756b33a9c2" +
            "24de02ab0911083328032a1ecc9b9443" +
            "2f179186aa726942e415032ab1437a9d" +
            "24bcc9e59718a9ebd36576407c000000" +
            "0f000d00000a72616c6974682e636f6d" +
            "0010000800060568712d3234002b0003" +
            "020304000d000a000808040403040102" +
            "01000a000600040017001dffa5002f00" +
            "2d000500048000400000070004800040" +
            "00000400048000800000080001010009" +
            "000103000100026710000e000104002d" +
            "0003020001";


    /**
     * Data: default
     * Offset: 0
     */
    @DisplayName("Encode crypto packet without offset, default data")
    @Test
    void shouldEncodeCryptoPacketWithoutOffsetDefaultDataTest() throws IOException {
        QuicCryptoFrame initCryptoFrame = new QuicCryptoFrame(0, initCryptoFrameData.getBytes());
        byte[] quicCryptoFrameBytes = initCryptoFrame.encode();
        assertEquals(initCryptoFrameExpected, quicCryptoFrameBytes);
    }

}
