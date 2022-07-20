package com.peratonlabs.closure.codegen.rpc.coder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tchen
 * 
 */
public class Reason
{
    // from Section 5.1.3.1.5 of the spec.
    public static final Reason REASON_NOT_GIVEN                     = newInstance(0);
    public static final Reason REASON_UNREADABLE_REQUEST            = newInstance(1);
    public static final Reason REASON_UNAUTHORIZED_REQUEST          = newInstance(2);
    public static final Reason REASON_UNKNOWN_NODE                  = newInstance(3);
    public static final Reason REASON_NODE_INFO_UNKNOWN             = newInstance(4);
    public static final Reason REASON_UNSUPPORTED_DATA_TYPE         = newInstance(5);
    public static final Reason REASON_UNSUPPORTED_FILE_ID           = newInstance(6);
    public static final Reason REASON_FILE_NOT_FOUND                = newInstance(7);
    public static final Reason REASON_UNSUPPORTED_STATUS_METRICS    = newInstance(8);
    public static final Reason REASON_CONFIG_SWITCH_FAILURE         = newInstance(9);
    public static final Reason REASON_CHANGE_OWNERSHIP_FAILURE      = newInstance(10);
    public static final Reason REASON_INVALID_OWNERSHIP_HIERARCHY   = newInstance(11);
    public static final Reason REASON_NODE_BUSY                     = newInstance(12);
    public static final Reason REASON_DATA_TABLE_VERSION_MISMATCH   = newInstance(13);
    public static final Reason REASON_NO_RESPONSE                   = newInstance(14);
    public static final Reason REASON_PROTOCOL_VERSION_MISMATCH     = newInstance(15);
    public static final Reason REASON_MAX_ENTRIES_ECEEDED            = newInstance(16);

    private static Map<Integer, Reason> lookup;

    private static final int USER_MIN = 200;
    private static final int USER_MAX = 255;

    private int code;

    private Reason(int code) {
        this.code = code;
    }

    private static Reason newInstance(int code) {
        if (lookup == null)
            lookup = new HashMap<Integer, Reason>();
        
        Reason type = lookup.get(code);
        if (type != null)
           return type;        

        Reason reason = new Reason(code);
        lookup.put(code, reason);
        return reason;
    }

    /**
     * Get the Reason associated with the code. If the code is in the user defined range,
     * a new intance of TaskType is returned.
     * @param code
     * @throws FailToDecodeException - thrown if no object is associated with the code.
     */
    public static Reason get(int code) throws FailToDecodeException {
        Reason type = lookup.get(code);
        if (type != null)
           return type;

        if (code >= USER_MIN && code <= USER_MAX)
            return newInstance(code);

        throw new FailToDecodeException("unknown state code: " + code);
    }

    /**
     * Get the Reason associated with the code.
     * @param code
     * @return null if no object is associated with the code.
     */
    public static Reason getSafe(int code) {
        return lookup.get(code);
    }
    
    /**
     * Check if the code is within the user defined code range.
     * @param code
     */
    public static boolean isUserDefined(int code) {
        return (code >= USER_MIN && code <= USER_MAX);
    }
    
    public int getCode() {
        return code;
    }
    
    public String toString() {
        String val;
        
        if (this == REASON_NOT_GIVEN)
            val = "Reason Not Given";
        else if (this == REASON_UNREADABLE_REQUEST)
            val = "Unreadable Request";
        else if (this == REASON_UNAUTHORIZED_REQUEST)
            val = "Unauthorized Request";
        else if (this == REASON_UNKNOWN_NODE)
            val = "Unknown Node";
        else if (this == REASON_NODE_INFO_UNKNOWN)
            val = "Node Info Unknown";
        else if (this == REASON_UNSUPPORTED_DATA_TYPE)
            val = "Unsupported Data Type";
        else if (this == REASON_UNSUPPORTED_FILE_ID)
            val = "Unsupported File Id";
        else if (this == REASON_FILE_NOT_FOUND)
            val = "File Not Found";
        else if (this == REASON_UNSUPPORTED_STATUS_METRICS)
            val = "Unsupported Status Metrics";
        else if (this == REASON_CONFIG_SWITCH_FAILURE)
            val = "Config Switch Failure";
        else if (this == REASON_CHANGE_OWNERSHIP_FAILURE)
            val = "Change Ownership Failure";
        else if (this == REASON_INVALID_OWNERSHIP_HIERARCHY)
            val = "Invalid Ownership Hierarchy";
        else if (this == REASON_NODE_BUSY)
            val = "Node Busy";
        else if (this == REASON_DATA_TABLE_VERSION_MISMATCH)
            val = "Data Table Version Mismatch";
        else if (this == REASON_NO_RESPONSE)
            val = "No Response";
        else if (this == REASON_PROTOCOL_VERSION_MISMATCH)
            val = "Protocol Version Mismatch";
        else if (this == REASON_MAX_ENTRIES_ECEEDED)
            val = "Maximum Number of Entries Exceeded";
        else if (isUserDefined(code))
            val = "User Defined";
        else
            val = "invalid";

        return val + "(" + code + ")";
    }
}
