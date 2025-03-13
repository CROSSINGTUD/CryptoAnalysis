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

public class CryptoAnalysisParserException extends CryptoAnalysisException {

    private static final long serialVersionUID = 5931419586323153592L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message a detail message.
     */
    public CryptoAnalysisParserException(String message) {
        super(message);
    }
}
