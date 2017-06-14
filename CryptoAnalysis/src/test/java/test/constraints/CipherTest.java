package test.constraints;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.analysis.ConstraintSolver;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.ParentPredicate;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import test.Assertion;
import test.IDEALCrossingTestingFramework;
import test.assertions.Assertions;

public class CipherTest{

	protected CryptSLRule getCryptSLFile() {
		return CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "Cipher.cryptslbin"));
	}
	
	@Test
	public void testCipher1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Multimap<String, String> values = HashMultimap.create();
		values.put("transformation", "AES/CBC/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<String> variables = new ArrayList<String>();
				variables.add("alg");
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		
		Assertions.assertValue((Integer) cs.evaluateRelConstraints(), (Integer) 0);
	}

	@Test
	public void testCipher2() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Multimap<String, String> values = HashMultimap.create();
		values.put("transformation", "AES");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<String> variables = new ArrayList<String>();
				variables.add("alg");
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		Integer evaluateRelConstraints = cs.evaluateRelConstraints();
		System.out.println(evaluateRelConstraints);
		assertEquals(evaluateRelConstraints, (Integer) 2);
	}
	
	
}
