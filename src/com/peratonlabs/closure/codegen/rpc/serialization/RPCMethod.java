/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 28, 2021
 */
package com.peratonlabs.closure.codegen.rpc.serialization;

@SuppressWarnings("serial")
public class RPCMethod extends RPCObject
{
    protected String methodName;
    protected Class<?>[] argTypes;
    protected Object[] args;
    
    public RPCMethod(int oid, String fqcn, String methodName, Class<?>[] argTypes, Object[] args) {
        super(oid, fqcn, (oid == -1) ? RPCObject.FLAG_STATIC : 0);
        
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.args = args;
    }
    
    public String getUid() {
    	return fqcn + "." + methodName + argTypesToString(argTypes);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
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
