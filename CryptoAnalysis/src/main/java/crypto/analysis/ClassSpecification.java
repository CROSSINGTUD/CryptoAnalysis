package crypto.analysis;

import java.io.File;
import java.util.Set;

import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import crypto.statemachine.CryptoTypestateAnaylsisProblem;
import crypto.statemachine.FiniteStateMachineToTypestateChangeFunction;
import ideal.Analysis;
import ideal.AnalysisSolver;
import ideal.FactAtStatement;
import ideal.ResultReporter;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import typestate.TypestateDomainValue;
import typestate.finiteautomata.Transition;

public class ClassSpecification {
	private CryptoTypestateAnaylsisProblem problem;
	private File specification;
	private Analysis<TypestateDomainValue<StateNode>> analysis;
	private IInfoflowCFG icfg;
	private final SpecificationManager specManager;
	private ErrorReporter errorReporter;
	private ResultReporter<TypestateDomainValue<StateNode>> resultReporter = new ResultReporter<TypestateDomainValue<StateNode>>() {
		@Override
		public void onSeedFinished(FactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
			for(Cell<Unit,AccessGraph, TypestateDomainValue<StateNode>>c : solver.results().cellSet()){
				if(c.getValue().getStates().isEmpty()){
					errorReporter.report(ClassSpecification.this, c.getRowKey(), new ErrorReporter.TypestateViolation());
				}
			}
		}
	};
	public ClassSpecification(final File specification, IInfoflowCFG icfg, SpecificationManager specificationManager, ErrorReporter errorReporter) {
		this.specification = specification;
		this.icfg = icfg;
		this.specManager = specificationManager;
		this.errorReporter = errorReporter;
		this.problem = new CryptoTypestateAnaylsisProblem() {
			@Override
			public ResultReporter<TypestateDomainValue<StateNode>> resultReporter() {
				return resultReporter;
			}
			
			@Override
			public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
				return new FiniteStateMachineToTypestateChangeFunction(this){
					@Override
					public Set<Transition<StateNode>> getCallToReturnTransitionsFor(AccessGraph d1, Unit callSite,
							AccessGraph d2, Unit returnSite, AccessGraph d3) {
						specManager.onCallToReturnFlow(ClassSpecification.this, d1,callSite, d2);
						return super.getCallToReturnTransitionsFor(d1, callSite, d2, returnSite, d3);
					}
				};
			}

			@Override
			public IInfoflowCFG icfg() {
				return ClassSpecification.this.icfg;
			}
			
			@Override
			public IDebugger<TypestateDomainValue<StateNode>> debugger() {
				return new NullDebugger<>();
			}
			
			@Override
			public File getStateMachineFile() {
				return specification;
			}
		};
		analysis = new Analysis<TypestateDomainValue<StateNode>>(problem);	
	
	}
	public boolean isRootNode(){
		return true;
	}
	
	public void runTypestateAnalysisForAllSeeds() {
		analysis.run();
		checkConstraintSystem();
	}
	
	
	private void checkConstraintSystem() {
		// TODO Auto-generated method stub
		// Values are stored here: 
		System.out.println(problem.getCollectedValues());
		// Report error with ErrorReporter:
		//this.errorReporter.report(this, stmt, details);
	}
	public void runTypestateAnalysisForConcreteSeed(FactAtStatement seed) {
		analysis.analysisForSeed(seed);
		checkConstraintSystem();
	}
	public CryptoTypestateAnaylsisProblem getAnalysisProblem(){
		return problem;
	}
	
	@Override
	public String toString() {
		return specification.toString();
	}
	public void checkForForbiddenMethods() {
		//TODO Iterate over ICFG and report on usage of forbidden method.
	}
}
