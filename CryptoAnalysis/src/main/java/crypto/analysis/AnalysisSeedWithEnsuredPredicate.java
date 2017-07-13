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
import soot.SootMethod;
import soot.Unit;
import typestate.TypestateDomainValue;

public class AnalysisSeedWithEnsuredPredicate implements IAnalysisSeed{

	private IFactAtStatement delegate;
	private CryptoScanner cryptoScanner;
	private boolean analyzed;
	private Multimap<Unit, AccessGraph> analysisResults = HashMultimap.create();
	private Set<EnsuredCryptSLPredicate> ensuredPredicates = Sets.newHashSet();
	private CryptoTypestateAnaylsisProblem problem;

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner cryptoScanner, IFactAtStatement delegate, SootMethod methodOf) {
		this.cryptoScanner = cryptoScanner;
		this.delegate = delegate;
	}

	@Override
	public AccessGraph getFact() {
		return delegate.getFact();
	}

	@Override
	public Unit getStmt() {
		return delegate.getStmt();
	}


	@Override
	public void execute() {
		if(!analyzed){
			getOrCreateAnalysis(new ResultReporter<TypestateDomainValue<StateNode>>() {
				@Override
				public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
					analysisResults = solver.getResultsAtStatement();
					for(EnsuredCryptSLPredicate pred : ensuredPredicates)
						ensurePredicates(pred);
				}

				@Override
				public void onSeedTimeout(IFactAtStatement seed) {
				}
			}).analysisForSeed(this);
			analyzed = true;
		}
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
	public boolean isSolved() {
		return analyzed;
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
