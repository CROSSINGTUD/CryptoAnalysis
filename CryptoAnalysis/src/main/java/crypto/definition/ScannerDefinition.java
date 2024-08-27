package crypto.definition;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.rules.CrySLRule;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.Collections;

public interface ScannerDefinition {

    CallGraph constructCallGraph(Collection<CrySLRule> ruleset);

    Collection<CrySLRule> readRuleset();

    default DataFlowScope createDataFlowScope(Collection<CrySLRule> ruleset) {
        return new CryptoAnalysisDataFlowScope(ruleset, Collections.emptySet());
    }

    default Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
        return new Debugger<>();
    }

    default SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
        return SparseCFGCache.SparsificationStrategy.NONE;
    }

    default int timeout() {
        return 10000;
    }
}
