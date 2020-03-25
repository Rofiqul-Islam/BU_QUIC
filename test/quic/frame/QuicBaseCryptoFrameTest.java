package quic.frame;

import java.lang.reflect.Field;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public abstract class QuicBaseCryptoFrameTest extends QuicBaseFrameCryptoStreamTest{

    protected final String dataFieldName = "data";
    protected final String offsetFieldName = "offset";

    public byte[] getDataFromObject(String fieldValue){
        QuicCryptoFrame cryptoFrame = new QuicCryptoFrame(0, "".getBytes());
        Field field = null;
        try {
            field = cryptoFrame.getClass().getDeclaredField("data");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(cryptoFrame, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return cryptoFrame.getData();
    }

    public long getOffsetFromObject(int offset){
        QuicCryptoFrame cryptoFrame = new QuicCryptoFrame(0, "".getBytes());
        Field field = null;
        try {
            field = cryptoFrame.getClass().getDeclaredField("offset");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(cryptoFrame, offset);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return cryptoFrame.getOffset();
    }



}
