/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Feb 18, 2022
 */
package com.peratonlabs.closure.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.Entry;

public class CodeGenRMI
{
    public void genRmiImpl(Enclave enclave, String rmiImplFile) {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(rmiImplFile)));
            
            contents = contents.replaceAll("##LEVEL##", enclave.getName());

            FileWriter myWriter = new FileWriter(rmiImplFile);
            myWriter.write(contents);
            myWriter.close();
            
            System.out.println("generated " + rmiImplFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genEntryPoint(String templateDir, String parentDir, String rmiDir, Entry entry) {
        try {
            String mainPath = templateDir + "/main.java";
            String contents = new String(Files.readAllBytes(Paths.get(mainPath)));
            
            String fqcn = entry.getMainClass();
            int lastDot = fqcn.lastIndexOf(".");
            String fqpn = fqcn.substring(0, lastDot);
            String className = fqcn.substring(lastDot + 1);
            
            contents = contents.replaceAll("##FQPN##", fqpn);
            contents = contents.replaceAll("##ClassName##", className);

            String path = parentDir + "/" + entry.getFilepath();
            File original = new File(path);
            if (original.delete())
                System.out.println("deleted original " + path);
            else
                System.err.println("cannot delete original " + path);
            
            String newClass = rmiDir + "/" + className + ".java"; 
            
            FileWriter myWriter = new FileWriter(newClass);
            myWriter.write(contents);
            myWriter.close();
            
            System.out.println("generated " + path);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
