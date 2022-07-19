/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.partition;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

//import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class Xdcc
{
//    private static final Logger logger = Logger.getLogger(Xdcc.class);
    
    private String jar;       // JAR name without the extension
    private String rootDir;   // root directory of the project
    private String codeDir;   // root directory of the source (same or sub-directory of the root)
    private Entry entry;      // program entry point
    
    private ArrayList<Assignment> assignments;
    private ArrayList<Cut> cuts;
    
    private ArrayList<Enclave> enclaves;
    
    public static Xdcc load(String designJsonFile) {
        try {
            JsonReader reader = new JsonReader(new FileReader(designJsonFile)); //Util.getPath(mappingJsonFile)));
            Xdcc cleDesign = new Gson().fromJson(reader, Xdcc.class);
            
            if (cleDesign.assignments == null) {
                cleDesign.buildAssignments();
            }
            
            return cleDesign;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Xdcc loadFromString(String designJson) {
        Xdcc cleDesign = new Gson().fromJson(designJson, Xdcc.class);
//        logger.info("Design loaded from string" + designJson);

        return cleDesign;
    }
    
    public void buildAssignments() {
        assignments = new ArrayList<Assignment>();
        for (Enclave enclave : enclaves) {
            for (String clz : enclave.getAssignedClasses()) {
                assignments.add(new Assignment(clz, enclave.getName()));
            }
        }
    }
    
    public String findMaster() {
        String mainClass = entry.getMainClass();
        for (Enclave enclave : enclaves) {
            ArrayList<String> assignedClasses = enclave.getAssignedClasses();
            if (assignedClasses == null)
                continue;
            for (String clz : assignedClasses) {
                if (clz.equals(mainClass))
                    return enclave.getName();
            }
        }
        return null;
    }
    
    // TODO: this does not make sense, it should be the other way around.
    // but enclave is needed, not level, from the cuts.
    public Enclave findEnclaveByLevel(String level) {
        for (Enclave enclave : enclaves) {
            if (enclave.getLevel().equals(level))
                return enclave;
        }
        return null;
    }
    
    public HashSet<String> assignedToEnclave(String ownEnclave, String fqcn) {
        if (assignments == null) {
            System.err.println("assignedToEnclave: null assignment");
            return null;
        }
        
        HashSet<String> set = new HashSet<String>();
        for (Assignment assignment : assignments) {
            if (assignment.getClassName().equals(fqcn) && !assignment.getEnclave().equals(ownEnclave)) {
                set.add(assignment.getEnclave());
            }
        }
        return set;
    }
    
    public String toJson(boolean pretty) {
        Gson gson;
    
        if (pretty)
            gson = new GsonBuilder().setPrettyPrinting().create();
        else
            gson = new GsonBuilder().create();
        
        return gson.toJson(this);
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String srcDir) {
        this.rootDir = srcDir;
    }

    public ArrayList<Enclave> getEnclaves() {
        return enclaves;
    }

    public void setEnclaves(ArrayList<Enclave> enclaves) {
        this.enclaves = enclaves;
    }

    public String getCodeDir() {
        return codeDir;
    }

    public void setCodeDir(String codeDir) {
        this.codeDir = codeDir;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(ArrayList<Assignment> assignments) {
        this.assignments = assignments;
    }

    public ArrayList<Cut> getCuts() {
        return cuts;
    }

    public void setCuts(ArrayList<Cut> cuts) {
        this.cuts = cuts;
    }
}
