package crypto.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.TransitionEdge;
import crypto.typestate.CallSiteWithParamIndex;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public interface ICrySLResultsListener {

	void reportError(AbstractError error);
	
	void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

	void predicateContradiction(Node<Statement,Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair);

	void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates);

	void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints);
	
	void onSeedTimeout(Node<Statement,Val> seed);
	
	void onSeedFinished(IAnalysisSeed seed, WeightedBoomerang<TransitionFunction> solver);
	
	void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Statement> collectedValues);

	void discoveredSeed(IAnalysisSeed curr);

	void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location);
}
