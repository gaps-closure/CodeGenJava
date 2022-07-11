/**
 * Copyright (c) 2021 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jul 23, 2021
 */
package com.peratonlabs.closure.codegen;

public class ClosurePermissionException extends Exception
{
    private static final long serialVersionUID = 1L;

    public ClosurePermissionException(String message) {
        super(message);
    }
}
