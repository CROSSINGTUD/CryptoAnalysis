package tests.pattern;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import org.junit.Test;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestTest extends UsagePatternTestingFramework {

	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.JavaCryptographicArchitecture;
	}
	
	@Test
	public void mdUsagePatternTest1() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
	}

	@Test
	public void mdUsagePatternTest2() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input);
		Assertions.notHasEnsuredPredicate(output);
		Assertions.violatedConstraint(md);
	}

	@Test
	public void mdUsagePatternTest3() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		md.update(input);
		Assertions.mustNotBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input);
		md.digest();
	}

	@Test
	public void mdUsagePatternTest4() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		md.update(input);
		byte[] digest = md.digest();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(digest);
	}

	@Test
	public void mdUsagePatternTest5() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final String[] input = {"input1", "input2", "input3", "input4"};
		int i = 0;
		while (i < input.length) {
			md.update(input[i].getBytes(StandardCharsets.UTF_8));
			i++;
		}
		byte[] digest = md.digest();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(digest);
	}

	@Test
	public void mdUsagePatternTest6() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		md.reset();
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		md.digest();
	}

	@Test
	@SuppressWarnings("ConstantConditions")
	public void mdUsagePatternTest7() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		output = null;
		Assertions.notHasEnsuredPredicate(output);
		md.reset();
		output = md.digest(input);
		Assertions.mustBeInAcceptingState(md);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
	}

	@Test
	public void mdUsagePatternTest8() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		final byte[] input2 = "input2".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		md.reset();
		md.update(input2);
		Assertions.mustNotBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input2);
		Assertions.hasEnsuredPredicate(output);
		md.digest();
	}

	@Test
	public void mdUsagePatternTest9() throws GeneralSecurityException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		Assertions.extValue(0);
		final byte[] input = "input".getBytes(StandardCharsets.UTF_8);
		final byte[] input2 = "input2".getBytes(StandardCharsets.UTF_8);
		byte[] output = md.digest(input);
		Assertions.hasEnsuredPredicate(input);
		Assertions.hasEnsuredPredicate(output);
		Assertions.mustBeInAcceptingState(md);

		md = MessageDigest.getInstance("MD5");
		output = md.digest(input2);
		Assertions.mustBeInAcceptingState(md);
		Assertions.notHasEnsuredPredicate(input2);
		Assertions.notHasEnsuredPredicate(output);
	}
	
	@Test
	public void messageDigest() throws NoSuchAlgorithmException {
		while (Math.random() > 0.5) {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(new byte[] {});
			md.update(new byte[] {});
			byte[] digest = md.digest();
			Assertions.hasEnsuredPredicate(digest);
		}
	}
	
	@Test
	public void messageDigestReturned() throws NoSuchAlgorithmException {
		MessageDigest d = createDigest();
		byte[] digest = d.digest(new byte[] {});
		Assertions.hasEnsuredPredicate(digest);
		Assertions.typestateErrors(0);
	}

	private MessageDigest createDigest() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("SHA-256");
	}

}
