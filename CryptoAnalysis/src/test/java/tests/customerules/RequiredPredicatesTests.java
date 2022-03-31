package tests.customerules;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.internal.util.Lists;

import crypto.HeadlessCryptoScanner;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.cryslhandler.CrySLModelReaderClassPath;
import main.prefined.A;
import main.prefined.B;
import main.prefined.Requires;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;
import test.core.selfrunning.ImprecisionException;
import tests.headless.AbstractHeadlessTest;
import tests.headless.MavenProject;

public class RequiredPredicatesTests extends UsagePatternTestingFramworkForCustomRules {

	protected A pred1onA() {
		A a = new A();
		a.ensurePred1OnThis();
		Assertions.hasEnsuredPredicate(a);
		return a;
	}
	
	protected A pred2onA() {
		A a = new A();
		a.ensurePred2OnThis();
		Assertions.hasEnsuredPredicate(a);
		return a;
	}
	
	protected A noPredOnA() {
		A a = new A();
		Assertions.notHasEnsuredPredicate(a);
		return a;
	}
	
	@Test
	public void testTheTest() {
		A a = pred1onA();
		Assertions.hasEnsuredPredicate(a);
	}
	
	//
	// OBJECTS OF SAME CLASS AS PARAMS
	//
	
	// SIMPLE
	
	@Test
	public void pred1onP1() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r; 
		r = new Requires();
		r.pred1onP1(pred1onA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1(noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1);
	}
	
	@Test
	public void notPred1onP1(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r; 
		r = new Requires();
		r.pred1onP1(noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1(pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1);
	}
	
	// AND
	
	// same predicate
	@Test
	public void pred1onP1_AND_pred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, pred1onA);
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, noPredOnA);
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void pred1onP1_AND_notPred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_notPred1onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_notPred1onP2(noPredOnA, pred1onA);
		
		r = new Requires();
		r.pred1onP1_AND_notPred1onP2(pred1onA, pred1onA);
		
		r = new Requires();
		r.pred1onP1_AND_notPred1onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_pred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, noPredOnA);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, pred1onA);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_notPred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, noPredOnA);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA, pred1onA);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}

	// multi predicates
	@Test
	public void pred1onP1_AND_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(pred1onA, noPredOnA);
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(noPredOnA, noPredOnA);
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(noPredOnA, pred2onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void pred1onP1_AND_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(noPredOnA, pred2onA);
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(pred1onA, pred2onA);
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(pred1onA, noPredOnA);
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(pred1onA, pred2onA);
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		Requires r;
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA, noPredOnA);
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA, pred2onA);
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(noPredOnA, pred2onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(4);
	}
	
	// OR
	
	// same predicate
	@Test
	public void pred1onP1_OR_pred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		//assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void pred1onP1_OR_notPred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(noPredOnA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void notPred1onP1_OR_pred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		Requires r;
		// assert true
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void notPred1onP1_OR_notPred1onP2(){
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred1onA2 = new A();
		pred1onA2.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
//		r = new Requires();
//		r.notPred1onP1_OR_notPred1onP2(noPredOnA, noPredOnA);
//		Assertions.hasEnsuredPredicate(r);
//		r = new Requires();
//		r.notPred1onP1_OR_notPred1onP2(noPredOnA, pred1onA);
//		Assertions.hasEnsuredPredicate(r);
//		r = new Requires();
//		r.notPred1onP1_OR_notPred1onP2(pred1onA, noPredOnA);
//		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2(pred1onA, pred1onA2);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}

	// multi predicates
	@Test
	public void pred1onP1_OR_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void pred1onP1_OR_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(noPredOnA, pred2onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void notPred1onP1_OR_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
		
	@Test
	public void notPred1onP1_OR_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_notPred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred2onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_notPred2onP2(pred1onA, pred2onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(2); // two, because each parameter will be reported
	}
	
	// 3 cases same predicate
	
	@Test
	public void pred1onP1_OR_pred1onP2_OR_pred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void pred1onP1_OR_notPred1onP2_OR_pred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void notPred1onP1_OR_pred1onP2_OR_pred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_pred1onP3(pred1onA, noPredOnA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void notPred1onP1_OR_notPred1onP2_OR_pred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2_OR_pred1onP3(pred1onA, pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void pred1onP1_OR_pred1onP2_OR_notPred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void pred1onP1_OR_notPred1onP2_OR_notPred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void notPred1onP1_OR_pred1onP2_OR_notPred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	@Test
	public void notPred1onP1_OR_notPred1onP2_OR_notPred1onP3() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(noPredOnA, noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2_OR_notPred1onP3(pred1onA, pred1onA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(3); // two, because each parameter will be reported
	}
	
	// IMPLICATE
	
	// same predicate
	@Test
	public void pred1onP1_IMPL_pred1onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2(pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	@Test
	public void pred1onP1_IMPL_notPred1onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_IMPL_notPred1onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_notPred1onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_notPred1onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_IMPL_notPred1onP2(pred1onA, pred1onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	// multi predicates
	@Test
	public void pred1onP1_IMPL_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_IMPL_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_IMPL_pred2onP2(pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	@Test
	public void pred1onP1_IMPL_notPred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
//		r = new Requires();
//		r.pred1onP1_IMPL_pred2onP2(pred1onA, noPredOnA);
//		Assertions.hasEnsuredPredicate(r);
//		r = new Requires();
//		r.pred1onP1_IMPL_notPred2onP2(noPredOnA, pred2onA);
//		Assertions.hasEnsuredPredicate(r);
//		r = new Requires();
//		r.pred1onP1_IMPL_notPred2onP2(noPredOnA, noPredOnA);
//		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_IMPL_notPred2onP2(pred1onA, pred2onA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1); // only the missing will be reported
	}
	
	// OR WITH IMPLICATION
	// same predicate
	@Test
	public void pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2() {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2(noPredOnA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		
		// assert false
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_pred1onP1_IMPL_pred2onP2(pred1onA, noPredOnA);
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(1); // only the missing will be reported
	}
	public void pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(A p1, A p2) {
		A pred1onA = new A();
		pred1onA.ensurePred1OnThis();
		A pred2onA = new A();
		pred2onA.ensurePred2OnThis();
		A noPredOnA = new A();
		
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(pred1onA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(noPredOnA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(pred1onA, pred2onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(pred1onA, noPredOnA);
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_IMPL_pred1onP2_OR_notPred1onP1_IMPL_pred2onP2(noPredOnA, pred1onA);
		Assertions.hasEnsuredPredicate(r);
		// assert nothing false, because one condition is always not satisfied
		
		Assertions.hasEnsuredPredicate(pred1onA);
		Assertions.hasEnsuredPredicate(pred2onA);
		Assertions.notHasEnsuredPredicate(noPredOnA);
		Assertions.predicateErrors(0); // only the missing will be reported
	}
		
}
