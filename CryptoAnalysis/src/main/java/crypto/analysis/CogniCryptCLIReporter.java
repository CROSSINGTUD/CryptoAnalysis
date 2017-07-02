package crypto.analysis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import boomerang.util.StmtWithMethod;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.ErrorStateNode;
import heros.InterproceduralCFG;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;

public class CogniCryptCLIReporter implements CryptSLAnalysisListener{
	Set<IAnalysisSeed> analysisSeeds = Sets.newHashSet();
	Set<IFactAtStatement> typestateTimeouts = Sets.newHashSet();
	Multimap<IAnalysisSeed,StmtWithMethod> reportedTypestateErros = HashMultimap.create();
	Multimap<ClassSpecification,StmtWithMethod> callToForbiddenMethod = HashMultimap.create();
	private InterproceduralCFG<Unit, SootMethod> icfg;
	public CogniCryptCLIReporter(InterproceduralCFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
	}
	
	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		for(SootMethod m : solver.getVisitedMethods()){
			if(!m.hasActiveBody())
				continue;
			for(Unit u : m.getActiveBody().getUnits()){
				Map<AccessGraph, TypestateDomainValue<StateNode>> resultsAt = solver.resultsAt(u);
				for(Entry<AccessGraph, TypestateDomainValue<StateNode>> e : resultsAt.entrySet()){
					if(e.getValue().getStates().contains(ErrorStateNode.v()) && seed instanceof AnalysisSeedWithSpecification){
						typestateErrorAt((AnalysisSeedWithSpecification)seed, createStmtWithMethodFor(u));
					}
				}
			}
		}
		Multimap<Unit, AccessGraph> endPathOfPropagation = solver.getEndPathOfPropagation();
		for(Entry<Unit, AccessGraph> c : endPathOfPropagation.entries()){
			TypestateDomainValue<StateNode> resultAt = solver.resultAt(c.getKey(), c.getValue());
			for(StateNode n : resultAt.getStates()){
				if(!n.getAccepting()){
					typestateErrorAt((AnalysisSeedWithSpecification) seed, createStmtWithMethodFor(c.getKey()));
				}
			}
		}
	}
	
	private StmtWithMethod createStmtWithMethodFor(Unit u){
		return new StmtWithMethod(u,icfg.getMethodOf(u));
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Value> collectedValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite) {
		callToForbiddenMethod.put(classSpecification, createStmtWithMethodFor(callSite));
	}
	
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt){
		reportedTypestateErros.put(classSpecification, stmt);
	}
	@Override
	public void violateConstraint(ClassSpecification spec, Unit callSite) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {
		analysisSeeds.add(curr);
	}

	public Multimap<IAnalysisSeed, StmtWithMethod> getTypestateErrors() {
		return reportedTypestateErros;
	}
	public Multimap<ClassSpecification, StmtWithMethod> getCallToForbiddenMethod() {
		return callToForbiddenMethod;
	}
	public Set<IAnalysisSeed> getAnalysisSeeds() {
		return analysisSeeds;
	}

	@Override
	public void onSeedTimeout(IFactAtStatement seed) {
		typestateTimeouts.add(seed);
	}
	
	public Set<IFactAtStatement> getTypestateTimeouts() {
		return typestateTimeouts;
	}
	
	@Override
	public String toString() {
		String s = "================SEEDS=======================\n";
		s += Joiner.on("\n").join(analysisSeeds);
		s += "================CALL TO FORBIDDEN METHODS==================\n";
		s += Joiner.on("\n").join(callToForbiddenMethod.entries());

		s += "================REPORTED TYPESTATE ERRORS==================\n";
		s += Joiner.on("\n").join(reportedTypestateErros.entries());

		s += "================Timeouts: ==================\n";
		s += Joiner.on("\n").join(typestateTimeouts);

		return s;
	}

	@Override
	public void ensuredPredicates(
			Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates) {
		// TODO Auto-generated method stub
		
	}
}
