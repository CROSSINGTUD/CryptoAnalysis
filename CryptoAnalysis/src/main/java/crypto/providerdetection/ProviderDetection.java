package crypto.providerdetection;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.DefaultBoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.callgraph.ObservableICFG;
import boomerang.scene.Val;
import boomerang.results.AbstractBoomerangResults;
import boomerang.results.BackwardBoomerangResults;
import boomerang.seedfactory.SeedFactory;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import wpds.impl.Weight.NoWeight;

/**
 * The ProviderDetection class helps in detecting the provider used when
 * coding with JCA's Cryptographic APIs and chooses the corresponding set of
 * CrySL rules that are implemented for that provider.
 *
 * @author  Enri Ozuni
 * 
 */
public class ProviderDetection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderDetection.class);
	
	private String provider = null;
	private String rulesDirectory = null;
	private final CrySLRuleReader reader;
	private static final String BOUNCY_CASTLE = "BouncyCastle";
	private static final String[] PROVIDER_VALUES = new String[] {"BC", "BCPQC", "BCJSSE"};
	private static final Set<String> SUPPORTED_PROVIDERS = new HashSet<>(Arrays.asList(PROVIDER_VALUES));

	public ProviderDetection(){
		this(new CrySLRuleReader());
	}

	public ProviderDetection(CrySLRuleReader reader){
		if (reader == null){
			throw new IllegalArgumentException("reader must not be null");
		}
		this.reader = reader;
	}
	
	public String getProvider() {
		return provider;
	}

	public String getRulesDirectory() {
		return rulesDirectory;
	}
	
	//This method is used for testing purposes.
	protected void setRulesDirectory(String rulesDirectory) {
		this.rulesDirectory = rulesDirectory;
	}
	
	
	/**
	 * This method does the Provider Detection analysis and returns the detected 
	 * provider after the analysis is finished. If no Provider is detected, 
	 * then it will return null value, meaning that there was no provider used.
	 * 
	 * @param observableDynamicICFG observableDynamicICFG
	 *       
	 * @param rootRulesDirectory directory for the rules
	 * 
	 * @return the detected provider
	 */
	public String doAnalysis(ObservableICFG<Unit, SootMethod> observableDynamicICFG, String rootRulesDirectory) {
		
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
								
								if((methodName.matches("getInstance")) && (methodParameterCount==2) ) {
									// Gets the second parameter from getInstance() method, since it is the provider parameter
									Value providerValue = expression.getArg(1);
									String providerType = getProviderType(providerValue);
										
									if(providerType.matches("java.security.Provider")) {
										this.provider = getProviderWhenTypeProvider(statement, sootMethod, providerValue, observableDynamicICFG);
										if((this.provider != null) && (rulesExist(rootRulesDirectory+File.separator+this.provider))) {
											this.rulesDirectory = rootRulesDirectory+File.separator+this.provider;
											return this.provider;
										}
									}
										
									else if (providerType.matches("java.lang.String")) {
										if(SUPPORTED_PROVIDERS.contains(providerValue.toString().replaceAll("\"",""))) {
											if(providerValue.toString().replaceAll("\"","").contains("BC")) {
												return "BouncyCastle-JCA";
											}
										}
										this.provider = getProviderWhenTypeString(providerValue, body);
										
										// Gets the boolean value of whether the provider is passed
										// using IF-ELSE, SWITCH statements or TERNARY operators
										boolean ifStmt = checkIfStmt(providerValue, body);
										boolean switchStmt = checkSwitchStmt(providerValue, body);
										
										if((!ifStmt) && (!switchStmt) && (this.provider != null) && (rulesExist(rootRulesDirectory+File.separator+this.provider))) {
											this.rulesDirectory = rootRulesDirectory+File.separator+this.provider;
											return this.provider;
										}
									}
								}
							}
						}
					}
				}	
			}
		}
	 			
		return this.provider;
	}
	
	
	/**
	 * This method returns the type of Provider detected, since
	 * it can be either `java.security.Provider` or `java.lang.String`.
	 * 
	 * @param providerValue the value for the provider
	 * @return the provider type
	 */
	private String getProviderType(Value providerValue) {
		String providerType = providerValue.getType().toString();
		return providerType;
	}
	
	
	/**
	 * This method return the provider used when Provider detected is of type `java.security.Provider`.
	 * 
	 * @param statement statement
	 *            
	 * @param sootMethod soot method
	 *           
	 * @param providerValue provider value
	 *            
	 * @param icfg icfg
	 *            
	 * @return the provider
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
					provider = "BouncyCastle-JCA";
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
	 * This method return the provider used when Provider detected is of type `java.lang.String`.
	 * 
	 * @param providerValue value for the provider
	 *            
	 * @param body - i.e. the ActiveBody
	 * 
	 * @return the provider
	 */
	private String getProviderWhenTypeString(Value providerValue, Body body) {
		for(Unit unit : body.getUnits()) {
			if(unit instanceof JAssignStmt) {
				JAssignStmt assignStatement = (JAssignStmt) unit;
				if(assignStatement.getLeftOp().equals(providerValue)) {
					String provider = assignStatement.getRightOp().toString().replaceAll("\"","");
					if(provider.equals("BC") || provider.equals("BCPQC") || provider.equals("BCJSSE")) {
						provider = "BouncyCastle-JCA";
						return provider;
					}
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
	 * @param providerValue value for the provider
	 *            
	 * @param body - i.e. the ActiveBody
	 *         
	 * @return true if the provider has only one allocation site and flows not
	 * 		   through IF-ELSE statements or TERNARY operators
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
	 * @param providerValue value for the provider
	 *            
	 * @param body - i.e. the ActiveBody
	 *            
	 * @return true if the provider detected has only one allocation site
	 * 		   and it is not flowing through SWITCH statements
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
	 * This method is used to check if the CryptSL rules from the detected Provider do exist.
	 * 
	 * @param providerRulesDirectory the path to the crysl rules
	 * 
	 * @return true if the CryptSL rules from the detected Provider do exist
	 */
	private boolean rulesExist(String providerRulesDirectory) {
		File rulesDirectory = new File(providerRulesDirectory);
		if(rulesDirectory.exists()) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * This method is used to choose the CryptSL rules in a directory from the detected provider and should
	 * be called after the `doAnalysis()` method.
	 *            
	 * @param providerRulesDirectory the path to the crysl rules
	 * 
	 * @return CryptSL rules from the detected provider
	 */
	public List<CrySLRule> chooseRules(String providerRulesDirectory) {
		List<CrySLRule> rules = Lists.newArrayList();
		this.rulesDirectory = providerRulesDirectory;
		try {
			rules.addAll(reader.readFromDirectory(new File(providerRulesDirectory)));
		} catch (CryptoAnalysisException e) {
			LOGGER.error("Error happened when getting the CrySL rules from the "
					+ "specified directory: "+providerRulesDirectory, e);
		}
		return rules;
	}
	
	/**
	 * This method is used to choose the CryptSL rules in a zip file from the detected provider and should
	 * be called after the `doAnalysis()` method.
	 *            
	 * @param providerRulesZip the path to the zip file
	 *          
	 * @return list of crysl rules in the zip file
	 */
	public List<CrySLRule> chooseRulesZip(String providerRulesZip) {
		List<CrySLRule> rules = Lists.newArrayList();
		this.rulesDirectory = providerRulesZip;
		try {
			rules.addAll(reader.readFromZipFile(new File(providerRulesZip)));
		} catch (CryptoAnalysisException e) {
			LOGGER.error("Error happened when getting the CrySL rules from the "
					+ "specified zip file: "+providerRulesZip, e);
		}
		return rules;
	}
		  
}
