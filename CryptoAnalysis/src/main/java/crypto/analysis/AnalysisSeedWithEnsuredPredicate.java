package crypto.analysis;

import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.ForwardQuery;
import boomerang.WeightedBoomerang;
import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.ExtendedIDEALAnaylsis;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.TypestateDomainValue;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private Multimap<Unit, Val> analysisResults = HashMultimap.create();
	private Set<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private ExtendedIDEALAnaylsis problem;
	private boolean analyzed;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, Node<Statement,Val> delegate) {
		super(cryptoScanner,delegate.stmt(),delegate.fact());
	}

	@Override
	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		ExtendedIDEALAnaylsis solver = getOrCreateAnalysis();
		solver.run(this.asNode(), null);
		analysisResults = solver.getResultsAtStatement();
		for(EnsuredCryptSLPredicate pred : ensuredPredicates)
			ensurePredicates(pred);
		cryptoScanner.getAnalysisListener().seedFinished(this);
		analyzed = true;
	}

	protected void ensurePredicates(EnsuredCryptSLPredicate pred) {
		if(analysisResults == null)
			return;

		for(Entry<Unit, AccessGraph> c : analysisResults.entries()){
			cryptoScanner.addNewPred(this,c.getKey(), c.getValue(), pred);
		}
	}


	private ExtendedIDEALAnaylsis getOrCreateAnalysis() {
		problem = new ExtendedIDEALAnaylsis() {
			
			@Override
			protected BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return cryptoScanner.icfg();
			}
			
			@Override
			public StateMachineGraph getStateMachine() {
				StateMachineGraph m = new StateMachineGraph();
				StateNode s = new StateNode("0", true, true){
					@Override
					public String toString() {
						return "";
					}
				};
				m.addNode(s);
				m.addEdge(new TransitionEdge(Lists.newLinkedList(), s,s));
				return m;
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
		return "AnalysisSeedWithEnsuredPredicate:"+this +" " + ensuredPredicates; 
	}

	@Override
	public boolean contradictsNegations() {
		return false;
	}
}
