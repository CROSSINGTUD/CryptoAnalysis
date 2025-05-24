/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test;

import java.io.File;

public class TestRules {

    public static final String RULES_BASE_DIR =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "rules"
                    + File.separator;

    public static final String RULES_TEST_DIR =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "testrules"
                    + File.separator;

    public static final String JCA = "JavaCryptographicArchitecture";

    public static final String BOUNCY_CASTLE = "BouncyCastle";

    public static final String TINK = "Tink";

    public static final String CONSTRAINTS = "constraints";

    public static final String IMPRECISE_VALUE_EXTRACTION = "impreciseValueExtraction";

    public static final String INCOMPLETE_OPERATION = "incompleteOperation";

    public static final String PREDEFINED_PREDICATES = "predefinedPredicates";

    public static final String PREDICATE_CONTRADICTION = "predicateContradiction";

    public static final String PREDICATE_IMPLICATION = "predicateImplication";

    public static final String REQUIRED_PREDICATES = "requiredPredicates";

    public static final String REQUIRED_PREDICATE_WITH_THIS = "requiredPredicateWithThis";

    public static final String TYPESTATE = "typestate";

    public static final String TRANSFORMATION = "transformation";

    public static final String SEEDS = "seeds";

    public static final String ISSUE_314 = "issue314";

    public static final String ISSUE_318 = "issue318";
}
