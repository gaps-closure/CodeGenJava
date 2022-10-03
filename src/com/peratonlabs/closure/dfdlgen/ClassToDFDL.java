/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Mar 13, 2022
 */
package com.peratonlabs.closure.dfdlgen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import clsvis.model.Class_;
import clsvis.model.ElementKind;
import clsvis.model.ParameterizableElement;
import clsvis.process.importer.CompiledClassImporter;

public class ClassToDFDL
{ 
    private static final String ELEMENT = "element";
    private static final String CHOICE = "choice";
    private static final String SEQUENCE = "sequence";
    private static final String COMPLEX_TYPE = "complexType";
    
    private static final int NUM_SPACES = 2;
    private static final String DELTA = String.format("%1$" + NUM_SPACES + "s", "");
    
    private String indent = "  ";
    
    private BufferedWriter writer;
    private Class_ root; 
    private ArrayList<Class_> sortedClasses;
    
    private HashSet<Class<?>> arrays = new HashSet<Class<?>>();
    
    private boolean debug = false;
    private boolean gaps = false;
    private String outputDir = "/home/tchen/gaps/dfdl/";
    private CompiledClassImporter classImporter;

    public ClassToDFDL(CompiledClassImporter classImporter) {
        this.classImporter = classImporter;
    }
    
    public void convert(Class_ clazz) throws IOException {
        this.root = clazz;
        if (debug)
            writer = new BufferedWriter(new OutputStreamWriter(System.out));
        else {
            String fn = clazz.getType().getSimpleName();
            writer = new BufferedWriter(new FileWriter(outputDir + fn + ".xsd"));
        }
        
        sortedClasses = new ArrayList<Class_>(clazz.getSubclasses());
        Utils.sortByDepth(root, sortedClasses);  // bottom up in the class hierarchy is important
        
        for (Class_ clz : classImporter.getImportedClasses()) {  // sort the fields according to Java Serialization Spec
            clz.sortFields();
        }
        
        openSchema();
        writeStream();
        writeObject();
        closeSchema();
    }
    
    private void writeStream() {
        openSequence("Stream", null);
        writeElement("STREAM_MAGIC", Types.SHORT);
        writeElement("STREAM_VERSION", Types.SHORT);
        openChoice("Instances");
        for (Class_ clz : sortedClasses) {
            String refName = Types.topLevelClassName(clz, false);
            writeElementRef(refName);
        }
        closeChoice();
        closeSequence();
        writeln();
    }
    
    // TODO: incomplete: newObject only for now
    private void writeObject() {
        for (Class_ clazz : sortedClasses) {
            writeNewObject(clazz);
        }
    }
    
    // newObject:
    //    TC_OBJECT classDesc newHandle classdata[] // data for each class
    private void writeNewObject(Class_ clazz) {
        openSequence(Types.topLevelClassName(clazz, true), null); 
        writeElement("TC_OBJECT", Types.BYTE);
        writeElementRef(Types.classDescDataName(clazz, true, true));
        writeElement("Data", Types.classDescDataName(clazz, false, true));
        closeSequence();
        writeln();
        
        writeClassDesc(clazz, false);
        writeln();
        
        writeClassData(clazz, false);
        writeln();
    }
    
    // classDesc:
    //   newClassDesc
    //   nullReference
    //   (ClassDesc)prevObject      // an object required to be of type ClassDesc
    private void writeClassDesc(Class_ clazz, boolean nameless) {
        writeNewClassDesc(clazz, nameless);
    }
    
