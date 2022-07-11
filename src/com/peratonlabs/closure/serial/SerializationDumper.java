/*
 * This is based on the code at https://github.com/NickstaDB/SerializationDumper
 */
package com.peratonlabs.closure.serial;

import java.io.ObjectStreamConstants;
import java.util.ArrayList;
import java.util.LinkedList;

public class SerializationDumper
{
    private static int INDENT_SPACES = 1;
    private static String INDENT = new String(new char[INDENT_SPACES]).replace("\0", " ");
    
    private final LinkedList<Byte> _data;   // The data being parsed
    private String _indent;	// A string representing the current indentation level for output printing
    private int _handleValue;	// The current handle value
    
    // Array of all class data descriptions to use with TC_REFERENCE classDesc elements
    private final ArrayList<ClassDataDesc> _classDataDescriptions;

    //Flag to control console printing (used when rebuilding to test a rebuilt serialization stream)
    private boolean _enablePrinting = true;
    
    public static void parsePojo(String name, byte[] serialized) {
        SerializationDumper sd = new SerializationDumper();
        for (int i = 0; i < serialized.length; i++) {
            sd._data.add(serialized[i]);
        }
        try {
            System.out.println("generating " + name);
            sd.parseStream(name);
            System.out.println("generated " + name);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public SerializationDumper() {
        this._data = new LinkedList<Byte>();
        this._indent = "  ";
        this._handleValue = 0x7e0000;
        this._classDataDescriptions = new ArrayList<ClassDataDesc>();
    }

    private void print(String s) {
        if (this._enablePrinting == true) {
            System.out.println(this._indent + s);
        }
    }
	
    private void increaseIndent() {
        _indent = _indent + "  ";
    }
    
    private void decreaseIndent() {
        if (_indent.length() < 2) {
            throw new RuntimeException("Error: Illegal indentation decrease.");
        }
        _indent = _indent.substring(0, _indent.length() - 2);
    }
	
    private String byteToHex(byte b) {
        return String.format("%02x", b);
    }

    private byte[] hexStrToBytes(String h) {
        byte[] outBytes = new byte[h.length() / 2];
        for (int i = 0; i < outBytes.length; ++i) {
            outBytes[i] = (byte) ((Character.digit(h.charAt(i * 2), 16) << 4) + Character.digit(h.charAt((i * 2) + 1), 16));
        }
        return outBytes;
    }
	
    private String intToHex(int i) {
        return String.format("%02x",  (i & 0xff000000) >> 24) + 
               String.format(" %02x", (i & 0xff0000) >> 16)  + 
               String.format(" %02x", (i & 0xff00) >> 8) + 
               String.format(" %02x", (i & 0xff));
    }
	
    private int newHandle() {
        int handleValue = _handleValue;

        print("newHandle 0x" + intToHex(handleValue));

        _handleValue++;
        return handleValue;
    }
    
    /*******************
     * Parse the given serialization stream and dump the details out as text.
     ******************/
    private void parseStream(String pathname) throws Exception {
        byte b1, b2;

        // The stream may begin with an RMI packet type byte, print it if so
        if (_data.peek() != (byte)0xac) {
            b1 = _data.pop();
            switch(b1) {
            case (byte)0x50:
                print("RMI Call - 0x50");
            break;
            case (byte)0x51:
                print("RMI ReturnData - 0x51");
            break;
            case (byte)0x52:
                print("RMI Ping - 0x52");
            break;
            case (byte)0x53:
                print("RMI PingAck - 0x53");
            break;
            case (byte)0x54:
                print("RMI DgcAck - 0x54");
            break;
            default:
                print("Unknown RMI packet type - 0x" + byteToHex(b1));
                break;
            }
        }

        // Magic number, print and validate
        b1 = _data.pop();
        b2 = _data.pop();
        print("STREAM_MAGIC - 0x" + byteToHex(b1) + " " + byteToHex(b2));
        if (b1 != (byte)0xac || b2 != (byte)0xed) {
            print("Invalid STREAM_MAGIC, should be 0xac ed");
            return;
        }

        // Serialization version
        b1 = _data.pop();
        b2 = _data.pop();
        print("STREAM_VERSION - 0x" + byteToHex(b1) + " " + byteToHex(b2));
        if (b1 != (byte)0x00 || b2 != (byte)0x05) {
            print("Invalid STREAM_VERSION, should be 0x00 05");
        }

        // Remainder of the stream consists of one or more 'content' elements
        print("Contents");
        increaseIndent();
        while (_data.size() > 0) {
            readContentElement();
        }
        decreaseIndent();
    }

    /*******************
     * Could be any of:
     *	TC_OBJECT		(0x73)
     *	TC_CLASS		(0x76)
     *	TC_ARRAY		(0x75)
     *	TC_STRING		(0x74)
     *	TC_LONGSTRING		(0x7c)
     *	TC_ENUM			(0x7e)
     *	TC_CLASSDESC		(0x72)
     *	TC_PROXYCLASSDESC	(0x7d)
     *	TC_REFERENCE		(0x71)
     *	TC_NULL			(0x70)
     *	TC_EXCEPTION		(0x7b)
     *	TC_RESET		(0x79)
     *	TC_BLOCKDATA		(0x77)
     *	TC_BLOCKDATALONG	(0x7a)
     ******************/
    private void readContentElement() {
        // Peek the next byte and delegate to the appropriate method
        switch (_data.peek()) {
        case ObjectStreamConstants.TC_OBJECT:
            readNewObject();
            break;

        case ObjectStreamConstants.TC_CLASS:
            readNewClass();
            break;

        case ObjectStreamConstants.TC_ARRAY:
            readNewArray();
            break;

        case ObjectStreamConstants.TC_STRING: 
        case ObjectStreamConstants.TC_LONGSTRING:
            readNewString();
            break;

        case ObjectStreamConstants.TC_ENUM:
            readNewEnum();
            break;

        case ObjectStreamConstants.TC_CLASSDESC:
        case ObjectStreamConstants.TC_PROXYCLASSDESC:
            readNewClassDesc();
            break;

        case ObjectStreamConstants.TC_REFERENCE:
            readPrevObject();
            break;

        case ObjectStreamConstants.TC_NULL:
            readNullReference();
            break;

        // case (byte)0x7b: //TC_EXCEPTION
        // readException();
        // break;

        // case (byte)0x79: //TC_RESET
        // handleReset();
        // break;

        case ObjectStreamConstants.TC_BLOCKDATA:
            readBlockData();
            break;

        case ObjectStreamConstants.TC_BLOCKDATALONG:
            readLongBlockData();
            break;

        default:
            print("Invalid content element type 0x" + byteToHex(_data.peek()));
            throw new RuntimeException("Error: Illegal content element type.");
        }
    }
	
    /*******************
     * TC_ENUM classDesc newHandle enumConstantName
     ******************/
    private void readNewEnum() {
        byte b1;

        // TC_ENUM
        b1 = _data.pop();
        print("TC_ENUM - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x7e) {
            throw new RuntimeException("Error: Illegal value for TC_ENUM (should be 0x7e)");
        }
        
        increaseIndent();
        readClassDesc();
        newHandle();
        readNewString();
        decreaseIndent();
    }
	
    /*******************
     * TC_OBJECT classDesc newHandle classdata[]
     ******************/
    private void readNewObject() {
        ClassDataDesc cdd;
        byte b1;

        b1 = _data.pop();
        print("TC_OBJECT - 0x" + byteToHex(b1));
        if (b1 != ObjectStreamConstants.TC_OBJECT) {
            throw new RuntimeException("Error: Illegal value for TC_OBJECT (should be 0x73)");
        }
        
        increaseIndent();
        cdd = readClassDesc();
        newHandle();
        readClassData(cdd); // Read the class data based on the class data description - 
                            // TODO This needs to check if cdd is null before reading anything
        decreaseIndent();
    }
	
    /*******************
     * Could be:
     *	TC_CLASSDESC		(0x72)
     *	TC_PROXYCLASSDESC	(0x7d)
     *	TC_NULL			(0x70)
     *	TC_REFERENCE		(0x71)
     ******************/
    private ClassDataDesc readClassDesc() {
        int refHandle;
        
        switch(_data.peek()) {
        case ObjectStreamConstants.TC_CLASSDESC:
            return readNewClassDesc();
        case ObjectStreamConstants.TC_PROXYCLASSDESC:
            return readNewClassDesc();
        case ObjectStreamConstants.TC_NULL:
            readNullReference();
            return null;
        case ObjectStreamConstants.TC_REFERENCE:
            // Look up a referenced class data description object and return it
            refHandle = readPrevObject();
            //Iterate over all class data descriptions
            for (ClassDataDesc cdd: _classDataDescriptions) {
                //Iterate over all classes in this class data description
                for (int classIndex = 0; classIndex < cdd.getClassCount(); ++classIndex) {
                    if (cdd.getClassDetails(classIndex).getHandle() == refHandle) {
                        //Generate a ClassDataDesc starting from the given index and return it
                        return cdd.buildClassDataDescFromIndex(classIndex);
                    }
                }
            }
            throw new RuntimeException("Error: Invalid classDesc reference (0x" + intToHex(refHandle) + ")");
        default:
            print("Invalid classDesc type 0x" + byteToHex(_data.peek()));
            throw new RuntimeException("Error illegal classDesc type.");
        }
    }
	
    /*******************
     * Could be: TC_CLASSDESC (0x72) TC_PROXYCLASSDESC (0x7d)
     ******************/
    private ClassDataDesc readNewClassDesc() {
        ClassDataDesc cdd;

        switch (_data.peek()) {
        case ObjectStreamConstants.TC_CLASSDESC:
            cdd = readTC_CLASSDESC();
            _classDataDescriptions.add(cdd);
            return cdd;
        case ObjectStreamConstants.TC_PROXYCLASSDESC:
            cdd = readTC_PROXYCLASSDESC();
            _classDataDescriptions.add(cdd);
            return cdd;
        default:
            print("Invalid newClassDesc type 0x" + byteToHex(_data.peek()));
            throw new RuntimeException("Error illegal newClassDesc type.");
        }
    }
	
    /*******************
     * TC_CLASSDESC className serialVersionUID newHandle classDescInfo
     ******************/
    private ClassDataDesc readTC_CLASSDESC() {
        ClassDataDesc cdd = new ClassDataDesc();
        byte b1 = _data.pop();
        print("TC_CLASSDESC - 0x" + byteToHex(b1));
        if (b1 != ObjectStreamConstants.TC_CLASSDESC) {
            throw new RuntimeException("Error: Illegal value for TC_CLASSDESC (should be 0x72)");
        }
        
        increaseIndent();
        print("className");
        increaseIndent();
        
        cdd.addClass(readUtf()); // Add the class name to the class data description
        decreaseIndent();

        print("serialVersionUID - 0x" + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()) + 
                   " " + byteToHex(_data.pop()));

        cdd.setLastClassHandle(newHandle()); // Set the reference handle for the most recently added class
        readClassDescInfo(cdd); // Read class desc info, add the super class description to the ClassDataDesc if one is found
        decreaseIndent();

        return cdd;
    }
	
    /*******************
     * TC_PROXYCLASSDESC newHandle proxyClassDescInfo
     ******************/
    private ClassDataDesc readTC_PROXYCLASSDESC() {
        ClassDataDesc cdd = new ClassDataDesc();
        byte b1;

        // TC_PROXYCLASSDESC
        b1 = _data.pop();
        print("TC_PROXYCLASSDESC - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x7d) {
            throw new RuntimeException("Error: Illegal value for TC_PROXYCLASSDESC (should be 0x7d)");
        }
        increaseIndent();

        // Create the new class descriptor
        cdd.addClass("<Dynamic Proxy Class>");
        cdd.setLastClassHandle(newHandle()); // Set the reference handle for the most recently added class

        readProxyClassDescInfo(cdd); // Read proxy class desc info, add the super class description to the ClassDataDesc if one is found
        decreaseIndent();

        return cdd;
    }
	
    /*******************
     * classDescFlags fields classAnnotation superClassDesc
     ******************/
    private void readClassDescInfo(ClassDataDesc cdd) {
        String classDescFlags = "";
        byte b1;

        // classDescFlags
        b1 = _data.pop();
        if ((b1 & ObjectStreamConstants.SC_WRITE_METHOD) == ObjectStreamConstants.SC_WRITE_METHOD) {
            classDescFlags += "SC_WRITE_METHOD | ";
        }
        if ((b1 & ObjectStreamConstants.SC_SERIALIZABLE) == ObjectStreamConstants.SC_SERIALIZABLE) {
            classDescFlags += "SC_SERIALIZABLE | ";
        }
        if ((b1 & ObjectStreamConstants.SC_EXTERNALIZABLE) == ObjectStreamConstants.SC_EXTERNALIZABLE) {
            classDescFlags += "SC_EXTERNALIZABLE | ";
        }
        if ((b1 & ObjectStreamConstants.SC_BLOCK_DATA) == ObjectStreamConstants.SC_BLOCK_DATA) {
            classDescFlags += "SC_BLOCKDATA | ";
        }
        if (classDescFlags.length() > 0) {
            classDescFlags = classDescFlags.substring(0, classDescFlags.length() - 3);
        }
        print("classDescInfo - 0x" + byteToHex(b1) + " - " + classDescFlags);

        cdd.setLastClassDescFlags(b1); // Set the classDescFlags for the most recently added class

        // Validate classDescFlags
        if ((b1 & ObjectStreamConstants.SC_SERIALIZABLE) == ObjectStreamConstants.SC_SERIALIZABLE) {
            if ((b1 & ObjectStreamConstants.SC_EXTERNALIZABLE) == ObjectStreamConstants.SC_EXTERNALIZABLE) {
                throw new RuntimeException(
                        "Error: Illegal classDescFlags, SC_SERIALIZABLE is not compatible with SC_EXTERNALIZABLE.");
            }
            if ((b1 & ObjectStreamConstants.SC_BLOCK_DATA) == ObjectStreamConstants.SC_BLOCK_DATA) {
                throw new RuntimeException("Error: Illegal classDescFlags, SC_SERIALIZABLE is not compatible with SC_BLOCKDATA.");
            }
        }
        else if ((b1 & ObjectStreamConstants.SC_EXTERNALIZABLE) == ObjectStreamConstants.SC_EXTERNALIZABLE) {
            if ((b1 & ObjectStreamConstants.SC_WRITE_METHOD) == ObjectStreamConstants.SC_WRITE_METHOD) {
                throw new RuntimeException(
                        "Error: Illegal classDescFlags, SC_EXTERNALIZABLE is not compatible with SC_WRITE_METHOD.");
            }
        }
        else if (b1 != 0x00) {
            throw new RuntimeException("Error: Illegal classDescFlags, must include either SC_SERIALIZABLE or SC_EXTERNALIZABLE.");
        }

        readFields(cdd); // Read field descriptions and add them to the ClassDataDesc
        readClassAnnotation();
        cdd.addSuperClassDesc(readSuperClassDesc()); // Read the super class description and add it to the ClassDataDesc
    }
	
    /*******************
     * (int)count (utf)proxyInterfaceName[count] classAnnotation superClassDesc
     ******************/
    private void readProxyClassDescInfo(ClassDataDesc cdd) {
        byte b1, b2, b3, b4;
        int count;

        // count
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        count = (int) (((b1 << 24) & 0xff000000) + 
                       ((b2 << 16) & 0xff0000) + 
                       ((b3 << 8)  & 0xff00) + 
                       ((b4)       & 0xff));
        print("Interface count - " + count + 
                   " - 0x" + byteToHex(b1) + 
                   " " + byteToHex(b2) + 
                   " " + byteToHex(b3) + 
                   " " + byteToHex(b4));

        // proxyInterfaceName[count]
        print("proxyInterfaceNames");
        increaseIndent();
        for (int i = 0; i < count; ++i) {
            print(i + ":");
            increaseIndent();
            readUtf();
            decreaseIndent();
        }
        decreaseIndent();
        readClassAnnotation();

        cdd.addSuperClassDesc(readSuperClassDesc()); // Read the super class description and add it to the ClassDataDesc
    }
	
    /*******************
     * Could be either: contents TC_ENDBLOCKDATA (0x78)
     ******************/
    private void readClassAnnotation() {
        print("classAnnotations");
        increaseIndent();

        // Loop until we have a TC_ENDBLOCKDATA
        while (_data.peek() != ObjectStreamConstants.TC_ENDBLOCKDATA) {
            readContentElement();
        }

        // Pop and print the TC_ENDBLOCKDATA element
        _data.pop();
        print("TC_ENDBLOCKDATA - 0x78");
        decreaseIndent();
    }
	
    /*******************
     * classDesc
     ******************/
    private ClassDataDesc readSuperClassDesc() {
        ClassDataDesc cdd;

        print("superClassDesc");
        increaseIndent();
        cdd = readClassDesc();
        decreaseIndent();

        return cdd;
    }
	
    /*******************
     * (short)count fieldDesc[count]
     ******************/
    private void readFields(ClassDataDesc cdd) {
        byte b1, b2;
        short count;

        b1 = _data.pop();
        b2 = _data.pop();
        count = (short) (((b1 << 8) & 0xff00) + (b2 & 0xff));
        print("fieldCount - " + count); // + " - 0x" + byteToHex(b1) + " " + byteToHex(b2));
        
        if (count > 0) {
            print("Fields");
            increaseIndent();
            for (int i = 0; i < count; ++i) {
                print(i + ":");
                increaseIndent();
                readFieldDesc(cdd);
                decreaseIndent();
            }
            decreaseIndent();
        }
    }
	
    /*******************
     * Read a fieldDesc from the stream.
     * 
     * Could be either: prim_typecode fieldName obj_typecode fieldName
     * className1
     ******************/
    private void readFieldDesc(ClassDataDesc cdd) {
        byte b1;

        // prim_typecode/obj_typecode
        b1 = _data.pop();
        cdd.addFieldToLastClass(b1); // Add a field of the type in b1 to the
                                     // most recently added class
        switch ((char) b1) {
        case 'B':
            print("Byte - B");
            break;
        case 'C':
            print("Char - C");
            break;
        case 'D':
            print("Double - D");
            break;
        case 'F':
            print("Float - F");
            break;
        case 'I':
            print("Int - I");
            break;
        case 'J':
            print("Long - L");
            break;
        case 'S':
            print("Short - S");
            break;
        case 'Z':
            print("Boolean - Z");
            break;
        case '[':
            print("Array - [");
            break;
        case 'L':
            print("Object - L");
            break;
        default:
            // Unknown field type code
            throw new RuntimeException("Error: Illegal field type code ('" + (char) b1 + "', 0x" + byteToHex(b1) + ")");
        }
        print("fieldName");
        increaseIndent();
        cdd.setLastFieldName(readUtf()); // Set the name of the most recently added field
        decreaseIndent();

        // className1 (if obj_typecode)
        if ((char) b1 == '[' || (char) b1 == 'L') {
            print("className1");
            increaseIndent();
            byte b = _data.peek();
            cdd.setLastFieldClassName1(readNewString()); // Set the className1 of the most recently added field
//            if (b != ObjectStreamConstants.TC_REFERENCE)
//                writeSimpleElement("className1", "gma:GapsClassName");
            decreaseIndent();
        }
    }

    /*******************
     * Read classdata from the stream.
     * 
     * Consists of data for each class making up the object starting with the
     * most super class first. The length and type of data depends on the
     * classDescFlags and field descriptions.
     ******************/
    private void readClassData(ClassDataDesc cdd) {
        ClassDetails cd;
        int classIndex;

        print("classdata");
        increaseIndent();

        // Print class data if there is any
        if (cdd != null) {
            // Check for SC_EXTERNALIZABLE flags in any of the classes
            for (classIndex = 0; classIndex < cdd.getClassCount(); ++classIndex) {
                if (cdd.getClassDetails(classIndex).isSC_EXTERNALIZABLE() == true) {
                    print("externalContents");
                    increaseIndent();
                    print("Unable to parse externalContents as the format is specific to the implementation class.");
                    throw new RuntimeException("Error: Unable to parse externalContents element.");
                }
            }

            // Iterate backwards through the classes as we need to deal with the
            // most super (last added) class first
            for (classIndex = cdd.getClassCount() - 1; classIndex >= 0; --classIndex) {
                cd = cdd.getClassDetails(classIndex);

                print(cd.getClassName());
                increaseIndent();

                // Read the field values if the class is SC_SERIALIZABLE
                if (cd.isSC_SERIALIZABLE()) {
                    print("values");
                    increaseIndent();
                    for (ClassField cf : cd.getFields()) {
                        readClassDataField(cf);
                    }
                    decreaseIndent();
                }

                // Read object annotations if the right flags are set
                if ((cd.isSC_SERIALIZABLE() && cd.isSC_WRITE_METHOD()) || (cd.isSC_EXTERNALIZABLE() && cd.isSC_BLOCKDATA())) {
                    // Start the object annotations section and indent
                    print("objectAnnotation");
                    increaseIndent();

                    // Loop until we have a TC_ENDBLOCKDATA
                    while (_data.peek() != (byte) 0x78) {
                        // Read a content element
                        readContentElement();
                    }

                    // Pop and print the TC_ENDBLOCKDATA element
                    _data.pop();
                    print("TC_ENDBLOCKDATA - 0x78");
                    decreaseIndent();
                }
                decreaseIndent();
            }
        }
        else {
            print("N/A");
        }
        decreaseIndent();
    }

    /*******************
     * The data type depends on the given field description.
     * @param f  A description of the field data to read.
     ******************/
    private void readClassDataField(ClassField cf) {
        print(cf.getName());
        increaseIndent();
        readFieldValue(cf.getTypeCode());
        decreaseIndent();
    }

    private void readFieldValue(byte typeCode) {
        switch ((char) typeCode) {
        case 'B':
            readByteField();
            break;
        case 'C':
            readCharField();
            break;
        case 'D':
            readDoubleField();
            break;
        case 'F':
            readFloatField();
            break;
        case 'I':
            readIntField();
            break;
        case 'J':
            readLongField();
            break;
        case 'S':
            readShortField();
            break;
        case 'Z':
            readBooleanField();
            break;
        case '[':
            readArrayField();
            break;
        case 'L':
            readObjectField();
            break;
        default: // Unknown field type
            throw new RuntimeException("Error: Illegal field type code ('" + typeCode + "', 0x" + byteToHex((byte) typeCode) + ")");
        }
    }

    /*******************
     * TC_ARRAY classDesc newHandle (int)size values[size]
     ******************/
    private void readNewArray() {
        ClassDataDesc cdd;
        ClassDetails cd;
        byte b1, b2, b3, b4;
        int size;

        b1 = _data.pop();
        print("TC_ARRAY - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x75) {
            throw new RuntimeException("Error: Illegal value for TC_ARRAY (should be 0x75)");
        }
        increaseIndent();
        
        cdd = readClassDesc(); // Read the class data description to enable
                                    // array elements to be read
        if (cdd.getClassCount() != 1) {
            throw new RuntimeException("Error: Array class description made up of more than one class.");
        }
        cd = cdd.getClassDetails(0);
        if (cd.getClassName().charAt(0) != '[') {
            throw new RuntimeException("Error: Array class name does not begin with '['.");
        }

        newHandle();

        // Array size
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        size = (int) (((b1 << 24) & 0xff000000) + 
                      ((b2 << 16) & 0xff0000) + 
                      ((b3 << 8) & 0xff00) + 
                      ((b4) & 0xff));
        print("Array size - " + size + 
                   " - 0x" + byteToHex(b1) + 
                   " " + byteToHex(b2) + 
                   " " + byteToHex(b3) + 
                   " " + byteToHex(b4));
        
        // Array data
        print("Values");
        increaseIndent();
        for (int i = 0; i < size; ++i) {
            print("Index " + i + ":");
            increaseIndent();

            // Read the field values based on the classDesc read above
            readFieldValue((byte) cd.getClassName().charAt(1));
            decreaseIndent();
        }
        decreaseIndent();
        decreaseIndent();
    }

    /*******************
     * TC_CLASS classDesc newHandle
     ******************/
    private void readNewClass() {
        byte b1;

        b1 = _data.pop();
        print("TC_CLASS - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x76) {
            throw new RuntimeException("Error: Illegal value for TC_CLASS (should be 0x76)");
        }
        increaseIndent();
        readClassDesc();
        decreaseIndent();
        newHandle();
    }

    /*******************
     * TC_REFERENCE (int)handle
     ******************/
    private int readPrevObject() {
        byte b1, b2, b3, b4;
        int handle;

        b1 = _data.pop();
        print("TC_REFERENCE - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x71) {
            throw new RuntimeException("Error: Illegal value for TC_REFERENCE (should be 0x71)");
        }
        increaseIndent();

        // Reference handle
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        handle = (int) (((b1 << 24) & 0xff000000) + 
                        ((b2 << 16) & 0xff0000) + 
                        ((b3 << 8) & 0xff00) + 
                        ((b4) & 0xff));
        print("Handle - " + handle);

        decreaseIndent();
        return handle;
    }

    private void readNullReference() {
        byte b1;

        b1 = _data.pop();
        print("TC_NULL - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x70) {
            throw new RuntimeException("Error: Illegal value for TC_NULL (should be 0x70)");
        }
    }

    /*******************
     * TC_BLOCKDATA (unsigned byte)size contents
     ******************/
    private void readBlockData() {
        String contents = "";
        int len;
        byte b1;

        b1 = _data.pop();
        print("TC_BLOCKDATA - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x77) {
            throw new RuntimeException("Error: Illegal value for TC_BLOCKDATA (should be 0x77)");
        }
        increaseIndent();

        // size
        len = _data.pop() & 0xFF;
        print("Length - " + len + " - 0x" + byteToHex((byte) (len & 0xff)));

        // contents
        for (int i = 0; i < len; ++i) {
            contents += byteToHex(_data.pop());
        }
        print("Contents - 0x" + contents);

        decreaseIndent();
    }

    /*******************
     * TC_BLOCKDATALONG (int)size contents
     ******************/
    private void readLongBlockData() {
        String contents = "";
        long len;
        byte b1, b2, b3, b4;

        b1 = _data.pop();
        print("TC_BLOCKDATALONG - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x7a) {
            throw new RuntimeException("Error: Illegal value for TC_BLOCKDATA (should be 0x77)");
        }
        increaseIndent();

        // size
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        len = (int) (((b1 << 24) & 0xff000000) + 
                     ((b2 << 16) & 0xff0000) + 
                     ((b3 << 8) & 0xff00) + 
                     ((b4) & 0xff));
        print("Length - " + len);

        for (long l = 0; l < len; ++l) {
            contents += byteToHex(_data.pop());
        }
        print("Contents - 0x" + contents);
        decreaseIndent();
    }

    /*******************
     * Could be: TC_STRING (0x74) TC_LONGSTRING (0x7c)
     ******************/
    private String readNewString() {
        int handle;

        // Peek the type and delegate to the appropriate method
        switch (_data.peek()) {
        case ObjectStreamConstants.TC_STRING:
            return readTC_STRING(true);

        case ObjectStreamConstants.TC_LONGSTRING:
            return readTC_LONGSTRING();

        case ObjectStreamConstants.TC_REFERENCE:
            readPrevObject();
            return "[TC_REF]";

        default:
            print("Invalid newString type 0x" + byteToHex(_data.peek()));
            throw new RuntimeException("Error illegal newString type.");
        }
    }

    /*******************
     * TC_STRING newHandle utf
     ******************/
    private String readTC_STRING(boolean gen) {
        String val;
        byte b1;

        b1 = _data.pop();
        print("TC_STRING - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x74) {
            throw new RuntimeException("Error: Illegal value for TC_STRING (should be 0x74)");
        }
        
        increaseIndent();
        newHandle();
        val = readUtf();
        decreaseIndent();
        
        return val;
    }

    /*******************
     * TC_LONGSTRING newHandle long-utf
     ******************/
    private String readTC_LONGSTRING() {
        String val;
        byte b1;

        b1 = _data.pop();
        print("TC_LONGSTRING - 0x" + byteToHex(b1));
        if (b1 != (byte) 0x7c) {
            throw new RuntimeException("Error: Illegal value for TC_LONGSTRING (should be 0x7c)");
        }

        increaseIndent();
        newHandle();
        val = readLongUtf();
        decreaseIndent();

        return val;
    }

    /*******************
     * (short)length contents
     ******************/
    private String readUtf() {
        String content = "", hex = "";
        byte b1, b2;
        int len;

        // length
        b1 = _data.pop();
        b2 = _data.pop();
        len = (int) (((b1 << 8) & 0xff00) + (b2 & 0xff));
        print("Length: " + len);
        // writeSimpleElement("Length", "gma:gapsuint16");
        
        // Contents
        for (int i = 0; i < len; ++i) {
            b1 = _data.pop();
            content += (char) b1;
            hex += byteToHex(b1);
        }
        print("Value: " + content); // + " - 0x" + hex);
        // writeSimpleElement("Value", "xs:string");

        return content;
    }

    /*******************
     * (long)length contents
     ******************/
    private String readLongUtf() {
        String content = "", hex = "";
        byte b1, b2, b3, b4, b5, b6, b7, b8;
        long len;

        // Length
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        b5 = _data.pop();
        b6 = _data.pop();
        b7 = _data.pop();
        b8 = _data.pop();
        len = (long) (((b1 << 56) & 0xff00000000000000L) + 
                      ((b2 << 48) & 0xff000000000000L) + 
                      ((b3 << 40) & 0xff0000000000L) + 
                      ((b4 << 32) & 0xff00000000L) + 
                      ((b5 << 24) & 0xff000000) + 
                      ((b6 << 16) & 0xff0000) + 
                      ((b7 << 8) & 0xff00) + 
                       (b8 & 0xff));
        print("Length - " + len);

        // Contents
        for (long l = 0; l < len; ++l) {
            b1 = _data.pop();
            content += (char) b1;
            hex += byteToHex(b1);
        }
        print("Value - " + content + " - 0x" + hex);
        return content;
    }

    private void readByteField() {
        byte b1 = _data.pop();
        if (((int) b1) >= 0x20 && ((int) b1) <= 0x7e) {
            print("(byte)" + b1 + " (ASCII: " + ((char) b1) + ")");
        }
        else {
            print("(byte)" + b1);
        }
    }

    private void readCharField() {
        byte b1 = _data.pop();
        byte b2 = _data.pop();
        char c1 = (char) (((b1 << 8) & 0xff00) + b2 & 0xff);
        print("(char)" + (char) c1);
    }

    private void readDoubleField() {
        byte b1, b2, b3, b4, b5, b6, b7, b8;
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        b5 = _data.pop();
        b6 = _data.pop();
        b7 = _data.pop();
        b8 = _data.pop();
        print("(double)"
                + (double) (((b1 << 56) & 0xff00000000000000L) + 
                            ((b2 << 48) & 0xff000000000000L) + 
                            ((b3 << 40) & 0xff0000000000L) + 
                            ((b4 << 32) & 0xff00000000L) + 
                            ((b5 << 24) & 0xff000000) + 
                            ((b6 << 16) & 0xff0000) + 
                            ((b7 << 8) & 0xff00) + 
                             (b8 & 0xff)));
    }

    private void readFloatField() {
        byte b1, b2, b3, b4;
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        print("(float)" + (float) (((b1 << 24) & 0xff000000) + 
                                   ((b2 << 16) & 0xff0000) + 
                                   ((b3 << 8) & 0xff00) + 
                                    (b4 & 0xff)));
    }

    private void readIntField() {
        byte b1, b2, b3, b4;
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        print("(int) " + (int) (((b1 << 24) & 0xff000000) + 
                                ((b2 << 16) & 0xff0000) + 
                                ((b3 << 8) & 0xff00) + 
                                 (b4 & 0xff)));
    }

    private void readLongField() {
        byte b1, b2, b3, b4, b5, b6, b7, b8;
        b1 = _data.pop();
        b2 = _data.pop();
        b3 = _data.pop();
        b4 = _data.pop();
        b5 = _data.pop();
        b6 = _data.pop();
        b7 = _data.pop();
        b8 = _data.pop();
        print("(long)"
                + (long) (((b1 << 56) & 0xff00000000000000L) + 
                          ((b2 << 48) & 0xff000000000000L) + 
                          ((b3 << 40) & 0xff0000000000L) + 
                          ((b4 << 32) & 0xff00000000L) + 
                          ((b5 << 24) & 0xff000000) + 
                          ((b6 << 16) & 0xff0000) + 
                          ((b7 << 8) & 0xff00) +
                           (b8 & 0xff)));
    }

    private void readShortField() {
        byte b1, b2;
        b1 = _data.pop();
        b2 = _data.pop();
        print("(short)" + (short) (((b1 << 8) & 0xff00) + 
                                    (b2 & 0xff)));
    }

    private void readBooleanField() {
        byte b1 = _data.pop();
        print("(boolean)" + (b1 == 0 ? "false" : "true"));
    }

    private void readArrayField() {
        print("(array)");
        increaseIndent();

        switch (_data.peek()) {
        case ObjectStreamConstants.TC_NULL:
            readNullReference();
            break;
        case ObjectStreamConstants.TC_ARRAY:
            readNewArray();
            break;
        case ObjectStreamConstants.TC_REFERENCE:
            readPrevObject();
            break;
        default: // Unknown
            throw new RuntimeException("Error: Unexpected array field value type (0x" + byteToHex(_data.peek()));
        }
        decreaseIndent();
    }

    private void readObjectField() {
        print("(object)");
        increaseIndent();

        byte b = _data.peek();
        
        switch (_data.peek()) {
        case ObjectStreamConstants.TC_OBJECT:
            readNewObject();
            break;
        case ObjectStreamConstants.TC_REFERENCE:
            readPrevObject();
            break;
        case ObjectStreamConstants.TC_NULL:
            readNullReference();
            break;
        case ObjectStreamConstants.TC_STRING:
            readTC_STRING(true);
            break;
        case ObjectStreamConstants.TC_CLASS:
            readNewClass();
            break;
        case ObjectStreamConstants.TC_ARRAY:
            readNewArray();
            break;
        case ObjectStreamConstants.TC_ENUM:
            readNewEnum();
            break;
        default: // Unknown/unsupported
            throw new RuntimeException("Error: Unexpected identifier for object field value 0x" + byteToHex(_data.peek()));
        }
        decreaseIndent();
    }
}
