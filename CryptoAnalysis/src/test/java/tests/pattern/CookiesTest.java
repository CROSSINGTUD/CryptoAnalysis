package tests.pattern;

import javax.servlet.http.Cookie;
import org.junit.Test;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

public class CookiesTest extends UsagePatternTestingFramework{

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}

	@Test
	public void testOne() {
		Cookie ck= new Cookie("name","testing");
		ck.setSecure(true); // constraint is satisfied
		Assertions.hasEnsuredPredicate(ck);
		Assertions.mustBeInAcceptingState(ck);

	}

	@Test
	public void testTwo() {
		Cookie ck= new Cookie("name","testing");
		ck.setSecure(false); // constraint is violated
		Assertions.notHasEnsuredPredicate(ck);
		Assertions.mustBeInAcceptingState(ck);
	}
	
	@Test
	public void testThree() {
		Cookie ck= new Cookie("name","testing");
		// setSecure call is unused
		Assertions.notHasEnsuredPredicate(ck);
		Assertions.mustNotBeInAcceptingState(ck);
	}
}