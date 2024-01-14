package tests.customrules;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import org.junit.Ignore;
import org.junit.Test;

import test.UsagePatternTestingFramework;
import test.assertions.Assertions;
import tests.customrules.customclass.A;
import tests.customrules.customclass.Requires;

public class RequiredPredicatesTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.CustomRules;
	}
	
	//
	// OBJECTS OF SAME CLASS AS PARAMS
	//
	
	// SIMPLE
	
	@Test
	public void pred1OnPos1() {
		A pred1OnA = new A();
		pred1OnA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1OnA);

		A noPred1OnA = new A();
		Assertions.notHasEnsuredPredicate(noPred1OnA);

		Requires r1 = new Requires();
		r1.pred1onPos1(pred1OnA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.pred1onPos1(noPred1OnA);
		Assertions.notHasEnsuredPredicate(r2);

		Assertions.predicateErrors(1);
	}

	@Ignore
	@Test
	public void notPred1onPos1() {
		A pred1OnA = new A();
		pred1OnA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1OnA);

		A noPred1OnA = new A();
		Assertions.notHasEnsuredPredicate(noPred1OnA);

		Requires r1 = new Requires();
		r1.notPred1onPos1(noPred1OnA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.notPred1onPos1(pred1OnA);
		Assertions.notHasEnsuredPredicate(r2);

		Assertions.predicateErrors(1);
	}
	
	// AND
	
	// same predicate
	@Ignore
	@Test
	public void pred1onPos1_AND_pred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPred1onA = new A();
		Assertions.notHasEnsuredPredicate(noPred1onA);

		Requires r1 = new Requires();
		r1.pred1onPos1_AND_pred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.pred1onPos1_AND_pred1onPos2(pred1onA, noPred1onA);
		Assertions.notHasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_AND_pred1onPos2(noPred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_AND_pred1onPos2(noPred1onA, noPred1onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void pred1onPos1_AND_notPred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		Requires r1 = new Requires();
		r1.pred1onPos1_AND_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.pred1onPos1_AND_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r2);
		
		Requires r3 = new Requires();
		r3.pred1onPos1_AND_notPred1onPos2(pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r3);
		
		Requires r4 = new Requires();
		r4.pred1onPos1_AND_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void notPred1onPos1_AND_pred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.notPred1onPos1_AND_pred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.notPred1onPos1_AND_pred1onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r2);
		
		Requires r3 = new Requires();
		r3.notPred1onPos1_AND_pred1onPos2(pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r3);
		
		Requires r4 = new Requires();
		r4.notPred1onPos1_AND_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void notPred1onPos1_AND_notPred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.notPred1onPos1_AND_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);
		
		Requires r2 = new Requires();
		r2.notPred1onPos1_AND_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r2);
		
		Requires r3 = new Requires();
		r3.notPred1onPos1_AND_notPred1onPos2(pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r3);
		
		Requires r4 = new Requires();
		r4.notPred1onPos1_AND_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(3);
	}

	// multi predicates
	@Test
	public void pred1onPos1_AND_pred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.pred1onPos1_AND_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_AND_pred2onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_AND_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_AND_pred2onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void pred1onPos1_AND_notPred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.pred1onPos1_AND_notPred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_AND_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_AND_notPred2onPos2(pred1onA, pred2onA);
		Assertions.notHasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_AND_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void notPred1onPos1_AND_pred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.notPred1onPos1_AND_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_AND_pred2onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_AND_pred2onPos2(pred1onA, pred2onA);
		Assertions.notHasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_AND_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}

	@Ignore
	@Test
	public void notPred1onPos1_AND_notPred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		Requires r1 = new Requires();
		r1.notPred1onPos1_AND_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_AND_notPred2onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_AND_notPred2onPos2(pred1onA, pred2onA);
		Assertions.notHasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_AND_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(4);
	}
	
	// OR
	
	// same predicate
	@Test
	public void pred1onPos1_OR_pred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		//assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_pred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_pred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_pred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_OR_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore	// Nullpointer
	@Test
	public void pred1onPos1_OR_notPred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_notPred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_OR_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore		// Nullpointer
	@Test
	public void notPred1onPos1_OR_pred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_pred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_pred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_pred1onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore
	@Test
	public void notPred1onPos1_OR_notPred1onPos2(){
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred1onA2 = new A();
		pred1onA2.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA2);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_notPred1onPos2(pred1onA, pred1onA2);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	// multi predicates
	@Test
	public void pred1onPos1_OR_pred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_pred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_OR_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore
	@Test
	public void pred1onP1_OR_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_notPred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_notPred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_OR_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore
	@Test
	public void notPred1onP1_OR_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_pred2onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	@Ignore
	@Test
	public void notPred1onP1_OR_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_notPred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_notPred2onPos2(pred1onA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
	
	// 3 cases same predicate
	
	@Test
	public void pred1onPos1_OR_pred1onPos2_OR_pred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);
		Requires r5 = new Requires();
		r5.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.pred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore		// NullPointerException
	@Test
	public void pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.pred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore		// Nullpointer
	@Test
	public void notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.notPred1onPos1_OR_pred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore		// Nullpointer
	@Test
	public void notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.notPred1onPos1_OR_notPred1onPos2_OR_pred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore		// Nullpointer
	@Test
	public void pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);
		Requires r7 = new Requires();
		r7.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.pred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r8);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore		// Nullpointer
	@Test
	public void pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.pred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore 	// Nullpointer
	@Test
	public void notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);
		Requires r5 = new Requires();
		r5.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);
		Requires r7 = new Requires();
		r7.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.notPred1onPos1_OR_pred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}

	@Ignore
	@Test
	public void notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		Requires r4 = new Requires();
		r4.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r4);

		Requires r5 = new Requires();
		r5.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r5);

		Requires r6 = new Requires();
		r6.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r6);

		Requires r7 = new Requires();
		r7.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r7);
		
		// assert false
		Requires r8 = new Requires();
		r8.notPred1onPos1_OR_notPred1onPos2_OR_notPred1onPos3(pred1onA, pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r8);

		Assertions.predicateErrors(3); // three, because each parameter will be reported
	}
	
	// IMPLICATE
	
	// same predicate
	@Ignore
	@Test
	public void pred1onPos1_IMPL_pred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_IMPL_pred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_IMPL_pred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_IMPL_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);
		
		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_IMPL_pred1onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void pred1onPos1_IMPL_notPred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_IMPL_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_IMPL_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_IMPL_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);
		
		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_IMPL_notPred1onPos2(pred1onA, pred1onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void notPred1onPos1_IMPL_pred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_IMPL_pred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_IMPL_pred1onPos2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_IMPL_pred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_IMPL_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void notPred1onPos1_IMPL_notPred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_IMPL_notPred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_IMPL_notPred1onPos2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_IMPL_notPred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_IMPL_notPred1onPos2(noPredOnA, pred1onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	// multi predicates
	@Ignore
	@Test
	public void pred1onPos1_IMPL_pred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_IMPL_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_IMPL_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_IMPL_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);
		
		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_IMPL_pred2onPos2(pred1onA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void pred1onP1_IMPL_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_IMPL_pred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_IMPL_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_IMPL_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_IMPL_notPred2onPos2(pred1onA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void notPred1onP1_IMPL_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_IMPL_pred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_IMPL_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_IMPL_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_IMPL_pred2onPos2(noPredOnA, noPredOnA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void notPred1onP1_IMPL_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);

		// assert true
		Requires r1 = new Requires();
		r1.notPred1onPos1_IMPL_notPred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.notPred1onPos1_IMPL_notPred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.notPred1onPos1_IMPL_notPred2onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.notPred1onPos1_IMPL_notPred2onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	// OR WITH IMPLICATION
	// same predicate
	@Ignore
	@Test
	public void pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);
		
		// assert false
		Requires r4 = new Requires();
		r4.pred1onPos1_OR_pred2onPos1_IMPL_pred1onPos2(noPredOnA, pred2onA);
		Assertions.notHasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}

	@Ignore
	@Test
	public void pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2() {
		A pred1onA = new A();
		pred1onA.ensurePred1onThis();
		Assertions.hasEnsuredPredicate(pred1onA);

		A pred2onA = new A();
		pred2onA.ensurePred2onThis();
		Assertions.hasEnsuredPredicate(pred2onA);

		A noPredOnA = new A();
		Assertions.notHasEnsuredPredicate(noPredOnA);
		
		// assert true
		Requires r1 = new Requires();
		r1.pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r1);

		Requires r2 = new Requires();
		r2.pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r2);

		Requires r3 = new Requires();
		r3.pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r3);

		// assert false
		Requires r4 = new Requires();
		r4.pred2onPos1_IMPL_pred1onPos2_OR_pred2onPos2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r4);

		Assertions.predicateErrors(1); // only the missing will be reported
	}
}
