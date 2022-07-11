package com.peratonlabs.closure.serial;

public class ClassField
{
    private final byte _typeCode;       // The field type code
    private String _name;               // The field name
    private String _className1;         // The className1 property (object and array type fields)

    public ClassField(byte typeCode) {
        this._typeCode = typeCode;
        this._name = "";
        this._className1 = "";
    }

    public byte getTypeCode() {
        return this._typeCode;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public void setClassName1(String cn1) {
        this._className1 = cn1;
    }
    
    public String getClassName1() {
        return this._className1;
    }
}
