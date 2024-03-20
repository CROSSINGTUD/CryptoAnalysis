package crypto.analysis;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Table;
import crypto.rules.CrySLPredicate;

import java.util.Set;

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
	
	void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates);

}
