package crypto.reporting;

import java.util.Collection;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.ICrySLResultsListener;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class CollectErrorListener implements ICrySLResultsListener {

	private Collection<AbstractError> errors = Sets.newHashSet();

	public void reportError(AbstractError error) {
		errors.add(error);
	}

	public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
			Collection<ISLConstraint> relConstraints) {
	}

	public void onSeedTimeout(Node<Statement, Val> seed) {

	}

	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults) {

	}

	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
	}

	public void discoveredSeed(IAnalysisSeed curr) {

	}

	public void onSecureObjectFound(IAnalysisSeed analysisObject) {

	}

	
	public Collection<AbstractError> getErrors(){
		return errors;
	}

	@Override
	public void addProgress(int processedSeeds, int workListsize) {
		// TODO Auto-generated method stub
		
	}
}