    // newClassDesc:
    //   TC_CLASSDESC className serialVersionUID newHandle classDescInfo
    //   TC_PROXYCLASSDESC newHandle proxyClassDescInfo
    private void writeNewClassDesc(Class_ clazz, boolean nameless) {
        String name = (clazz == null) ? null : Types.classDescDataName(clazz, true, false);
        
        if (name != null)
            open(ELEMENT, name, null);
        writeDiscriminator("./desc/TC_CLASSDESC", ObjectStreamConstants.TC_CLASSDESC);
        // TODO: missing choice for proxy class desc
        open(COMPLEX_TYPE, null, null);
        open(CHOICE, null, null);
        
        openSequence("desc", null);
        writeElement("TC_CLASSDESC", Types.BYTE);
        writeElement("className", Types.FIELD_NAME);
        writeElement("serialVersionUID", Types.LONG);
        writeClassDescInfo(clazz, nameless);
        closeSequence();
        
        close(CHOICE);
        close(COMPLEX_TYPE);
        if (name != null)
            close(ELEMENT);
    }
    
    // classDescInfo:
    //     classDescFlags fields classAnnotation superClassDesc
    // classDescFlags:
    //     (byte)  
    // fields:
    //     (short)<count>  fieldDesc[count]
    private void writeClassDescInfo(Class_ clazz, boolean nameless) {
        openSequence("classDescInfo", null);
        writeElement("classDescFlags", Types.BYTE);

        if (clazz == null) {
            writeElement("fieldCount", Types.SHORT);
        }
        else {
            // openSequenceElement("fields", null);
            List<ParameterizableElement> fields = clazz.getMember(ElementKind.Fields);
            if (fields != null) {
                writeElement("fieldCount", Types.SHORT);
                if (fields.size() > 0) {
                    for (ParameterizableElement field : clazz.getPrimitiveFields()) {
                        writeFieldDesc(field, false);
                    }
                    HashSet<Class<?>> handles = new HashSet<Class<?>>();
                    for (ParameterizableElement field : clazz.getObjFields()) {
                        boolean ref = handles.contains(field.getType());
                        writeFieldDesc(field, ref);
                        handles.add(field.getType());
                    }
                }
            }
            //closeSequenceElement();
        }
        
        writeClassAnnotation(clazz);
        if (nameless)
            writeElement("TC_NULL", Types.BYTE);
        else
            writeSuperClassDesc(clazz);
        
        closeSequence();
    }
    
    private void writeSuperClassDesc(Class_ clazz) {
        if (clazz == null) {
            writeElement("TC_NULL", Types.BYTE);
            return;
        }

        Class_ superclass = clazz.getSuperclass();
        if (superclass == null || clazz == root) {
            writeElement("TC_NULL", Types.BYTE);
            return;
        }
        writeElementRef(Types.classDescDataName(superclass, true, true));
    }
    
    // TODO: incomplete; 
    private void writeClassAnnotation(Class_ clazz) {
        if (clazz == null)
            writeElement("TC_ENDBLOCKDATA", Types.BYTE);
        else {
            writeElement("TC_ENDBLOCKDATA", Types.BYTE);
        }
    }
    
    // fieldDesc:
    //     primitiveDesc
    //     objectDesc
    // primitiveDesc:
    //     prim_typecode fieldName
    // objectDesc:
    //     obj_typecode fieldName className1
    private void writeFieldDesc(ParameterizableElement field, boolean ref) {
        openSequence(Types.fieldName(field, true), null);
        
        writeElement(Types.fieldType(field), Types.BYTE);
        writeElement(Types.fieldName(field, false), Types.FIELD_NAME);
        
        if (!field.getType().isPrimitive()) {
            if (ref) {
                writeReference(Types.objectDesc(field, true));
            }
            else {
                openChoice(Types.objectDesc(field, true)); 
                writeElement("ObjectDesc", Types.CLASS_NAME);
                writeReference(Types.fieldType(field));
                closeChoice();
            }
        }
        closeSequence();
    }
    
    private void writeReference(String desc) {
        openSequence("Ref" + Types.capitalize(desc), null);
        writeElement("TC_REFERENCE", Types.BYTE);
        writeElement("Handle", Types.UNSIGNED_INT);
        closeSequence();
    }
    
