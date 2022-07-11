/**
 * 
 */
package com.peratonlabs.closure.codegen.rpc.coder;

import java.util.ArrayList;

/**
 * @author tchen
 *
 */
public class Coder
{
    /**
     * Get the the bits, up to 32-bits, stored in a byte array, 
     * starting at the 'bit_index' bit of the 'index' element, 
     * The bytes are in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param data - the byte array
     * @param byte_offset - starting index to data[]
     * @param bit_offset - bit offset into data[index]. MSB at 7. (76543210)
     * @param len - number of bits
     * @throws FailToDecodeException 
     */
    public static int getInt(byte data[], int byte_offset, int bit_offset, int len) throws FailToDecodeException {
        if (data == null || byte_offset >= data.length || 
            bit_offset > 7 || bit_offset < 0 || 
            len > 32 || len < 0) {
            throw new FailToDecodeException("data=" + data + ", data.length=" + data.length + 
                                            ", byte_offset=" + byte_offset +
                                            ", bit_index=" + bit_offset + ", len=" + len);
        }
        
        int value = 0;
        int tmp = 0;
        int lshift = 0;                
        while (len > 0) {
            if (byte_offset < 0)
                throw new FailToDecodeException("bad byte_offset: " + byte_offset);
            int blen = 8 - bit_offset;
            if (blen > len)
                blen = len;
        
            tmp = getBits(data[byte_offset], bit_offset, blen);
            value |= (tmp << lshift); 
        
            len -= blen;
            bit_offset = 0;
            lshift += blen;
            byte_offset--;
        }

        return value;
    }
    
    /**
     * Get the bits, up to 32, stored in a byte array, starting at the 'offset'. 
     * The bytes are in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param data - the byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToDecodeException 
     */
    public static int getInt(byte data[], Offset offset, int len) throws FailToDecodeException {
        if (len <= 0 || len > 32)
            throw new FailToDecodeException("length out of range: (0, 32] : " + len);

        offset.inc(len);
        return getInt(data, offset.getByteOffset(), offset.getBitOffset(), len);
    }
    
    /**
     * Get the bits, from 33 up to 64, stored in a byte array, starting at the 'offset'. 
     * The bytes are in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param data - the byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToDecodeException 
     */
    public static long getLong(byte data[], Offset offset, int len) throws FailToDecodeException {
        if (len <= 32 || len > 64)
            throw new FailToDecodeException("length out of range: (33, 64] : " + len);

        final long LOWER_MASK = (1L << 32) - 1;
        final long UPPER_MASK = (1L << (len - 32)) - 1;

        long high = getInt(data, offset, len - 32) & UPPER_MASK;
        long low = getInt(data, offset, 32) & LOWER_MASK;
        return ((high << 32) | low);
    }
    
    /**
     * Get the 32 bits float stored in a byte array, starting at the 'offset'. 
     * @param data - the byte array
     * @param offset - byte and bit offsets
     * @throws FailToDecodeException 
     */
    public static float getFloat(byte data[], Offset offset) throws FailToDecodeException {
        int intBits = getInt(data, offset, 32);
        return Float.intBitsToFloat(intBits);
    }
    
    /**
     * Get an arbitrary number of bits stored in a byte array, starting at the 'offset'. 
     * The bytes are in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param data - the byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToDecodeException 
     */
    public static byte[] getInts(byte data[], Offset offset, int len) throws FailToDecodeException {
        int bytes = (int) Math.ceil(len / 8.0);
        byte[] val = new byte[bytes];
        
        Offset dstOffset = new Offset(-1, 0);
        
        try {
            while (len > 0) {
                int seg = (len > 32) ? 32 : len;
                int tmp = Coder.getInt(data, offset, seg);          
                Coder.putInt(tmp, val, dstOffset, seg);
                len -= seg;
            }
        }
        catch (FailToEncodeException e) {
            e.printStackTrace();
        }
        
        return val;
    }
    
    /**
     * Put the bits, up to 32-bits, in an integer in a byte array, 
     * starting at the 'bit_index' bit of the 'index' element, 
     * The bytes are stored in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param value - the LSB 'len' bits are to be stored.
     * @param data - the destination byte array
     * @param byte_offset - starting index to data[]
     * @param bit_offset - bit offset into data[index]. MSB at 7. (76543210)
     * @param len - number of bits
     * @throws FailToEncodeException 
     */
    public static void putInt(int value, byte data[], int byte_offset, int bit_offset, int len) throws FailToEncodeException {       
        if (data == null || byte_offset >= data.length || 
            bit_offset > 7 || bit_offset < 0 || 
            len > 32 || len < 0) {
            throw new FailToEncodeException("data=" + data + ", data.len=" + data.length + ", byte_offset=" + byte_offset +
                                               ", bit_offset=" + bit_offset + ", len=" + len);
        }
        
        byte tmp = 0;
        int mask;                
        while (len > 0) {
            if (byte_offset < 0)
                throw new FailToEncodeException("bad byte_offset: " + byte_offset);
            
            int blen = 8 - bit_offset; 
            if (blen > len)
                blen = len;
        
            mask = (1 << blen) - 1;
            tmp = (byte) (value & mask);
            putBits(tmp, data, byte_offset, bit_offset, blen);
            
            value >>= blen;         
            len -= blen;
            bit_offset = 0;
            byte_offset--;          
        }
    }
    
