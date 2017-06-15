package test.constraints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crypto.analysis.ConstraintSolver;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.ParentPredicate;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.rules.ICryptSLPredicateParameter;
import test.IDEALCrossingTestingFramework;

public class CipherTest{

	protected CryptSLRule getCryptSLFile() {
		return CryptSLRuleReader.readFromFile(new File(IDEALCrossingTestingFramework.RESOURCE_PATH + "Cipher.cryptslbin"));
	}
	
	@Test
	public void testCipher1() {
		Multimap<String, String> values = HashMultimap.create();
		values.put("transformation", "AES/CBC/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest1", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 0);
	}

	@Test
	public void testCipher2() {
		//No mode of operation specified
		Multimap<String, String> values = HashMultimap.create();
		values.put("transformation", "AES");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest2", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 2);
	}
	
	@Test
	public void testCipher3() {
		//ECB Mode not allowed.
		Multimap<String, String> values = HashMultimap.create();
		values.put("transformation", "AES/ECB/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest3", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 1);
	}
	
	
	@Test
	public void testCipher4() {
		Multimap<String, String> values = HashMultimap.create();
		//algorithms of cipher and key mismatch
		values.put("transformation", "AES/CBC/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "DES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest4", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 1);
	}
	
	@Test
	public void testCipher5() {
		Multimap<String, String> values = HashMultimap.create();
		//not allowed algorithm DES
		values.put("transformation", "DES/CBC/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "DES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest5", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 1);
	}
	
	@Test
	public void testCipher6() {
		Multimap<String, String> values = HashMultimap.create();
		//not allowed algorithm DES and mismatch of key and cipher alg
		values.put("transformation", "DES/CBC/PKCS5Padding");
		values.put("encmode", "1");
		ConstraintSolver cs = new ConstraintSolver(new ParentPredicate() {
			
			@Override
			public List<EnsuredCryptSLPredicate> getEnsuredPredicates() {
				List<EnsuredCryptSLPredicate> ensuredPredList = new ArrayList<EnsuredCryptSLPredicate>();
				ArrayList<ICryptSLPredicateParameter> variables = new ArrayList<ICryptSLPredicateParameter>();
				variables.add(new CryptSLObject("key"));
				variables.add(new CryptSLObject("alg"));
				CryptSLPredicate keygenPred = new CryptSLPredicate("generatedKey", variables, false);
				Multimap<String, String> collectedValues = HashMultimap.create();
				collectedValues.put("alg", "AES");
				ensuredPredList.add(new EnsuredCryptSLPredicate(keygenPred, collectedValues));
				return ensuredPredList;
			}
		}, getCryptSLFile().getConstraints(), values);
		
		ResultPrinter.evaluateResults("CipherTest6", cs.getAllConstraints().size(), cs.getRelConstraints().size(), cs.evaluateRelConstraints(), 2);
	}
	

	
}
