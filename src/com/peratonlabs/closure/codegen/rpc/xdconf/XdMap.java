/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.rpc.xdconf;

import java.util.HashMap;

public class XdMap
{
    // enclave -> mux
    private static HashMap<String, Integer> muxMap = new HashMap<String, Integer>();
    
    // enclave -> signature -> typ
    private static HashMap<String, HashMap<String, Integer>> typMap = new HashMap<String, HashMap<String, Integer>>();
    
    private String from;
    private String to;
    private int mux;
    private int sec;
    private int typ;
    private String name;
    
    public XdMap(String to, int mux, int sec, int typ) {
        this.to = to;
        this.mux = mux;
        this.sec = sec;
        this.typ = typ;
    }
    
    public XdMap(XdMap src) {
        this.from = src.from;
        this.to = src.to;
        this.mux = src.mux;
        this.sec = src.sec;
        this.typ = src.typ;
        this.name = src.name;
    }
    
    public static int getMux(String enclave) {
        Integer mux = muxMap.get(enclave);
        if (mux == null) {
            int size = muxMap.size();
            muxMap.put(enclave, ++size);
            mux = size;
        }
        return mux;
    }
    
    public static int getType(String enclave, String signature) {
        HashMap<String, Integer> map = typMap.get(enclave);
        if (map == null) {
            map = new HashMap<String, Integer>();
            typMap.put(enclave, map);
        }
        
        Integer typ = map.get(signature);
        if (typ == null) {
            int size = map.size();
            map.put(signature, ++size);
            typ = size;
        }
        return typ;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
