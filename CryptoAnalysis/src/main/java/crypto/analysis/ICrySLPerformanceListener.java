package crypto.analysis;

import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import ideal.IFactAtStatement;

public interface ICrySLPerformanceListener {

	void beforeAnalysis();

	void afterAnalysis();

	void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);

	void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification);
	
	void seedFinished(IAnalysisSeed analysisSeedWithSpecification);

	void seedStarted(IAnalysisSeed analysisSeedWithSpecification);

	void boomerangQueryStarted(IFactAtStatement seed, AdditionalBoomerangQuery q);

	void boomerangQueryFinished(IFactAtStatement seed, AdditionalBoomerangQuery q);
	
}
