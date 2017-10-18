package crypto.analysis;

import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import soot.Unit;
import typestate.TypestateDomainValue;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed{

	private Multimap<Unit, AccessGraph> analysisResults = HashMultimap.create();
	private Set<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private CryptoTypestateAnaylsisProblem problem;
	private boolean analyzed;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, IFactAtStatement delegate) {
		super(cryptoScanner,delegate);
	}

	@Override
	public void execute() {
		cryptoScanner.getAnalysisListener().seedStarted(this);
		getOrCreateAnalysis(new ResultReporter<TypestateDomainValue<StateNode>>() {
			@Override
			public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
				analysisResults = solver.getResultsAtStatement();
				for(EnsuredCryptSLPredicate pred : ensuredPredicates)
					ensurePredicates(pred);
			}

			@Override
			public void onSeedTimeout(IFactAtStatement seed) {
				cryptoScanner.getAnalysisListener().seedFinished(AnalysisSeedWithEnsuredPredicate.this);
			}
		}).analysisForSeed(this);
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


	private Analysis<TypestateDomainValue<StateNode>> getOrCreateAnalysis(final ResultReporter<TypestateDomainValue<StateNode>> resultReporter) {
		problem = new CryptoTypestateAnaylsisProblem() {
			@Override
			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return resultReporter;
			}

			@Override
			public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
				return new FiniteStateMachineToTypestateChangeFunction(this);
			}

			@Override
			public IExtendedICFG icfg() {
				return cryptoScanner.icfg();
			}

			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return cryptoScanner.debugger();
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

		};
		return new Analysis<TypestateDomainValue<StateNode>>(problem);
	}

	public void addEnsuredPredicate(EnsuredCryptSLPredicate pred) {
		if(ensuredPredicates.add(pred) && analyzed)
			ensurePredicates(pred);
	}

	@Override
	public CryptoTypestateAnaylsisProblem getAnalysisProblem() {
		return problem;
	}

	@Override
	public String toString() {
		return "AnalysisSeedWithEnsuredPredicate:"+getFact()+"@" + getStmt() +" " + ensuredPredicates; 
	}

	@Override
	public boolean contradictsNegations() {
		return false;
	}
}
