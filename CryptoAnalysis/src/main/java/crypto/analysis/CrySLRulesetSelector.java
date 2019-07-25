package crypto.analysis;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import com.google.common.collect.Lists;

import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.cryptslhandler.CryslReaderUtils;
import org.apache.commons.io.FilenameUtils;

public class CrySLRulesetSelector {
	private static String binRulesDir=null;
	public static final String cryslFileEnding = ".cryptsl";
	public static final String cryslbinFileEnding = ".cryptslbin";
	public static final String BIN_RULES_RELATIVE_PATH = "RulesInBin";
	public static enum Ruleset {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}
	public static List<CryptSLRule> makeFromRuleset(String rulesBasePath,String ruleFormat, Ruleset... set) {
		List<CryptSLRule> rules = Lists.newArrayList();
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
	public static List<CryptSLRule> makeFromRulesetString(String rulesBasePath, String ruleFormat, String rulesetString) {
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
	
	private static List<CryptSLRule> getRulesset(String rulesBasePath, String ruleFormat, Ruleset s){
		List<CryptSLRule> rules = Lists.newArrayList();
		if(ruleFormat.equals("cryptslbin")) {
			File[] listFiles = new File(rulesBasePath + s+ "/").listFiles();
			for (File file : listFiles) {
				if (file.getName().endsWith(cryslbinFileEnding)) {
					rules.add(CryptSLRuleReader.readFromFile(file));
				}
			}
		}else {
			if (new File(rulesBasePath + s + "/" + BIN_RULES_RELATIVE_PATH).exists()){	
				File[] listFiles = new File(rulesBasePath + s + "/" + BIN_RULES_RELATIVE_PATH +"/").listFiles();
				for (File file : listFiles) {
					if (file.getName().endsWith(cryslbinFileEnding)) {
						rules.add(CryptSLRuleReader.readFromFile(file));
					}
				}
			}else {
				File[] listFiles = new File(rulesBasePath + s + "/").listFiles();
				binRulesDir = CryslReaderUtils.createBinRulesDir(rulesBasePath + s + "/" + "/" + BIN_RULES_RELATIVE_PATH);
				if(binRulesDir != null) {
					for (File file : listFiles) {
						if(file.getName().endsWith(cryslFileEnding)) {
							CryptSLRule rule;
							try {
								rule = CryptSLRuleReader.readFromSourceFile(file);
								rules.add(rule);
								CryslReaderUtils.storeRuletoFile(rule, binRulesDir);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.out.println("Failed to store" + FilenameUtils.removeExtension(file.getName()) + ".crypslbin file in" + binRulesDir);
								e.printStackTrace();
							}
						}
					}
				}else {
					System.out.println("Failed to create directory for crypslbin files in" + rulesBasePath + s+ BIN_RULES_RELATIVE_PATH);
				}
			}	
		}
		return rules;
	}
	public static CryptSLRule makeSingleRule(String rulesBasePath, String ruleFormat, Ruleset ruleset, String rulename) {
		if (ruleFormat.equals("cryptslbin")) {
			File file = new File(rulesBasePath +"/"+ruleset+"/"+rulename + cryslbinFileEnding);
			if (!file.exists()) {
				throw new RuntimeException("Could not locate rule " + rulename +" within set " + ruleset );
			}
			return CryptSLRuleReader.readFromFile(file);
		}else {	
			String binRulePath = rulesBasePath + "/" + ruleset+ "/"+ BIN_RULES_RELATIVE_PATH;
			if(new File(binRulePath+"/"+rulename+cryslbinFileEnding).exists()) {
					return CryptSLRuleReader.readFromFile(new File(binRulePath+"/"+rulename+cryslbinFileEnding));
			}else {
				if (new File(rulesBasePath +"/"+ruleset+"/"+rulename + cryslFileEnding).exists()){
					binRulesDir = CryslReaderUtils.createBinRulesDir(binRulePath);
					if(binRulesDir != null) {
						try {
							CryptSLRule rule = CryptSLRuleReader.readFromSourceFile(new File(rulesBasePath +"/"+ruleset+"/"+rulename + cryslFileEnding));
							CryslReaderUtils.storeRuletoFile(rule,binRulesDir);
							return rule;		
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println("Failed to store" +rulename+".crypslbin file in "+ binRulesDir );
							e.printStackTrace();
						}
					}else {
						System.out.println("Failed to create directory for crypslbin files in" + binRulePath);
				}
			}
			return null;
		}
	}
	}
	public static List<CryptSLRule> makeFromPath(File resourcesPath, String ruleFormat){
		if (!resourcesPath.isDirectory())
			throw new RuntimeException("The specified path is not a directory" + resourcesPath);
		List<CryptSLRule> rules = Lists.newArrayList();
		if(ruleFormat.equals("cryptslbin")) {
			File[] listFiles = resourcesPath.listFiles();
			for (File file : listFiles) {
				if (file.getName().endsWith(cryslbinFileEnding)) {
					rules.add(CryptSLRuleReader.readFromFile(file));
				}
			}
		}else {
			if(new File(resourcesPath.getAbsolutePath()+BIN_RULES_RELATIVE_PATH).exists()){
				File[] listFiles = new File(resourcesPath.getAbsolutePath()+BIN_RULES_RELATIVE_PATH).listFiles();
				for (File file : listFiles) {
					if (file.getName().endsWith(cryslbinFileEnding)) {
						rules.add(CryptSLRuleReader.readFromFile(file));
					}
				}
			}else {
				File[] listFiles = resourcesPath.listFiles();
				binRulesDir=CryslReaderUtils.createBinRulesDir(resourcesPath.getAbsolutePath()+ "/" + BIN_RULES_RELATIVE_PATH);
				if(binRulesDir != null) {
					for (File file : listFiles) {
						if(file.getName().endsWith(cryslFileEnding)) {
							try {
								CryptSLRule rule = CryptSLRuleReader.readFromSourceFile(file);
								rules.add(rule);
								CryslReaderUtils.storeRuletoFile(rule, binRulesDir);
							} catch ( IOException e) {
								// TODO Auto-generated catch block
								System.out.println("Failed to store" + FilenameUtils.removeExtension(file.getName()) + ".crypslbin file in" + binRulesDir);
								e.printStackTrace();
							}
							
						}
					}
				}else {
					System.out.println("Failed to create directory for crypslbin files in" + resourcesPath.getAbsolutePath()+BIN_RULES_RELATIVE_PATH);
				}
			}
		}
		if (rules.isEmpty()) {
			System.out.println("No CrySL rules found in " + resourcesPath);
		}
		return rules;
	}
	}
