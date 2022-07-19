/*******************************************************************************
 * Copyright (c) 2018 Perspecta Labs Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *  
 * @author tchen
 *******************************************************************************/
package com.peratonlabs.closure.codegen;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Config
{
    private String srcDir = "/home/tchen/eop2/eop2-demo-clean";
    private String codeDir = ".";              // root directory of the source (same or sub-directory of the src)
    private String dstDir = "/tmp/xdcc";
    private String cut = "test/cut-eop2.json";
    private String jar = "TESTPROGRAM";        // JAR name without the extension
    
    public Config() {
    }
    
    public static Config load(String jsonFile) {
        try {
            JsonReader reader = new JsonReader(new FileReader(jsonFile));
            return new Gson().fromJson(reader, Config.class);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public String getDstDir() {
        return dstDir;
    }

    public void setDstDir(String dstDir) {
        this.dstDir = dstDir;
    }

    public String getCut() {
        return cut;
    }

    public void setCut(String cut) {
        this.cut = cut;
    }

    public String getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(String srcDir) {
        this.srcDir = srcDir;
    }

    public String getCodeDir() {
        return codeDir;
    }

    public void setCodeDir(String codeDir) {
        this.codeDir = codeDir;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }
}
