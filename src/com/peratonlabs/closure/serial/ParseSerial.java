/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Sep 7, 2021
 */
package com.peratonlabs.closure.serial;

import java.beans.ExceptionListener;
import java.beans.XMLEncoder;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.peratonlabs.closure.codegen.rpc.coder.Serializer;
import com.peratonlabs.closure.codegen.rpc.serialization.RPCObject;

import test.dfdl.One;
import test.dfdl.Three;
import test.dfdl.Two_1;
import test.dfdl.Two_2;

public class ParseSerial
{
    private static boolean gaps = false;
    private static String className;
    
    public static void main(String[] args) {
        Object[] xargs = { "ABC", 1 };
        Class<?>[] argTypes = new  Class<?>[] { String.class, Integer.class };

        switch(args[0]) {
        case "One":
            write(new One());
            break;
        case "Two_1":
            write(new Two_1());
            break;
        case "Two_2":
            write(new Two_2());
            break;
        case "Three":
            write(new Three());
            break;
        case "String":
            write("This is a string.");
            break;
        case "Array":
            write(new int[] {1, 2});
            break;
        case "Class":
            write(int.class);
            break;
        default:
            Object[] all = {new One(), new Two_1(), new Two_2(), new Three() };
            for (Object obj : all)
                write(obj);
        }
    }
    
    private static void write(Object obj) {
        className = obj.getClass().getSimpleName();
        byte[] payload = Serializer.serialize(obj);
        SerializationDumper.parsePojo("c:/tmp/xxx.xsd", payload);
        
        writeBinary(payload);
    }
    
    public static void writeBinary(byte[] serialized) {
        String outputFile = "y:/gaps/dfdl/" + className + ".bin";

        try {
            byte[] header = new byte[8];
            byte[] trailer = new byte[16];
            
            OutputStream outputStream = new FileOutputStream(outputFile);

            if (gaps)
                outputStream.write(header);
            outputStream.write(serialized);
            if (gaps)
                outputStream.write(trailer);
            outputStream.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void serializeToXML(RPCObject settings) throws IOException {
        FileOutputStream fos = new FileOutputStream("c:/tmp/settings.xml");
        XMLEncoder encoder = new XMLEncoder(fos);
        encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    e.printStackTrace();
                    System.out.println("Exception! :"+e.toString());
                }
        });
        encoder.writeObject(settings);
        encoder.close();
        fos.close();
    }
}
