package com.peratonlabs.closure.serial;

import java.util.ArrayList;

public class ClassDetails
{
    private final String _className;    // The name of the class
    private int _refHandle;             // The reference handle for the class
    private byte _classDescFlags;       // The classDescFlags value for the class
    private final ArrayList<ClassField> _fieldDescriptions; // The class field descriptions

    public ClassDetails(String className) {
        this._className = className;
        this._refHandle = -1;
        this._classDescFlags = 0;
        this._fieldDescriptions = new ArrayList<ClassField>();
    }

    public String getClassName() {
        return this._className;
    }

    public void setHandle(int handle) {
        this._refHandle = handle;
    }

    public int getHandle() {
        return this._refHandle;
    }

    public void setClassDescFlags(byte classDescFlags) {
        this._classDescFlags = classDescFlags;
    }

    public boolean isSC_SERIALIZABLE() {
        return (this._classDescFlags & 0x02) == 0x02;
    }

    public boolean isSC_EXTERNALIZABLE() {
        return (this._classDescFlags & 0x04) == 0x04;
    }

    public boolean isSC_WRITE_METHOD() {
        return (this._classDescFlags & 0x01) == 0x01;
    }

    public boolean isSC_BLOCKDATA() {
        return (this._classDescFlags & 0x08) == 0x08;
    }

    public void addField(ClassField cf) {
        this._fieldDescriptions.add(cf);
    }

    public ArrayList<ClassField> getFields() {
        return this._fieldDescriptions;
    }

    public void setLastFieldName(String name) {
        this._fieldDescriptions.get(this._fieldDescriptions.size() - 1).setName(name);
    }
    
    public String getLastFieldName() {
        return this._fieldDescriptions.get(this._fieldDescriptions.size() - 1).getName();
    }

    public void setLastFieldClassName1(String cn1) {
        this._fieldDescriptions.get(this._fieldDescriptions.size() - 1).setClassName1(cn1);
    }
    
    public String getLastFieldClassName1() {
        return this._fieldDescriptions.get(this._fieldDescriptions.size() - 1).getClassName1();
    }
}
