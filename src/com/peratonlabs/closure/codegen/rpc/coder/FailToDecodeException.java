/**
 * 
 */
package com.peratonlabs.closure.codegen.rpc.coder;

/**
 * @author tchen
 *
 */
public class FailToDecodeException extends Exception
{
    private static final long serialVersionUID = -2086055233083469044L;
    private Reason reason = null;
    
    public FailToDecodeException(String msg) {
        super(msg);
        // TODO: Make sure that all cases where FailToEncodeException is raised
        // that a Reason code is given. Then this constructor can be removed.
        reason = Reason.REASON_NOT_GIVEN;
    }   

    public FailToDecodeException(Reason r) {
        super(r.toString());
        reason = r;
    }

    public Reason getReason() {
        return reason;
    }
}
