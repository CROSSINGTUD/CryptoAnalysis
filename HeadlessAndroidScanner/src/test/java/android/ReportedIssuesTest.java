package android;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class ReportedIssuesTest extends AbstractAndroidTest {

    @Test
    public void testIssue268() {
        // Related to https://github.com/CROSSINGTUD/CryptoAnalysis/issues/268
        HeadlessAndroidScanner scanner = createScanner("Issue268.apk");
        scanner.scan();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertTrue(errors.isEmpty());
    }
}
