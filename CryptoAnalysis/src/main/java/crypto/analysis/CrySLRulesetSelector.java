package crypto.analysis;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import crypto.cryslhandler.CrySLModelReader;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;

public class CrySLRulesetSelector {
	public static enum RuleFormat {
		SOURCE() {
			public String toString() {
				return CrySLModelReader.cryslFileEnding;
			}
		},
	}
	
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

	public static enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}

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
	public static List<CrySLRule> makeFromRulesetString(String rulesBasePath, RuleFormat ruleFormat,
			String rulesetString) {
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

	public static CrySLRule makeSingleRule(String rulesBasePath, RuleFormat ruleFormat, Ruleset ruleset,
			String rulename) {
		File file = new File(rulesBasePath + "/" + ruleset + "/" + rulename + RuleFormat.SOURCE);
		if (file.exists()) {
			CrySLRule rule = CrySLRuleReader.readFromSourceFile(file);
			return rule;
		}
		return null;
	}

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
