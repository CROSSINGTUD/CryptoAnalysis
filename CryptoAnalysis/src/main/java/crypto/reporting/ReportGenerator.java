package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;
import crypto.utils.ErrorUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReportGenerator {

    /**
     * Generates an analysis report content for the {@link CommandLineReporter} and {@link TXTReporter}.
     *
     * @param seeds the analyzed seeds
     * @param ruleset the ruleset used in the analysis
     * @param errorCollection the table containing the errors
     * @return the formatted report for the {@link CommandLineReporter} and {@link TXTReporter}
     */
    public static String generateReport(Collection<IAnalysisSeed> seeds, Collection<CrySLRule> ruleset, Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        StringBuilder report = new StringBuilder();

        report.append("Ruleset:\n");
        for (CrySLRule rule : ruleset) {
            report.append("\t").append(rule.getClassName()).append("\n");
        }

        report.append("\nAnalyzed Objects:\n");
        for (IAnalysisSeed seed : seeds) {
            report.append("\tObject:\n");
            report.append("\t\tVariable: ").append(seed.getFact().getVariableName()).append("\n");
            report.append("\t\tType: ").append(seed.getType()).append("\n");
            report.append("\t\tStatement: ").append(seed.getOrigin()).append("\n");
            report.append("\t\tLine: ").append(seed.getOrigin().getStartLineNumber()).append("\n");
            report.append("\t\tMethod: ").append(seed.getMethod()).append("\n");
            report.append("\t\tSHA-256: ").append(seed.getObjectId()).append("\n");
            report.append("\t\tSecure: ").append(seed.isSecure()).append("\n");
        }

        report.append("\n");
        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            report.append("Findings in class ").append(wrappedClass.getName()).append("\n");

            for (Map.Entry<Method, Set<AbstractError>> entry : errorCollection.row(wrappedClass).entrySet()) {
                report.append("\n\tin Method: ").append(entry.getKey().toString()).append("\n");

                List<AbstractError> orderedErrors = ErrorUtils.orderErrorsByLineNumber(entry.getValue());
                for (AbstractError error : orderedErrors) {
                    report.append("\t\t").append(error.getClass().getSimpleName()).append(" violating CrySL rule for ").append(error.getRule().getClassName()).append("\n");
                    report.append("\t\t\t").append(error.toErrorMarkerString()).append("\n");
                    report.append("\t\t\tat statement: ").append(error.getErrorStatement()).append("\n");
                    report.append("\t\t\tat line: ").append(error.getLineNumber()).append("\n\n");
                }
            }

            report.append("\n");
        }

        Map<String, Integer> errorCounts = ErrorUtils.getErrorCounts(errorCollection);
        report.append("======================= CryptoAnalysis Summary ==========================\n");
        report.append("\tNumber of CrySL rules: ").append(ruleset.size()).append("\n");
        report.append("\tNumber of Objects analyzed: ").append(seeds.size()).append("\n");

        if (errorCounts.isEmpty()) {
            report.append("\n\tNo violations of any of the rules found.\n");
        } else {
            for (Map.Entry<String, Integer> entry : errorCounts.entrySet()) {
                report.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return report.toString();
    }
}
