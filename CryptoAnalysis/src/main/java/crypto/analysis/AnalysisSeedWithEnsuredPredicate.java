package crypto.analysis;

import java.util.Set;

import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;

import boomerang.debugger.Debugger;
import boomerang.scene.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
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
		ExtendedIDEALAnaylsis solver = getOrCreateAnalysis();
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


	private ExtendedIDEALAnaylsis getOrCreateAnalysis() {
		return new ExtendedIDEALAnaylsis() {

			@Override
			public CallGraph callGraph() {
				return cryptoScanner.callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return cryptoScanner.getDataFlowScope();
			}

			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				StateMachineGraph m = new StateMachineGraph();
				StateNode s = new StateNode("0", true, true) {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "";
					}
				};
				m.addNode(s);
				m.createNewEdge(Lists.newLinkedList(), s, s);
				return new SootBasedStateMachineGraph(m);
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
