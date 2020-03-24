package crypto.analysis;

import java.util.Collection;

import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public interface ICrySLResultsListener {

	void reportError(AbstractError error);

	void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints);
	
	void onSeedTimeout(Node<Statement,Val> seed);
	
	void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults);
	
	void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues);

	void discoveredSeed(IAnalysisSeed curr);

	void onSecureObjectFound(IAnalysisSeed analysisObject);

	void addProgress(int processedSeeds, int workListsize);

}
