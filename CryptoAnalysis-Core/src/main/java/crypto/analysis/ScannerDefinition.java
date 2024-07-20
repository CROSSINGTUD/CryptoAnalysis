package crypto.analysis;

import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
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

    default int timeout() {
        return 10000;
    }
}
