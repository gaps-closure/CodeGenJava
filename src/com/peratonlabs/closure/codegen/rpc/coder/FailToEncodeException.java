/**
 * 
 */
package com.peratonlabs.closure.codegen.rpc.coder;

/**
 * @author tchen
 *
 */
public class FailToEncodeException extends Exception
{
    private static final long serialVersionUID = 4543509111204396737L;
    private Reason reason = null;
    
    public FailToEncodeException(String msg) {
        super(msg);
        // TODO: Make sure that all cases where FailToEncodeException is raised
        // that a Reason code is given. Then this constructor can be removed.
        reason = Reason.REASON_NOT_GIVEN;
    }

    public FailToEncodeException(Reason r) {
        super(r.toString());
        reason = r;
    }

    public Reason getReason() {
        return reason;
    }
}
