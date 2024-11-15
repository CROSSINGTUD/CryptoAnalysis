package android;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class HeadlessAndroidTest extends AbstractAndroidTest {

    @Test
    public void testFalseCrypt() {
        // From https://github.com/secure-software-engineering/FalseCrypt
        HeadlessAndroidScanner scanner = createScanner("FalseCrypt.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithCallbackDebugAndroidXAppCompatActivity() {
        // API 28, Debug Build, unsigned, AppCompatActivity using androidx
        // CODE:
        /*
            import androidx.appcompat.app.AppCompatActivity;

            public class MainActivity extends AppCompatActivity {
               @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                }

                public void sendMessage(View view)
                {
                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        HeadlessAndroidScanner scanner = createScanner("AndroidXAppCompatActivityCallbackDebug.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithCallbackDebugNormalAppCompatActivity() {
        // API 28, Debug Build, unsigned, AppCompatActivity using android.support
        // CODE:
        /*
            import android.support.v7.app.AppCompatActivity;

            public class MainActivity extends AppCompatActivity {
               @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                }

                public void sendMessage(View view)
                {
                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        HeadlessAndroidScanner scanner = createScanner("NormalAppCompatActivityCallbackDebug.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithCallbackDebugNormalActivity() {
        // API 28, Debug Build, unsigned, normal Activity
        // CODE:
        /*
            import android.app.Activity;

            public class MainActivity extends Activity {
               @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                }

                public void sendMessage(View view)
                {
                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        HeadlessAndroidScanner scanner = createScanner("NormalActivityCallbackDebug.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithoutCallbackDebug() {
        // API 28, Debug Build, unsigned
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        HeadlessAndroidScanner scanner = createScanner("NoCallBackDebug.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithoutCallbackRelease() {
        // API 28, Release Build, unsigned
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        HeadlessAndroidScanner scanner = createScanner("NoCallbackReleaseUnsigned.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }

    @Test
    public void runAnalysisWithoutCallbackReleaseSigned() {
        // API 28, Release Build, signed
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher c = Cipher.getInstance("DES");
                        c.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        HeadlessAndroidScanner scanner = createScanner("NoCallbackReleaseSigned.apk");
        scanner.run();

        Table<WrappedClass, Method, Set<AbstractError>> errors = scanner.getCollectedErrors();

        Assert.assertFalse(errors.isEmpty());
    }
}
