/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem;

import crypto.exceptions.CryptoAnalysisParserException;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;

public class Main {

    public static void main(String[] args) {
        try {
            HeadlessAndroidScanner scanner = HeadlessAndroidScanner.createFromCLISettings(args);
            scanner.scan();
        } catch (CryptoAnalysisParserException e) {
            throw new RuntimeException("Error while parsing the CLI arguments: " + e.getMessage());
        }
    }
}
