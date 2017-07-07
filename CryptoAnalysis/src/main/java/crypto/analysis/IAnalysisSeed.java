package crypto.analysis;

import crypto.typestate.CryptoTypestateAnaylsisProblem;
import ideal.IFactAtStatement;

public interface IAnalysisSeed extends IFactAtStatement{

	void execute();

	CryptoTypestateAnaylsisProblem getAnalysisProblem();

	boolean isSolved();

	boolean contradictsNegations();
}
