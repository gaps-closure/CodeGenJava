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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.peratonlabs.closure.codegen.partition.Entry;
import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.Xdcc;
import com.peratonlabs.closure.codegen.rpc.xdconf.Xdconf;

public class CodeGen
{
    private Config config;
    private Xdcc xdcc;

    public static void main(String[] args) {
        CodeGen codeGen = new CodeGen();
        codeGen.getOpts(args);

        codeGen.process();
    }
    
    private void process() {
        load();
        generate();
        if (config.isCompile())
            compile();
        pack();
    }
    
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
              .forEach(consumer);
        }
    }
    
    private void compile() {
        for (Enclave partition : xdcc.getEnclaves()) {
            String enclave = partition.getName();

            String[] cmds = {"ant", "ant -f build-closure.xml"};
            for (int i = 0; i < cmds.length; i++) {

                ProcessBuilder builder = new ProcessBuilder();
                builder.command("sh", "-c", cmds[i]);
                builder.directory(new File("/tmp/xdcc/" + enclave));
                Process process = null;
                try {
                    process = builder.start();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
                StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                Thread thread = new Thread(streamGobbler);
                // Executors.newSingleThreadExecutor().submit(streamGobbler);
                thread.start();

                int exitCode = 0;
                try {
                    exitCode = process.waitFor();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assert exitCode == 0;
            }
        }
    }
    
    private void pack() {
        for (Enclave partition : xdcc.getEnclaves()) {
            String dir = config.getDstDir() + "/" + partition.getName();
            String zip = dir + ".zip";
            
            System.out.println("Zipping " + zip);
            Utils.zipDirectory(dir, zip);
        }
    }
    
    private void load() {
        String spec = config.getCut();
        
        xdcc = Xdcc.load(spec);
        if (xdcc == null) {
            System.err.println("Can't load " + spec);
            return;
        }
//        System.out.println(xdcc.toJson(true));
    }

    private void generate() {
        // make a copy of the original code for each enclave
        for (Enclave partition : xdcc.getEnclaves()) {
            String dst = config.getDstDir() + "/" + partition.getName();
            Utils.copyDir(config.getSrcDir(), dst, true);
            // System.out.println("copied original code to the " + partition.getName() + " enclave");
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
            String parentDir = dst + "/" + config.getCodeDir();
            String aspectDir = parentDir + "/aspect"; 
            
            if (partition.getLevel() == null) {
                System.err.println("level must be specified for enclave: " + partition.getName());
                continue;
            }
            
            Utils.copyDir(common, parentDir, false);
            // System.out.println("copied " + common + " to the " + enclave + " enclave");
                       
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
            genBuildScript(templateDir, parentDir, config.getJar());
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
                // System.out.println("generated " + file);
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
            
//            xdconf.genPubDst(myWriter);
            
            myWriter.close();
            // System.out.println("generated " + file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void usage() {
        System.out.println("GAPS/Closure Java Code Generator\n" +
                           "  -h/--help                  \t this help\n" +
                           "  -c/--cutJson <cut.json>    \t cut JSON file\n" +
                           "  -d/--dstDir  <pathname>    \t destination directory of the generated code\n" +
                           "  -f/--config  <config.json> \t config JSON file\n" +
                           "  -i/--codeDir <source code> \t code directory relative to srcDir (def .)\n" +
                           "  -j/--jar     <jar name>    \t name of the application jar file\n" +
                           "  -p/--compile <true|false>  \t Compile the code after partition\n" +
                           "  -s/--srcDir  <app src dir> \t application source code\n");
        System.exit(0);
    }
    
    private void getOpts(String[] args) {
        String arg;
        
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            
            switch (arg) {
            case "--help":
            case "-h":
                usage();
                break;
                
            case "--dstDir":
            case "-d":
                if (config == null) {
                    config = new Config();
                }
                config.setDstDir(args[++i]);
                break;
                
            case "--srcDir":
            case "-s":
                if (config == null) {
                    config = new Config();
                }
                config.setSrcDir(args[i++]);
                break; 
                
            case "--codeDir":
            case "-i":
                if (config == null) {
                    config = new Config();
                }
                config.setCodeDir(args[i++]);
                break; 
                
            case "--jar":
            case "-j":
                if (config == null) {
                    config = new Config();
                }
                config.setJar(args[++i]);
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
                
            case "--compile":
            case "-p":
                if (config == null) {
                    config = new Config();
                }
                config.setCompile(Boolean.parseBoolean(args[++i]));
                break; 
                
            default:
                System.err.println("unknown option: \n" + arg);
                break;
            }
        }
        if (config == null) {  // all defaults
            config = new Config();
        }
    }
}
