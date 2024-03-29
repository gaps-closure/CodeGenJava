package com.peratonlabs.closure.serial;

import java.util.ArrayList;

public class ClassDataDesc
{
    private final ArrayList<ClassDetails> _classDetails; // List of all classes making up this class data description (i.e. class, superclass, etc)

    public ClassDataDesc() {
        this._classDetails = new ArrayList<ClassDetails>();
    }

    private ClassDataDesc(ArrayList<ClassDetails> cd) {
        this._classDetails = cd;
    }

    public ClassDataDesc buildClassDataDescFromIndex(int index) {
        ArrayList<ClassDetails> cd;

        // Build a list of the ClassDetails objects for the new ClassDataDesc
        cd = new ArrayList<ClassDetails>();
        for (int i = index; i < this._classDetails.size(); ++i) {
            cd.add(this._classDetails.get(i));
        }

        // Return a new ClassDataDesc describing this subset of classes
        return new ClassDataDesc(cd);
    }

    public void addSuperClassDesc(ClassDataDesc scdd) {
        // Copy the ClassDetails elements to this ClassDataDesc object
        if (scdd != null) {
            for (int i = 0; i < scdd.getClassCount(); ++i) {
                this._classDetails.add(scdd.getClassDetails(i));
            }
        }
    }

    public void addClass(String className) {
        this._classDetails.add(new ClassDetails(className));
    }

    public void setLastClassHandle(int handle) {
        this._classDetails.get(this._classDetails.size() - 1).setHandle(handle);
    }

    public void setLastClassDescFlags(byte classDescFlags) {
        this._classDetails.get(this._classDetails.size() - 1).setClassDescFlags(classDescFlags);
    }

    public void addFieldToLastClass(byte typeCode) {
        this._classDetails.get(this._classDetails.size() - 1).addField(new ClassField(typeCode));
    }

    /*******************
     * Set the name of the last field that was added to the last class to be
     * added to the ClassDataDesc.
     * 
     * @param name
     *            The field name.
     ******************/
    public void setLastFieldName(String name) {
        this._classDetails.get(this._classDetails.size() - 1).setLastFieldName(name);
    }

    public String getLastFieldName() {
        return this._classDetails.get(this._classDetails.size() - 1).getLastFieldName();
    }
    
    /*******************
     * Set the className1 of the last field that was added to the last class to
     * be added to the ClassDataDesc.
     * 
     * @param cn1
     *            The className1 value.
     ******************/
    public void setLastFieldClassName1(String cn1) {
        this._classDetails.get(this._classDetails.size() - 1).setLastFieldClassName1(cn1);
    }
    
    public String getLastFieldClassName1() {
        return this._classDetails.get(this._classDetails.size() - 1).getLastFieldClassName1();
    }

    /*******************
     * Get the details of a class by index.
     * 
     * @param index
     *            The index of the class to retrieve details of.
     * @return The requested ClassDetails object.
     ******************/
    public ClassDetails getClassDetails(int index) {
        return this._classDetails.get(index);
    }

    /*******************
     * Get the number of classes making up this class data description.
     * 
     * @return The number of classes making up this class data description.
     ******************/
    public int getClassCount() {
        return this._classDetails.size();
    }
}