    // classdata:
    //    nowrclass
    //    ...
    // nowrclass:
    //    values 
    private void writeClassData(Class_ clazz, boolean nameless) {
        String name = nameless ? null : Types.classDescDataName(clazz, false, false);
        open(COMPLEX_TYPE, name, null);
        open(SEQUENCE, null, null);
        
        if (!clazz.equals(root) && !nameless) {
            Class_ superclass = clazz.getSuperclass();
            writeElement("ValueSuper",  Types.classDescDataName(superclass, false, true));
        }
        for (ParameterizableElement field : clazz.getPrimitiveFields()) {
            writeValue(field, false, false);
        }
        // TODO: hanlde those in the super classes
        HashSet<Class<?>> handles = new HashSet<Class<?>>();
        for (ParameterizableElement field : clazz.getObjFields()) {
            boolean ref = handles.contains(field.getType());
            writeValue(field, ref, true);
            handles.add(field.getType());
        }
        close(SEQUENCE);
        close(COMPLEX_TYPE);
    }
    
    // TODO: incomplete
    private void writeValue(ParameterizableElement field, boolean ref, boolean nameless) {
        Class<?> clz = field.getType();
        if (clz.isPrimitive()) {
            writeElement(Types.fieldValue(field), Types.getPrimitiveType(clz));
        }
        else if (clz.isArray()) {
            writeNewArray(field, ref);
        }
        else if (clz.equals(String.class)) {
            writeNewString(field);
        }
        else {
            writeNewObject(field, ref, nameless);
        }
    }
    
    private void writeNewObject(ParameterizableElement field, boolean ref, boolean nameless) {
        Class<?> clz = field.getType();
        if (clz.isPrimitive() || clz.isArray()) {
            System.err.println("not an class data " + field.name);
            return;
        }

        openSequence(Types.fieldValue(field), null);
        writeElement("TC_OBJECT", Types.BYTE); // ----
        
        Class_ clazz = classImporter.getImportedClass(clz.getTypeName());
        if (ref) {
            writeReference(Types.arrayDesc(field, true));
        }
        else {
            // array descriptor or reference
            openChoice(Types.arrayDesc(field, true));
//            open(ELEMENT, "desc", null);
            writeClassDesc(clazz, nameless);
//            close(ELEMENT);
            writeReference(Types.arrayDesc(field, true));
            closeChoice();
        }
        // Object values
        open(ELEMENT, "desc", null);
        writeClassData(clazz, true);
        close(ELEMENT);
        closeSequence();
    }
    
    private void writeNewString(ParameterizableElement field) {
        String name = field.getSignature();

        open(ELEMENT, name, null);
        open(COMPLEX_TYPE, null, null);
        open(CHOICE, null, null);
        
        writeAnnotatation("stringValue", Types.CLASS_NAME, "./TC_STRING", ObjectStreamConstants.TC_STRING);
        writeAnnotatation("TC_NULL", Types.BYTE, ".", ObjectStreamConstants.TC_NULL);

        close(CHOICE);
        close(COMPLEX_TYPE);
        close(ELEMENT);
    }
    
    private void writeNewArray(ParameterizableElement field, boolean ref) {
        Class<?> clz = field.getType();
        if (!clz.isArray()) {
            System.err.println("not an array data " + field.name);
            return;
        }
        
        Class<?> compType = clz.getComponentType();  // +++
        if (compType.isPrimitive()) {
            arrays.add(compType);
        }
        
        openSequence(Types.fieldValue(field), null);
        writeElement("TC_ARRAY", Types.BYTE);
        
        if (ref) {
            writeReference(Types.arrayDesc(field, true));
        }
        else {
            // array descriptor or reference
            openChoice(Types.arrayDesc(field, true));
            open(ELEMENT, "Descriptor", null);
            writeClassDesc(null, false);
            close(ELEMENT);
            writeReference(Types.arrayDesc(field, true));
            closeChoice();
        }
        // array values
        writeElement(Types.fieldName(field, true),  Types.arrayTypeName(compType, false));
        closeSequence();
    }

