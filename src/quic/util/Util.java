package quic.util;

import quic.exception.QuicException;
import quic.frame.*;
import quic.packet.*;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.Set;


public class Util {

    public static long largestPacketNumber =0;

    public static int variableLengthIntegerLength(byte b){
        int temp = (int)b;
        int lenArry[] = new int[2];
        for(int c=7;c>=6;c--){
            int x = (int) Math.pow(2,c);
            if((x&temp)==0){
                lenArry[7-c]=0;
            }
            else{
                lenArry[7-c]=1;
            }
        }
        if(lenArry[0]==0 && lenArry[1]==0){
            return  1;
        }
        else if(lenArry[0]==0 && lenArry[1]==1){
            return 2;
        }
        else if(lenArry[0]==1 && lenArry[1]==0){
            return 4;
        }
        else if(lenArry[0]==1 && lenArry[1]==1){
            return 8;
        }

        return 0;
    }

    public static long variableLengthInteger(byte[] input,int type){
        if(type==0) {
            String s = bytesArrayToHex(input);
            Long result = Long.parseLong(s, 16);
            return result;
        }
        else if(type == 1){
            String s =Util.byteToHex((byte)(input[0]&63));
            byte [] temp = new byte[input.length-1];
            for(int i=1;i<input.length;i++){
                temp[i-1]=input[i];
            }
            s+=bytesArrayToHex(temp);
            //System.out.println("s = "+s);
            Long result = Long.parseLong(s, 16);
            return result;
        }
        return 0;
    }

