/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.partition;

public class Entry
{
    private String mainClass;   // entry point of the program
    private String filepath;    // file pathname to the entry point
    private String enclave;     // the mainClass of the enclave is the master of the partitioned program 
    
    public String getMainClass() {
        return mainClass;
    }
    
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
    public String getFilepath() {
        return filepath;
    }
    
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getEnclave() {
        return enclave;
    }

    public void setEnclave(String enclave) {
        this.enclave = enclave;
    }
}
