/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Feb 18, 2022
 */
package com.peratonlabs.closure.codegen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.peratonlabs.closure.codegen.rpc.coder.Serializer;
import com.peratonlabs.closure.codegen.rpc.serialization.RPCConstructor;
import com.peratonlabs.closure.codegen.rpc.serialization.RPCField;
import com.peratonlabs.closure.codegen.rpc.serialization.RPCMethod;
import com.peratonlabs.closure.codegen.rpc.serialization.RPCObject;
import com.peratonlabs.closure.serial.SerializationDumper;

public class CodeGenDFDL
{
    public void genDFDLSchema(String fqcn) {
        try {
            Class<?> clazz = Class.forName(fqcn);

            genDFDLSchemaConstructors(clazz, fqcn);
            genDFDLSchemaFields(clazz, fqcn);
            genDFDLSchemaMethods(clazz, fqcn);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    
    private void genDFDLSchemaConstructors(Class<?> clazz, String fqcn) {
        Constructor<?>[] ctors = clazz.getDeclaredConstructors();

        for (Constructor<?> c : ctors) {
            Class<?>[] argTypes = c.getParameterTypes();

            Object[] args = new Object[argTypes.length];
            String name = fqcn;
            for (int i = 0; i < argTypes.length; i++) {
                args[i] = getDummyValue(argTypes[i]);
                name += "_" + argTypes[i];
            }
            RPCObject obj = new RPCConstructor(11111, fqcn, argTypes, args, 0);

            byte[] payload = Serializer.serialize(obj);
            SerializationDumper.parsePojo("" + name + ".xsd", payload);
        }
    }
    
    private void genDFDLSchemaMethods(Class<?> clazz, String fqcn) {
        Method[] methods = clazz.getDeclaredMethods();

        for (Method m : methods) {
            Class<?>[] argTypes = m.getParameterTypes();
            Object[] args = new Object[argTypes.length];
            String name = fqcn + "." + m.getName();
            for (int i = 0; i < argTypes.length; i++) {
                args[i] = getDummyValue(argTypes[i]);
                name += "_" + argTypes[i];
            }
            RPCObject obj = new RPCMethod(11111, fqcn, m.getName(), argTypes, args);

            byte[] payload = Serializer.serialize(obj);
            SerializationDumper.parsePojo("" + name + ".xsd", payload);
        }
    }
    
    private void genDFDLSchemaFields(Class<?> clazz, String fqcn) {
        Field[] fields = clazz.getDeclaredFields();
        
        for(Field f : fields){
           Class<?> fieldType = f.getType();
           Object value = getDummyValue(fieldType);
           
           RPCObject obj = new RPCField(11111, fqcn, f.getName(), value, 222);
           byte[] payload = Serializer.serialize(obj);
           SerializationDumper.parsePojo("" + fqcn + "." + f.getName() + ".xsd", payload);
        }
    }

    Object getDummyValue(Class<?> clazz) {
        Object value;
        if (clazz == int.class) { 
            value = 1;
        }
        else if (clazz == byte.class) {
            value = (byte)1;
        }
        else if (clazz == short.class) {
            value = (short)1;
        }
        else if (clazz == long.class) {
            value = 1L;
        }
        else if (clazz == double.class){
            value = 1.0D;
        }
        else if (clazz == float.class){
            value = 1.0F;
        }
        else if (clazz == boolean.class) {
            value = false;
        }
        else if (clazz == Object.class){
            value = new Object();
        }
        else {
            value = new Object();
        }
        return value;
    }
}