    private void openSchema() throws IOException {
        String filePath = "resources/dfdl/schemaOpen.xsd";
        includeFile(filePath);

        if (gaps) {
            filePath = "resources/dfdl/gapsPDU.xsd";
            includeFile(filePath);
        }
    }
    
    private void includeFile(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> {
                    write(s + "\n");
            });
            writeln();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void closeSchema() {
        try {
            for (Class<?> compType : arrays) {
                writeln();
                writeArrayValue(compType);
            }
            
            writeStringType();
            
            writer.write("</xs:schema>");
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeArrayValue(Class<?> compType) {
        try {
            int bytes = Types.getPrimitiveSize(compType) / 8;

            String filePath = "resources/templates/array-template.xsd";
            String sCurrentLine = new String(Files.readAllBytes(Paths.get(filePath)));

            sCurrentLine = sCurrentLine.replaceAll("###TYPE_ARRAY###", Types.arrayTypeName(compType, true));
            sCurrentLine = sCurrentLine.replaceAll("###ELEMENT_TYPE###", Types.getPrimitiveType(compType));
            sCurrentLine = sCurrentLine.replaceAll("###ELEMENT_SIZE###", bytes + "");

            write(sCurrentLine);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void writeDiscriminator(String path, int value) {
        writeln("<xs:annotation>");
        writeln("  <xs:appinfo source=\"http://www.ogf.org/dfdl/\">");
        writeln("    <dfdl:discriminator test='{" + path + " eq " + value + "}'/>");
        writeln("  </xs:appinfo>");
        writeln("</xs:annotation>");
    }
    
    private void writeAnnotatation(String name, String type, String path, int value) {
        open(ELEMENT, name, "type=\"" + type + "\"");
        writeDiscriminator(path, value);
        close(ELEMENT);
    }
    
    private void writeStringType() {
        try {
            String template = "resources/dfdl/string.xsd";
            String contents = new String(Files.readAllBytes(Paths.get(template)));
                
            writeln();
            write(contents);
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeElement(String name, String type) {
        String typeStr = (type == null) ? "\"" : ("\" type=\"" + type + "\"");

        writeln("<xs:element name=\"" + name + typeStr + "/>");
    }
    
    private void writeElementRef(String name) {
        writeln("<xs:element ref=\"" + name + "\"/>");
    }
    
    private void write(String line) {
        try {
            writer.write(line);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeln(String line) {
        try {
            writer.write(indent + line + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeln() {
        try {
            writer.write("\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void incIndent() {
        indent = indent + DELTA;
    }

    private void decIndent() {
        if (indent.length() < NUM_SPACES) {
            System.err.println("indent length too small: " + indent.length());
        }
        else
            indent = indent.substring(0, indent.length() - NUM_SPACES);
    }
    
    private void openSequence(String name, String attributes) {
        open(ELEMENT, name, null);
        open(COMPLEX_TYPE, null, null);
        open(SEQUENCE, null, attributes);
    }

    private void closeSequence() {
        close(SEQUENCE);
        close(COMPLEX_TYPE);
        close(ELEMENT);
    }
    
    private void openChoice(String name) {
        open(ELEMENT, name, null);
        open(COMPLEX_TYPE, null, null);
        open(CHOICE, null, null);
    }  
    
    private void closeChoice() {
        close(CHOICE);
        close(COMPLEX_TYPE);
        close(ELEMENT);
    }

    private void open(String entity, String name, String attr) {
        try {
            String attributes = (attr == null) ? "" : (" " + attr);
            String entity_name = (name == null) ? "" : " name=\"" + name + "\"";
            
            writer.write(indent + "<xs:" + entity + entity_name + attributes + ">\n");
            
            incIndent();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void close(String entity) {
        try {
            decIndent();
            writer.write(indent + "</xs:" + entity + ">\n");
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
