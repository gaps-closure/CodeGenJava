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

public class Cut
{
    private MethodSignature methodSignature;
    private ArrayList<Call> allowedCallers;
    private Call callee;
    
    public MethodSignature getMethodSignature() {
        return methodSignature;
    }
    
    public void setMethodSignature(MethodSignature methodSignature) {
        this.methodSignature = methodSignature;
    }
    
    public ArrayList<Call> getAllowedCallers() {
        return allowedCallers;
    }
    
    public void setAllowedCallers(ArrayList<Call> allowedCallers) {
        this.allowedCallers = allowedCallers;
    }
    
    public Call getCallee() {
        return callee;
    }
    
    public void setCallee(Call callee) {
        this.callee = callee;
    }
}
