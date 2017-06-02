package crypto.analysis;

import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.ExtendedStandardFlowFunction;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.FactAtStatement;
import ideal.PerSeedAnalysisContext;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.Transition;

public class ClassSpecification {
	private CryptoTypestateAnaylsisProblem problem;
	private CryptSLRule cryptSLRule;
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private final CryptoScanner cryptoScanner;
	public ClassSpecification(final CryptSLRule rule, final CryptoScanner cScanner) {
		this.cryptSLRule = rule;
		this.cryptoScanner = cScanner;
		this.problem = new CryptoTypestateAnaylsisProblem() {
			@Override
			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return cryptoScanner.analysisListener();
			}
			
			@Override
			public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
				return new FiniteStateMachineToTypestateChangeFunction(this){
					@Override
					public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite,
							AccessGraph d2, Unit returnSite, AccessGraph d3) {
						cryptoScanner.onCallToReturnFlow(ClassSpecification.this, d1,callSite, d2);
						return super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
					}
				};
			}

			@Override
			public IInfoflowCFG icfg() {
				return cryptoScanner.icfg();
			}
			
			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return cryptoScanner.debugger();
			}
			
			
			@Override
			public StateMachineGraph getStateMachine() {
				return rule.getUsagePattern();
			}
			
			@Override
			public StandardFlowFunctions<TypestateDomainValue<StateNode>> flowFunctions(
					PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context) {
				return new ExtendedStandardFlowFunction(context,rule);
			}
		};
		analysis = new Analysis<TypestateDomainValue<StateNode>>(problem);	
	
	}
	public boolean isRootNode(){
		return true;
	}
	
	public void runTypestateAnalysisForAllSeeds() {
		analysis.run();
		cryptoScanner.analysisListener().collectedValues(this,problem.getCollectedValues());
		checkConstraintSystem();
	}
	
	
	private void checkConstraintSystem() {
//		Multimap<FactAtStatementWithVarName, Value> actualValues = problem.getCollectedValues();
//		ConstraintSolver solver = new ConstraintSolver();
//		for (ISLConstraint cons : specification.getConstraints()) {
//			if (cons instanceof CryptSLValueConstraint) {
//				CryptSLValueConstraint valueCons = (CryptSLValueConstraint) cons;
//				if (!solver.evaluate(valueCons, actualValues.get(valueCons.getVarName()).toString())) {
//					this.errorReporter.report(this, null, null);
//				}
//			} else {
//				if (!solver.evaluate(cons)) {
//					this.errorReporter.report(this, null, null);
//				}
//			}
//		}
		
	}
	public void runTypestateAnalysisForConcreteSeed(FactAtStatement seed) {
		analysis.analysisForSeed(seed);
		cryptoScanner.analysisListener().collectedValues(this,problem.getCollectedValues());
		checkConstraintSystem();
	}
	public CryptoTypestateAnaylsisProblem getAnalysisProblem(){
		return problem;
	}
	
	@Override
	public String toString() {
		return cryptSLRule.toString();
	}
	public void checkForForbiddenMethods() {
		//TODO Iterate over ICFG and report on usage of forbidden method.
	}
}
