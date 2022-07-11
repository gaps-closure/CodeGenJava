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

public class GapsTag
{
    public static final int SIZE = 12;    // size sum of mux, sec and typ;
    public static final int INT_SIZE = 4;
    
    private int mux;
    private int sec;
    private int typ;
    
    public GapsTag() {
    }
    
    public GapsTag(int mux, int sec, int typ) {
        this.mux = mux;
        this.sec = sec;
        this.typ = typ;
    }
    
    public String toString() {
    	return "mux/sec/type: " + mux + "/" + sec + "/" + typ;
    }
    
    public void tag_encode(GapsTag tag_in) {
        this.mux = HalZmq.htonl(tag_in.getMux());
        this.sec = HalZmq.htonl(tag_in.getSec());
        this.typ = HalZmq.htonl(tag_in.getTyp());
    }
    
    public void tag_decode(GapsTag tag_in) {
        this.mux = HalZmq.ntohl(tag_in.getMux());
        this.sec = HalZmq.ntohl(tag_in.getSec());
        this.typ = HalZmq.ntohl(tag_in.getTyp());
    }
    
    public void encode(byte[] packet, Offset offset) throws FailToEncodeException {
        Coder.putInt(mux, packet, offset, INT_SIZE * 8);
        Coder.putInt(sec, packet, offset, INT_SIZE * 8);
        Coder.putInt(typ, packet, offset, INT_SIZE * 8);
    }
    
    public void decode(byte[] packet, Offset offset) throws FailToDecodeException {
        this.mux = Coder.getInt(packet, offset, INT_SIZE * 8);
        this.sec = Coder.getInt(packet, offset, INT_SIZE * 8);
        this.typ = Coder.getInt(packet, offset, INT_SIZE * 8);
    }
    
    public int getMux() {
        return mux;
    }

    public void setMux(int mux) {
        this.mux = mux;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
    }
}
