/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.exceptions;

/**
 * This is an exception that is thrown when something is not working as expected and is explicitly
 * related to the CryptoAnalysis tool.
 */
public class CryptoAnalysisException extends RuntimeException {

    private static final long serialVersionUID = -4977113204413613078L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message a detail message.
     */
    public CryptoAnalysisException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message a detail message.
     * @param cause the cause of the exception.
     */
    public CryptoAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
