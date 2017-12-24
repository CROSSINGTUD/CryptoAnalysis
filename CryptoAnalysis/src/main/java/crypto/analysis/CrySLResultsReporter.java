package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.TransitionEdge;
import crypto.typestate.CallSiteWithParamIndex;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public class CrySLResultsReporter  {

	private List<CrySLAnalysisListener> listeners;

	public CrySLResultsReporter() {
		listeners = new ArrayList<CrySLAnalysisListener>();
	}

	public boolean addReportListener(CrySLAnalysisListener listener) {
		return listeners.add(listener);
	}

	public boolean removeReportListener(CrySLAnalysisListener listener) {
		return listeners.remove(listener);
	}

	public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Statement> collectedValues) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.collectedValues(seed, collectedValues);
		}
	}

	public void callToForbiddenMethod(ClassSpecification classSpecification, Statement callSite, List<CryptSLMethod> alternatives) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.callToForbiddenMethod(classSpecification, callSite, alternatives);
		}
	}

	public void discoveredSeed(IAnalysisSeed curr) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.discoveredSeed(curr);
		}
	}

	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.ensuredPredicates(existingPredicates, expectedPredicates, missingPredicates);
		}
	}

	public void predicateContradiction(Node<Statement,Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.predicateContradiction(node, disPair);
		}
	}

	public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.missingPredicates(seed, missingPredicates);
		}
	}

	public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, Statement unit) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.constraintViolation(analysisSeedWithSpecification, con, unit);
		}
	}

	public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.checkedConstraints(analysisSeedWithSpecification, relConstraints);
		}
	}

	public void beforeAnalysis() {
		for (CrySLAnalysisListener listen : listeners) {
			listen.beforeAnalysis();
		}
	}

	public void afterAnalysis() {
		for (CrySLAnalysisListener listen : listeners) {
			listen.afterAnalysis();
		}
	}

	public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.beforeConstraintCheck(analysisSeedWithSpecification);
		}
	}

	public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.afterConstraintCheck(analysisSeedWithSpecification);
		}
	}

	public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.beforePredicateCheck(analysisSeedWithSpecification);
		}
	}

	public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.afterPredicateCheck(analysisSeedWithSpecification);
		}
	}

	public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.seedStarted(analysisSeedWithSpecification);
		}
	}

	public void boomerangQueryStarted(Query seed, BackwardQuery q) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.boomerangQueryStarted(seed, q);
		}
	}

	public void boomerangQueryFinished(Query seed, BackwardQuery q) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.boomerangQueryFinished(seed, q);
		}
	}	
	
	public void onSeedFinished(IAnalysisSeed seed, WeightedBoomerang<TransitionFunction> solver) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.onSeedFinished(seed, solver);
		}
	}
	
	public void onSeedTimeout(Node<Statement,Val> seed) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.onSeedTimeout(seed);
		}
	}
	
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, Statement stmt, Collection<SootMethod> expectedMethodCalls) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.typestateErrorAt(classSpecification, stmt, expectedMethodCalls);
		}
	}
	
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, Val value, Statement stmt, Set<TransitionEdge> expectedMethodsToBeCalled) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.typestateErrorEndOfLifeCycle(classSpecification, value, stmt, expectedMethodsToBeCalled);
		}
	}
	
	public void unevaluableConstraint(AnalysisSeedWithSpecification classSpecification, ISLConstraint con, Statement stmt) {
		for (CrySLAnalysisListener listen : listeners) {
			listen.unevaluableConstraint(classSpecification, con, stmt);
		}
	}
	
}