    /**
     * Put the bits, up to 32, in an integer in a byte array, starting at the 'offset'. 
     * The bytes are stored in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param value - the LSB 'len' bits are to be stored.
     * @param data - the destination byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToEncodeException 
     */
    public static void putInt(int value, byte data[], Offset offset, int len) throws FailToEncodeException {
        if (len <= 0 || len > 32)
            throw new FailToEncodeException("length out of range: (0, 32] : " + len);
        if (value > (1L << len) - 1)
            throw new FailToEncodeException("value out of range, max = " + ((1L << len) - 1) + ", received " + value);
        
        offset.inc(len);
        putInt(value, data, offset.getByteOffset(), offset.getBitOffset(), len);
    }
    
    /**
     * Put the bits, from 33 up to 64, in an integer in a byte array, starting at the 'offset'. 
     * The bytes are stored in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param value - the LSB 'len' bits are to be stored.
     * @param data - the destination byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToEncodeException 
     */
    public static void putLong(long value, byte data[], Offset offset, int len) throws FailToEncodeException {
        if (len <= 32 || len > 64)
            throw new FailToEncodeException("length out of range: (32,64] : " + len);
             
        final long LOWER_MASK = (1L << 32) - 1;
        final long UPPER_MASK = (1L << (len - 32)) - 1;

        Coder.putInt((int)((value >> 32) & UPPER_MASK), data, offset, len - 32);
        Coder.putInt((int)(value & LOWER_MASK), data, offset, 32);
    }
    
    /**
     * Put a 32 bits float into a byte array, starting at the 'offset'. 
     * @param value - the LSB 'len' bits are to be stored.
     * @param data - the destination byte array
     * @param offset - byte and bit offsets
     * @throws FailToEncodeException 
     */
    public static void putFloat(float value, byte data[], Offset offset) throws FailToEncodeException {
        int intBits = Float.floatToRawIntBits(value);
        putInt(intBits, data, offset, 32);
    }
    
    /**
     * Put an arbitrary number of bits into a byte array, starting at the 'offset'. 
     * The bytes are stored in network byte order in the array.
     * The MSB in a byte is at position 7, and LSB at 0. (76543210)  
     * @param src - the source byte array.
     * @param data - the destination byte array
     * @param offset - byte and bit offsets
     * @param len - number of bits
     * @throws FailToEncodeException 
     */
    public static void putInts(byte[] src, byte data[], Offset offset, int len) throws FailToEncodeException {
        Offset srcOffset = new Offset(-1, 0);
        
        try {
            while (len > 0) {
                int seg = (len > 32) ? 32 : len;
                int tmp = Coder.getInt(src, srcOffset, seg);
                Coder.putInt(tmp, data, offset, seg);
                len -= seg;
            }         
        }
        catch (FailToDecodeException e) {
            e.printStackTrace();
        } 
    }
    
    /**
     * Get the 'len' bits of a byte 'val', starting at the index 'start'. 
     *
     * @param val - source of the bits.
     * @param start - starting bit, the MSB is at index 7 and LSB at index 0. (i.e. 76543210)
     * @param len - number of bits, must be greater than 0 and less than 8
     * @return the bits as a byte, with the bit at the original index 'start' as the LSB.
     * @throws FailToDecodeException 
     */
    private static int getBits(byte val, int start, int len) throws FailToDecodeException {
        if (start > 7 || start < 0 || start + len > 8 || len <= 0) {
            throw new FailToDecodeException("bad arguments: start=" + start + ", len=" + len);
        }
        int mask = ((1 << len) - 1) << start;
        
        return (((val & mask) >> start) & 0xff); 
    }
    
    /**
     * Put the least significant 'len' bits of a byte 'val' into data[index],
     * starting the index 'start'. 
     *
     * @param val - source of the bits.
     * @param data - destination byte array.
     * @param byte_offset - index to the data array.
     * @param bit_offset - starting bit of data[index], the MSB is at index 7 and LSB at index 0. (i.e. 76543210)
     * @param len - number of bits, must be greater than 0 and less than 8
     * @throws FailToEncodeException 
     */
    private static void putBits(byte val, byte[] data, int byte_offset, int bit_offset, int len) throws FailToEncodeException {
        if (bit_offset > 7 || bit_offset < 0 || bit_offset + len > 8 || 
            data == null || byte_offset < 0 || byte_offset >= data.length) {
            throw new FailToEncodeException("bad arguments: data=" + data + ", byte_offset=" + byte_offset +
                                            ", bit_offset=" + bit_offset + ", len=" + len);
        }
        int mask = ((1 << len) - 1) << bit_offset;
        
        data[byte_offset] &= ~mask;   // turn off the bits first 
        data[byte_offset] |= ((val << bit_offset) & mask); 
    }
    
    /**
     * Convert the given integer array list into a byte array, where each element of the list has 'size' bits.
     * @param list - the integer array list
     * @param size - size of each list element in bits.
     * @return a byte array containing the integers 
     * @throws FailToEncodeException 
     */
    public static byte[] getByteArray(ArrayList<Integer> list, int size) throws FailToEncodeException {
        int total = list.size() * size;
        int bytes = (int) Math.ceil(total / 8.0);
        
        byte[] data = new byte[bytes];
        Offset offset = Offset.newInstance();

        for (Integer element : list) {
            putInt(element, data, offset, size);
        }
        return data;
    }
}
