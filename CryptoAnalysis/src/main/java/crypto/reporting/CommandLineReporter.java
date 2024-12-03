package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Set;

public class CommandLineReporter extends Reporter {

    public CommandLineReporter(Collection<CrySLRule> ruleset) {
        super(ruleset);
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        String report = ReportGenerator.generateReport(seeds, ruleset, errorCollection, statistics);
        System.out.println(report);
    }
}
