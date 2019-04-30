package crypto.analysis;

import java.io.File;
import java.util.List;

import com.google.inject.internal.util.Lists;

import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;

public class CrySLRulesetSelector {
	public static enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}

	/**
	 * The directory containing the rulesets in subdirectories. Must end in a slash.
	 */
	public static String RESOURCE_PATH = "src/main/resources/";

	public static List<CryptSLRule> makeFromRuleset(Ruleset... set) {
		List<CryptSLRule> rules = Lists.newArrayList();
		for (Ruleset s : set) {
			rules.addAll(getRulesset(s));
		}
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found for rulesset " + set);
		}
		return rules;
	}

	/**
	 * Computes the ruleset from a string. The sting
	 * 
	 * @param rulesetString
	 * @return
	 */
	public static List<CryptSLRule> makeFromRulesetString(String rulesetString) {
		String[] set = rulesetString.split(",");
		List<Ruleset> ruleset = Lists.newArrayList();
		for (String s : set) {
			if (s.equalsIgnoreCase(Ruleset.JavaCryptographicArchitecture.name())) {
				ruleset.add(Ruleset.JavaCryptographicArchitecture);
			}
			if (s.equalsIgnoreCase(Ruleset.BouncyCastle.name())) {
				ruleset.add(Ruleset.BouncyCastle);
			}
			if (s.equalsIgnoreCase(Ruleset.Tink.name())) {
				ruleset.add(Ruleset.Tink);
			}
		}
		if (ruleset.isEmpty()) {
			throw new RuntimeException("Could not parse " + rulesetString + ". Was not able to find rulesets.");
		}
		return makeFromRuleset(ruleset.toArray(new Ruleset[ruleset.size()]));
	}
	
	private static List<CryptSLRule> getRulesset(Ruleset s){
		List<CryptSLRule> rules = Lists.newArrayList();
		File[] listFiles = new File(RESOURCE_PATH + s +"/").listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}

	public static CryptSLRule makeSingleRule(Ruleset ruleset, String rulename) {
		File file = new File(RESOURCE_PATH + ruleset+"/"+rulename + ".cryptslbin");
		if(!file.exists()) {
			throw new RuntimeException("Could not locate rule " + rulename +" within set " + ruleset );
		}
		return CryptSLRuleReader.readFromFile(file);
	}

	/*
	 * Takes a File resource path and finds all .cryptslbin files in the directory.
	 */
	public static List<CryptSLRule> makeFromPath(File resourcesPath) {
		if(!resourcesPath.isDirectory())
			throw new RuntimeException("The specified path is not a directory" + resourcesPath);
		List<CryptSLRule> rules = Lists.newArrayList();
		File[] listFiles = resourcesPath.listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found in " + resourcesPath);
		}
		return rules;
	}
}
