package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.CallToError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.InstanceOfError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.NoCallToError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.listener.AnalysisStatistics;
import crypto.rules.CrySLRule;
import crypto.utils.ErrorUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVSummaryReporter extends Reporter {

    private static final String FILE_ENDING = ".csv";
    private static final String CSV_SEPARATOR = ";";

    private final List<String> headers = Arrays.asList("ErrorType", "ErrorCount");

    public CSVSummaryReporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        super(outputDir, ruleset);
    }

    @Override
    public void createAnalysisReport(Collection<IAnalysisSeed> seeds, Table<WrappedClass, Method, Set<AbstractError>> errorCollection, AnalysisStatistics statistics) {
        Map<String, Integer> errorCounts = ErrorUtils.getErrorCounts(errorCollection);

        String fileName = outputFile.getAbsolutePath() + File.separator + REPORT_NAME + "-Summary" + FILE_ENDING;

        try (FileWriter writer = new FileWriter(fileName)) {
            String header = Joiner.on(CSV_SEPARATOR).join(headers);
            writer.write(header + "\n");

            for (Map.Entry<String, Integer> entry : errorCounts.entrySet()) {
                String line = entry.getKey() + CSV_SEPARATOR + entry.getValue();

                writer.write(line + "\n");
            }

            LOGGER.info("CSV Summary report generated in {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Could not write CSV Summary report to {}: {}", fileName, e.getMessage());
        }
    }
}
