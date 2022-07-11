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

public class RPCConstructor extends RPCObject 
{
    private static final long serialVersionUID = 1L;
    
    protected Class<?>[] argTypes;
    protected Object[] args;
    
    public RPCConstructor() {}
    
    public RPCConstructor(int oid, String fqcn, Class<?>[] argTypes, Object[] args, int flags) {
        super(oid, fqcn, flags);
        
        this.argTypes = argTypes;
        this.args = args;
    }
    
    public String getUid() {
    	int lastDot = fqcn.lastIndexOf('.');
    	String clazz = "";
    	if (lastDot < 0) {
    		System.err.println("ERR: dot not found: " + fqcn);
    	}
    	else {
    		clazz = fqcn.substring(lastDot + 1);
    	}
    	
    	return fqcn + "." + clazz + argTypesToString(argTypes);
    }
    
    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
