package crypto.analysis;

import java.util.ArrayList;
import java.util.Set;

import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;

import boomerang.debugger.Debugger;
import boomerang.scene.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.ExtendedIDEALAnalysis;
import crypto.typestate.MatcherTransitionCollection;
import ideal.IDEALSeedSolver;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private ForwardBoomerangResults<TransitionFunction> analysisResults;
	private final Set<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();
	private boolean analyzed;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, Node<ControlFlowGraph.Edge, Val> delegate) {
		super(cryptoScanner,delegate.stmt(),delegate.fact(), TransitionFunction.one());
	}

	@Override
	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		ExtendedIDEALAnalysis solver = getOrCreateAnalysis();
		solver.run(this);
		analysisResults = solver.getResults();
		for(EnsuredCrySLPredicate pred : ensuredPredicates)
			ensurePredicates(pred);
		cryptoScanner.getAnalysisListener().onSeedFinished(this, analysisResults);
		analyzed = true;
	}

	protected void ensurePredicates(EnsuredCrySLPredicate pred) {
		if(analysisResults == null)
			return;

		for(Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : analysisResults.asStatementValWeightTable().cellSet()){
			predicateHandler.addNewPred(this,c.getRowKey().getTarget(), c.getColumnKey(), pred);
		}
	}


	private ExtendedIDEALAnalysis getOrCreateAnalysis() {
		return new ExtendedIDEALAnalysis() {

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

	public void addEnsuredPredicate(EnsuredCrySLPredicate pred) {
		if(ensuredPredicates.add(pred) && analyzed)
			ensurePredicates(pred);
	}


	@Override
	public String toString() {
		return "AnalysisSeedWithEnsuredPredicate:"+this.asNode() +" " + ensuredPredicates; 
	}

}
