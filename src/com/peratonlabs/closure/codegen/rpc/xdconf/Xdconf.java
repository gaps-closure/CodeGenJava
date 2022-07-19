/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.rpc.xdconf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

//import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.peratonlabs.closure.codegen.CodeGenAspectJ;
import com.peratonlabs.closure.codegen.partition.Call;
import com.peratonlabs.closure.codegen.partition.Cut;
import com.peratonlabs.closure.codegen.partition.Enclave;
import com.peratonlabs.closure.codegen.partition.MethodSignature;
import com.peratonlabs.closure.codegen.partition.Xdcc;

public class Xdconf
{
    private ArrayList<XdEnclave> enclaves = new ArrayList<XdEnclave>();
    private transient ArrayList<XdMap> xdMaps = new ArrayList<XdMap>();

    public Xdconf() {
    }
    
    public void populate(Xdcc xdcc) {
        XdMap.cartneq(xdcc);
        
        ArrayList<Cut> cuts = xdcc.getCuts();
        if (cuts == null) {
            System.err.println("missing 'cuts'");
            return;
        }
        for (Cut cut : cuts) {
            Call call = cut.getCallee();
            String toLevel = call.getLevel();
            Enclave enclaveTo = xdcc.findEnclaveByLevel(toLevel);
            String enclaveToName = enclaveTo.getName();
            
            MethodSignature signature = cut.getMethodSignature();
            String sigStr = signature.toString();
            
//            int mux = XdMap.getMux(enclave);
//            int sec = mux;
//            int typ = XdMap.getType(enclave, sigStr);
            
            for (Call caller : cut.getAllowedCallers()) {
                String fromLevel = caller.getLevel();
                Enclave enclaveFrom = xdcc.findEnclaveByLevel(fromLevel);
                String enclaveFromName = enclaveFrom.getName();
                
                int sec = XdMap.getSec(enclaveFromName, enclaveToName);
                int mux = sec;
                int typ = XdMap.getType(enclaveToName, sigStr);
                
                XdMap xdMap = new XdMap(enclaveToName, mux, sec, typ);
                xdMap.setFrom(enclaveFromName);
                xdMap.setName(sigStr);
                
                xdMaps.add(xdMap);
                
                String rsp = sigStr + "_rsp";
                sec = XdMap.getSec(enclaveToName, enclaveFromName);
                mux = sec;
                typ = XdMap.getType(fromLevel, rsp);
                
                xdMap = new XdMap(enclaveFromName, mux, sec, typ);
                xdMap.setFrom(enclaveToName);
                xdMap.setName(rsp); // + signature.getReturnType());

                xdMaps.add(xdMap);
            }
        }
    }
    
    public void genTags(String filename) {
        try {
            FileWriter myWriter = new FileWriter(filename);
            for (XdMap xdMap : xdMaps) {
                myWriter.write(xdMap.getName() + " " + xdMap.getMux() + " " + xdMap.getSec() + " " + xdMap.getTyp() + "\n");
            }
            myWriter.close();
            System.out.println("generated " + filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void gen(Xdcc xdcc, String templateDir) {
    	try (BufferedReader br = new BufferedReader(new FileReader(templateDir + "/ipc.txt"))) {
    		String inUri = br.readLine().trim();
    		String outUri = br.readLine().trim();

    		for (Enclave enclave : xdcc.getEnclaves()) {
    		    String name = enclave.getName();
    		    String sub = inUri + name.toLowerCase();
    		    String pub = outUri + name.toLowerCase();
    		    XdEnclave xdEnclave = new XdEnclave(name, sub, pub);

    		    ArrayList<XdMap> myMaps = new ArrayList<XdMap>();
    		    for (XdMap xdMap : xdMaps) {
    		        XdMap myXdMap = new XdMap(xdMap);
    		        
    		        if (!xdMap.getFrom().equals(enclave.getName()) &&
    		            !xdMap.getTo().equals(enclave.getName()) )
    		            continue;
    		        myMaps.add(myXdMap);
    		    }
    		    xdEnclave.setHalmaps(myMaps);
    		    enclaves.add(xdEnclave);
    		}

    		try {
    			String filename = xdcc.getDstDir() + "/" + "xdconf.ini";
    			FileWriter myWriter;
    			myWriter = new FileWriter(filename);
    			myWriter.write(toJson(true));
    			myWriter.close();
    			System.out.println("generated " + filename);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void genPubDst(FileWriter myWriter) {
        try {
            myWriter.write("\n");
            for (XdMap xdMap : xdMaps) {
                String name = xdMap.getName();
                if (!name.endsWith("_rsp"))
                    myWriter.write(xdMap.getName() + " " + xdMap.getFrom() + " " + xdMap.getTo() + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public XdEnclave findEnclave(String name) {
        for (XdEnclave xdEnclave : enclaves) {
            if (xdEnclave.getEnclave().contentEquals(name)) {
                return xdEnclave;
            }
        }
        return null;
    }
    
    public static Xdconf load(String designJsonFile) {
        try {
            JsonReader reader = new JsonReader(new FileReader(designJsonFile)); //Util.getPath(mappingJsonFile)));
            Xdconf cleDesign = new Gson().fromJson(reader, Xdconf.class);
//            logger.info("Design loaded from file " + designJsonFile);

            return cleDesign;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public String toJson(boolean pretty) {
        Gson gson;
    
        if (pretty)
            gson = new GsonBuilder().setPrettyPrinting().create();
        else
            gson = new GsonBuilder().create();
        
        return gson.toJson(this);
    }
}
