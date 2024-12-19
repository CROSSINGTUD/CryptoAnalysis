package scanner.targets;

import crypto.analysis.errors.AlternativeReqPredicateError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import de.fraunhofer.iem.scanner.HeadlessJavaScanner;
import java.io.File;
import org.junit.Test;
import scanner.setup.AbstractHeadlessTest;
import scanner.setup.ErrorSpecification;
import scanner.setup.MavenProject;

/**
 * The following Headless tests are deducted from the MUBench benchmarking tool that detects various
 * API misuses. For the creating these Headless tests, only previous Cryptographic API misuses were
 * considered from the data-set of MUBench, which contains numerous projects having these kind of
 * API misuses.
 *
 * @author Enri Ozuni
 */
public class MUBenchExamplesTest extends AbstractHeadlessTest {

    @Test
    public void muBenchExamples() {
        String mavenProjectPath =
                new File("../CryptoAnalysisTargets/MUBenchExamples").getAbsolutePath();
        MavenProject mavenProject = createAndCompile(mavenProjectPath);
        HeadlessJavaScanner scanner = createScanner(mavenProject);

        /*
         * The links for each test case redirect to the description of the misuse,
         * and also contain the correct usage of the corresponding misuse in the directory
         * where `misuse.yml` file is contained
         */

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/tap-apps/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesBlowfishExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/drftpd3-extended/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.CipherUsesBlowfishWithECBModeExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/chensun/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesDESExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/secure-tcp/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesDSAExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/corona-old/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesJustAESExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/chensun/misuses/2/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesNonRandomKeyExample", "main", 1)
                        .withTPs(NeverTypeOfError.class, 1)
                        .withTPs(RequiredPredicateError.class, 1)
                        .withTPs(AlternativeReqPredicateError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/minecraft-launcher/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.CipherUsesPBEWithMD5AndDESExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/dalvik/misuses/3/misuse.yml
        // **MUBench hints that using RSA with PKCS1Padding is unsafe, which the Cipher CrySL rule
        //   does not agree, so added the test case of using RSA with CBC mode**
        addErrorSpecification(
                new ErrorSpecification.Builder("example.CipherUsesRSAWithCBCExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .withTPs(IncompleteOperationError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/pawotag/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.EmptyArrayUsedForCipherDoFinalExample", "main", 1)
                        .withTPs(ConstraintError.class, 1)
                        .build());

        // This test case corresponds to the following project in MUBench having this misuse:
        // https://github.com/akwick/MUBench/blob/master/data/red5-server/misuses/1/misuse.yml
        addErrorSpecification(
                new ErrorSpecification.Builder(
                                "example.InitInMacCalledMoreThanOnceExample", "main", 1)
                        .withTPs(TypestateError.class, 1)
                        .build());

        scanner.run();
        assertErrors(scanner.getCollectedErrors());
    }
}
