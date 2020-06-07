package crypto.analysis;

import java.io.File;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;

import crypto.cryslhandler.CrySLModelReader;
import crypto.exceptions.CryptoAnalysisException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrySLRulesetSelector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrySLRulesetSelector.class);
	

	/**
	 * the supported rule formats
	 */
	public static enum RuleFormat {
		SOURCE() {
			public String toString() {
				return CrySLModelReader.cryslFileEnding;
			}
		},
	}

	/**
	 * current RuleSets
	 */
	public static enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}

	/**
	 * Creates {@link CrySLRule} objects for the given RuleSet argument and returns them as {@link List}.
	 * 
	 * @param rulesBasePath a {@link String} path giving the location of the CrySL ruleset base folder
	 * @param ruleFormat the file extension of the CrySL files
	 * @param set the {@link Ruleset} for which the {@link CrySLRule} objects should be created for
	 * @return the {@link List} with {@link CrySLRule} objects
	 * @throws CryptoAnalysisException 
	 */ 
	public static List<CrySLRule> makeFromRuleset(String rulesBasePath, RuleFormat ruleFormat, Ruleset... set) throws CryptoAnalysisException {
		
		List<CrySLRule> rules = Lists.newArrayList();
		for (Ruleset s : set) {
			rules.addAll(getRulesset(rulesBasePath, ruleFormat, s));
		}
		if (rules.isEmpty()) {
			LOGGER.info("No CrySL rules found for rulesset " + set);
		}
		return rules;
	}

	/**
	 * Computes the ruleset from a string. The sting
	 * 
	 * @param rulesetString
	 * @return
	 * @throws CryptoAnalysisException 
	 */
	public static List<CrySLRule> makeFromRulesetString(String rulesBasePath, RuleFormat ruleFormat,
			String rulesetString) throws CryptoAnalysisException {
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
		return makeFromRuleset(rulesBasePath, ruleFormat, ruleset.toArray(new Ruleset[ruleset.size()]));
	}

	private static List<CrySLRule> getRulesset(String rulesBasePath, RuleFormat ruleFormat, Ruleset s) throws CryptoAnalysisException {
		List<CrySLRule> rules = Lists.newArrayList();
		File[] listFiles = new File(rulesBasePath + s + "/").listFiles();
		for (File file : listFiles) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			if(rule != null) {
				rules.add(rule);
			}
		}
		
		if (rules.isEmpty()) {
			throw new CryptoAnalysisException("No CrySL rules found in " + rulesBasePath+s+"/");
		}
		
		return rules;
	}

	/**
	 * Creates and returns a single {@link CrySLRule} object for the given RuleSet argument.
	 * 
	 * @param rulesBasePath a {@link String} path giving the location of the CrySL ruleset base folder
	 * @param ruleFormat the file extension of the CrySL file
	 * @param ruleset the {@link Ruleset} where the rule belongs to 
	 * @param rulename the name of the rule
	 * @return the {@link CrySLRule} object
	 * @throws CryptoAnalysisException 
	 */
	public static CrySLRule makeSingleRule(String rulesBasePath, RuleFormat ruleFormat, Ruleset ruleset, String rulename) throws CryptoAnalysisException {
		File file = new File(rulesBasePath + "/" + ruleset + "/" + rulename + RuleFormat.SOURCE);
		if (file.exists() && file.isFile()) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			if(rule != null) {
			 return rule;
			} else {
				throw new CryptoAnalysisException("CrySL rule couldn't created from path " + file.getAbsolutePath());
			}
		} else {
			throw new CryptoAnalysisException("The specified path is not a file " + file.getAbsolutePath());
		}
	}


	/**
	 * Creates the {@link CrySLRule} objects for the given {@link File} argument and returns them as {@link List}.
	 * 
	 * @param 	resourcesPath a {@link File} with the path giving the location of the CrySL file folder
	 * @param 	ruleFormat the {@link Ruleset} where the rules belongs to 
	 * @return  the {@link List} with {@link CrySLRule} objects. If no rules are found it returns an empty list.
	 * @throws CryptoAnalysisException Throws when a file could not get processed to a {@link CrySLRule}
	 */
	@Deprecated
	public static List<CrySLRule> makeFromPath(File resourcesPath, RuleFormat ruleFormat) throws CryptoAnalysisException {
		return CrySLRuleReader.readFromDirectory(resourcesPath);
	}
	
	/**
	 * Creates {@link CrySLRule} objects from a Zip file and returns them as {@link List}.
	 * 
	 * @param resourcesPath the Zip {@link File} which contains the CrySL files
	 * @return the {@link List} with {@link CrySLRule} objects from the Zip file.
	 * 		If no rules are found it returns an empty list.
	 * @throws CryptoAnalysisException Throws when a file could not get processed to a {@link CrySLRule}
	 */
	@Deprecated
	public static List<CrySLRule> makeFromZip(File resourcesPath) throws CryptoAnalysisException {
		return CrySLRuleReader.readFromZipFile(resourcesPath);
	}
}
