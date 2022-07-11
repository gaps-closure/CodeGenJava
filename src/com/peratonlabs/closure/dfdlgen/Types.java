/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Mar 30, 2022
 */
package com.peratonlabs.closure.dfdlgen;

import clsvis.model.Class_;
import clsvis.model.ParameterizableElement;

public class Types
{
    public static final String NS = "gma:";
    
    public static final String LONG = "xs:long";
    public static final String INT = "xs:int";
    public static final String SHORT = "xs:short";
    public static final String BYTE = "xs:byte";

    public static final String UNSIGNED_LONG = "xs:unsignedLong";
    public static final String UNSIGNED_INT = "xs:unsignedInt";
    public static final String UNSIGNED_SHORT = "xs:unsignedShort";
    public static final String UNSIGNED_BYTE = "xs:unsignedByte";
    
    public static final String DOUBLE = "xs:double";
    public static final String FLOAT = "xs:float";
    
    public static final String BOOLEAN = UNSIGNED_BYTE;     // check
    public static final String CHAR = UNSIGNED_SHORT;  // check
    
    public static final String CLASS_NAME = NS + "GapsClassName";
    public static final String FIELD_NAME = NS + "GapsFieldName";
        
    public static String topLevelClassName(Class_ clazz, boolean decl) {
        return (decl ? "" : NS) + "Class_" + clazz.getFullTypeName();
    }

    public static String fieldName(ParameterizableElement field, boolean prefix) {
        return (prefix ? "Field" : "") + capitalize(field.getSignature());
    }
    
    public static String fieldValue(ParameterizableElement field) {
        return "Value" + capitalize(field.getSignature());
    }
    
    public static String fieldType(ParameterizableElement field) {
        Class<?> type = field.getType();
        if (type.isPrimitive()) {
            return type.getSimpleName();
        }
        else if (type.isArray()) {
            return type.getSimpleName().replaceAll("\\[\\]", "Array");
        }
        else {
            return type.getSimpleName();
        }
    }
    
    public static String objectDesc(ParameterizableElement field, boolean prefix) {
        return (prefix ? "Object" : "") + capitalize(field.getSignature());
    }
    
    public static String arrayDesc(ParameterizableElement field, boolean prefix) {
        return "Desc" + capitalize(fieldType(field));
    }
    
    public static String stringDesc(ParameterizableElement field, boolean prefix) {
        return (prefix ? "string_" : "") + field.getSignature();
    }
    
    public static String arrayTypeName(Class<?> clazz, boolean decl) {
        return (decl ? "" : NS) + capitalize(clazz) + "Array";
    }
    
    public static String classDescDataName(Class_ clazz, boolean desc, boolean namespace) {
        String prefix = desc ? "ClassDesc_" : "ClassData_";
        String ns = (namespace) ? NS : "";
        return ns + prefix + clazz.getFullTypeName();
    }
    
    private static String capitalize(Class<?> compType) {
        return capitalize(compType.toString());
    }
    
    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    public static String getPrimitiveType(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            System.err.println("not a primitive type: " + clazz.getTypeName());
            return "";
        }
        
        if (clazz.equals(long.class))
            return LONG;
        else if (clazz.equals(int.class))
            return INT;
        else if (clazz.equals(short.class))
            return SHORT;
        else if (clazz.equals(byte.class))
            return BYTE;
        else if (clazz.equals(double.class))
            return DOUBLE;
        else if (clazz.equals(float.class))
            return FLOAT;
        else if (clazz.equals(boolean.class))
            return BOOLEAN;
        else if (clazz.equals(char.class))
            return CHAR;
        else {
            System.err.println("unknown type: " + clazz.getTypeName());
            return "";
        }
    }
    
    public static char getPrimitiveTypeCode(String type) {
        switch (type) {
        case "byte":
            return 'B';
        case "char":
            return 'C';
        case "double":
            return 'D';
        case "float":
            return 'F';
        case "int":
            return 'I';
        case "long":
            return 'J';
        case "short":
            return 'S';
        case "boolean":
            return 'Z';
        default:
            throw new RuntimeException("Not primitive type: " + type);
        }
    }
    
    private static int getPrimitiveSize(char typecode) {
        switch (typecode) {
        case 'B':
            return 8;
        case 'C':
            return 16;
        case 'D':
            return 64;
        case 'F':
            return 32;
        case 'I':
            return 32;
        case 'J':
            return 64;
        case 'S':
            return 16;
        case 'Z':
            return 8;
        default:
            throw new RuntimeException("Not primitive type code: " + typecode);
        }
    }
    
    public static int getPrimitiveSize(Class<?> type) {
        return getPrimitiveSize(getPrimitiveTypeCode(type.toString()));
    }
}
