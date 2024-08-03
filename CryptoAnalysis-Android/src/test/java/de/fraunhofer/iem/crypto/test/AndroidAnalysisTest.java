package de.fraunhofer.iem.crypto.test;

import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

public class AndroidAnalysisTest
{

    @Test
    public void testFalseCrypt()
    {
        // From https://github.com/secure-software-engineering/FalseCrypt
        String apkPath = ".\\src\\test\\resources\\falsecrypt.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = Lists.newArrayList();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithCallbackDebugAndroidXAppCompatActivity()
    {
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
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        String apkPath = ".\\src\\test\\resources\\AdroidXAppCompatActivityCallbackDebug.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithCallbackDebugNormalAppCompatActivity()
    {
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
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        String apkPath = ".\\src\\test\\resources\\NormalAppCompatActivityCallbackDebug.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithCallbackDebugNormalActivity()
    {
        // API 28, Debug Build, unsigned, normal Activity
        // CODE:
        /*
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
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
              }
         */
        String apkPath = ".\\src\\test\\resources\\NormalActivityCallbackDebug.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithoutCallbackDebug()
    {
        // API 28, Debug Build, unsigned
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        String apkPath = ".\\src\\test\\resources\\NoCallBackDebug.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithoutCallbackRelease()
    {
        // API 28, Release Build, unsigned
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        String apkPath = ".\\src\\test\\resources\\NoCallbackReleaseUnsigned.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }

    @Test
    public void runAnalysisWithoutCallbackReleaseSigned()
    {
        // API 28, Release Build, signed
        // CODE:
        /*
                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);

                    try {
                        Cipher cipher = Cipher.getInstance("DES");
                        cipher.doFinal();
                    } catch (GeneralSecurityException e){
                    }
                }
         */
        String apkPath = ".\\src\\test\\resources\\NoCallbackReleaseSigned.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }
}
