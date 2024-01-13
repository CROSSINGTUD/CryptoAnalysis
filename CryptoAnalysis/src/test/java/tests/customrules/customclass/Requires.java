package main.prefined;

import main.prefined.A;
import main.prefined.B;
import main.prefined.C;

public class Requires {
	
	// SIMPLE
	
	public void pred1onP1(A p1) {}
	public void notPred1onP1(A p1) {}
	
	// AND
	
	// same predicate
	public void pred1onP1_AND_pred1onP2(A p1, A p2) {}
	public void pred1onP1_AND_notPred1onP2(A p1, A p2) {}
	public void notPred1onP1_AND_pred1onP2(A p1, A p2) {}
	public void notPred1onP1_AND_notPred1onP2(A p1, A p2) {}
	
	// multi predicates
	public void pred1onP1_AND_pred2onP2(A p1, A p2) {}
	public void pred1onP1_AND_notPred2onP2(A p1, A p2) {}
	public void notPred1onP1_AND_pred2onP2(A p1, A p2) {}
	public void notPred1onP1_AND_notPred2onP2(A p1, A p2) {}
	
	// OR
	
	// same predicate
	public void pred1onP1_OR_pred1onP2(A p1, A p2) {}
	public void pred1onP1_OR_notPred1onP2(A p1, A p2) {}
	public void notPred1onP1_OR_pred1onP2(A p1, A p2) {}
	public void notPred1onP1_OR_notPred1onP2(A p1, A p2) {}
	
	// multi predicates
	public void pred1onP1_OR_pred2onP2(A p1, A p2) {}
	public void pred1onP1_OR_notPred2onP2(A p1, A p2) {}
	public void notPred1onP1_OR_pred2onP2(A p1, A p2) {}
	public void notPred1onP1_OR_notPred2onP2(A p1, A p2) {}
	
	// 3 cases same predicate
	public void pred1onP1_OR_pred1onP2_OR_pred1onP3(A p1, A p2, A p3) {}
	public void pred1onP1_OR_notPred1onP2_OR_pred1onP3(A p1, A p2, A p3) {}
	public void notPred1onP1_OR_pred1onP2_OR_pred1onP3(A p1, A p2, A p3) {}
	public void notPred1onP1_OR_notPred1onP2_OR_pred1onP3(A p1, A p2, A p3) {}
	public void pred1onP1_OR_pred1onP2_OR_notPred1onP3(A p1, A p2, A p3) {}
	public void pred1onP1_OR_notPred1onP2_OR_notPred1onP3(A p1, A p2, A p3) {}
	public void notPred1onP1_OR_pred1onP2_OR_notPred1onP3(A p1, A p2, A p3) {}
	public void notPred1onP1_OR_notPred1onP2_OR_notPred1onP3(A p1, A p2, A p3) {}
	
	// IMPLICATE
	
	// same predicate
	public void pred1onP1_IMPL_pred1onP2(A p1, A p2) {}
	public void pred1onP1_IMPL_notPred1onP2(A p1, A p2) {}
	
	// multi predicates
	public void pred1onP1_IMPL_pred2onP2(A p1, A p2) {}
	public void pred1onP1_IMPL_notPred2onP2(A p1, A p2) {}
	
	// OR WITH IMPLICATION
	// same predicate
	public void pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2(A p1, A p2) {}
	public void pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_!pred2onP2(A p1, A p2) {}
	
}
