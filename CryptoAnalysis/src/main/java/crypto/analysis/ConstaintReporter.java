package crypto.analysis;

import typestate.interfaces.ISLConstraint;

public interface ConstaintReporter {

	public void constraintViolated(ISLConstraint con);

}