    public static byte[] generateVariableLengthInteger(Long input){
        if(input<Math.pow(2,6)){              // adding 00 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input),1);
            temp[0]+=0;
            return temp;
        }
        else if(input<Math.pow(2,14)){        // adding 01 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input),2);
            temp[0]+=64;
            return temp;
        }
        else if(input<Math.pow(2,30)){        // adding 10 before the length
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input),4);
            temp[0]+=128;
            return temp;
        }
        else if(input<(long)Math.pow(2,62)){     //adding 11 before the integer
            byte[] temp = Util.hexStringToByteArray(Long.toHexString(input),8);
            temp[0]+=192;
            return temp;
        }
        return null;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesArrayToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static String byteToHex(byte b){
        int v = b & 0xFF;
        String hex_string = HEX_ARRAY[v >>> 4]+"";
        hex_string+= HEX_ARRAY[v & 0x0F];
        return hex_string;
    }

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



    public static boolean allZero(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static String printConnectionId(byte[] connectionId) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < connectionId.length; i++) {
            // Only print the last two digits, but add a 0 if we need one
            String b = "0" + Integer.toHexString(connectionId[i]);
            builder.append(b.substring(b.lastIndexOf("") - 2));
        }
        return builder.toString();
    }

    public static QuicPacket quicIntialPacketDecoder(int type, byte[] arr, int headerByte, int headerArry[]) throws QuicException {
        try {
            int p = 1;
            byte[] version_arr = new byte[4];
            int n = p;
            for (; n < p + 4; n++) {
                version_arr[n - p] = arr[n];
            }
            long version = Util.variableLengthInteger(version_arr, 0);
            System.out.println("version = " + version);
            p = n;
            ////////////////////////////
            int dcIdLenD = (int) arr[p];
            System.out.println("dcidlen = " + dcIdLenD);
            p++;
            byte[] dcIdD = new byte[dcIdLenD];
            int i = p;
            for (; i < p + dcIdLenD; i++) {
                dcIdD[i - p] = arr[i];
            }
            System.out.print("dcId = ");
            for (byte x : dcIdD) {
                System.out.print(Util.byteToHex(x) + " ");
            }
            System.out.println();
            p = i;
            ///////////////////////////
            int scIdLenD = (int) arr[p];
            System.out.println("scidlen = " + scIdLenD);
            p++;
            byte[] scIdD = new byte[scIdLenD];
            int j = p;
            for (; j < p + scIdLenD; j++) {
                scIdD[j - p] = arr[j];
            }
            System.out.print("scId = ");
            for (byte x : scIdD) {
                System.out.print(Util.byteToHex(x) + " ");
            }
            System.out.println();
            p = j;
            ////////////////////////////
            int tokenLengthLen = Util.variableLengthIntegerLength(arr[p]);
            byte[] tokenLength_arr = new byte[tokenLengthLen];
            for (int c = p; c < p + tokenLengthLen; c++) {
                tokenLength_arr[c - p] = arr[c];
            }
            p += tokenLengthLen;
            long tokenLength = Util.variableLengthInteger(tokenLength_arr, 1);
            System.out.println("Token length = " + tokenLength);

            byte[] token = new byte[(int) tokenLength];
            for (int c = p; c < p + tokenLength; c++) {
                token[c - p] = arr[c];
            }
            p += tokenLength;
            ////////////////////////////
            int lengthSize = Util.variableLengthIntegerLength(arr[p]);
            byte[] len_arr = new byte[lengthSize];
            for (int c = p; c < lengthSize + p; c++) {
                len_arr[c - p] = arr[c];
            }
            p += lengthSize;
            long length = Util.variableLengthInteger(len_arr, 1);
            System.out.println("length = " + length);
            ///////////////////////////////////////
            int packetNoLen = (headerByte & 3) + 1;

            byte[] packNum_arr = new byte[packetNoLen];
            for (int c = p; c < packetNoLen + p; c++) {
                packNum_arr[c - p] = arr[c];
            }
            p += packetNoLen;
            long packetNum = Util.variableLengthInteger(packNum_arr, 0);
            System.out.println("packetNumber = " + packetNum);
            /////////
            byte[] payload = new byte[(int) (length - packetNoLen)];
            int k = p;

            for (; k < p + (length - (packetNoLen)); k++) {
                payload[k - p] = arr[k];
                System.out.print(k + " ");
            }
            p = k;
            if (type == 0) {
                QuicPacket initialPacket = new QuicInitialPacket(dcIdD, packetNum, version, scIdD, frameDecode(payload));
                return initialPacket;
            }
        }catch (Exception e){
            throw new QuicException(10,0,"initial packet decode error");
        }

        return null;
    }

    public static Set<QuicFrame> frameDecode(byte[] payload) throws QuicException {
        Set<QuicFrame> temp = new HashSet<>();
        QuicFrame.setPayloadPostionIndicator(0);
        while(QuicFrame.getPayloadPostionIndicator()<payload.length){
            System.out.println("paylodad indicatior = "+QuicFrame.getPayloadPostionIndicator()+" Payload length = "+payload.length);
            temp.add(QuicFrame.decode(payload));
        }
        return temp;
    }

    public static QuicPacket quicLongHeaderPacketDecoder(int type, byte[] arr, int headerByte, int headerArry[]) throws QuicException {
        try {
            int p = 1;
            byte[] version_arr = new byte[4];
            int n = p;
            for (; n < p + 4; n++) {
                version_arr[n - p] = arr[n];
            }
            long version = Util.variableLengthInteger(version_arr, 0);
            System.out.println("version = " + (version-4278190080l));
            p = n;
            ////////////////////////////
            int dcIdLenD = (int) arr[p];
            System.out.println("dcidlen = " + dcIdLenD);
            p++;
            byte[] dcIdD = new byte[dcIdLenD];
            int i = p;
            for (; i < p + dcIdLenD; i++) {
                dcIdD[i - p] = arr[i];
            }
            System.out.print("dcId = ");
            for (byte x : dcIdD) {
                System.out.print(Util.byteToHex(x) + " ");
            }
            System.out.println();
            p = i;
            ///////////////////////////
            int scIdLenD = (int) arr[p];
            System.out.println("scidlen = " + scIdLenD);
            p++;
            byte[] scIdD = new byte[scIdLenD];
            int j = p;
            for (; j < p + scIdLenD; j++) {
                scIdD[j - p] = arr[j];
            }
            System.out.print("scId = ");
            for (byte x : scIdD) {
                System.out.print(Util.byteToHex(x) + " ");
            }
            System.out.println();
            p = j;
            ////////////////////////////
            int lengthSize = Util.variableLengthIntegerLength(arr[p]);
            byte[] len_arr = new byte[lengthSize];
            for (int c = p; c < lengthSize + p; c++) {
                len_arr[c - p] = arr[c];
            }
            p += lengthSize;
            long length = Util.variableLengthInteger(len_arr, 1);
            System.out.println("length = " + length);
            ///////////////////////////////////////
            int packetNoLen = (headerByte & 3) + 1;

            byte[] packNum_arr = new byte[packetNoLen];
            for (int c = p; c < packetNoLen + p; c++) {
                packNum_arr[c - p] = arr[c];
            }
            p += packetNoLen;
            long packetNum = Util.variableLengthInteger(packNum_arr, 0);
            System.out.println("packetNumber = " + packetNum);
            /////////
            System.out.println("***** Payload start *****");
            byte[] payload = new byte[(int) (length - packetNoLen)];
            int k = p;

            for (; k < p + (length - (packetNoLen)); k++) {
                payload[k - p] = arr[k];
            }
            p = k;


            if (type == 1) {
                QuicPacket zeroRttPacket = new QuicZeroRTTPacket(dcIdD, packetNum, version, scIdD, frameDecode(payload));
                System.out.println("***** Payload finsihed *****");
                return zeroRttPacket;
            } else if (type == 2) {
                QuicPacket handshakePacket = new QuicHandshakePacket(dcIdD, packetNum, version, scIdD, frameDecode(payload));
                System.out.println("***** Payload finsihed *****");
                return handshakePacket;
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new QuicException(100,0,"longheader decoder error");
        }

        return null;
    }


    public static QuicPacket quicShortHeaderDecoder(byte[] arr, int dcIdSize) throws QuicException {
        try {
            int p = 0;
            int headerArry[] = new int[8];
            int headerByte = (int) arr[0];;
            for (int c = 7; c >= 0; c--) {
                int x = (int) Math.pow(2, c);
                if ((x & headerByte) == 0) {
                    headerArry[7 - c] = 0;
                } else {
                    headerArry[7 - c] = 1;
                }
            }
            /////////////////////////
            p++;
            int dcIdLenD = dcIdSize;
            System.out.println("dcidlen = " + dcIdLenD);
            byte[] dcIdD = new byte[dcIdLenD];
            int i = p;
            for (; i < p + dcIdLenD; i++) {
                dcIdD[i - p] = arr[i];
            }
            System.out.print("dcId = ");
            for (byte x : dcIdD) {
                System.out.print(Util.byteToHex(x) + " ");
            }
            System.out.println();
            p = i;
            //////////////////////////////
            int packetNoLen = (headerByte & 3) + 1;

            byte[] packNum_arr = new byte[packetNoLen];
            for (int c = p; c < packetNoLen + p; c++) {
                packNum_arr[c - p] = arr[c];
            }
            p += packetNoLen;
            long packetNum = Util.variableLengthInteger(packNum_arr, 0);
            System.out.println("packetNumber = " + packetNum);
            //////////////////////////////////
            byte[] payload = new byte[(int) (arr.length - p)];
            int k = p;

            for (; k < arr.length; k++) {
                payload[k - p] = arr[k];
                System.out.print(k + " ");
            }
            p = k;
            QuicPacket shortHeaderPacket = new QuicShortHeaderPacket(dcIdD, packetNum, frameDecode(payload));
            return shortHeaderPacket;

        }catch (Exception e){
            throw new QuicException(10,0,"Short Header packet decoder error");
        }
    }
    //////////////////////////

    public static DecodedFrame quicAckFrameDecoder(byte[] arr, int index){

        System.out.println("Frame Type  = ACK Frame");
        System.out.println("header byte = "+(int)arr[index]);
        int p=index+1;
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

        QuicAckFrame quicAckFrame = new QuicAckFrame(largestAck,ackDelay,ackRangeCount,firstAckRange);
        for(QuicAckRange x : tempAckRanges){
            quicAckFrame.addAckRange(x);
        }
        return new DecodedFrame(quicAckFrame,p);


    }

    public static DecodedFrame quicStreamFrameDecoder(byte[] arr, byte headerByte,int index){
        System.out.println("Frame Type  = Stream Frame");
        System.out.println("header byte = "+(int)arr[index]);
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
        int p = index+1;
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

        return new DecodedFrame(new QuicStreamFrame(streamId,offset,finBit,streamData),p);
    }

    public static DecodedFrame quicCryptoFrameDecoder(byte[] arr,int index){
        System.out.println("Frame Type  = Crypto Frame");
        System.out.println("header byte = "+(int)arr[index]);
        int p = index+1;
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
        System.out.println("crypto Data Length = "+cryptoDataLength);
        ///////////////////////////////////
        byte[] cryptoData = new byte[(int) cryptoDataLength];
        for(int i=p;i<p+cryptoDataLength;i++){
            cryptoData[i-p]=arr[i];
            //System.out.println("cryptoData "+cryptoData[]);
        }
        p=p+(int)cryptoDataLength;


        return new DecodedFrame(new QuicCryptoFrame(offset,cryptoData),p);

    }

    public static DecodedFrame quicConnectionCloseFrameDecoder(byte[] arr, int index) throws QuicException {
        System.out.println("Frame Type  = Connection Close Frame");
        System.out.println("header byte = "+(int)arr[index]);
        /////////////
        int p = index+1;
        int errorCodeLen =  Util.variableLengthIntegerLength(arr[p]);
        byte[] errorCode_arr = new byte[errorCodeLen];
        for (int n=p;n<p+errorCodeLen;n++){
            errorCode_arr[n-p]=arr[n];
        }
        long errorCode = Util.variableLengthInteger(errorCode_arr,1);
        System.out.println("Error Code = "+ errorCode);
        p=p+errorCodeLen;
        ///////////////////////////////////////////////
        int frameTypeLen =  Util.variableLengthIntegerLength(arr[p]);
        byte[] frameType_arr = new byte[frameTypeLen];
        for (int n=p;n<p+frameTypeLen;n++){
            frameType_arr[n-p]=arr[n];
        }
        long frameType = Util.variableLengthInteger(frameType_arr,1);
        System.out.println("Frame Type = "+ frameType);
        p=p+frameTypeLen;

        ///////////////////////////////////////////////
        int tempReasonLength =  Util.variableLengthIntegerLength(arr[p]);
        byte[] reasonLen_arr = new byte[tempReasonLength];
        for (int n=p;n<p+tempReasonLength;n++){
            reasonLen_arr[n-p]=arr[n];
        }
        long reasonLength = Util.variableLengthInteger(frameType_arr,1);
        System.out.println("Reason Phrase length = "+reasonLength);
        p=p+tempReasonLength;
        ////////////////////////////////////////
        byte[] reasonPhrase=new byte[0];
        if(reasonLength>0){
            reasonPhrase= new byte[(int) reasonLength];
            for(int i=p;i<p+reasonLength;i++){
                reasonPhrase[i-p]=arr[i];
            }
        }
        p=p+(int)reasonLength;
        String reasonP = null;
        try{
            reasonP = new String(reasonPhrase,"UTF-8");
            System.out.println("Reason Phrase = "+reasonP);
        }catch (Exception e){
            throw new QuicException(0,0,"Connection Close exception");
        }

        return new DecodedFrame(new QuicConnectionCloseFrame(errorCode,frameType,reasonP),p);

    }




}
