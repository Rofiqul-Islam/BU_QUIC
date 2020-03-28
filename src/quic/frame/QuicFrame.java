package quic.frame;


import quic.exception.QuicException;
import quic.util.Util;

import java.io.*;


/**
 * Represents a QUIC frame.
 *
 * @author Md Rofiqul Islam
 * @version 1.1
 */

public abstract class QuicFrame {
    /**
     * payload index variable
     */
    static int payloadPostionIndicator;
    /**
     * indicator for multiple frame in payload
     */
    static int flag = 0;

    /**
     * Quic frame constructor
     */
    public QuicFrame() {
        if (flag == 0) {
            payloadPostionIndicator = 0;          // for multiple frame in a same packet
        }

    }

    public static int getFlag() {
        return flag;
    }

    public static void setFlag(int flag) {
        QuicFrame.flag = flag;
    }

    /**
     * Encodes the frame into bytes to be sent over the network
     *
     * @return the encoded frame
     */


    public abstract byte[] encode() throws IOException;

    public static int getPayloadPostionIndicator() {
        return payloadPostionIndicator;
    }

    public static void setPayloadPostionIndicator(int payloadPostionIndicator) {
        QuicFrame.payloadPostionIndicator = payloadPostionIndicator;
    }

    /**
     * Decodes a byte stream from the network into a frame
     *
     * @param arr the byte array from which to read the encoded packet
     */
    public static QuicFrame decode(byte[] arr) throws QuicException {

        byte headerByte = arr[payloadPostionIndicator];       // payloadpositionIndicator indicates the next byte should be read from payload
        if (headerByte == 2 || headerByte == 3) {       //ACK frame = type 2 & 3
            return quicAckFrameDecoder(arr);
        } else if (headerByte >= 8 && headerByte <= 15) {          //Stream frame = type 8-15
            return quicStreamFrameDecoder(arr, headerByte);
        } else if (headerByte == 28 || headerByte == 29) {
            return quicConnectionCloseFrameDecoder(arr);    // connection Close frame, type 28 and 29
        } else {
            throw new QuicException(7, 0, "Unknown headerByte of frame");
        }
    }

