package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Val;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.ExtendedIDEALAnalysis;
import crypto.typestate.MatcherTransitionCollection;
import ideal.IDEALSeedSolver;
import typestate.TransitionFunction;

import java.util.ArrayList;
import java.util.Set;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private final ExtendedIDEALAnalysis solver;
	private final Set<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();

	private ForwardBoomerangResults<TransitionFunction> analysisResults;
	private boolean analyzed;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, ControlFlowGraph.Edge stmt, Val fact) {
		super(cryptoScanner, new WeightedForwardQuery<>(stmt, fact, TransitionFunction.one()));

		solver = new ExtendedIDEALAnalysis() {

			@Override
			public CallGraph callGraph() {
				return cryptoScanner.callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return cryptoScanner.getDataFlowScope();
			}

			@Override
			public MatcherTransitionCollection getMatcherTransitions() {
				StateMachineGraph smg = new StateMachineGraph();
				StateNode node = new StateNode("0", true, true);

				smg.addNode(node);
				smg.createNewEdge(new ArrayList<>(), node, node);

				return new MatcherTransitionCollection(smg);
			}

			@Override
			public CrySLResultsReporter analysisListener() {
				return cryptoScanner.getAnalysisListener();
			}


			@Override
			protected Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return cryptoScanner.debugger(solver, AnalysisSeedWithEnsuredPredicate.this);
			}
		};
	}

	@Override
	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		solver.run(getForwardQuery());
		analysisResults = solver.getResults();

		for (EnsuredCrySLPredicate pred : ensuredPredicates) {
			ensurePredicates(pred);
		}

		cryptoScanner.getAnalysisListener().onSeedFinished(this, analysisResults);
		analyzed = true;
	}

	protected void ensurePredicates(EnsuredCrySLPredicate pred) {
		if (analysisResults == null) {
			return;
		}

		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : analysisResults.asStatementValWeightTable().cellSet()) {
			predicateHandler.addNewPred(this, c.getRowKey(), c.getColumnKey(), pred);
		}
	}

	public void addEnsuredPredicate(EnsuredCrySLPredicate pred) {
		if (ensuredPredicates.add(pred) && analyzed) {
			ensurePredicates(pred);
		}
	}

	@Override
	public String toString() {
		return "AnalysisSeedWithEnsuredPredicate: " + getForwardQuery().asNode() + " " + ensuredPredicates;
	}

}
