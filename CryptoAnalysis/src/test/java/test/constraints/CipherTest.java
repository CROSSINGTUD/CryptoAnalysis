package test.constraints;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import crypto.analysis.ConstraintSolver;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.ParentPredicate;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import test.IDEALCrossingTestingFramework;
import typestate.interfaces.ISLConstraint;

public class CipherTest{

	protected CryptSLRule getCryptSLFile() {
		return CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "Cipher.cryptslbin"));
	}
	
	@Test
	public void testCipher1() throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher c = Cipher.getInstance("AES");
		Multimap<String, String> values = HashMultimap.create();
		values.put("alg", "AES");
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
		});
		Integer failed = 0;
		for(ISLConstraint cons : getCryptSLFile().getConstraints()) {
			if (!cs.evaluate(cons, values)) {
				failed++;
			}
		}
		System.out.println(failed);
	}

	
}
