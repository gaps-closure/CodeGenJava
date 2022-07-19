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
    private Config config;
    
    public static void main(String[] args) {
        CodeGen codeGen = new CodeGen();
        codeGen.getOpts(args);
        
        codeGen.generate();
    }
    
    private void generate() {
        String spec = config.getCut();
        
        Xdcc xdcc = Xdcc.load(spec);
        if (xdcc == null) {
            System.err.println("Can't load " + spec);
            return;
        }
//        System.out.println(xdcc.toJson(true));
        
        // make a copy of the original code for each enclave
        for (Enclave partition : xdcc.getEnclaves()) {
            String dst = config.getDstDir() + "/" + partition.getName();
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
        if (master == null) {
            master = xdcc.findMaster();
            if (master != null)
                System.out.println(master + " is the master enclave.");
            else {
                System.err.println("could not determine the master enclave");
            }
        }
        
        for (Enclave partition : xdcc.getEnclaves()) {
            String enclave = partition.getName();
            String dst = config.getDstDir() + "/" + enclave;
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
                    break;
                }
            }
            genIPC(xdconf, templateDir, aspectDir, enclave);
            xdconf.genTags(aspectDir + "/tags.txt");
            genBuildScript(templateDir, parentDir, xdcc.getJar());
        }
        
        xdconf.gen(config, xdcc, templateDir); 
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

    private void genIPC(Xdconf xdconf, String templateDir, String aspectDir, String enclave) {
        try (BufferedReader br = new BufferedReader(new FileReader(templateDir + "/ipc.txt"))) {
            String inUri = br.readLine().trim() + enclave.toLowerCase() + "\n";
            String outUri = br.readLine().trim() + enclave.toLowerCase() + "\n";
            
            String file = aspectDir + "/ipc.txt";
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(inUri);
            myWriter.write(outUri);
            
            xdconf.genPubDst(myWriter);
            
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
    
    private void getOpts(String[] args) {
        String arg;
        
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            switch (arg) {
            case "--dstDir":
            case "-d":
                if (config == null) {
                    config = new Config();
                }
                config.setDstDir(args[++i]);
                break;
            case "--cutJson":
            case "-c":
                if (config == null) {
                    config = new Config();
                }
                config.setCut(args[++i]);
                break; 
            case "--config":
            case "-f":
                if (config != null) {
                    System.err.println("-f must be used as the first option.");
                    System.exit(1);;
                }
                config = Config.load(args[++i]);
                break;
            default:
                System.err.println("unknown option: " + arg);
                break;
            }
        }
        if (config == null) {  // all defaults
            config = new Config();
        }
    }
}
