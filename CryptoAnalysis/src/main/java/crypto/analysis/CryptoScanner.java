package crypto.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import crypto.rules.CryptSLRule;
import crypto.statemachine.CallSiteWithParamIndex;
import soot.Value;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

public abstract class CryptoScanner {
	
	IInfoflowCFG icfg;
	LinkedList<AnalysisSeedWithSpecification> worklist = Lists.newLinkedList();
	

	private final SpecificationManager specManager;
	public CryptoScanner(List<CryptSLRule> specs){
		specManager = new SpecificationManager(icfg(), worklist, errorReporter(), debugger());
		
		for (CryptSLRule rule : specs) {
			specManager.addSpecification(rule);	
		}		
	}
	
	
	public abstract IInfoflowCFG icfg();
	public abstract ErrorReporter errorReporter();

	public void scan(){
		initialize();
		Set<AnalysisSeedWithSpecification> visited = Sets.newHashSet();
		while(!worklist.isEmpty()){
			AnalysisSeedWithSpecification curr = worklist.poll();
			if(!visited.add(curr))
				continue;
			System.out.println(curr);
			curr.spec.runTypestateAnalysisForConcreteSeed(curr.factAtStmt);
		}
	}

	private void initialize() {
		for(ClassSpecification spec : specManager.getClassSpecifiction()){
			spec.checkForForbiddenMethods();
			if(!spec.isRootNode())
				continue;
			spec.runTypestateAnalysisForAllSeeds();
		}
	}
	
	public CrypSLAnalysisDebugger debugger(){
		return new CrypSLAnalysisDebugger() {
			
			@Override
			public void collectedValues(ClassSpecification classSpecification,
					Multimap<CallSiteWithParamIndex, Value> collectedValues) {
			}
		};
	}
}
