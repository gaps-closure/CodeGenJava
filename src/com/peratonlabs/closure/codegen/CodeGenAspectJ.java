/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Feb 10, 2022
 */
package com.peratonlabs.closure.codegen;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.peratonlabs.closure.codegen.partition.Call;
import com.peratonlabs.closure.codegen.partition.Cut;
import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.Entry;
import com.peratonlabs.closure.codegen.partition.MethodSignature;
import com.peratonlabs.closure.codegen.partition.Xdcc;
import com.peratonlabs.closure.remote.ClosureShadow;

public class CodeGenAspectJ
{
//    private static ArrayList<String> wovenClasses = new ArrayList<String>(); 
    
    public static void genAspectJ(Xdcc xdcc, Enclave myEnclave, String templateDir, String aspectDir) {
        try {
            String myEnclaveName = myEnclave.getName();
            String myLevel = myEnclave.getLevel();
            if (myLevel == null) {
                System.err.println("level must be specified for enclave: " + myEnclave.getName());
                System.exit(1);;
            }

            String file = aspectDir + "/" + myEnclaveName + ".aj";
            FileWriter myWriter = openAspectJFile(file, myEnclaveName, templateDir + "/aspectj-imports.template");

            for (Cut cut : xdcc.getCuts()) {
                for (Call call : cut.getAllowedCallers()) {
                    if (!call.getLevel().contentEquals(myLevel))  // I am not allowed to call
                        continue;

                    String assigned = xdcc.assignedToEnclave(call.getType());
                    if (assigned.equals(myEnclaveName))  // hosted by myself;
                        continue;

                    MethodSignature signature = cut.getMethodSignature();

                    String fqcn = signature.getFqcn();
                    int index = fqcn.lastIndexOf(".");
                    String className = fqcn.substring(index + 1);
                    String pointcut = fqcn.replaceAll("\\.", "_");
                    String methodName = signature.getName();
                    String argTypes = getArgs(signature.getParameterTypes());

                    String template = null;
                    if (className.equals(methodName)) {
                        template = templateDir + "/aspectj-constructor.template";
                    }
                    else { // TODO
                        template = templateDir + "/aspectj-method.template";
                    }

                    String lines = new String(Files.readAllBytes(Paths.get(template)));
                    lines = lines.replaceAll("##ClassName##", className);
                    lines = lines.replaceAll("##MethodName##", methodName);
                    lines = lines.replaceAll("##FQCN##", fqcn);
                    lines = lines.replaceAll("##PointCut##", pointcut);
                    lines = lines.replaceAll("##EnclaveName##", myEnclaveName);
                    lines = lines.replaceAll("##ARGS##", argTypes);

                    writeAspectJFile(myWriter, lines);
                    
                    genDisallow(templateDir, fqcn, aspectDir, myEnclaveName);
                }
            }
            closeAspectJ(myWriter, file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void genShadows(String FQCN, String className, String templateDir, String aspectDir) {
        try {
            String template = templateDir + "/class.template";
            String file = aspectDir + "/Shadows.java";
            
            FileWriter fileWriter = new FileWriter(file);
            String lines = new String(Files.readAllBytes(Paths.get(template)));
            lines = lines.replaceAll("##ClassName##", className);
            lines = lines.replaceAll("##FQCN##", FQCN);
            
            fileWriter.write(lines);
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void genDisallow(String templateDir, String fqcn, String aspectDir, String enclave) {
        try {
            String template = templateDir + "/aspectj-disallow.template";
            int lastDot = fqcn.lastIndexOf(".");
            String className = fqcn.substring(lastDot + 1);

            String sCurrentLine = new String(Files.readAllBytes(Paths.get(template)));
            sCurrentLine = sCurrentLine.replaceAll("##FQCN##", fqcn);
            sCurrentLine = sCurrentLine.replaceAll("##ClassName##", className);
            sCurrentLine = sCurrentLine.replaceAll("##EnclaveName##", enclave);

            String file = aspectDir + "/" + className + "ClosureAspect.aj";
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(sCurrentLine);
            myWriter.close();
            System.out.println("generated " + file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void genAspectJMain(String templateDir, String aspectDir, Entry entry) {
        try {
            String template = templateDir + "/aspectj-main.template";
            String fqcn = entry.getMainClass();
            int lastDot = fqcn.lastIndexOf(".");
            String className = fqcn.substring(lastDot + 1);

            String sCurrentLine = new String(Files.readAllBytes(Paths.get(template)));
            sCurrentLine = sCurrentLine.replaceAll("##ClassName##", className);
            sCurrentLine = sCurrentLine.replaceAll("##EntryClass##", entry.getMainClass());
            
            String closureRemote;
            ClosureShadow.RemoteType remoteType = ClosureShadow.getRemoteType();
            switch(remoteType) {
            case HAL:
                closureRemote = "ClosureRemoteHalSlave";
                break;
            case RMI:
                closureRemote = "ClosureRemoteRMI";
                break;
            default:
                closureRemote = "ClosureRemoteInvalid";
                break;
            }
            sCurrentLine = sCurrentLine.replaceAll("##ClosureRemote##", closureRemote);

            String file = aspectDir + "/" + className + "MainAspect.aj";
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(sCurrentLine);
            myWriter.close();
            System.out.println("generated " + file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getArgs(ArrayList<String> args) {
        String list = "";
        if (args == null || args.size() == 0)
            return list;
        
        for (String type : args) {
            if (list.length() > 0)
                list += ", ";
            list += type;
        }
        return list;
    }
    
    private static FileWriter openAspectJFile(String file, String myEnclaveName, String template) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            String lines = new String(Files.readAllBytes(Paths.get(template)));
            fileWriter.write(lines);
            
            fileWriter.write("public aspect " + myEnclaveName + "\n{\n"); // + "_ClosureAspect\n{\n");
            
            return fileWriter;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static void writeAspectJFile(FileWriter myWriter, String lines) {
        try {
            myWriter.write(lines);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void closeAspectJ(FileWriter myWriter, String file) {
        try {
            myWriter.write("}\n");
            myWriter.close();
            System.out.println("generated " + file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
