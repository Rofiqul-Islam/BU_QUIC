package quic.util;

import quic.exception.QuicException;
import quic.frame.*;
import quic.packet.*;
import java.util.HashSet;

import java.util.Set;

/**
 * Util class
 *
 * @author Md Rofiqul Islam
 */
public class Util {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Method for retrieveing the length of variable length integer
     *
     * @param b first byte of variable length integer
     * @return the length of variable length integer
     */
    public static int variableLengthIntegerLength(byte b) {
        int temp = (int) b;
        int lenArry[] = new int[2];
        for (int c = 7; c >= 6; c--) {
            int x = (int) Math.pow(2, c);
            if ((x & temp) == 0) {
                lenArry[7 - c] = 0;
            } else {
                lenArry[7 - c] = 1;
            }
        }
        if (lenArry[0] == 0 && lenArry[1] == 0) {
            return 1;
        } else if (lenArry[0] == 0 && lenArry[1] == 1) {
            return 2;
        } else if (lenArry[0] == 1 && lenArry[1] == 0) {
            return 4;
        } else if (lenArry[0] == 1 && lenArry[1] == 1) {
            return 8;
        }

        return 0;
    }

    /**
     * Method for converting byte array to long.
     * Have two functionalites.
     * Able to convert both normal byte array and variavle length integer byte array to Long
     *
     * @param input Byte array containg variable length integer
     * @param type  type =0 for normal byte array, type =1 for variable length integer array
     * @return the long value of byte array and variable length integer array
     */
    public static long variableLengthInteger(byte[] input, int type) {
        if (type == 0) {
            String s = bytesArrayToHex(input);
            Long result = Long.parseLong(s, 16);
            return result;
        } else if (type == 1) {
            String s = Util.byteToHex((byte) (input[0] & 63));
            byte[] temp = new byte[input.length - 1];
            for (int i = 1; i < input.length; i++) {
                temp[i - 1] = input[i];
            }
            s += bytesArrayToHex(temp);
            //System.out.println("s = "+s);
            Long result = Long.parseLong(s, 16);
            return result;
        }
        return 0;
    }

