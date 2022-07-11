/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 28, 2021
 */
package com.peratonlabs.closure.codegen.rpc.serialization;

public class RPCField extends RPCObject
{
	private static final long serialVersionUID = 1L;
	protected String fieldName;
    protected Object value;

    public RPCField(int oid, String fqcn, String fieldName, Object value, int flags) {
        super(oid, fqcn, flags);

        this.fieldName = fieldName;
        this.value = value;
    }
    
    public String getUid() {
    	return fqcn + "." + fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
