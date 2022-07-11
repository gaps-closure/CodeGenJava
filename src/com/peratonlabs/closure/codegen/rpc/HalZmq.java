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

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.zeromq.ZMQ;

import com.peratonlabs.closure.codegen.rpc.coder.FailToDecodeException;
import com.peratonlabs.closure.codegen.rpc.coder.FailToEncodeException;
import com.peratonlabs.closure.codegen.rpc.coder.Offset;

public class HalZmq
{
    public HashMap<MessageType, Codec> codecMap = new HashMap<MessageType, Codec>();

    private ZMQ.Context ctx;
    private ZMQ.Socket pub_socket;
    private ZMQ.Socket sub_socket;
    
    public HalZmq(String out_uri, String in_uri, GapsTag tag) {
        this.ctx = ZMQ.context(1);
        
        this.pub_socket = ctx.socket(ZMQ.PUB);                                                      
//        pub_socket.bind(out_uri);
        pub_socket.connect(out_uri);
//        System.out.println("pub connected to " + out_uri);
        
        this.sub_socket = ctx.socket(ZMQ.SUB);
        sub_socket.connect(in_uri);
//        System.out.println("sub connected to " + in_uri);

        try {
            GapsTag tag4filter = new GapsTag();
            tag4filter.tag_encode(tag);
            
            Offset offset = Offset.newInstance();
            byte[] topic = new byte[GapsTag.SIZE];
            
            tag4filter.encode(topic, offset);
//            sub_socket.subscribe(topic);
            sub_socket.subscribe("".getBytes());  // no filter for now
        }
        catch (FailToEncodeException e) {
            e.printStackTrace();
        }
        
        try {
            Thread.sleep(1000);  /* zmq socket join delay */
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
    public boolean type_check(int typeCode) {
        MessageType type = MessageType.get(typeCode);
        if (type == null)
            return false;
        
        return (codecMap.get(type) != null);
    }
    
    public void xdc_register(CodecFunc encode, CodecFunc decode, int typeCode) {
        Codec codec = new Codec(encode, decode, true);
        MessageType type = MessageType.get(typeCode);
        if (type == null) {
            System.out.println("invalid message type code: " + typeCode);
            return;
        }
        codecMap.put(type, codec);
    }
    
    public int len_encode(int len) {
        return htonl(len);
    }
    
    public int len_decode(int len) {
        return ntohl(len);
    }
    
    /*
     * return type change to int from void and remove argument sdh_len
     * The caller is to use the return value as the value of sdh_len.
     * len_out is also removed since its value is to come from the call to Codec.encodeBuf
     */
    public int gaps_data_encode(SDH sdh, byte[] buff_in, GapsTag tag) {
        int typ = tag.getTyp();
        type_check(typ);
        
//        MessageType tp = MessageType.get(typ);
//        if (tp == null) {
//            System.err.println("bad message type: " + typ);
//            return -1;
//        }
//        
//        Codec codec = codecMap.get(tp);
//        if (codec == null) {
//            System.err.println("encoder not found for " + tp);
//            return -1;
//        }
//        int len_out = codec.encode(sdh.getData(), buff_in);
        int len_out = buff_in.length;
        System.arraycopy(buff_in, 0, sdh.getData(), 0, len_out);
        sdh.getTag().tag_encode(tag);
        sdh.setDataLen(len_encode(len_out));

        return GapsTag.SIZE + SDH.SIZE_OF_DATA_LEN_INT + len_out;
    }
    
    public int gaps_data_decode(SDH sdh) { // , byte[] buff_out, GapsTag tag) {
//        int typ = tag.getTyp();
//        type_check(typ);
        
//        MessageType tp = MessageType.get(typ);
//        if (tp == null) {
//            System.err.println("bad message type: " + typ);
//            return -1;
//        }
        
        sdh.getTag().tag_decode(sdh.getTag());  // result of host byte order
        int len_out = len_decode(sdh.getDataLen());
        
        sdh.setDataLen(len_out);  // result of host byte order
        
//        Codec codec = codecMap.get(tp);
//        if (codec == null) {
//            System.err.println("decoder not found for " + tp);
//            return -1;
//        }
//        codec.decode(buff_out, sdh.getData());
        
        return len_out;
    }
    
    public void xdc_asyn_send(byte[]adu, GapsTag tag) {                                       
        try {
            SDH sdh = new SDH();                                                                                   
            int packet_len = gaps_data_encode(sdh, adu, tag);

            byte[] pk_bytes = sdh.encode();
            if (pub_socket == null) {
                System.err.println("null pub socket");
                return;
            }
//byte[] cmp = ClosureUtils.compress(pk_bytes);
//float ratio = (100 - cmp.length * 100 / (float) pk_bytes.length);
//System.out.println("compressed len=" + cmp.length + ", a " + String.format("%6.2f", ratio) + "% reduction");
            boolean sent = pub_socket.send(pk_bytes, 0, sdh.getSize(), 0);
            if (!sent) {
                System.err.println("failed to send " + tag);
            }
            else
                System.out.println("sent " + pk_bytes.length + " bytes");
        }
        catch (FailToEncodeException e) {
            e.printStackTrace();
        }
    }
    
    public SDH xdc_blocking_recv() { // byte[] adu, GapsTag tag) {      
        try {
            SDH sdh = new SDH();  
            int len = sdh.getMaxSize();
            byte[] buffer = new byte[len];
            
            if (sub_socket == null) {
                System.err.println("null pub socket");
                return null;
            }
            int bytes_rcvd = sub_socket.recv(buffer, 0, len, 0);
            System.out.println("rcvd " + bytes_rcvd + " bytes");                       
            
            sdh.decode(buffer);
            
//            int packet_len = gaps_data_decode(sdh); //, adu, tag);
            return sdh;
        }
        catch (FailToDecodeException e) {
            e.printStackTrace();
        }
        return null;
    }                                                                                                                      
 
    public void xdc_pub_socket() {                                                                                   
        // handled in the ctor
    }                          
    
    public void xdc_sub_socket(GapsTag tag, ZMQ.Context ctx) {
        // handled in the ctor
    }
    
    public void shutdown() {
        if (pub_socket != null)
            pub_socket.close();
        
        if (sub_socket != null)
            sub_socket.close();
        
        ctx.term();
    }
    
    // assuming host is little endian
    public static int htonl(int x) {
        byte[] res = new byte[4];
        for (int i = 0; i < 4; i++) {
            res[i] = (byte) (x >>> 24);
            x <<= 8;
        }
        
        ByteBuffer wrapped = ByteBuffer.wrap(res);
        return wrapped.getInt();
    }
    
    public static int ntohl(int x) {
        ByteBuffer dbuf = ByteBuffer.allocate(4);
        dbuf.putInt(x);
        byte[] data = dbuf.array();
        
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res <<= 8;
            res |= (int) data[i];
        }
        return res;
    }
}
