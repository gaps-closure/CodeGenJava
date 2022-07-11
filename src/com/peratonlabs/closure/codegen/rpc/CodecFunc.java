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

public interface CodecFunc
{
    // (void *buff_out, void *buff_in, size_t *len_out)
    // Note the signature is changed with len_out becoming the return value
    public int codec(byte[] buff_out, byte[] buff_in); 
}
