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
    private String srcDir = "/home/closure/gaps/capo/Java/examples/eop2-demo";
    private String codeDir = ".";              // root directory of the source (same or sub-directory of the src)
    private String dstDir = "/home/closure/gaps/xdcc";
    private String cut = "test/cut.json";
    private String jar = "TESTPROGRAM";        // JAR name without the extension
    private boolean compile = true;            // whether to compile the code after partition
    
    private String halCfg = "/home/closure/gaps/hal/java-eop2-demo-hal/hal_autoconfig-multienclave.py";
    private String deviceFile = "/home/closure/gaps/hal/java-eop2-demo-hal/devices_eop2_java_alllocal.json";
    
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

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public String getHalCfg() {
        return halCfg;
    }

    public void setHalCfg(String halCfg) {
        this.halCfg = halCfg;
    }

    public String getDeviceFile() {
        return deviceFile;
    }

    public void setDeviceFile(String deviceFile) {
        this.deviceFile = deviceFile;
    }
}
