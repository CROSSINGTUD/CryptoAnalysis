/**
 * The ProviderDetection class helps in detecting the provider used when
 * coding with JCA's Cryptographic APIs and chooses the corresponding set of
 * CrySL rules that are implemented for that provider.
 *
 * @author  Enri Ozuni
 * 
 */
package crypto.providerdetection;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.DefaultBoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.preanalysis.BoomerangPretransformer;
import boomerang.results.AbstractBoomerangResults;
import boomerang.results.BackwardBoomerangResults;
import boomerang.seedfactory.SeedFactory;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import de.darmstadt.tu.crossing.crysl.rules.CrySLRule;
import de.darmstadt.tu.crossing.crysl.rules.CrySLRuleReader;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import soot.Body;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.options.Options;
import wpds.impl.Weight.NoWeight;

public class ProviderDetection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderDetection.class);
	
	private String provider = null;
	private String rulesDirectory = null;
	
	private static final Ruleset defaultRuleset = Ruleset.JavaCryptographicArchitecture;
	private static final String rootRulesDirectory = System.getProperty("user.dir")+File.separator+"src"+File.separator+"main"+File.separator+"resources";
	private static final String defaultRulesDirectory = rootRulesDirectory+File.separator+defaultRuleset;
	private static final String sootClassPath = System.getProperty("user.dir") + File.separator+"target"+File.separator+"test-classes";
	
	private static final String CRYSL = RuleFormat.SOURCE.toString();
	private static final String BOUNCY_CASTLE = "BouncyCastleProvider";
	
	
	public String getProvider() {
		return provider;
	}

	public String getRulesDirectory() {
		return rulesDirectory;
	}
	
	
	/**
	 * This method is used to get the Soot classpath from `src/test/java`
	 * in order to run the JUnit test cases for Provider Detection
	 */
	public String getSootClassPath(){
		//Assume target folder to be directly in user directory
		File classPathDir = new File(sootClassPath);
		if (!classPathDir.exists()){
			throw new RuntimeException("Classpath for the test cases could not be found.");
		}
		return sootClassPath;
	}
	
	/**
	 * This method is used to setup Soot
	 */
	public void setupSoot(String sootClassPath, String mainClass) {
		G.v().reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("cg.cha", "on");
//		Options.v().setPhaseOption("cg", "all-reachable:true");
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_soot_classpath(sootClassPath);
		SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
		if (c != null) {
			c.setApplicationClass();
		}

		List<String> includeList = new LinkedList<String>();
		includeList.add("java.lang.AbstractStringBuilder");
		includeList.add("java.lang.Boolean");
		includeList.add("java.lang.Byte");
		includeList.add("java.lang.Class");
		includeList.add("java.lang.Integer");
		includeList.add("java.lang.Long");
		includeList.add("java.lang.Object");
		includeList.add("java.lang.String");
		includeList.add("java.lang.StringCoding");
		includeList.add("java.lang.StringIndexOutOfBoundsException");

		Options.v().set_include(includeList);
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
	}
	
	
	public void analyze() {
		Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
		PackManager.v().getPack("wjtp").add(transform);
		PackManager.v().getPack("cg").apply();
		PackManager.v().getPack("wjtp").apply();
	}
	
	
	private Transformer createAnalysisTransformer() {
		return new SceneTransformer() {
			
			@Override
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				BoomerangPretransformer.v().reset();
				BoomerangPretransformer.v().apply();
				ObservableDynamicICFG observableDynamicICFG = new ObservableDynamicICFG(false);
				List<CrySLRule> defaultCryptoRules = Lists.newArrayList();
				defaultCryptoRules = CrySLRulesetSelector.makeFromPath(new File(defaultRulesDirectory), RuleFormat.SOURCE);
				doAnalysis(observableDynamicICFG, defaultCryptoRules);
			}
		};
	}

	
	/**
	 * This method does the Provider Detection analysis and returns the detected set 
	 * of CrySL rules after the analysis is finished. If no Provider is detected, 
	 * it returns the default set of CrySL rules. Otherwise it returns all CrySL 
	 * rules for that provider, plus additional default CrySL rules that were not 
	 * yet implemented for the detected provider
	 * 
	 * @param icfg
	 *            
	 * @param rules 
	 */
	public List<CrySLRule> doAnalysis(ObservableICFG<Unit, SootMethod> observableDynamicICFG, List<CrySLRule> rules) {
		
		outerloop:
		for(SootClass sootClass : Scene.v().getApplicationClasses()) {
			for(SootMethod sootMethod : sootClass.getMethods()) {
				if(sootMethod.hasActiveBody()) {
					Body body = sootMethod.getActiveBody();
					for (Unit unit : body.getUnits()) {
						if(unit instanceof JAssignStmt) {
							JAssignStmt statement = (JAssignStmt) unit;
							Value rightSideOfStatement = statement.getRightOp();
							if (rightSideOfStatement instanceof JStaticInvokeExpr) {
								JStaticInvokeExpr expression = (JStaticInvokeExpr) rightSideOfStatement;
									
								SootMethod method = expression.getMethod();
								String methodName = method.getName();
									
								SootClass declaringClass = method.getDeclaringClass();
								String declaringClassName = declaringClass.toString();
								declaringClassName = declaringClassName.substring(declaringClassName.lastIndexOf(".") + 1);
									
								int methodParameterCount = method.getParameterCount();
								
								// List of all CrySL rules
								List<String> availableCrySLRules = new ArrayList<String>();
								
								for(CrySLRule rule : rules) {
									String ruleName = rule.getClassName().substring(rule.getClassName().lastIndexOf(".") + 1);
									availableCrySLRules.add(ruleName);
								}
									
								// Checks if detected declaring class is implemented as a CrySL rule
								boolean ruleFound = availableCrySLRules.contains(declaringClassName);
									
								if((ruleFound) && (methodName.matches("getInstance")) && (methodParameterCount==2) ) {
									// Gets the second parameter from getInstance() method, since it is the provider parameter
									Value providerValue = expression.getArg(1);
									String providerType = getProviderType(providerValue);
										
									if(providerType.matches("java.security.Provider")) {
										this.provider = getProviderWhenTypeProvider(statement, sootMethod, providerValue, observableDynamicICFG);
										this.rulesDirectory = defaultRulesDirectory;
										
										if((this.provider != null) && (ruleExists(provider, declaringClassName))) {
											this.rulesDirectory = defaultRulesDirectory+File.separator+provider;
											
											rules = chooseRules(rules, provider, declaringClassName);
											break outerloop;
										}
									}
										
									else if (providerType.matches("java.lang.String")) {
										this.provider = getProviderWhenTypeString(providerValue, body);
										rulesDirectory = defaultRulesDirectory;
										
										// Gets the boolean value of whether the provider is passed
										// using IF-ELSE, SWITCH statements or TERNARY operators
										boolean ifStmt = checkIfStmt(providerValue, body);
										boolean switchStmt = checkSwitchStmt(providerValue, body);
										
										if((!ifStmt) && (!switchStmt) && (this.provider != null) && (ruleExists(provider, declaringClassName))) {
											rulesDirectory = defaultRulesDirectory+File.separator+provider;
											
											rules = chooseRules(rules, provider, declaringClassName);
											break outerloop;
										}
									}
								}
							}
						}
					}
				}	
			}
		}
	 			
		return rules;
	}
	
	
	
	// Methods used from the `doAnalysis()` method
	//-----------------------------------------------------------------------------------------------------------------
	
	/**
	 * This method returns the type of Provider detected, since
	 * it can be either `java.security.Provider` or `java.lang.String`
	 * 
	 * @param providerValue
	 */
	private String getProviderType(Value providerValue) {
		String providerType = providerValue.getType().toString();
		return providerType;
	}
	
	
	/**
	 * This method return the provider used when Provider detected is of type `java.security.Provider`
	 * 
	 * @param statement
	 *            
	 * @param sootMethod
	 *           
	 * @param providerValue
	 *            
	 * @param icfg
	 *            
	 */
	private String getProviderWhenTypeProvider(JAssignStmt statement, SootMethod sootMethod, Value providerValue, ObservableICFG<Unit, SootMethod> observableDynamicICFG) {
		String provider = null;
		
		//Create a Boomerang solver.
		Boomerang solver = new Boomerang(new DefaultBoomerangOptions(){
			public boolean onTheFlyCallGraph() {
				//Must be turned of if no SeedFactory is specified.
				return false;
			};
		}) {
			@Override
			public ObservableICFG<Unit, SootMethod> icfg() {
				return observableDynamicICFG;
			}

			@Override
			public SeedFactory<NoWeight> getSeedFactory() {
				return null;
			}
		};
		Map<ForwardQuery, AbstractBoomerangResults<NoWeight>.Context> map = Maps.newHashMap();
		for(Unit pred : observableDynamicICFG.getPredsOf(statement)) {
			//Create a backward query
			BackwardQuery query = new BackwardQuery(new Statement((Stmt) pred,sootMethod), new Val(providerValue, sootMethod));
			//Submit query to the solver.
			
			BackwardBoomerangResults<NoWeight> backwardQueryResults = solver.solve(query);
			map.putAll(backwardQueryResults.getAllocationSites());
		}
		
		// The Provider can be correctly detected from this static analysis, if there is only one allocation site
		// where the Provider variable was initialized. Otherwise, it throws an error because it is not possible
		// to detect for sure the provider, if is given as parameter to the getInstance() method through the use of
		// IF-ELSE, SWITCH statements or TERNARY operators
		if(map.size() == 1) {
			for(Entry<ForwardQuery, AbstractBoomerangResults<NoWeight>.Context> entry : map.entrySet()) {
				ForwardQuery forwardQuery = entry.getKey();
				
				Val forwardQueryVal = forwardQuery.var();
				Value value = forwardQueryVal.value();
				Type valueType = value.getType();
				String valueTypeString = valueType.toString();
				
				// In here are listed all the supported providers so far
				if(valueTypeString.contains(BOUNCY_CASTLE)) {
					provider = "BC";
				}
			}
		}
		else if (map.size() > 1) {
			LOGGER.error("The provider parameter must be passed directly to the"
					+ " getInstance() method call, and not through IF-ELSE, SWITCH statements or"
					+ " TERNARY operators.");
		}
		else {
			LOGGER.error("Error occured to detect provider in the Provider Detection"
					+ " analysis.");
		}
		return provider;
	}
	
	
	/**
	 * This method return the provider used when Provider detected is of type `java.lang.String`
	 * 
	 * @param providerValue
	 *            
	 * @param body
	 *            - i.e. the ActiveBody
	 *            
	 */
	private String getProviderWhenTypeString(Value providerValue, Body body) {
		for(Unit unit : body.getUnits()) {
			if(unit instanceof JAssignStmt) {
				JAssignStmt assignStatement = (JAssignStmt) unit;
				if(assignStatement.getLeftOp().equals(providerValue)) {
					return assignStatement.getRightOp().toString().replaceAll("\"","");
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * This method checks if the provider detected has only one allocation site
	 * and it is not flowing through IF-ELSE statements or TERNARY operators, because
	 * otherwise the provider can not be correctly detected through the use of
	 * static analysis. In case it has more than one allocation site, this method 
	 * return true.
	 * 
	 * @param providerValue
	 *            
	 * @param body
	 *            - i.e. the ActiveBody
	 *            
	 */
	private boolean checkIfStmt(Value providerValue, Body body) {
		String value = providerValue.toString();
		for(Unit unit : body.getUnits()) {
			if(unit instanceof JIfStmt) {
				JIfStmt ifStatement = (JIfStmt) unit;
				if(ifStatement.toString().contains(value)) {
					LOGGER.error("The provider parameter must be passed directly to the"
							+ " getInstance() method call, and not through IF-ELSE statements or"
							+ " TERNARY operators.");
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * This method checks if the provider detected has only one allocation site
	 * and it is not flowing through SWITCH statements, because otherwise the 
	 * provider can not be correctly detected through the use of static analysis.
	 * In case it has more than one allocation site, this method return true.
	 * 
	 * @param providerValue
	 *            
	 * @param body
	 *            - i.e. the ActiveBody
	 *            
	 */
	private boolean checkSwitchStmt(Value providerValue, Body body) {
		String value = providerValue.toString();
		for(Unit unit : body.getUnits()) {
			if(unit instanceof TableSwitchStmt) {
				TableSwitchStmt switchStatement = (TableSwitchStmt) unit;
				if(switchStatement.toString().contains(value)) {
					LOGGER.error("The provider parameter must be passed directly to the"
							+ " getInstance() method call, and not through SWITCH statements.");
					return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * This method is used to check if the CrySL rule for the detected provider exists
	 * 
	 * @param provider
	 *            - i.e. BC
	 * @param declaringClassName
	 *            - i.e. MessageDigest
	 */
	private boolean ruleExists(String provider, String declaringClassName) {
		boolean ruleExists = false;
		String rule = declaringClassName;
		
		File rulesDirectory = new File(defaultRulesDirectory+File.separator+provider);
		if(rulesDirectory.exists()) {
			File[] listRulesDirectoryFiles = rulesDirectory.listFiles();
			for (File file : listRulesDirectoryFiles) {
				if (file != null && file.getAbsolutePath().endsWith(rule+CRYSL)) {
					ruleExists = true;
					break;
				}
			}
		}
		
		return ruleExists;
	}
	
	
	/**
	 * This method is used to choose the CrySL rules from the detected Provider
	 * 
	 * @param rules
	 *            
	 * @param provider
	 *            - i.e. BC
	 * @param declaringClassName
	 * 			  - i.e. MessageDigest
	 */
	private List<CrySLRule> chooseRules(List<CrySLRule> rules, String provider, String declaringClassName) {
		
		String newRulesDirectory = defaultRulesDirectory+File.separator+provider;
		
		// Forms a list of all the new CrySL rules in the detected provider's directory.
		// This list contains only String elements and it holds only the rule's names, i.e Cipher, MessageDigest, etc
		List<String> newRules = new ArrayList<String>();
		File[] files = new File(newRulesDirectory).listFiles();
		for (File file : files) {
		    if (file.isFile() && file.getName().endsWith(CRYSL)) {
		        newRules.add(StringUtils.substringBefore(file.getName(), "."));
		    }
		}
		
		// A new CrySL rules list is created which will contain all the new rules.
		// Firstly, all the default rules that are not present in the detected provider's rules are added.
		// e.g if Cipher rule is not present in the detected provider's directory, then the default Cipher rule
		// is added to the new CrySL rules list
		List<CrySLRule> newCrySLRules = Lists.newArrayList();
		for(CrySLRule rule : rules) {
			String ruleName = rule.getClassName().substring(rule.getClassName().lastIndexOf(".") + 1);
			if(!newRules.contains(ruleName)) {
				newCrySLRules.add(rule);
			}
		}
		
		// At the end, the remaining CrySL rules from the detected provider's directory
		// are added to the new CrySL rules list
		File[] listFiles = new File(newRulesDirectory).listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(CRYSL)) {
				newCrySLRules.add(CrySLRuleReader.readFromSourceFile(file));
			}
		}
		return newCrySLRules;
	}
	
	
	/**
	 * This method is used to get all the default CrySL rules
	 * 
	 * @param rulesDirectory
	 * 
	 * @param rules
	 */
	private List<CrySLRule> getRules(String rulesDirectory, List<CrySLRule> rules) {
		File directory = new File(rulesDirectory);
		
		File[] listFiles = directory.listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(CRYSL)) {
				rules.add(CrySLRuleReader.readFromSourceFile(file));
			}
		}
		if (rules.isEmpty())
			System.out.println("Did not find any rules to start the analysis for. \n It checked for rules in "+ rulesDirectory);
		
		return rules;
	}
	//-----------------------------------------------------------------------------------------------------------------
}
