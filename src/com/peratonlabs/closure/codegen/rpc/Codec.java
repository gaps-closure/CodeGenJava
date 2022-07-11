/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Aug 25, 2021
 */
package com.peratonlabs.closure.codegen.rpc;

public class Codec
{
    private boolean valid;
    public CodecFunc encoder;
    public CodecFunc decoder;
    
    public Codec(CodecFunc encode, CodecFunc decode) {
        this.valid = false;
        this.encoder = encode;
        this.decoder = decode;
    }
    
    public Codec(CodecFunc encode, CodecFunc decode, boolean valid) {
        this.valid = valid;
        this.encoder = encode;
        this.decoder = decode;
    }
    
    public int encode(byte[] buff_out, byte[] buff_in) {
        return encoder.codec(buff_out, buff_in);
    }
    
    public int decode(byte[] buff_out, byte[] buff_in) {
        return decoder.codec(buff_out, buff_in);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public CodecFunc getEncode() {
        return encoder;
    }

    public void setEncode(CodecFunc encode) {
        this.encoder = encode;
    }

    public CodecFunc getDecode() {
        return decoder;
    }

    public void setDecode(CodecFunc decode) {
        this.decoder = decode;
    }
}
