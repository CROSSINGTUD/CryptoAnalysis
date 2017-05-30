package crypto.analysis;

import crypto.rules.StateNode;
import ideal.ResultReporter;
import typestate.TypestateDomainValue;

public interface CryptSLAnalysisReporter extends ResultReporter<TypestateDomainValue<StateNode>> {

}
