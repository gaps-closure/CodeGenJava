/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.gedl;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.peratonlabs.closure.codegen.partition.Xdcc;

public class Gedls
{
//    private static final Logger logger = Logger.getLogger(Xdcc.class);
    
    private ArrayList<Gedl> gedl;      
    
    public static Gedls load(String designJsonFile) {
        try {
            JsonReader reader = new JsonReader(new FileReader(designJsonFile)); //Util.getPath(mappingJsonFile)));
            Gedls cleDesign = new Gson().fromJson(reader, Gedls.class);

            return cleDesign;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Gedls loadFromString(String designJson) {
        Gedls cleDesign = new Gson().fromJson(designJson, Gedls.class);

        return cleDesign;
    }
    
    public String toJson(boolean pretty) {
        Gson gson;
    
        if (pretty)
            gson = new GsonBuilder().setPrettyPrinting().create();
        else
            gson = new GsonBuilder().create();
        
        return gson.toJson(this);
    }
    
    public static void main(String[] args) {
        Gedls gedls = Gedls.load("c:/tmp/egress_xdcc.gedl");
        System.out.println(gedls.toJson(true));
    }

}
