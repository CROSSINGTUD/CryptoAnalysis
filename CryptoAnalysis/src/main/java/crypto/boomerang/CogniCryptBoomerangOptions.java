package crypto.boomerang;

import boomerang.DefaultBoomerangOptions;

public class CogniCryptBoomerangOptions extends DefaultBoomerangOptions {
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
}
