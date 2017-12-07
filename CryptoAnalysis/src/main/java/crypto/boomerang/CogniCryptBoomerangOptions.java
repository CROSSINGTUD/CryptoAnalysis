package crypto.boomerang;

import boomerang.DefaultBoomerangOptions;

public class CogniCryptBoomerangOptions extends DefaultBoomerangOptions {
	@Override
	public boolean onTheFlyCallGraph() {
		return false;
	}
}
