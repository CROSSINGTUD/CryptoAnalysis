package crypto.analysis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.ErrorStateNode;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;

public class CogniCryptCLIReporter implements CryptSLAnalysisListener{
	Set<AnalysisSeedWithSpecification> analysisSeeds = Sets.newHashSet();
	Multimap<AnalysisSeedWithSpecification,Unit> reportedTypestateErros = HashMultimap.create();
	Multimap<ClassSpecification,Unit> callToForbiddenMethod = HashMultimap.create();
	
	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		for(SootMethod m : solver.getVisitedMethods()){
			if(!m.hasActiveBody())
				continue;
			for(Unit u : m.getActiveBody().getUnits()){
				Map<AccessGraph, TypestateDomainValue<StateNode>> resultsAt = solver.resultsAt(u);
				for(Entry<AccessGraph, TypestateDomainValue<StateNode>> e : resultsAt.entrySet()){
					if(e.getValue().getStates().contains(ErrorStateNode.v()) && seed instanceof AnalysisSeedWithSpecification){
						typestateErrorAt((AnalysisSeedWithSpecification)seed, u);
					}
				}
			}
		}
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Value> collectedValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite) {
		callToForbiddenMethod.put(classSpecification, callSite);
	}
	
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, Unit stmt){
		reportedTypestateErros.put(classSpecification, stmt);
	}

	@Override
	public void discoveredSeed(AnalysisSeedWithSpecification curr) {
		analysisSeeds.add(curr);
	}
}
