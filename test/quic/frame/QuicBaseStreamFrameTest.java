package quic.frame;

import java.lang.reflect.Field;

/**
 * @author Jan Svacina
 * @version 1.1
 */
public class QuicBaseStreamFrameTest extends QuicBaseFrameCryptoStreamTest{
    protected final String streamId = "streamId";
    protected final String offset = "offset";
    protected final String endOfStream = "endOfStream";
    protected final String data = "data";

    public byte[] getDataFromObject(String fieldValue){
        QuicStreamFrame sf = new QuicStreamFrame(0, 0, true, "".getBytes());
        Field field = null;
        try {
            field = sf.getClass().getDeclaredField(data);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(sf, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return sf.getData();
    }

    public long getOffsetFromObject(int fieldValue){
        QuicStreamFrame sf = new QuicStreamFrame(0, 0, true, "".getBytes());
        Field field = null;
        try {
            field = sf.getClass().getDeclaredField(offset);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(sf, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return sf.getOffset();
    }

    public long getStreamIdFromObject(int fieldValue){
        QuicStreamFrame sf = new QuicStreamFrame(0, 0, true, "".getBytes());
        Field field = null;
        try {
            field = sf.getClass().getDeclaredField(streamId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(sf, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return sf.getStreamId();
    }

    public boolean getEndOfStreamFromObject(boolean fieldValue){
        QuicStreamFrame sf = new QuicStreamFrame(0, 0, true, "".getBytes());
        Field field = null;
        try {
            field = sf.getClass().getDeclaredField(endOfStream);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.err.println("Error getting field " + field.getName());
        }
        field.setAccessible(true);
        try {
            field.set(sf, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error setting field " + field.getName());
        }
        return sf.isEndOfStream();
    }
}
