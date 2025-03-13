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

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.android.HeadlessAndroidScanner;
import org.junit.Test;

public class HeadlessAndroidTest extends AbstractAndroidTest {

    @Test
    public void testFalseCrypt() {
        // From https://github.com/secure-software-engineering/FalseCrypt
        HeadlessAndroidScanner scanner = createScanner("FalseCrypt.apk");
        scanner.scan();

        addExpectedErrors(ConstraintError.class, 6);
        addExpectedErrors(RequiredPredicateError.class, 7);
        addExpectedErrors(AlternativeReqPredicateError.class, 2);
        addExpectedErrors(TypestateError.class, 1);
        addExpectedErrors(IncompleteOperationError.class, 4);
        addExpectedErrors(ImpreciseValueExtractionError.class, 2);

        assertErrors(scanner.getCollectedErrors());
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
        HeadlessAndroidScanner scanner =
                createScanner("AndroidXAppCompatActivityCallbackDebug.apk");

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
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

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
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

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
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

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
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

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
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

        addExpectedErrors(ConstraintError.class, 1);
        addExpectedErrors(TypestateError.class, 1);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
