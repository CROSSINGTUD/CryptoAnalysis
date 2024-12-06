package test;

import java.io.File;

public class TestConstants {

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

    public static final String JCA_RULESET_PATH =
            RULES_BASE_DIR + "JavaCryptographicArchitecture" + File.separator;

    public static final String BOUNCY_CASTLE_RULESET_PATH =
            RULES_BASE_DIR + "BouncyCastle" + File.separator;

    public static final String TINK_RULESET_PATH = RULES_BASE_DIR + "Tink" + File.separator;

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
}
