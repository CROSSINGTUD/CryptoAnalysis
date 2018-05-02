package crypto.analysis;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.ForwardQuery;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public interface ICrySLResultsListener {

	void reportError(AbstractError error);
	
	void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

	void predicateContradiction(Node<Statement,Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair);

	void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints);
	
	void onSeedTimeout(Node<Statement,Val> seed);
	
	void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> analysisResults);
	
	void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues);

	void discoveredSeed(IAnalysisSeed curr);

	void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location);
}