    /**
     * Method for decoding the byte array which contains Quic Ack frame
     *
     * @param arr input array
     * @return Quic Ack frame
     * @throws QuicException
     */
    public static QuicFrame quicAckFrameDecoder(byte[] arr) throws QuicException {
        try {
            int pointer = payloadPostionIndicator + 1;
            // ---------------Largest Acknowledgement---------//
            int largestAckLen = Util.variableLengthIntegerLength(arr[pointer]);  // calculating the length of variable integer
            byte[] largestAck_arr = new byte[largestAckLen];
            for (int n = pointer; n < pointer + largestAckLen; n++) {
                largestAck_arr[n - pointer] = arr[n];       // creating the array which contains variable length integer
            }
            long largestAck = Util.variableLengthInteger(largestAck_arr, 1);  // calculating largest Ack
            pointer = pointer + largestAckLen;
            //--------------- Ack Delay ------------------//

            int ackDelayLen = Util.variableLengthIntegerLength(arr[pointer]);   // calculating the length of variable integer
            byte[] ackDelay_arr = new byte[ackDelayLen];
            for (int n = pointer; n < pointer + ackDelayLen; n++) {
                ackDelay_arr[n - pointer] = arr[n];            // creating the array which contains variable length integer
            }
            long ackDelay = Util.variableLengthInteger(ackDelay_arr, 1);        // Calculating ack delay
            pointer = pointer + ackDelayLen;
            //---------Ack Range Count-----------//
            int ackRangeCountLen = Util.variableLengthIntegerLength(arr[pointer]);        // calculating the length of variable intege
            byte[] ackRangeCount_arr = new byte[ackRangeCountLen];
            for (int n = pointer; n < pointer + ackRangeCountLen; n++) {
                ackRangeCount_arr[n - pointer] = arr[n];                   // creating the array which contains variable length integer
            }
            long ackRangeCount = Util.variableLengthInteger(ackRangeCount_arr, 1);    // calculating the ack range count
            pointer = pointer + ackRangeCountLen;
            //--------------- First Ack Range -----------------//
            int firstAckRangeLen = Util.variableLengthIntegerLength(arr[pointer]);     // calculating the length of variable integer
            byte[] firstAckRange_arr = new byte[firstAckRangeLen];
            for (int n = pointer; n < pointer + firstAckRangeLen; n++) {
                firstAckRange_arr[n - pointer] = arr[n];                     // creating the array which contains variable length integer
            }
            long firstAckRange = Util.variableLengthInteger(firstAckRange_arr, 1);  // calculating the First AckRange
            pointer = pointer + firstAckRangeLen;
            //--------- ACK Ranges-------//
            /*List<Long> gapList = new ArrayList<>();     // list of GAP
            List<Long> ackList = new ArrayList<>();     // list of ACK
            for (long i = 0; i < ackRangeCount; i++) {
                int gapLen = Util.variableLengthIntegerLength(arr[pointer]);    // calculating gap length
                byte[] gap_arr = new byte[gapLen];
                for (int n = pointer; n < pointer + gapLen; n++) {
                    gap_arr[n - pointer] = arr[n];
                }
                long gap = Util.variableLengthInteger(gap_arr, 1);   // calculating gap by variable length integer parsing
                gapList.add(gap);
                pointer = pointer + gapLen;

                int ackRangeLen = Util.variableLengthIntegerLength(arr[pointer]);    // calculating ACK length
                byte[] ackRange_arr = new byte[ackRangeLen];
                for (int n = pointer; n < pointer + ackRangeLen; n++) {
                    ackRange_arr[n - pointer] = arr[n];
                }
                long ackRange = Util.variableLengthInteger(ackRange_arr, 1);      // calculating ACK by variable length integer parsing
                ackList.add(ackRange);
                pointer = pointer + ackRangeLen;

            }*/

            setPayloadPostionIndicator(pointer); // updating the next posting of payload to be decoded
            //Iterator<Long> gapIterator = gapList.iterator();
            //Iterator<Long> ackIterator = ackList.iterator();
            QuicAckFrame quicAckFrame = new QuicAckFrame(largestAck, ackDelay, ackRangeCount, firstAckRange); // generating ACK frame
            return quicAckFrame;
        } catch (Exception e) {
            throw new QuicException(10, 2, "Ack frame decoding error");
        }
    }

