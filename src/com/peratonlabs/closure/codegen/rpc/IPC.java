/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Dec 3, 2021
 */
package com.peratonlabs.closure.codegen.rpc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class IPC
{
    private static String inUri = "ipc:///tmp/sock_suborange";
    private static String outUri = "ipc:///tmp/sock_puborange";
    
    static {
        try (BufferedReader br = new BufferedReader(new FileReader("aspect/ipc.txt"))) {
            inUri = br.readLine();
            outUri = br.readLine();
            
            System.out.println(inUri + " " + outUri);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getInUri() {
        return inUri;
    }
    
    public static void setInUri(String inUri) {
        IPC.inUri = inUri;
    }
    
    public static String getOutUri() {
        return outUri;
    }
    
    public static void setOutUri(String outUri) {
        IPC.outUri = outUri;
    }
}
