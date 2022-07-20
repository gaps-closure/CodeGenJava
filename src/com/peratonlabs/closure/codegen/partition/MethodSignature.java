/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.partition;

import java.util.ArrayList;

public class MethodSignature
{
    private String fqcn;
    private String name;
    private String returnType;
    private ArrayList<String> parameterTypes;
    
    public String toString() {
        // keep in sync with getUid() and getUidResponse() of RPCObjects
        // TODO: may want to instantitate RPCObjects and use their getUid()
        
        String argTypes = "";
        for (int i = 0; i < parameterTypes.size(); i++)
            argTypes += "." + parameterTypes.get(i);
        
        return fqcn + "." + name + argTypes;
    }
    
    public String getFqcn() {
        return fqcn;
    }
    
    public void setFqcn(String fqcn) {
        this.fqcn = fqcn;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getReturnType() {
        return returnType;
    }
    
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    
    public ArrayList<String> getParameterTypes() {
        return parameterTypes;
    }
    
    public void setParameterTypes(ArrayList<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
