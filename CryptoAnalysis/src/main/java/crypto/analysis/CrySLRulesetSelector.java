package crypto.analysis;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import crypto.cryslhandler.CrySLModelReader;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;

public class CrySLRulesetSelector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrySLRulesetSelector.class);
	
	/**
	 * current RuleSets
	 */
	public enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}

	/**
	 * Creates {@link CrySLRule} objects for the given RuleSet argument and returns them as {@link List}.
	 * 
	 * @param rulesBasePath a {@link String} path giving the location of the CrySL ruleset base folder
	 * @param set the {@link Ruleset} for which the {@link CrySLRule} objects should be created for
	 * @return the {@link List} with {@link CrySLRule} objects
	 * @throws CryptoAnalysisException If there is an error reading a ruleset
	 */ 
	public static List<CrySLRule> makeFromRuleset(String rulesBasePath, Ruleset... set) throws CryptoAnalysisException {
		
		List<CrySLRule> rules = Lists.newArrayList();
		for (Ruleset s : set) {
			rules.addAll(getRulesset(rulesBasePath, s));
		}
		if (rules.isEmpty()) {
			LOGGER.info("No CrySL rules found for rulesset " + set);
		}
		return rules;
	}

	/**
	 * Computes the ruleset from a string.
	 * 
	 * @param rulesBasePath a {@link String} path giving the location of the CrySL ruleset base folder
	 * @param rulesetString String 
	 * @return a list of the rules from the ruleset
	 * @throws CryptoAnalysisException If there is no ruleset
	 */
	public static List<CrySLRule> makeFromRulesetString(String rulesBasePath, String rulesetString) throws CryptoAnalysisException {
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
			throw new CryptoAnalysisException("Could not parse " + rulesetString + ". Was not able to find rulesets.");
		}
		return makeFromRuleset(rulesBasePath, ruleset.toArray(new Ruleset[ruleset.size()]));
	}

	private static List<CrySLRule> getRulesset(String rulesBasePath, Ruleset s) throws CryptoAnalysisException {
		File[] listFiles = new File(rulesBasePath + s + "/").listFiles();
		List<File> files = Arrays.asList(listFiles);
		
		List<CrySLRule> rules = new CrySLRuleReader().readFromSourceFiles(files);
		
		if (rules.isEmpty()) {
			throw new CryptoAnalysisException("No CrySL rules found in " + rulesBasePath + s + "/");
		}
		
		return rules;
	}

	/**
	 * Creates and returns a single {@link CrySLRule} object for the given RuleSet argument.
	 * 
	 * @param rulesBasePath a {@link String} path giving the location of the CrySL ruleset base folder
	 * @param ruleset the {@link Ruleset} where the rule belongs to 
	 * @param rulename the name of the rule
	 * @return the {@link CrySLRule} object
	 * @throws CryptoAnalysisException If the rule cannot be found
	 */
	public static CrySLRule makeSingleRule(String rulesBasePath, Ruleset ruleset, String rulename) throws CryptoAnalysisException {
		File file = new File(rulesBasePath + "/" + ruleset + "/" + rulename + CrySLModelReader.cryslFileEnding);
		
		if (file.exists() && file.isFile()) {
			CrySLRule rule = new CrySLRuleReader().readFromSourceFile(file);
			
			if(rule != null) {
				return rule;
			} else {
				throw new CryptoAnalysisException("CrySL rule couldn't created from path " + file.getAbsolutePath());
			}
		} else {
			throw new CryptoAnalysisException("The specified path is not a file " + file.getAbsolutePath());
		}
	}
}
