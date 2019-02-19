package crypto;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.DefaultBoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.AbstractBoomerangResults;
import boomerang.results.BackwardBoomerangResults;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import wpds.impl.Weight.NoWeight;

public class ProviderDetection {
	
	public ProviderDetection() {
		//default constructor
	}
	
	public List<CryptSLRule> doAnalysis(JimpleBasedInterproceduralCFG icfg, List<CryptSLRule> rules) {
		
		Iterator<SootClass> iterator = Scene.v().getApplicationClasses().snapshotIterator();
		
		while(iterator.hasNext()) {
			
			for(SootMethod sootMethod : iterator.next().getMethods()) {
				if(sootMethod.hasActiveBody()) {
					Body body = sootMethod.getActiveBody();
				 
					for (Unit unit : body.getUnits()) {
						
						if(unit instanceof JAssignStmt) {
							JAssignStmt stmt = (JAssignStmt) unit;
							Value rightVal = stmt.getRightOp();
								
							if (rightVal instanceof JStaticInvokeExpr) {
									
								JStaticInvokeExpr exp = (JStaticInvokeExpr) rightVal;
									
								SootMethod method = exp.getMethod();
								String methodName = method.getName();
									
								SootClass methodRef = method.getDeclaringClass();
								String refName = methodRef.toString();
									
								int parameterCount = method.getParameterCount();
									
								//list of JCA engine classes that are supported as CryptSL rules
								String[] crySLRules = new String[] {"java.security.SecureRandom", "java.security.MessageDigest",
																	"java.security.Signature", "javax.crypto.Cipher", 
																	"javax.crypto.Mac", "javax.crypto.SecretKeyFactory",
																	"javax.crypto.KeyGenerator", "java.security.KeyPairGenerator",
																	"java.security.AlgorithmParameters", "java.security.KeyStore",
																	"javax.net.ssl.KeyManagerFactory", "javax.net.ssl.SSLContext",
																	"javax.net.ssl.TrustManagerFactory"};
									
								boolean ruleFound = Arrays.asList(crySLRules).contains(refName);
									
								if((ruleFound) && (methodName.matches("getInstance")) && (parameterCount==2) ) {
									Value vl = exp.getArg(1);
									String strType = getProviderType(vl);
										
									if(strType.matches("java.security.Provider")) {
										String provider = getProvider(stmt, method, vl, icfg);
										rules = chooseRules(rules, provider, refName); 
									}
										
									else if (strType.matches("java.lang.String")) {
										String provider = vl.toString();
										rules = chooseRules(rules, provider, refName);
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
	
	//methods used from the "doAnalysis() method"
	//-----------------------------------------------------------------------------------------------------------------
	
	private String getProviderType(Value vl) {
		Type type = vl.getType();
		String strType = type.toString();
		return strType;
	}
	
	
	private String getProvider(JAssignStmt stmt, SootMethod method, Value vl, JimpleBasedInterproceduralCFG icfg) {
		String provider = null;
		
		BackwardQuery query = new BackwardQuery(new Statement(stmt,method), new Val(vl, method));
		
		//Create a Boomerang solver.
		Boomerang solver = new Boomerang(new DefaultBoomerangOptions(){
			public boolean onTheFlyCallGraph() {
				//Must be turned of if no SeedFactory is specified.
				return false;
			};
		}) {
			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return icfg;
			}

			@Override
			public boomerang.seedfactory.SeedFactory<NoWeight> getSeedFactory() {
				return null;
			}
		};
		
		//Submit query to the solver.
		BackwardBoomerangResults<NoWeight> backwardQueryResults = solver.solve(query);
		solver.debugOutput();
		
		Map<ForwardQuery, AbstractBoomerangResults<NoWeight>.Context> map = backwardQueryResults.getAllocationSites();
		Entry<ForwardQuery, AbstractBoomerangResults<NoWeight>.Context> entry = map.entrySet().iterator().next();
		ForwardQuery forwardQuery = entry.getKey();
		
		Val forwardQueryVal = forwardQuery.var();
		Value value = forwardQueryVal.value();
		Type valueType = value.getType();
		String valueTypeString = valueType.toString();
		
		if(valueTypeString.contains("BouncyCastle")) {
			provider = "BC";
		}
		
		return provider;
	}
	
	
	private List<CryptSLRule> chooseRules(List<CryptSLRule> rules, String provider, String refName) {
		File rule = new File(".\\.\\.\\test\\resources\\"+provider+"\\"+refName+".cryptslbin");
		if(rule.exists()) {
			//delete the default rules and load the new rules from the "Provider" directory
			String newDirectory = ".\\.\\.\\test\\resources\\"+provider;
			
			rules.clear();
			
			File[] listFiles = new File(newDirectory).listFiles();
			for (File file : listFiles) {
				if (file != null && file.getName().endsWith(".cryptslbin")) {
					rules.add(CryptSLRuleReader.readFromFile(file));
				}
			}
		}
		
		return rules;
	}
	//-----------------------------------------------------------------------------------------------------------------
}
