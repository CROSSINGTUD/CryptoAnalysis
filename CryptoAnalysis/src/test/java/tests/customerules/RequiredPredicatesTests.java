package tests.customerules;

import java.io.File;
import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
	
	@Test
	public void test1() {
		A a = new A();
		B b = new B();
		Requires r = new Requires();
		Assertions.notHasEnsuredPredicate(a);
		Assertions.notHasEnsuredPredicate(b);
		
		r.pred1OnParam1AndNotPred1OnParam2(a, b);
		Assertions.predicateErrors(1);
	}
	
	@Test
	public void test2() {
		A a1 = new A();
		A a2 = new A();
		Requires r = new Requires();
		Assertions.notHasEnsuredPredicate(a1);
		Assertions.notHasEnsuredPredicate(a2);
		
		a1.ensurePred1OnThis();
		Assertions.hasEnsuredPredicate(a1);
		Assertions.notHasEnsuredPredicate(a2);
		
		a2.ensurePred1OnAttr1();
		
		r.pred1OnParam1AndNotPred1OnParam2(a1, a2);
		Assertions.predicateErrors(0);
	}
	
	@Test
	public void test3() {
		A a1 = new A();
		A a2 = new A();
		A a3 = new A();
		Requires r = new Requires();
		Assertions.notHasEnsuredPredicate(a1);
		Assertions.notHasEnsuredPredicate(a2);
		Assertions.notHasEnsuredPredicate(a3);
		
		a1.ensurePred1OnThis();
		Assertions.hasEnsuredPredicate(a1);
		
		a2.ensurePred1OnThis();
		Assertions.hasEnsuredPredicate(a2);
		
		a3.ensurePred1OnThis();
		Assertions.hasEnsuredPredicate(a3);
		
		r.notPred1OnParam1OrNotPred1OnParam2OrNotPred1OnParam3(a1, a2, a3);
		Assertions.predicateErrors(1);
	}	
	
}
