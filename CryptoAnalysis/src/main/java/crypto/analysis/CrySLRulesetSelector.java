package crypto.analysis;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import com.google.common.collect.Lists;

import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;

public class CrySLRulesetSelector {
	public static enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}
	public static List<CryptSLRule> makeFromRuleset(String rulesBasePath, Ruleset... set) {
		List<CryptSLRule> rules = Lists.newArrayList();
		for (Ruleset s : set) {
			//rules.addAll(getRulesset(rulesBasePath, s));
			/*
			 * getRules as sourcecode format
			 */
			rules.addAll(getRulessetFromSource(rulesBasePath,s));
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
	public static List<CryptSLRule> makeFromRulesetString(String rulesBasePath, String rulesetString) {
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
		return makeFromRuleset(rulesBasePath, ruleset.toArray(new Ruleset[ruleset.size()]));
	}
	
	/*
	 * read rules as cryptslbin format.
	 * private static List<CryptSLRule> getRulesset(String rulesBasePath, Ruleset s){
		List<CryptSLRule> rules = Lists.newArrayList();
		File[] listFiles = new File(rulesBasePath + s+"/").listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		return rules;
	}
	public static CryptSLRule makeSingleRule(String rulesBasePath, Ruleset ruleset, String rulename) {
		File file = new File(rulesBasePath +"/"+ruleset+"/"+rulename + ".cryptslbin");
		if(!file.exists()) {
			throw new RuntimeException("Could not locate rule " + rulename +" within set " + ruleset );
		}
		return CryptSLRuleReader.readFromFile(file);
	}
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
	}*/
	
	/*
	 *Take rule files as source code format and create ruleset 
	 */
	private static List<CryptSLRule> getRulessetFromSource(String rulesBasePath, Ruleset s){
		List<CryptSLRule> rules = Lists.newArrayList();
		File[] listFiles = new File(rulesBasePath + s+"/").listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptsl")) {
				try {
					rules.add(CryptSLRuleReader.readFromSourceFile(file));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return rules;
	}
	
	/*
	 *Take single rule file as source code format and create ruleset 
	 */
	public static CryptSLRule makeSingleRuleFromSource(String rulesBasePath, Ruleset ruleset, String rulename) {
		File file = new File(rulesBasePath +"/"+ruleset+"/"+rulename + ".cryptsl");
		CryptSLRule rule;
		if(!file.exists()) {
			throw new RuntimeException("Could not locate rule " + rulename +" within set " + ruleset );
		}
		try {
			rule = CryptSLRuleReader.readFromSourceFile(file);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rule=null;
		} 
		return rule;
	}
	/*
	 * Take a resource path and find all .cryptsl files in the directory
	 */
	public static List<CryptSLRule> makeFromPathFromSource(File resourcesPath) {
		if(!resourcesPath.isDirectory())
			throw new RuntimeException("The specified path is not a directory" + resourcesPath);
		List<CryptSLRule> rules = Lists.newArrayList();
		File[] listFiles = resourcesPath.listFiles();
		for (File file : listFiles) {
			if (file.getName().endsWith(".cryptsl")) {
				try {
					rules.add(CryptSLRuleReader.readFromSourceFile(file));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found in " + resourcesPath);
		}
		return rules;
	}
}
