package de.fraunhofer.iem.crypto.test;

import crypto.analysis.errors.AbstractError;
import de.fraunhofer.iem.crypto.CogniCryptAndroidAnalysis;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

public class AndroidAnalysisTest
{
    @Test
    public void runAnalysisWithCallbackDebug()
    {
        // API 28, Debug Build, unsigned
        // CODE:
        /*
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
         */
        String apkPath = ".\\src\\test\\resources\\CallbackDebug.apk";
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
