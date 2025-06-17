/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package android;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportedIssuesTest extends AbstractAndroidTest {

    @Test
    public void testIssue268() {
        // Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/268
        HeadlessAndroidScanner scanner = createScanner("Issue268.apk");
        scanner.scan();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assertions.assertTrue(errors.isEmpty());
    }
}
