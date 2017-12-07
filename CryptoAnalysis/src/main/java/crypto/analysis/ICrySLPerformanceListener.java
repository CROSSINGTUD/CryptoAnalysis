package crypto.analysis;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.typestate.ExtendedIDEALAnaylsis.AdditionalBoomerangQuery;
import sync.pds.solver.nodes.Node;

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
	
}