    /**
     * Method for decoding Quic Stream Frame
     *
     * @param arr        input array containing Stream frame
     * @param headerByte header of the frame
     * @return Quic stream frame
     * @throws QuicException
     */
    public static QuicFrame quicStreamFrameDecoder(byte[] arr, byte headerByte) throws QuicException {
        boolean offbit = false;
        boolean lenBit = false;
        boolean finBit = false;

        // calcualting offset-bit, length-bit and fin-bit from header byte
        if ((headerByte & 4) > 0) {         // 0x4 positon of header byte indicates offset bit
            offbit = true;                 //offset bit is set when the offset is more than 0
        }
        if ((headerByte & 2) > 0) {        // 0x2 positon of header byte indicates the length bit
            lenBit = true;              // length bit indicates the length field is present in frame. if it is not set then the
        }                               // length is not set in frame, that means the remaining part is all payload.

        if ((headerByte & 1) > 0) {        // 0x1 position of header byte indicates the FIN bit. that means it is the last frame of data
            finBit = true;
        }
        int p = payloadPostionIndicator + 1;
        //---------------Stream ID ---------------//
        int streamIdLen = Util.variableLengthIntegerLength(arr[p]);  // calculating the length of variable length integer
        byte[] streamId_arr = new byte[streamIdLen];
        for (int n = p; n < p + streamIdLen; n++) {
            streamId_arr[n - p] = arr[n];
        }
        long streamId = Util.variableLengthInteger(streamId_arr, 1);     // calcualting Stream id by variable length integer parsing
        p = p + streamIdLen;
        //-----------------Offset----------------//
        long offset = 0;
        if (offbit) {                // offset is only present in payload when the offset bit of header is set
            int offsetLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] offset_arr = new byte[offsetLen];
            for (int n = p; n < p + offsetLen; n++) {
                offset_arr[n - p] = arr[n];
            }
            offset = Util.variableLengthInteger(offset_arr, 1);          // calcualting the offset by variable length integer parsing
            p = p + offsetLen;
        }
        //------------- length --------------//
        long streamDataLength = 0;
        if (lenBit) {           // length field in only present in Stream frame when len bit of header is set
            int streamDataLengthLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] streamDataLength_arr = new byte[streamDataLengthLen];
            for (int n = p; n < p + streamDataLengthLen; n++) {
                streamDataLength_arr[n - p] = arr[n];
            }
            streamDataLength = Util.variableLengthInteger(streamDataLength_arr, 1);   // calculating length by variable length integer parsing
            p = p + streamDataLengthLen;
        }
        byte[] streamData = new byte[(int) streamDataLength];
        for (int n = p; n < p + streamDataLength; n++) {
            streamData[n - p] = arr[n];            // generating the data of stream frame
        }
        p = p + (int) streamDataLength;
        payloadPostionIndicator = p;
        return new QuicStreamFrame(streamId, offset, finBit, streamData);
    }


    /**
     * Method for decoding the Quic Connection close frame
     *
     * @param arr input byte array containg connection close frame
     * @return quic connection close frame
     * @throws QuicException
     */
    public static QuicFrame quicConnectionCloseFrameDecoder(byte[] arr) throws QuicException {
        try {
            int pointer = payloadPostionIndicator + 1;
            //----------- Error Code--------//
            int errorCodeLen = Util.variableLengthIntegerLength(arr[pointer]);
            byte[] errorCode_arr = new byte[errorCodeLen];
            for (int n = pointer; n < pointer + errorCodeLen; n++) {
                errorCode_arr[n - pointer] = arr[n];
            }
            long errorCode = Util.variableLengthInteger(errorCode_arr, 1);  // generating Error code by variable length integer parsing
            pointer = pointer + errorCodeLen;
            //-------------------- frame type ------------//
            int frameTypeLen = Util.variableLengthIntegerLength(arr[pointer]);
            byte[] frameType_arr = new byte[frameTypeLen];
            for (int n = pointer; n < pointer + frameTypeLen; n++) {
                frameType_arr[n - pointer] = arr[n];
            }
            long frameType = Util.variableLengthInteger(frameType_arr, 1); // generating frame type using variable length integer parse
            pointer = pointer + frameTypeLen;

            //------------------- Reason Length----------//
            int tempReasonLength = Util.variableLengthIntegerLength(arr[pointer]);
            byte[] reasonLen_arr = new byte[tempReasonLength];
            for (int n = pointer; n < pointer + tempReasonLength; n++) {
                reasonLen_arr[n - pointer] = arr[n];
            }
            long reasonLength = Util.variableLengthInteger(reasonLen_arr, 1);   // generating reasong length using variable length integer parsing
            pointer = pointer + tempReasonLength;
            // ---------Reason Phrase ---------//
            byte[] reasonPhrase = new byte[0];
            if (reasonLength > 0) {
                reasonPhrase = new byte[(int) reasonLength];
                for (int i = pointer; i < pointer + reasonLength; i++) {
                    reasonPhrase[i - pointer] = arr[i];
                }
            }
            pointer += reasonLength;
            String reasonP = null;
            reasonP = new String(reasonPhrase, "UTF-8");    // generating Reason Phrase String
            payloadPostionIndicator = pointer;

            return new QuicConnectionCloseFrame(errorCode, frameType, reasonP);
        } catch (Exception e) {
            throw new QuicException(10, 28, "Close connection decode error");
        }

    }


}
