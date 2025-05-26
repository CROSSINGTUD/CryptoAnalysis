/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package scanner.targets;

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Ignore;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

/**
 * The following headless tests are deducted from the Braga et al. paper which benchmarks several
 * static analyzer tools against several hundred test projects containing various Cryptographic
 * providers of the JCA framework. For the creation of these headless tests, various projects from
 * Braga paper were considered and used for testing the provider detection functionality. The test
 * projects of the paper can be found in the link below: <a
 * href="https://bitbucket.org/alexmbraga/cryptogooduses/src/master/">BragaCryptoGoodUses</a>
 *
 * @author Enri Ozuni
 */
public class BragaCryptoGoodUsesTest extends AbstractHeadlessTest {

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cai/alwaysDefineCSP/
    @Test
    public void alwaysDefineCSPExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/alwaysDefineCSP")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider1", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider2", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider3", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider4", "main", 1)
                        .withTPs(IncompleteOperationError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider5", "main", 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider6", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DefinedProvider7", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/avoidCodingErros/
    @Test
    public void avoidCodingErrorsExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidCodingErrors")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PBEwLargeCountAndRandomSalt", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotSaveToString", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.GenerateRandomIV", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidConstantPwdPBE/
    @Test
    public void avoidConstantPwdPBEExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidConstantPwdPBE")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PBEwParameterPassword", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidDeterministicRSA/
    @Test
    public void avoidDeterministicRSAExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidDeterministicRSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseOAEPForRSA", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseOAEPForRSA", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UsePKCS1ForRSA", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UsePKCS1ForRSA", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(IncompleteOperationError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidFixedPredictableSeed/
    @Test
    public void avoidFixedPredictableSeedExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidFixedPredictableSeed")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotUseWeakSeed1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidHardcodedKeys/
    @Test
    public void avoidHardcodedKeysExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidHardcodedKeys")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseDynamicKeyFor3DES", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseDynamicKeyForAES", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseDynamicKeyforMAC1", "main", 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseDynamicKeyforMAC2", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidImproperKeyLen/
    @Test
    public void avoidImproperKeyLenExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidImproperKeyLen")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidInsecureDefaults/
    @Test
    public void avoidInsecureDefaultsExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureDefaults")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseQualifiedNameForPBE1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(ForbiddenMethodError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseExplicitMode1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 3)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseExplicitPadding1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(IncompleteOperationError.class, 3)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 2)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.UseQualifiedNameForRSAOAEP", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.UseQualifiedNameForRSAOAEP", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.UseQualifiedParamsForRSAOAEP", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.UseQualifiedParamsForRSAOAEP", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureHash/
    @Test
    public void avoidInsecureHashExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureHash")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseSHA2_1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseSHA2_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseSHA3_1", "main", 1)
                        .withTPs(ConstraintError.class, 4)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseSHA3_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureMAC/
    @Test
    public void avoidInsecureMACExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureMAC")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseHMACSHA2_1", "main", 1)
                        .withTPs(ConstraintError.class, 4)
                        .withTPs(RequiredPredicateError.class, 4)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/avoidInsecurePadding/
    @Test
    public void avoidInsecurePaddingExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePadding")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x256_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x256_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x256_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x256_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x384_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x384_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x384_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x384_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x512_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x512_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x512_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.OAEP_2048x512_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/avoidInsecurePadding/
    @Test
    public void avoidInsecurePaddingSignExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecurePaddingSign")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA256Signature", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA256Signature", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA384Signature", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA384Signature", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA512Signature", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSSwSHA512Signature", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/wc/avoidInsecureSymEnc/
    @Test
    public void avoidInsecureSymEncExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidInsecureSymEnc")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseAEADwAES_GCM", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseAES_CTR", "main", 1)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 6)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkm/avoidKeyReuseInStreams/
    @Test
    public void avoidKeyReuseInStreamsExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidKeyReuseInStreams")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotReuseKeyStreamCipher1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotReuseKeyStreamCipher2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotReuseKeyStreamCipher3", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotReuseKeyStreamCipher4", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotReuseKeyStreamCipher5", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 4)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/avoidSideChannels/
    @Test
    public void avoidSideChannelsExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidSideChannels")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/br/avoidStatisticPRNG/
    @Test
    public void avoidStatisticPRNGExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/avoidStatisticPRNG")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/icv/completeValidation/
    @Ignore("Boomerang cannot finish query computation ")
    @Test
    public void completeValidationExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/completeValidation")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.SSLClientCertPathCRLValidation", "main", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.SSLClientCompleteValidation", "main", 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ka/DHandECDH/
    @Test
    public void DHandECDHExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/DHandECDH")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedEphemeralECDH_128", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedEphemeralECDH_192", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedEphemeralECDH_256", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedEphemeralDH_2048", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedDH_2048", "positiveTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withTPs(RequiredPredicateError.class, 18)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.NonAuthenticatedDH_2048", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 18)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignDSAandECDSA/
    @Test
    public void digSignDSAandECDSAExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignDSAandECDSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.BC_128bits_DSA3072xSHA256", "main", 1)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.BC_ECDSAprime192", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.BC_ECDSAprime239", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.BC_ECDSAprime256", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.RandomMessageNonceECDSA", "main", 1)
                        .withFPs(
                                RequiredPredicateError.class,
                                2,
                                "setSeed is correctly called (cf. https://github.com/CROSSINGTUD/CryptoAnalysis/issues/295")
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SUN_112bits_DSA2048wSHA256", "positiveTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SUN_112bits_DSA2048wSHA256", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.SUN_112bits_ECDSA224wSHA224", "main", 1)
                        .withTPs(ConstraintError.class, 3)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.SUN_192bits_ECDSA384wSHA384", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.SUN_256bits_ECDSA571wSHA512", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/sign/digSignRSA/
    @Test
    public void digSignRSAExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/digSignRSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PKCS1_112bitsSign2048xSHA256_1", "positiveTestCase", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PKCS1_112bitsSign2048xSHA256_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PKCS1_112bitsSign2048xSHA256_2", "positiveTestCase", 0)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PKCS1_112bitsSign2048xSHA256_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PKCS1_128bitsSign3072xSHA256_1", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PKCS1_128bitsSign3072xSHA256_2", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PKCS1_192bitsSign7680xSHA384_1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PKCS1_192bitsSign7680xSHA384_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PSS_112bitsSign2048xSHA256_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PSS_112bitsSign2048xSHA256_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PSS_112bitsSign2048xSHA256_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.PSS_112bitsSign2048xSHA256_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSS_128bitsSign3072xSHA256_1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSS_128bitsSign3072xSHA256_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSS_192bitsSign7680xSHA384_1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.PSS_192bitsSign7680xSHA384_2", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/cib/doNotPrintSecrets/
    @Test
    public void doNotPrintSecretsExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/doNotPrintSecrets")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintECDHPrivKey1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintECDHSecret1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintECDSAPrivKey1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 5)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintPrivKey1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintPrivKey1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintSecKey1", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintDHSecret1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder("example.DoNotPrintDHPrivKey1", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 16)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/encryptThenHashOrMAC/
    @Test
    public void encryptThenHashOrMACExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/encryptThenHashOrMAC")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.EncryptThenHashCiphertextAndIV", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.EncryptThenMacCiphertextAndIV", "main", 1)
                        .withTPs(TypestateError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/ivm/randomIV/
    @Test
    public void randomIVExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/randomIV")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseRandomIVsForCBC", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseRandomIVsForCFB", "main", 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());
        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseRandomIVsForCFB128", "main", 1)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(IncompleteOperationError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/enc/secureConfigsRSA/
    @Test
    public void secureConfigsRSAExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureConfigsRSA")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig112bitsRSA_2048x256_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_3072x384_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig128bitsRSA_4096x512_2", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x384_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_1", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        // This is correct because there are two different 'SHA-256' variables, i.e.
                        // the pred is ensured on the first but not on the second one
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 5)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // positive test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_2", "positiveTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        // negative test case
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureConfig192bitsRSA_7680x512_2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 2)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(AlternativeReqPredicateError.class, 2)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pkc/ecc/securecurves/
    @Test
    public void secureCurvesExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/securecurves")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp192k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp192r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp224k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp224r1", "negativeTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp256k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp256r1", "negativeTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp384r1", "negativeTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_secp521r1", "negativeTestCase", 0)
                        .withNoErrors(ConstraintError.class)
                        .withNoErrors(RequiredPredicateError.class)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect163k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect163r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect163r2", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect233k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect233r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect239k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect283k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect283r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect409k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect409r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect571k1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.SecureCurve_sect571r1", "negativeTestCase", 0)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(RequiredPredicateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }

    // This test case corresponds to the following project in BragaCryptoBench:
    // https://bitbucket.org/alexmbraga/cryptogooduses/src/master/pdf/secureStreamCipher/
    @Test
    public void secureStreamCipherExamples() {
        String mavenProjectPath =
                new File(
                                "../CryptoAnalysisTargets/BragaCryptoBench/cryptogooduses/secureStreamCipher")
                        .getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        addErrorSpecification(
                new ErrorSpecification.Builder("example.UseMacWithMaleableStream", "main", 1)
                        .withTPs(RequiredPredicateError.class, 3)
                        .withTPs(TypestateError.class, 2)
                        .build());

        scanner.scan();
        assertErrors(scanner.getCollectedErrors());
    }
}
