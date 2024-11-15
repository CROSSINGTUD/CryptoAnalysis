package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crypto.rules.CrySLRule;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class TXTReporter extends Reporter {

    private static final String FILE_ENDING = ".txt";

    public TXTReporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        super(outputDir, ruleset);
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        String report = ReportGenerator.generateReport(seeds, ruleset, errorCollection, statistics);

        String fileName = outputFile.getAbsolutePath() + File.separator + REPORT_NAME + FILE_ENDING;
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(report);

            LOGGER.info("TXT report generated in {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Could not write TXT report to {}: {}", fileName, e.getMessage());
        }
    }
}
