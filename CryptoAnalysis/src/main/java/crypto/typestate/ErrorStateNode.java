package crypto.typestate;

import typestate.finiteautomata.State;

public class ErrorStateNode implements State {

	private static ErrorStateNode instance;

	private ErrorStateNode() {
	}
	
	public static ErrorStateNode v(){
		if(instance == null)
			instance = new ErrorStateNode();
		return instance;
	}

	@Override
	public boolean isErrorState() {
		return true;
	}

	@Override
	public boolean isInitialState() {
		return false;
	}
}
