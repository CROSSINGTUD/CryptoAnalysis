package crypto.definition;

import boomerang.scope.FrameworkScope;
import crypto.listener.AnalysisReporter;
import crysl.rule.CrySLRule;
import java.util.Collection;
import sparse.SparsificationStrategy;

public interface Definitions {

    record SeedDefinition(
            FrameworkScope frameworkScope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record TypestateDefinition(
            FrameworkScope frameworkScope, Collection<CrySLRule> rules, int timeout) {}

    record ConstraintsDefinition(
            FrameworkScope frameworkScope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record ExtractParameterDefinition(
            FrameworkScope frameworkScope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}

    record QuerySolverDefinition(
            FrameworkScope frameworkScope,
            int timeout,
            SparsificationStrategy<?, ?> strategy,
            AnalysisReporter reporter) {}
}
