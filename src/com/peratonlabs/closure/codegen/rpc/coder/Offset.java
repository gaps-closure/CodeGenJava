/**
 * 
 */
package com.peratonlabs.closure.codegen.rpc.coder;

/**
 * @author tchen
 *
 */
public class Offset
{
    private int byteOffset;
    private int bitOffset;
    
    /**
     * Create an Offset at the specified offset. 
     * The byte and bit offset represent the offset just before the encoding or decoding process
     * for the next field is started. If encoding or decoding for a fresh object, byte offset should be
     * -1 and bit offset 0 (cf. newInstance()).
     * @param byteOffset - byte offset
     * @param bitOffset - bit offset. Bit 7 is the MSB (76543210).
     */
    public Offset(int byteOffset, int bitOffset) {
        this.byteOffset = byteOffset;
        this.bitOffset = bitOffset;
    }
    
    /**
     * Create a new instance of Offset.
     * Invoked just before encoding or decoding for a fresh object, byte offset set to
     * -1 and bit offset 0.
     */
    public static Offset newInstance() {
        return new Offset(-1, 0);
    }
    
    /**
     * Advance the byte and bit offsets so that the next field of 'bits' bits
     * can start reading/writing at those offsets.
     * @param lenInBits
     */
    public void inc(int lenInBits) {
        int extra = lenInBits - bitOffset;
        if (extra > 0) {
            byteOffset += (int) Math.ceil(extra / 8.0);
            bitOffset = (8 - extra % 8) % 8;
        }
        else {
            bitOffset = -extra;
        }
    }
    
    public int getByteOffset() {
        return byteOffset;
    }
    
    public void setByteOffset(int byteOffset) {
        this.byteOffset = byteOffset;
    }
    
    public int getBitOffset() {
        return bitOffset;
    }

    public void setBitOffset(int bitOffset) {
        this.bitOffset = bitOffset;
    }       
}
