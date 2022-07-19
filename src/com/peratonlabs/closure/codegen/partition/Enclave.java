/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.partition;

import java.util.ArrayList;

public class Enclave
{
    private String name;
    private String level;            // level the enclave is operating with
    private ArrayList<String> assignedClasses;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<String> getAssignedClasses() {
        return assignedClasses;
    }
    
    public void setOwnedClasses(ArrayList<String> assignedClasses) {
        this.assignedClasses = assignedClasses;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
