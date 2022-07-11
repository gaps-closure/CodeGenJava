/*******************************************************************************
 * Copyright (c) 2018 Perspecta Labs Inc  - All Rights Reserved.
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *  
 * @author tchen
 *******************************************************************************/
package com.peratonlabs.closure.codegen.rpc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MessageType 
{
    NEXTRPC(111),
    OKAY(112),
    REQUEST_ECHO_COMPONENT_HEARTBEATS(113),
    RESPONSE_ECHO_COMPONENT_HEARTBEATS(114);
    
    private int code;

    private static final Map<Integer, MessageType> lookup = new HashMap<Integer, MessageType>();

    static {
        for (MessageType s : EnumSet.allOf(MessageType.class))
            lookup.put(s.getCode(), s);
    }

    private MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType get(int code) {
        MessageType type = lookup.get(code);
        if (type == null) {
            return null;
        }

        return type;
    }

    public static MessageType get(String status) {
        try {
            Integer i = Integer.parseInt(status);
            return get(i);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static MessageType getByName(String status) {
        for (MessageType det : lookup.values())
            if (det.toString().equalsIgnoreCase(status))
                return det;
        
        return null;
    }
}
