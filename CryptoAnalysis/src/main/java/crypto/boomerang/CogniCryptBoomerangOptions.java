package crypto.boomerang;

import boomerang.DefaultBoomerangOptions;
import boomerang.customize.EmptyCalleeFlow;

public class CogniCryptBoomerangOptions extends DefaultBoomerangOptions {
	private EmptyCalleeFlow backwardEmptyCalleeFlow = new CogniCryptBackwardEmptyCalleeFlow();
	private EmptyCalleeFlow forwardEmptyCalleeFlow = new CogniCryptForwardEmptyCalleeFlow();

	@Override
	public boolean onTheFlyCallGraph() {
		return false;
	}

	@Override
	public boolean arrayFlows() {
		return true;
	}
	
	@Override
	public int analysisTimeoutMS() {
		return 5000;
	}

	@Override
	public EmptyCalleeFlow getBackwardEmptyCalleeFlow(){
		return backwardEmptyCalleeFlow;
	}

	@Override
	public EmptyCalleeFlow getForwardEmptyCalleeFlow(){
		return forwardEmptyCalleeFlow;
	}
}
