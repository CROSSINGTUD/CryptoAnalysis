package main.prefined;

import main.prefined.A;
import main.prefined.B;
import main.prefined.C;

public class Requires {
	
	// AND
	
	public void pred1OnParam1AndNotPred1OnParam2(A param1, A param2) {
		return;
	}
	
	public void pred1OnParam1AndNotPred1OnParam2(A param1, B param2) {
		return;
	}
	
	//
	// ALTERNATIVES
	//
	
	public void pred1OnParam1OrNotPred1OnParam2(A param1, A param2) {
		return;
	}
	
	public void notPred1OnParam1OrNotPred1OnParam2(A param1, A param2) {
		return;
	}
	
	public void notPred1OnParam1OrNotPred1OnParam2OrNotPred1OnParam3(A param1, A param2, A param3) {
		return;
	}
	
}
