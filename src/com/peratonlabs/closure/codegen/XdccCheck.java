/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jul 12, 2021
 */
package com.peratonlabs.closure.codegen;

import java.util.ArrayList;
import com.peratonlabs.closure.codegen.partition.Call;
import com.peratonlabs.closure.codegen.partition.Cut;
import com.peratonlabs.closure.codegen.partition.MethodSignature;
import com.peratonlabs.closure.codegen.partition.Xdcc;

public class XdccCheck
{
    private static Xdcc xdcc = null;
    
    public static boolean allowedMethod(String callerEnclave, String fqcn, String methodName, Class<?>[] argTypes) {
        if (xdcc == null) {
            xdcc = Xdcc.load("/home/tchen/tmp/cut.json");
        }

        boolean allowed = false;
        for (Cut cut: xdcc.getCuts()) {
            boolean found = false;
            for (Call call : cut.getAllowedCallers()) {
                if (callerEnclave.equals(call.getLevel())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                continue;
            
            MethodSignature signature = cut.getMethodSignature();
            if (!fqcn.equals(signature.getFqcn()))
                continue;
            
            if (!methodName.equals(signature.getName()))
                continue;
            
            ArrayList<String> formals = signature.getParameterTypes();
            if (formals == null || argTypes.length != formals.size())
                continue;
            
            boolean matched = true;
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> clazz = argTypes[i];
                if (!formals.get(i).equals(clazz.getName())) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                allowed = true;
                break;
            }
        }
        
        return allowed;
    }
    
    public static void main(String[] args) {
        boolean allowed = allowedMethod("Orange", "com.peratonlabs.closure.testprog.example1.Extra",
                "getValue", new Class<?>[0]);
        System.out.println("########### "+ allowed);
    }
}
