package quic.frame;


import quic.exception.QuicException;
import quic.util.DecodedFrame;
import quic.util.Util;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Represents a QUIC frame.
 * @version 1.1
 */

public abstract class QuicFrame {

    static int payloadPostionIndicator;

    static QuicFrame intFrame;

    public QuicFrame() {

    }


    /**
     * Encodes the frame into bytes to be sent over the network
     *
     * @return the encoded frame
     *
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

        System.out.println("---------------------frame-----------------");

        byte headerByte = arr[payloadPostionIndicator];
        System.out.println("header byte = "+headerByte );
        if(headerByte == 2 || headerByte == 3){       //ACK frame
            return quicAckFrameDecoder(arr);
        }
        else if(headerByte == 6){                   // crypto frame
            return quicCryptoFrameDecoder(arr);
        }
        else if(headerByte>=8 && headerByte<=15){          //Stream frame
            return quicStreamFrameDecoder(arr,headerByte);
        }
        else if(headerByte == 28 || headerByte == 29){
            return quicConnectionCloseFrameDecoder(arr);
        }
        else{
            throw new QuicException(7,0,"Unknown headerByte of frame");
        }
    }
    public static QuicFrame quicAckFrameDecoder(byte[] arr){
        int p=payloadPostionIndicator+1;
        int largestAckLen = Util.variableLengthIntegerLength(arr[p]);
        byte[] largestAck_arr = new byte[largestAckLen];
        for(int n = p;n<p+largestAckLen;n++){
            largestAck_arr[n-p] = arr[n];
        }
        long largestAck = Util.variableLengthInteger(largestAck_arr,1);
        p=p+largestAckLen;
        System.out.println("largest Ack = "+largestAck);
        ///////////////////////////////////////////////////////

        int ackDelayLen = Util.variableLengthIntegerLength(arr[p]);
        byte[] ackDelay_arr = new byte[ackDelayLen];
        for(int n = p;n<p+ackDelayLen;n++){
            ackDelay_arr[n-p] = arr[n];
        }
        long ackDelay = Util.variableLengthInteger(ackDelay_arr,1);
        p=p+ackDelayLen;
        System.out.println("Ack delay = "+ackDelay);
        ///////////////////////////////////////
        int ackRangeCountLen = Util.variableLengthIntegerLength(arr[p]);
        byte[]  ackRangeCount_arr = new byte[ackRangeCountLen];
        for(int n= p; n<p+ackRangeCountLen;n++){
            ackRangeCount_arr[n-p] = arr[n];
        }
        long ackRangeCount = Util.variableLengthInteger(ackRangeCount_arr, 1);
        p=p+ackRangeCountLen;
        System.out.println("Ack Range Count  = "+ackRangeCount);
        //////////////////////////////////////////////////////////
        int firstAckRangeLen = Util.variableLengthIntegerLength(arr[p]);
        byte[] firstAckRange_arr = new byte[firstAckRangeLen];
        for(int n= p;n<p+firstAckRangeLen;n++){
            firstAckRange_arr[n-p]= arr[n];
        }
        long firstAckRange = Util.variableLengthInteger(firstAckRange_arr,1);
        p=p+firstAckRangeLen;
        System.out.println("first ACK range = "+firstAckRange);
        ////////////////////////////////////////////////////
        ArrayList<QuicAckRange> tempAckRanges = new ArrayList<>();
        for(long i=0;i<ackRangeCount;i++){
            int gapLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] gap_arr = new byte[gapLen];
            for (int n = p;n<p+gapLen;n++){
                gap_arr[n-p]=arr[n];
            }
            long gap = Util.variableLengthInteger(gap_arr,1);
            p=p+gapLen;

            int ackRangeLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] ackRange_arr = new byte[ackRangeLen];
            for (int n = p;n<p+ackRangeLen;n++){
                ackRange_arr[n-p]=arr[n];
            }
            long ackRange = Util.variableLengthInteger(ackRange_arr,1);
            p=p+ackRangeLen;

            tempAckRanges.add(new QuicAckRange(gap,ackRange));
        }

        payloadPostionIndicator=p;
        QuicAckFrame quicAckFrame = new QuicAckFrame(largestAck,ackDelay,ackRangeCount,firstAckRange);
        for(QuicAckRange x : tempAckRanges){
            quicAckFrame.addAckRange(x);
        }

        return quicAckFrame;


    }

    public static QuicFrame quicStreamFrameDecoder(byte[] arr, byte headerByte){
        boolean offbit=false;
        boolean lenBit = false;
        boolean finBit = false;
        if((headerByte & 4)>0){
            offbit = true;
        }
        if((headerByte & 2)>0){
            lenBit = true;
        }
        if((headerByte & 1)>0){
            finBit = true;
        }
        int p = payloadPostionIndicator+1;
        int streamIdLen = Util.variableLengthIntegerLength(arr[p]);
        byte[] streamId_arr = new byte[streamIdLen];
        for(int n = p;n<p+streamIdLen;n++){
            streamId_arr[n-p] = arr[n];
        }
        long streamId = Util.variableLengthInteger(streamId_arr,1);
        p=p+streamIdLen;
        ///////////////////////////////////////
        long offset=0;
        if(offbit){
            int offsetLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] offset_arr = new byte[offsetLen];
            for(int n = p;n<p+offsetLen;n++){
                offset_arr[n-p] = arr[n];
            }
            offset = Util.variableLengthInteger(offset_arr,1);
            p=p+offsetLen;
        }
        //////////////////////////////////////
        long streamDataLength = 0;
        if(lenBit){
            int streamDataLengthLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] streamDataLength_arr = new byte[streamDataLengthLen];
            for(int n = p;n<p+streamDataLengthLen;n++){
                streamDataLength_arr[n-p] = arr[n];
            }
            streamDataLength = Util.variableLengthInteger(streamDataLength_arr,1);
            p=p+streamDataLengthLen;
        }else{
            streamDataLength = arr.length - p;
        }
        byte[] streamData = new byte[(int)streamDataLength];
        for(int n=p;n<p+streamDataLength;n++){
            streamData[n-p]=arr[n];
        }
        p=p+(int)streamDataLength;
        payloadPostionIndicator=p;
        return new QuicStreamFrame(streamId,offset,finBit,streamData);
    }

    public static QuicFrame quicCryptoFrameDecoder(byte[] arr){
        System.out.println("-------------Crypto frame--------------");
        int p = payloadPostionIndicator+1;
        int offsetLen =  Util.variableLengthIntegerLength(arr[p]);
        byte[] offset_arr = new byte[offsetLen];
        for (int n=p;n<p+offsetLen;n++){
            offset_arr[n-p]=arr[n];
        }
        long offset = Util.variableLengthInteger(offset_arr,1);
        p=p+offsetLen;
        System.out.println("offset "+offset);
        //////////////////////////////////////////////
        int tempCryptoDataLen = Util.variableLengthIntegerLength(arr[p]);
        byte[] length_arr = new byte[tempCryptoDataLen];
        for(int n=p;n<p+tempCryptoDataLen;n++){
            length_arr[n-p]=arr[n];
        }
        long cryptoDataLength = Util.variableLengthInteger(length_arr,1);
        p=p+tempCryptoDataLen;
        System.out.println("cryptoDtaLength "+cryptoDataLength);
        ///////////////////////////////////
        byte[] cryptoData = new byte[(int) cryptoDataLength];
        for(int i=p;i<p+cryptoDataLength;i++){
            cryptoData[i-p]=arr[i];
            //System.out.println("cryptoData "+cryptoData[]);
        }
        p+=cryptoDataLength;
        payloadPostionIndicator=p;
        return new QuicCryptoFrame(offset,cryptoData);

    }

    public static QuicFrame quicConnectionCloseFrameDecoder(byte[] arr){
        System.out.println("-------------Close connection frame--------------");
        int p = payloadPostionIndicator+1;
        int errorCodeLen =  Util.variableLengthIntegerLength(arr[p]);
        byte[] errorCode_arr = new byte[errorCodeLen];
        for (int n=p;n<p+errorCodeLen;n++){
            errorCode_arr[n-p]=arr[n];
        }
        long errorCode = Util.variableLengthInteger(errorCode_arr,1);
        System.out.println("Error code = "+errorCode);
        p=p+errorCodeLen;
        ///////////////////////////////////////////////
        int frameTypeLen =  Util.variableLengthIntegerLength(arr[p]);
        byte[] frameType_arr = new byte[frameTypeLen];
        for (int n=p;n<p+frameTypeLen;n++){
            frameType_arr[n-p]=arr[n];
        }
        long frameType = Util.variableLengthInteger(frameType_arr,1);
        System.out.println("frame type = "+frameType);
        p=p+frameTypeLen;

        ///////////////////////////////////////////////
        int tempReasonLength =  Util.variableLengthIntegerLength(arr[p]);
        byte[] reasonLen_arr = new byte[tempReasonLength];
        for (int n=p;n<p+tempReasonLength;n++){
            reasonLen_arr[n-p]=arr[n];
        }
        long reasonLength = Util.variableLengthInteger(reasonLen_arr,1);
        System.out.println("Reason length = "+reasonLength);
        p=p+tempReasonLength;
        ////////////////////////////////////////
        byte[] reasonPhrase=new byte[0];
        if(reasonLength>0){
            reasonPhrase= new byte[(int) reasonLength];
            for(int i=p;i<p+reasonLength;i++){
                reasonPhrase[i-p]=arr[i];
            }
        }
        p+=reasonLength;
        String reasonP = null;
        try{
            reasonP = new String(reasonPhrase,"UTF-8");
        }catch (Exception e){

        }
        System.out.println("Reason  = "+reasonP);
        payloadPostionIndicator=p;

        return new QuicConnectionCloseFrame(errorCode,frameType,reasonP);

    }



    //////////////////////////////







}
