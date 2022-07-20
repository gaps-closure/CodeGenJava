/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 27, 2021
 */
package com.peratonlabs.closure.codegen.rpc.serialization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.peratonlabs.closure.codegen.rpc.GapsTag;

// to be auto generated
public class RPCObjectTag
{
    static HashMap<String, GapsTag> tagsMap = new HashMap<String, GapsTag>();
    
    static {
        try (BufferedReader br = new BufferedReader(new FileReader("aspect/tags.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] fields = line.trim().split("\\s+");
                if (fields.length < 4) {
                    System.err.println("bad tag: " + line);
                    continue;
                }
                int mux = Integer.parseInt(fields[1]);
                int sec = Integer.parseInt(fields[2]);
                int typ = Integer.parseInt(fields[3]);
                GapsTag tag = new GapsTag(mux, sec, typ);
                tagsMap.put(fields[0], tag);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Look up the corresponding tag, based on FQCN (TODO: and maybe other information)
     */
    public static GapsTag lookup(String fullname) {
        if (fullname.equals("response"))
            return new GapsTag(2, 2, 1);
        else {
            GapsTag tag = tagsMap.get(fullname);
            if (tag == null) {
                System.err.println("missing tag for " + fullname);
                return new GapsTag(2,2,1);
            }
            
            return tag;
        }
    }
}
