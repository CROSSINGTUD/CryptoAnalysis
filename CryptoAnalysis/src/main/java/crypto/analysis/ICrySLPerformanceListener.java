package crypto.analysis;

import java.util.Set;

import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.rules.CryptSLPredicate;

public interface ICrySLPerformanceListener {

	void beforeAnalysis();

	void afterAnalysis();

	void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);
	
	void seedStarted(IAnalysisSeed analysisSeedWithSpecification);

	void boomerangQueryStarted(Query seed, BackwardQuery q);

	void boomerangQueryFinished(Query seed, BackwardQuery q);
	
	void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates);

}
