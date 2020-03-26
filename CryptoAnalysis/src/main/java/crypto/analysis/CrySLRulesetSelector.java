package crypto.analysis;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import crypto.cryslhandler.CrySLModelReader;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;


public class CrySLRulesetSelector {
	
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
	 * the supported Zip formats
	 */
	public static enum ZipFormat {
		ZIP("zip");
//		TAR("tar"),
//		SevenZIP("7z"),
//		GZIP("gz"),
//		RAR("rar");
		
		private String fileExtension;
		private ZipFormat(String fileExtension) {
			this.fileExtension = fileExtension;
		}
		
		public String getFileExtension() {
			return fileExtension;
		}
		
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
	 */ 
	public static List<CrySLRule> makeFromRuleset(String rulesBasePath, RuleFormat ruleFormat, Ruleset... set) {
		
		List<CrySLRule> rules = Lists.newArrayList();
		for (Ruleset s : set) {
			rules.addAll(getRulesset(rulesBasePath, ruleFormat, s));
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
	public static List<CrySLRule> makeFromRulesetString(String rulesBasePath, RuleFormat ruleFormat, String rulesetString) {
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
		return makeFromRuleset(rulesBasePath, ruleFormat, ruleset.toArray(new Ruleset[ruleset.size()]));
	}

	private static List<CrySLRule> getRulesset(String rulesBasePath, RuleFormat ruleFormat, Ruleset s) {
		List<CrySLRule> rules = Lists.newArrayList();
		File[] listFiles = new File(rulesBasePath + s + "/").listFiles();
		for (File file : listFiles) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			if(rule != null) {
				rules.add(rule);
			}
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
	 */
	public static CrySLRule makeSingleRule(String rulesBasePath, RuleFormat ruleFormat, Ruleset ruleset, String rulename) {
		File file = new File(rulesBasePath + "/" + ruleset + "/" + rulename + RuleFormat.SOURCE);
		if (file.exists()) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			return rule;
		}
		return null;
	}


	/**
	 * Creates the {@link CrySLRule} objects for the given {@link File} argument and returns them as {@link List}.
	 * 
	 * @param 	resourcesPath a {@link File} with the path giving the location of the CrySL file folder
	 * @param 	ruleFormat the {@link Ruleset} where the rules belongs to 
	 * @return  the {@link List} with {@link CrySLRule} objects
	 */
	public static List<CrySLRule> makeFromPath(File resourcesPath, RuleFormat ruleFormat) {
		if (!resourcesPath.isDirectory())
			System.out.println("The specified path is not a directory " + resourcesPath);
		List<CrySLRule> rules = Lists.newArrayList();
		File[] listFiles = resourcesPath.listFiles();
		for (File file : listFiles) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			if(rule != null) {
				rules.add(rule);
			}
		}
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found in " + resourcesPath);
		}
		return rules;
	}
	
	/**
	 * Creates {@link CrySLRule} objects from a Zip file and returns them as {@link List}.
	 * 
	 * @param resource the Zip {@link File} which contains the CrySL files
	 * @return the {@link List} with {@link CrySLRule} objects from the Zip file
	 */
	public static List<CrySLRule> makeFromZip(File resource) {
		List<CrySLRule> rules = Lists.newArrayList();	

		if(resource.exists()) {
			String resourceFileExtension = Files.getFileExtension(resource.getAbsolutePath());
			boolean acceptedFormat = false;
			
			for(ZipFormat zipFormat : ZipFormat.values()) {
				if(resourceFileExtension.equals(zipFormat.getFileExtension())) {
					acceptedFormat = true;
				}
			}
			if(!acceptedFormat) {
				System.err.println("The format is not supported yet: " + resourceFileExtension);
				return rules;
			}
		} else {
			System.err.println("The specified path is not a file: " + resource);
			return rules;
		}
	
		rules = CrySLRuleReader.readFromZipFile(resource);	
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found in " + resource);
		}
		return rules;
	}
}
