/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.peratonlabs.closure.codegen.partition.Entry;
import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.Xdcc;
import com.peratonlabs.closure.codegen.rpc.xdconf.Xdconf;

public class CodeGen
{
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: <this program> <input json>");
            return;
        }
        CodeGen codeGen = new CodeGen();
        codeGen.generate(args[0]);
    }
    
    private void generate(String spec) {
        Xdcc xdcc = Xdcc.load(spec);
        if (xdcc == null) {
            System.err.println("Can't load " + spec);
            return;
        }
//        System.out.println(xdcc.toJson(true));
        
        // make a copy of the original code for each enclave
        for (Enclave partition : xdcc.getEnclaves()) {
            String dst = xdcc.getDstDir() + "/" + partition.getName();
            Utils.copyDir(xdcc.getRootDir(), dst, true);
            System.out.println("copied original code to the " + partition.getName() + " enclave");
        }
        
        String cwd = System.getProperty("user.dir");
        String common = cwd + "/resources/common";
        String templateDir = cwd + "/resources/templates";
        
        Xdconf xdconf = new Xdconf();
        xdconf.populate(xdcc);
        Entry entry = xdcc.getEntry();
        String master = entry.getEnclave();
        
        for (Enclave partition : xdcc.getEnclaves()) {
            String enclave = partition.getName();
            String dst = xdcc.getDstDir() + "/" + enclave;
            String parentDir = dst + "/" + xdcc.getCodeDir();
            String aspectDir = parentDir + "/aspect"; 
            
            if (partition.getLevel() == null) {
                System.err.println("level must be specified for enclave: " + partition.getName());
                continue;
            }
            
            Utils.copyDir(common, parentDir, false);
            System.out.println("copied " + common + " to the " + enclave + " enclave");
                       
            // String template = templateDir + "/aspectj.template";
            // all slave enclaves are passive, their main classes is replaced by a loop waiting for access.
            if (!enclave.equals(master)) {
                CodeGenAspectJ.genAspectJMain(templateDir, aspectDir, entry);
            }
            
            // each enclave needs aspectJ definitions for the constructs hosted in remote enclaves 
            for (Enclave peer : xdcc.getEnclaves()) {
                if (peer.getName().equals(enclave)) {
                    continue;
                }
                ArrayList<String> ownedByPeer = peer.getAssignedClasses();
                if (ownedByPeer != null && !ownedByPeer.isEmpty()) {
                    CodeGenAspectJ.genAspectJ(xdcc, partition, templateDir, aspectDir);
                }
            }
            genIPC(templateDir, aspectDir, enclave);
            xdconf.genTags(aspectDir + "/tags.txt");
            genBuildScript(templateDir, parentDir, xdcc.getJar());
        }
        
        xdconf.gen(xdcc, templateDir); 
    }

    private void genBuildScript(String templateDir, String buildDir, String appJar) {
        try {
            String[] scripts = { "build-closure.xml" };
            
            for (String script : scripts) {
                String filePath = templateDir + "/" + script;
                String sCurrentLine = new String(Files.readAllBytes(Paths.get(filePath)));
                sCurrentLine = sCurrentLine.replaceAll("##APP_JAR##", appJar);

                String file = buildDir + "/" + script;
                FileWriter myWriter = new FileWriter(file);
                myWriter.write(sCurrentLine);
                myWriter.close();
                System.out.println("generated " + file);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genIPC(String templateDir, String aspectDir, String enclave) {
        try (BufferedReader br = new BufferedReader(new FileReader(templateDir + "/ipc.txt"))) {
            String inUri = br.readLine().trim() + enclave.toLowerCase() + "\n";
            String outUri = br.readLine().trim() + enclave.toLowerCase() + "\n";
            
            String file = aspectDir + "/ipc.txt";
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(inUri);
            myWriter.write(outUri);
            myWriter.close();
            System.out.println("generated " + file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
