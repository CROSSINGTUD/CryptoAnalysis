package crypto.reporting;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;

import java.util.Collection;
import java.util.Set;

public class CommandLineReporter extends Reporter {

    public CommandLineReporter(Collection<CrySLRule> ruleset) {
        super(ruleset);
    }

    @Override
    public void createAnalysisReport(Collection<IAnalysisSeed> seeds, Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        String report = ReportGenerator.generateReport(seeds, ruleset, errorCollection);
        System.out.println(report);
    }
}
