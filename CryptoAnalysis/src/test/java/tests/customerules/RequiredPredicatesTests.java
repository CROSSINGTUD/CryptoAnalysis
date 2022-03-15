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
	
	//
	// OBJECTS OF SAME CLASS AS PARAMS
	//
	
	// SIMPLE
	
	@Test
	public void pred1onP1() {
		Requires r; 
		r = new Requires();
		r.pred1onP1(pred1onA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1(noPredOnA());
		
		Assertions.predicateErrors(1);
	}
	
	@Test
	public void notPred1onP1(){
		Requires r; 
		r = new Requires();
		r.pred1onP1(noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1(pred1onA());
		
		Assertions.predicateErrors(1);
	}
	
	// AND
	
	// same predicate
	@Test
	public void pred1onP1_AND_pred1onP2(){
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), pred1onA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), noPredOnA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void pred1onP1_AND_notPred1onP2(){
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), pred1onA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), pred1onA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), noPredOnA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_pred1onP2(){
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), pred1onA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), noPredOnA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_notPred1onP2(){
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), pred1onA());
		
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), pred1onA());
		
		Assertions.predicateErrors(4);
	}

	// multi predicates
	@Test
	public void pred1onP1_AND_pred2onP2() {
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(pred1onA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(pred1onA(), noPredOnA());
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(noPredOnA(), noPredOnA());
		
		r = new Requires();
		r.pred1onP1_AND_pred2onP2(noPredOnA(), pred2onA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void pred1onP1_AND_notPred2onP2() {
		Requires r;
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(noPredOnA(), pred2onA());
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(pred1onA(), pred2onA());
		
		r = new Requires();
		r.pred1onP1_AND_notPred2onP2(noPredOnA(), noPredOnA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_pred2onP2() {
		Requires r;
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(noPredOnA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(pred1onA(), noPredOnA());
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(pred1onA(), pred2onA());
		
		r = new Requires();
		r.notPred1onP1_AND_pred2onP2(noPredOnA(), noPredOnA());
		
		Assertions.predicateErrors(4);
	}
	
	@Test
	public void notPred1onP1_AND_notPred2onP2() {
		Requires r;
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA(), noPredOnA());
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA(), pred2onA());
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(noPredOnA(), pred2onA());
		
		Assertions.predicateErrors(4);
	}
	
	// OR
	
	// same predicate
	@Test
	public void pred1onP1_OR_pred1onP2(){
		//assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(pred1onA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(noPredOnA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred1onP2(noPredOnA(), noPredOnA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void pred1onP1_OR_notPred1onP2(){
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(pred1onA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred1onP2(noPredOnA(), pred1onA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void notPred1onP1_OR_pred1onP2(){
		Requires r;
		// assert true
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(noPredOnA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred1onP2(pred1onA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void notPred1onP1_OR_notPred1onP2(){
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_notPred1onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(noPredOnA(), pred1onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_AND_pred1onP2(pred1onA(), pred1onA());
		Assertions.predicateErrors(1);
	}

	// multi predicates
	@Test
	public void pred1onP1_OR_pred2onP2() {
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(pred1onA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(noPredOnA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_pred2onP2(noPredOnA(), noPredOnA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void pred1onP1_OR_notPred2onP2() {
		// assert true
		Requires r;
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(pred1onA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.pred1onP1_OR_notPred2onP2(noPredOnA(), pred2onA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void notPred1onP1_OR_pred2onP2() {
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(noPredOnA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(pred1onA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_OR_pred2onP2(pred1onA(), noPredOnA());
		Assertions.predicateErrors(1);
	}
		
	@Test
	public void notPred1onP1_OR_notPred2onP2() {
		// assert true
		Requires r;
		r = new Requires();
		r.notPred1onP1_OR_notPred2onP2(noPredOnA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA(), noPredOnA());
		Assertions.hasEnsuredPredicate(r);
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(noPredOnA(), pred2onA());
		Assertions.hasEnsuredPredicate(r);
		// assert false
		r = new Requires();
		r.notPred1onP1_AND_notPred2onP2(pred1onA(), pred2onA());
		Assertions.predicateErrors(1);
	}
		
}
