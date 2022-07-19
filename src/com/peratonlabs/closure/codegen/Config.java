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
    private String dstDir = "/tmp/xdcc";
    private String cut = "test/cut-eop2.json";
    
    public Config() {
    }
    
    public static Config load(String jsonFile) {
        try {
            JsonReader reader = new JsonReader(new FileReader(jsonFile));
            return new Gson().fromJson(reader, Config.class);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
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
}
