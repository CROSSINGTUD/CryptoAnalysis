package crypto.analysis;

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
	
	void seedFinished(IAnalysisSeed analysisSeedWithSpecification);

	void seedStarted(IAnalysisSeed analysisSeedWithSpecification);

	void boomerangQueryStarted(Node<Statement,Val> seed, AdditionalBoomerangQuery q);

	void boomerangQueryFinished(Node<Statement,Val> seed, AdditionalBoomerangQuery q);
	
}
