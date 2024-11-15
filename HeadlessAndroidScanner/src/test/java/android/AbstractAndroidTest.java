package android;

import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.io.File;

/**
 * Running the tests requires an Android platform. Since they are licensed and quite large, they
 * should not be uploaded to the GitHub remote. If you plan to run the tests, copy a platform (e.g.
 * 'android-35') into the "src/test/resources/platforms/" directory. The files inside this directory
 * are ignored for GitHub.
 */
public abstract class AbstractAndroidTest {

    protected static final String APK_PATH =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "apk"
                    + File.separator;
    protected static final String PLATFORMS_PATH =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "test"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "platforms"
                    + File.separator;
    protected static final String JCA_RULES_DIR =
            "."
                    + File.separator
                    + "src"
                    + File.separator
                    + "main"
                    + File.separator
                    + "resources"
                    + File.separator
                    + "JavaCryptoGraphicArchitecture"
                    + File.separator;

    protected HeadlessAndroidScanner createScanner(String apkFileName) {
        String apkFile = APK_PATH + apkFileName;

        return new HeadlessAndroidScanner(apkFile, PLATFORMS_PATH, JCA_RULES_DIR);
    }
}
