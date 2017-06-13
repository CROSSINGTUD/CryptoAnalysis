package crypto.typestate;

import crypto.rules.StateNode;

public class ErrorStateNode extends StateNode {

	private static ErrorStateNode instance;

	private ErrorStateNode() {
		super("ERROR");
	}
	
	public static ErrorStateNode v(){
		if(instance == null)
			instance = new ErrorStateNode();
		return instance;
	}
}
