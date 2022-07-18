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
import java.util.HashSet;

import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.MethodSignature;
import com.peratonlabs.closure.codegen.partition.Xdcc;

public class XdMap
{
    // enclave -> mux
    private static HashMap<String, Integer> muxMap = new HashMap<String, Integer>();
    
    private static HashMap<String, HashMap<String, Integer>> encPairs = new HashMap<String, HashMap<String, Integer>>();
    private static HashMap<String, HashMap<String, Integer>> levelPairs = new HashMap<String, HashMap<String, Integer>> ();
    private static int encLinks = 1;
    private static int levelLinks = 1;
    
    // enclave -> signature -> typ
    private static HashMap<String, Integer> typMap = new HashMap<String, Integer>();
    
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
    
    public static int getSec(String fromLevel, String toLevel) {
        HashMap<String, Integer> map = levelPairs.get(fromLevel);
        if (map == null)
            return -1;
        return map.get(toLevel);
    }
    
    public static int getType(String enclave, String signature) {
        Integer typ = typMap.get(signature);
        if (typ == null) {
            int size = typMap.size();
            typMap.put(signature, ++size);
            typ = size;
        }
        return typ;
    }

    public static void cartneq(Xdcc xdcc) {
        for (Enclave enc : xdcc.getEnclaves()) {
            String name = enc.getName();
            String level = enc.getLevel();
            
            HashMap<String, Integer> encMap = encPairs.get(name);
            if (encMap == null) {
                encMap = new HashMap<String, Integer>();
                encPairs.put(name, encMap);
            }
            
            HashMap<String, Integer> levelMap = levelPairs.get(level);
            if (levelMap == null) {
                levelMap = new HashMap<String, Integer>();
                levelPairs.put(level, levelMap);
            }
            
            for (Enclave encInner : xdcc.getEnclaves()) {
                String nameInner = encInner.getName();
                String levelInner = encInner.getLevel();
                if (nameInner.equals(name))
                    continue;
                
                Integer mux = encMap.get(nameInner);
                if (mux == null) {
                    mux = encLinks++;
                    encMap.put(nameInner, mux);
                }
                
                Integer sec = levelMap.get(levelInner);
                if (sec == null) {
                    sec = levelLinks++;
                    levelMap.put(levelInner, sec);
                }
            }
        }
        
        for (String key : encPairs.keySet()) {
            HashMap<String, Integer>  encMap = encPairs.get(key);
            for (String k2 : encMap.keySet()) {
                Integer i = encMap.get(k2);
                System.out.println("========== " + key + " " + k2 + " " + i);
            }
        }
        
        for (String key : levelPairs.keySet()) {
            HashMap<String, Integer>  encMap = levelPairs.get(key);
            for (String k2 : encMap.keySet()) {
                Integer i = encMap.get(k2);
                System.out.println("========== " + key + " " + k2 + " " + i);
            }
        }
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
    
    static class Pair {
        String key;
        String value;
        
        Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean equals(Object obj) {
            Pair that = (Pair) obj;
            if (that instanceof Pair) {
                return this.key.equals(that.key) &&
                        this.value.equals(that.value);
            }
            else
                return false;
        }
    }    
}