    /**
     * Method for converting long input to variable length integer
     *
     * @param input long input which should be converted into variable length integer
     * @return byte array containing the variable length integer
     */
    public static byte[] generateVariableLengthInteger(Long input) {
        if (input < Math.pow(2, 6)) {              // adding 00 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input), 1);
            temp[0] += 0;
            return temp;
        } else if (input < Math.pow(2, 14)) {        // adding 01 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input), 2);
            temp[0] += 64;
            return temp;
        } else if (input < Math.pow(2, 30)) {        // adding 10 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input), 4);
            temp[0] += 128;
            return temp;
        } else if (input < (long) Math.pow(2, 62)) {     //adding 11 before the integer
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input), 8);
            temp[0] += 192;
            return temp;
        }
        return null;
    }


    /**
     * Method for converting bytes array to Hex String
     *
     * @param bytes input byte array
     * @return Hex String generated from byte array
     */
    public static String bytesArrayToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Method for converting byte to Hex
     *
     * @param b input byte
     * @return Hex value
     */
    public static String byteToHex(byte b) {
        int v = b & 0xFF;
        String hex_string = HEX_ARRAY[v >>> 4] + "";
        hex_string += HEX_ARRAY[v & 0x0F];
        return hex_string;
    }

    /**
     * Method for converting Hex String to byte array
     *
     * @param s           input string which need to be converted into byte array
     * @param requiredLen parameter for generating a fix size byte array
     * @return fixed size byte array
     */
    public static byte[] hexStringToByteArray(String s, int requiredLen) {
        if (requiredLen == 0) {
            int len = s.length();
            byte[] data = new byte[s.length() / 2];
            for (int i = 0; i < s.length(); i += 2) {
                data[i / 2] = (byte) (((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16)));

            }
            return data;
        } else {
            int len = s.length();
            int diff = requiredLen * 2 - len;
            for (int i = 0; i < diff; i++) {
                s = "0" + s;
            }
            byte[] data = new byte[s.length() / 2];
            for (int i = 0; i < s.length(); i += 2) {
                data[i / 2] = (byte) (((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16)));

            }
            return data;
        }
    }

    /**
     * Method for converting byte array to string
     *
     * @param input byte array
     * @return output string
     */
    public static String byteToString(byte[] input) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            // Only print the last two digits, but add a 0 if we need one
            String b = "0" + Integer.toHexString(input[i]);
            string.append(b.substring(b.lastIndexOf("") - 2));
        }
        return string.toString();
    }

    /**
     * Method for decoding initial packet
     *
     * @param arr        byte array containing encoded initial packet
     * @param headerByte
     * @return
     * @throws QuicException
     */
    public static QuicPacket quicIntialPacketDecoder(byte[] arr, int headerByte) throws QuicException {
        try {
            //-----------version -------------//
            int pointer= 1;
            byte[] version_arr = new byte[4];   // version takes 4 bytes
            int n = pointer;
            for (; n < pointer + 4; n++) {
                version_arr[n - pointer] = arr[n];
            }
            long version = Util.variableLengthInteger(version_arr, 0); // generating long version from byte array
            System.out.println("version = " + version);
            pointer = n;
            //-------------Destination ID -----------------//
            int dcIdLenD = (int) arr[pointer]; //destination id length
            pointer++;
            byte[] dcIdD = new byte[dcIdLenD];
            int i = pointer;
            for (; i < pointer + dcIdLenD; i++) {
                dcIdD[i - pointer] = arr[i];           // generating Destination Id byte array
            }
            pointer= i;
            //--------------Source Id ------------------//
            int scIdLenD = (int) arr[pointer];
            pointer++;
            byte[] scIdD = new byte[scIdLenD];
            int j = pointer;
            for (; j < pointer + scIdLenD; j++) {
                scIdD[j - pointer] = arr[j];        // generating Source Id byte array
            }
            pointer = j;
            //----------------token -----------------------//
            int tokenLengthLen = Util.variableLengthIntegerLength(arr[pointer]);
            byte[] tokenLength_arr = new byte[tokenLengthLen];
            for (int c = pointer; c < pointer + tokenLengthLen; c++) {
                tokenLength_arr[c - pointer] = arr[c];
            }
            pointer += tokenLengthLen;
            long tokenLength = Util.variableLengthInteger(tokenLength_arr, 1); // generating token length from
                                                                                     //  variable length integer
            byte[] token = new byte[(int) tokenLength];
            for (int c = pointer; c < pointer + tokenLength; c++) {
                token[c - pointer] = arr[c];             // generating token array
            }
            pointer += tokenLength;
            //------------ length-----------------//
            int lengthSize = Util.variableLengthIntegerLength(arr[pointer]);
            byte[] len_arr = new byte[lengthSize];
            for (int c = pointer; c < lengthSize + pointer; c++) {
                len_arr[c - pointer] = arr[c];
            }
            pointer += lengthSize;
            long length = Util.variableLengthInteger(len_arr, 1); // generating length from variable length integer
            //-------------packet number------------------//
            int packetNoLen = (headerByte & 3) + 1;  // last two bit of header byte indicates the packet number length

            byte[] packNum_arr = new byte[packetNoLen];
            for (int c = pointer; c < packetNoLen + pointer; c++) {
                packNum_arr[c - pointer] = arr[c];            // generating packet number containing byte array
            }
            pointer += packetNoLen;
            long packetNum = Util.variableLengthInteger(packNum_arr, 0); // converting byte array to long
            //----------payload--------------//
            byte[] payload = new byte[(int) (length - packetNoLen)];   // (length -packen number length) indicates the payload size
            int k = pointer;
            for (; k < pointer + (length - (packetNoLen)); k++) {
                payload[k - pointer] = arr[k];           // generating the payload array
                System.out.print(k + " ");
            }
            pointer = k;
            QuicPacket initialPacket = new QuicInitialPacket(dcIdD, packetNum, version, scIdD, frameDecode(payload)); // creating new initial packet
            return initialPacket;
        } catch (Exception e) {
            throw new QuicException(10, 0, "initial packet decode error");
        }


    }

    /**
     * Method for decoding the payload and generate frame set
     *
     * @param payload payload from packet
     * @return set of frames
     * @throws QuicException
     */
    public static Set<QuicFrame> frameDecode(byte[] payload) throws QuicException {
        Set<QuicFrame> temp = new HashSet<>();
        QuicFrame.setPayloadPostionIndicator(0);
        QuicFrame.setFlag(1);
        while (QuicFrame.getPayloadPostionIndicator() < payload.length) {
            temp.add(QuicFrame.decode(payload));
        }
        QuicFrame.setFlag(0);
        return temp;
    }


    /**
     * Method for decoding the short header packet
     * @param arr   byte array containing encoded shortHeader packet
     * @param dcIdSize
     * @return
     * @throws QuicException
     */
    public static QuicPacket quicShortHeaderDecoder(byte[] arr, int dcIdSize) throws QuicException {
        try {
            int pointer = 0;
            int headerByte = (int) arr[0];
            //--------------Destination ID -----------------//
            pointer ++;
            int dcIdLenD = dcIdSize;
            byte[] dcIdD = new byte[dcIdLenD];
            int i = pointer ;
            for (; i < pointer + dcIdLenD; i++) {
                dcIdD[i - pointer] = arr[i];        //generating Destination byte array
            }
            pointer = i;
            //---------packet number ---------------//
            int packetNoLen = (headerByte & 3) + 1;      // last two bit of header byte indicates the packet number length

            byte[] packNum_arr = new byte[packetNoLen];
            for (int c = pointer; c < packetNoLen + pointer; c++) {
                packNum_arr[c - pointer] = arr[c]; // generating packet number array
            }
            pointer += packetNoLen;
            long packetNum = Util.variableLengthInteger(packNum_arr, 0);
            //----------------payload ----------------//
            byte[] payload = new byte[(int) (arr.length - pointer)];        // all bytes after packet number is a part of payload
            int k = pointer;

            for (; k < arr.length; k++) {
                payload[k - pointer] = arr[k];         // generating payload
                System.out.print(k + " ");
            }
            pointer = k;
            QuicPacket shortHeaderPacket = new QuicShortHeaderPacket(dcIdD, packetNum, frameDecode(payload));  // creating Quic short header packet
            return shortHeaderPacket;

        } catch (Exception e) {
            throw new QuicException(10, 0, "Short Header packet decoder error");
        }
    }


}
