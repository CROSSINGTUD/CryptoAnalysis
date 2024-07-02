package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public abstract class Reporter {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Reporter.class);
    protected static final String REPORT_NAME = "CryptoAnalysis-Report";

    public enum ReportFormat {
        CMD, TXT, SARIF, CSV, CSV_SUMMARY, GITHUB_ANNOTATION
    }

    protected final File outputFile;
    protected final Collection<CrySLRule> ruleset;

    protected Reporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        if (outputDir == null) {
            throw new RuntimeException("Cannot create report without directory (try using --reportDir or setOutputDirectory)");
        }
        this.outputFile = new File(outputDir);
        this.ruleset = ruleset;

        if (!outputFile.exists()) {
            throw new IOException("Directory " + outputDir + " does not exist");
        }

        if (!outputFile.isDirectory()) {
            throw new IOException(outputDir + " is not a directory");
        }
    }

    protected Reporter(Collection<CrySLRule> ruleset) {
        this.outputFile = new File("");
        this.ruleset = ruleset;
    }

    public abstract void createAnalysisReport(Collection<IAnalysisSeed> seeds, Table<WrappedClass, Method, Set<AbstractError>> errorCollection);
}
