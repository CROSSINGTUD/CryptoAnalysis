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
    public void runAnalysis()
    {
        String apkPath = ".\\src\\test\\resources\\app-debug.apk";
        String platformPath = "C:\\Android\\platforms";
        String rulesPath = "..\\CryptoAnalysis\\src\\main\\resources\\JavaCryptographicArchitecture";

        Collection<String> filter = new ArrayList<>();
        CogniCryptAndroidAnalysis analysis = new CogniCryptAndroidAnalysis(apkPath, platformPath, rulesPath, filter);
        Collection<AbstractError> errors = analysis.run();

        Assert.assertTrue(errors.size() >  0);
    }
}
