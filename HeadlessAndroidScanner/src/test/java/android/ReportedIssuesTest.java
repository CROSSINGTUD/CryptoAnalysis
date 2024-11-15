package android;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.util.Set;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore(
        "Running the tests requires an Android platform. Since they are licensed, they cannot be uploaded to "
                + "the GitHub remote. If you plan to run the tests, copy a platform (e.g. 'android-35') into the "
                + "src/test/resources/platforms/ directory. The files inside this directory are ignored for GitHub.")
public class ReportedIssuesTest extends AbstractAndroidTest {

    @Test
    public void testIssue268() {
        // Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/268
        HeadlessAndroidScanner scanner = createScanner("Issue268.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertTrue(errors.isEmpty());
    }
}
