package crypto.analysis;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;

public class CogniCryptCLIReporter implements CryptSLAnalysisListener{
	private IExtendedICFG icfg;

	public CogniCryptCLIReporter(IExtendedICFG icfg) {
		this.icfg = icfg;
	}
	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		HashBasedTable<Unit, AccessGraph, TypestateDomainValue<StateNode>> analysisResults = solver.results();
		for(Cell<Unit, AccessGraph, TypestateDomainValue<StateNode>> c : analysisResults.cellSet()){
			boolean empty = c.getValue().getStates().isEmpty();
			if(empty){
				for(Unit u : icfg.getPredsOf(c.getRowKey())){
					TypestateDomainValue<StateNode> ideVal = analysisResults.get(u, c.getColumnKey());
					if(ideVal != null){
						if(!ideVal.getStates().isEmpty()){
							if(seed instanceof AnalysisSeedWithSpecification){
								typestateErrorAt((AnalysisSeedWithSpecification)seed, u);
							}
						}
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
		// TODO Auto-generated method stub
		
	}
	
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, Unit stmt){
		
	}

}
