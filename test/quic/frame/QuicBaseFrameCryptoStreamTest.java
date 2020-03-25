package quic.frame;

import java.lang.reflect.Field;
/**
 * @author Jan Svacina
 * @version 1.1
 */
public abstract class QuicBaseFrameCryptoStreamTest {

    public Object getDataFromDataField(QuicFrame quicCryptoFrame, String fieldName){
        Field dataField = null;
        try {
            dataField = quicCryptoFrame.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        dataField.setAccessible(true);

        String actual = "";
        try {
            actual = (String) dataField.get(quicCryptoFrame);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Reflection failed on field" + dataField.getName());
        }

        return actual;
    }
}
