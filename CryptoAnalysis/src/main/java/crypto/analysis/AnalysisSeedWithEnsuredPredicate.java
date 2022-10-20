package crypto.analysis;

import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;

import boomerang.callgraph.ObservableICFG;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import ideal.IDEALSeedSolver;
import soot.SootMethod;
import soot.Unit;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private ForwardBoomerangResults<TransitionFunction> analysisResults;
	private Set<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();
	private ExtendedIDEALAnaylsis problem;
	private boolean analyzed;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, Node<Statement,Val> delegate) {
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

		for(Cell<Statement, Val, TransitionFunction> c : analysisResults.asStatementValWeightTable().cellSet()){
			predicateHandler.addNewPred(this,c.getRowKey(), c.getColumnKey(), pred);
		}
	}


	private ExtendedIDEALAnaylsis getOrCreateAnalysis() {
		problem = new ExtendedIDEALAnaylsis() {
			
			@Override
			protected ObservableICFG<Unit, SootMethod> icfg() {
				return cryptoScanner.icfg();
			}
			
			@Override
			public SootBasedStateMachineGraph getStateMachine() {
				StateMachineGraph m = new StateMachineGraph();
				StateNode s = new StateNode("0", true, true){
					@Override
					public String toString() {
						return "";
					}
				};
				m.addNode(s);
				m.createNewEdge(Lists.newLinkedList(), s,s);
				return new SootBasedStateMachineGraph(m);
			}
			
			@Override
			public CrySLResultsReporter analysisListener() {
				return cryptoScanner.getAnalysisListener();
			}
			

			@Override
			protected Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return cryptoScanner.debugger(solver,AnalysisSeedWithEnsuredPredicate.this);
			}
		};
		return problem;
	}

	public void addEnsuredPredicate(EnsuredCrySLPredicate pred) {
		if(ensuredPredicates.add(pred) && analyzed)
			ensurePredicates(pred);
	}


	@Override
	public String toString() {
		return "AnalysisSeedWithEnsuredPredicate:"+this.asNode() +" " + ensuredPredicates; 
	}

	@Override
	public Set<Node<Statement, Val>> getDataFlowPath() {
		return analysisResults.getDataFlowPath();
	}
}
