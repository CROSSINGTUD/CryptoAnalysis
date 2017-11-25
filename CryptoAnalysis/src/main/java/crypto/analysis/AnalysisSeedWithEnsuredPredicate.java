package crypto.analysis;

import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.SootBasedStateMachineGraph;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private Table<Statement, Val, TransitionFunction> analysisResults = HashBasedTable.create();
	private Set<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
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
		analysisResults = solver.getResults(this);
		for(EnsuredCryptSLPredicate pred : ensuredPredicates)
			ensurePredicates(pred);
		cryptoScanner.getAnalysisListener().seedFinished(this);
		analyzed = true;
	}

	protected void ensurePredicates(EnsuredCryptSLPredicate pred) {
		if(analysisResults == null)
			return;

		for(Cell<Statement, Val, TransitionFunction> c : analysisResults.cellSet()){
			cryptoScanner.addNewPred(this,c.getRowKey().getUnit().get(), c.getColumnKey(), pred);
		}
	}


	private ExtendedIDEALAnaylsis getOrCreateAnalysis() {
		problem = new ExtendedIDEALAnaylsis() {
			
			@Override
			protected BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
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
				m.addEdge(new TransitionEdge(Lists.newLinkedList(), s,s));
				return new SootBasedStateMachineGraph(m);
			}
			
			@Override
			public CrySLAnalysisResultsAggregator analysisListener() {
				return null;
			}
		};
		return problem;
	}

	public void addEnsuredPredicate(EnsuredCryptSLPredicate pred) {
		if(ensuredPredicates.add(pred) && analyzed)
			ensurePredicates(pred);
	}


	@Override
	public String toString() {
		return "AnalysisSeedWithEnsuredPredicate:"+this.asNode() +" " + ensuredPredicates; 
	}

	@Override
	public boolean contradictsNegations() {
		return false;
	}
}
