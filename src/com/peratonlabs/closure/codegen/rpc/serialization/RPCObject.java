/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 26, 2021
 */
package com.peratonlabs.closure.codegen.rpc.serialization;

@SuppressWarnings("serial")
public class RPCObject implements java.io.Serializable
{
    public static final int FLAG_READ = 1;
    public static final int FLAG_WRITE = 2;
    public static final int FLAG_STATIC = 4;
    
    protected int oid;
    protected String fqcn;
    protected int flags;
    
    public RPCObject() {
    }
    
    public RPCObject(int oid, String fqcn, int flags) {
        this.oid = oid;
        this.fqcn = fqcn;
        this.flags = flags;
    }
    
    public String getUid() {
        return fqcn;
    }
    
    public String getUidResponse() {
        return getUid() + "_rsp";
    }
    
    protected String argTypesToString(Class<?>[] argTypes) {
        String s = "";
        for (int i = 0; i < argTypes.length; i++)
            s += "." + argTypes[i].getCanonicalName();
        
        return s;
    }
    
    public boolean isRead() {
        return (flags & FLAG_READ) != 0;
    }
    
    public boolean isWrite() {
        return (flags & FLAG_WRITE) != 0;
    }
    
    public boolean isStatic() {
        return (flags & FLAG_STATIC) != 0;
    }
    
    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getFqcn() {
        return fqcn;
    }

    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }
}
