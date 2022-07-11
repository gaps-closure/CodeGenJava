/*******************************************************************************
 * Copyright (c) 2021 Peraton Labs, Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *    
 * @author tchen
 *
 *******************************************************************************/
package com.peratonlabs.closure.codegen.rpc.xdconf;

import java.util.ArrayList;

public class XdEnclave
{
    private String enclave;
    private String inuri;
    private String outuri;
    
    private ArrayList<XdMap> halmaps;

    public XdEnclave(String enclave, String inuri, String outuri) {
        this.enclave = enclave;
        this.inuri = inuri;
        this.outuri = outuri;
    }
    
    public String getEnclave() {
        return enclave;
    }

    public void setEnclave(String enclave) {
        this.enclave = enclave;
    }

    public String getInuri() {
        return inuri;
    }

    public void setInuri(String inuri) {
        this.inuri = inuri;
    }

    public String getOuturi() {
        return outuri;
    }

    public void setOuturi(String outuri) {
        this.outuri = outuri;
    }

    public ArrayList<XdMap> getHalmaps() {
        return halmaps;
    }

    public void setHalmaps(ArrayList<XdMap> halmaps) {
        this.halmaps = halmaps;
    }
}
