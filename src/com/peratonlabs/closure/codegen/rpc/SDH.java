/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 25, 2021
 */
package com.peratonlabs.closure.codegen.rpc;

import com.peratonlabs.closure.codegen.rpc.coder.Coder;
import com.peratonlabs.closure.codegen.rpc.coder.FailToDecodeException;
import com.peratonlabs.closure.codegen.rpc.coder.FailToEncodeException;
import com.peratonlabs.closure.codegen.rpc.coder.Offset;

public class SDH
{
    public static final int SIZE_OF_DATA_LEN_INT = 4;  // sizeof(dataLen)
    
    public static final int ADU_SIZE_MAX_C = 2000;
    public static final int MAX_DATA_TYP_MAX = 200;
    public static final int RX_FILTER_LEN = 12;
    
    GapsTag tag = new GapsTag();
    int dataLen = -1;
    byte[] data = new byte[ADU_SIZE_MAX_C];
    
    public byte[] encode() throws FailToEncodeException {
        Offset offset = Offset.newInstance();

        int bytes = getSize();
        byte[] packet = new byte[bytes];

        tag.encode(packet, offset);
        Coder.putInt(dataLen, packet,  offset, 32);
        Coder.putInts(data,  packet, offset, dataLen * 8);

        return packet;
    }
    
    public void decode(byte[] packet) throws FailToDecodeException {
        Offset offset = Offset.newInstance();
        
        tag.decode(packet, offset);
        this.dataLen = Coder.getInt(packet, offset, SIZE_OF_DATA_LEN_INT * 8);
        this.data = Coder.getInts(packet, offset, dataLen * 8);
    }
    
    public int getSize() {
        return GapsTag.SIZE + SDH.SIZE_OF_DATA_LEN_INT + dataLen;
    }
    
    public int getMaxSize() {
        return GapsTag.SIZE + SDH.SIZE_OF_DATA_LEN_INT + ADU_SIZE_MAX_C;
    }
    
    public GapsTag getTag() {
        return tag;
    }
    
    public void setTag(GapsTag tag) {
        this.tag = tag;
    }
    
    public int getDataLen() {
        return dataLen;
    }
    
    public void setDataLen(int dataLen) {
        this.dataLen = dataLen;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public static int getAduSizeMaxC() {
        return ADU_SIZE_MAX_C;
    }
    
    public static int getMaxDataTypMax() {
        return MAX_DATA_TYP_MAX;
    }
    
    public static int getRxFilterLen() {
        return RX_FILTER_LEN;
    }
}
