package crypto.reporting;

import crypto.AnalysisSettings;
import crypto.rules.CrySLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class ReporterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterFactory.class);

    public static Collection<Reporter> createReporters(Collection<AnalysisSettings.ReportFormat> reportFormats, String outputDir, Collection<CrySLRule> rules) {
        Collection<Reporter> reporters = new HashSet<>();

        for (AnalysisSettings.ReportFormat format : reportFormats) {
            try {
                switch (format) {
                    case CMD:
                        Reporter cmdReporter = new CommandLineReporter(rules);
                        reporters.add(cmdReporter);
                        break;
                    case TXT:
                        Reporter txtReporter = new TXTReporter(outputDir, rules);
                        reporters.add(txtReporter);
                        break;
                    case CSV:
                        Reporter csvReporter = new CSVReporter(outputDir, rules);
                        reporters.add(csvReporter);
                        break;
                    case CSV_SUMMARY:
                        Reporter csvSummaryReporter = new CSVSummaryReporter(outputDir, rules);
                        reporters.add(csvSummaryReporter);
                        break;
                    case SARIF:
                        Reporter sarifReporter = new SARIFReporter(outputDir, rules);
                        reporters.add(sarifReporter);
                        break;
                    case GITHUB_ANNOTATION:
                        Reporter annotationReporter = new GitHubAnnotationReporter(rules);
                        reporters.add(annotationReporter);
                        break;
                    default:
                        LOGGER.error("Could not create reporter for format {}", format);
                }
            } catch (IOException e) {
                LOGGER.error("Could not create reporter for format {}: {}", format, e.getMessage());
            }
        }

        return reporters;
    }
}
