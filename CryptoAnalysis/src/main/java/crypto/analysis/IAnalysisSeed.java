package crypto.analysis;

import crypto.typestate.CryptoTypestateAnaylsisProblem;
import ideal.IFactAtStatement;

public interface IAnalysisSeed extends IFactAtStatement,  ParentPredicate{

	void execute();

	CryptoTypestateAnaylsisProblem getAnalysisProblem();

	boolean isSolved();
}
