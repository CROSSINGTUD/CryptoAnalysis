package android;

import de.fraunhofer.iem.android.HeadlessAndroidScanner;

import java.io.File;

public abstract class AbstractAndroidTest {

    protected static final String APK_PATH = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "apk" + File.separator;
    protected static final String PLATFORMS_PATH = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "platforms" + File.separator;
    protected static final String JCA_RULES_DIR = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "JavaCryptoGraphicArchitecture" + File.separator;

    protected HeadlessAndroidScanner createScanner(String apkFileName) {
        String apkFile = APK_PATH + apkFileName;

        return new HeadlessAndroidScanner(apkFile, PLATFORMS_PATH, JCA_RULES_DIR);
    }

}
