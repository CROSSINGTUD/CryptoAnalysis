package crypto.providerdetection;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;

import com.google.common.collect.Lists;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.DefaultBoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.preanalysis.BoomerangPretransformer;
import boomerang.results.AbstractBoomerangResults;
import boomerang.results.BackwardBoomerangResults;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import soot.Body;
import soot.G;
import soot.Local;
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
import soot.JastAddJ.LabeledStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.options.Options;
import soot.toDex.LabelAssigner;
import wpds.impl.Weight.NoWeight;

public class ProviderDetection {
	
	public String provider = null;
	public String sootClassPath = null;
	public List<CryptSLRule> rules = Lists.newArrayList();
	
	public ProviderDetection() {
		//default constructor
	}
	
	//Used to initially test Provider Detection from the main folder
	public String getMainSootClassPath() {
		//Assume target folder to be directly in user dir; this should work in eclipse
		this.sootClassPath = System.getProperty("user.dir") + File.separator+"target"+File.separator+"classes";
		File classPathDir = new File(this.sootClassPath);
		if (!classPathDir.exists()){
			//We haven't found our target folder
			//Check if if it is in the boomerangPDS in user dir; this should work in IntelliJ
			this.sootClassPath = System.getProperty("user.dir") + File.separator + "boomerangPDS"+ File.separator+
					"target"+File.separator+"classes";
			classPathDir = new File(this.sootClassPath);
			if (!classPathDir.exists()){
				//We haven't found our bytecode anyway, notify now instead of starting analysis anyway
				throw new RuntimeException("Classpath could not be found.");
			}
		}
//		System.out.println(this.sootClassPath);
		return this.sootClassPath;
	}
	

	public String getSootClassPath(){
		//Assume target folder to be directly in user dir; this should work in eclipse
		this.sootClassPath = System.getProperty("user.dir") + File.separator+"target"+File.separator+"test-classes";
		File classPathDir = new File(this.sootClassPath);
		if (!classPathDir.exists()){
			//We haven't found our target folder
			//Check if if it is in the boomerangPDS in user dir; this should work in IntelliJ
			this.sootClassPath = System.getProperty("user.dir") + File.separator + "boomerangPDS"+ File.separator+
					"target"+File.separator+"test-classes";
			classPathDir = new File(this.sootClassPath);
			if (!classPathDir.exists()){
				//We haven't found our bytecode anyway, notify now instead of starting analysis anyway
				throw new RuntimeException("Classpath could not be found.");
			}
		}
		return this.sootClassPath;
	}
	
	
	public void setupSoot(String sootClassPath, String mainClass) {
		G.v().reset();
		Options.v().set_whole_program(true);
		Options.v().setPhaseOption("cg.spark", "on");
//		Options.v().setPhaseOption("cg", "all-reachable:true");
//		Options.v().setPhaseOption("cg.cha", "on");
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
		includeList.add("java.lang.*");
		includeList.add("java.util.*");
		includeList.add("java.io.*");
		includeList.add("sun.misc.*");
		includeList.add("java.net.*");
		includeList.add("javax.servlet.*");
		includeList.add("javax.crypto.*");

		Options.v().set_include(includeList);
		Options.v().set_full_resolver(true);
		Scene.v().loadNecessaryClasses();
		
		for(SootMethod m : c.getMethods()){
			System.out.println(m);
		}
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
				final JimpleBasedInterproceduralCFG icfg = new JimpleBasedInterproceduralCFG(false);
				String rulesDirectory = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources";
//				System.out.println("Rules dir is: "+rulesDirectory);
				rules = getRules(rulesDirectory);
//				System.out.println("The list size of the rules returned is: "+rules.size());
				doAnalysis(icfg, rules);
				 
			}
		};
	}


	public List<CryptSLRule> doAnalysis(JimpleBasedInterproceduralCFG icfg, List<CryptSLRule> rules) {
		
		outerloop:
		for(SootClass sootClass : Scene.v().getApplicationClasses()) {
			for(SootMethod sootMethod : sootClass.getMethods()) {
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
//								System.out.println(refName);
									
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
//									System.out.println(vl.toString());
									String strType = getProviderType(vl);
//									System.out.println("The provider used is of type: "+strType);
										
									if(strType.matches("java.security.Provider")) {
										this.provider = getProvider(stmt, sootMethod, vl, icfg);
										if(ruleExists(provider, refName)) {
											rules = chooseRules(rules, provider, refName);
											break outerloop;
										} 
									}
										
									else if (strType.matches("java.lang.String")) {
//										for(Unit u : body.getUnits()) {
//											if(u instanceof JIfStmt) {
//												JIfStmt l = (JIfStmt) u;
//												System.out.println("labeled: "+l.toString() + " "+l.getClass());
//												System.out.println(l.getUnitBoxes());
//											}
//										}
										
										this.provider = getProviderWhenTypeString(vl, body);
										if(ruleExists(provider, refName)) {
											rules = chooseRules(rules, provider, refName);
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
	
	
	
	//methods used from the "doAnalysis() method"
	//-----------------------------------------------------------------------------------------------------------------
	
	private String getProviderType(Value vl) {
		Type type = vl.getType();
//		System.out.println("Soot type: "+type);
		String strType = type.toString();
		return strType;
	}
	
	
	private String getProviderWhenTypeString(Value vl, Body body) {
		String provider = null;
		for(Unit u : body.getUnits()) {
			if(u instanceof JAssignStmt) {
				JAssignStmt s = (JAssignStmt) u;
				if(s.getLeftOp().equals(vl)) {
					String prov = s.getRightOp().toString().replaceAll("\"","");
					provider = prov;
				}
			}
		}
		
		return provider;
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
//		System.out.println("Map size is: "+map.size());
		
		for(Entry<ForwardQuery, AbstractBoomerangResults<NoWeight>.Context> entry : map.entrySet()) {
			ForwardQuery forwardQuery = entry.getKey();
			
			Val forwardQueryVal = forwardQuery.var();
			Value value = forwardQueryVal.value();
			Type valueType = value.getType();
			String valueTypeString = valueType.toString();
//			System.out.println(valueTypeString);
			
			if(valueTypeString.contains("BouncyCastleProvider")) {
				provider = "BC";
			}
			
			else if (valueTypeString.contains("BouncyCastlePQCProvider")) {
				provider = "BCPQC";
			}
		}
		
//		System.out.println(provider);
		return provider;
	}
	
	
	private List<CryptSLRule> chooseRules(List<CryptSLRule> rules, String provider, String refName) {
		
		//delete the default rules and load the new rules from the "Provider" directory
		rules.clear();
//		System.out.println("List size after rules are deleted is: "+rules.size());
		
		String newDirectory = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+provider;
		
		File[] listFiles = new File(newDirectory).listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		
//		System.out.println("After provider is detected, the rules size is: "+rules.size());
		return rules;
	}
	
	
	private boolean ruleExists(String provider, String refName) {
		boolean ruleExists = false;
		String rule = refName.substring(refName.lastIndexOf(".") + 1);
		
		File ruleDir = new File(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+provider);
		if(ruleDir.exists()) {
			File[] listRuleDirFiles = ruleDir.listFiles();
			for (File file : listRuleDirFiles) {
//				System.out.println(file.getName());
				if (file != null && file.getAbsolutePath().endsWith(rule+".cryptslbin")) {
					ruleExists = true;
				}
			}
		}
		
		
//		System.out.println(rule);
		return ruleExists;
	}
	
	
	private List<CryptSLRule> getRules(String rulesDirectory) {
		File directory = new File(rulesDirectory);
		
		File[] listFiles = directory.listFiles();
		for (File file : listFiles) {
			if (file != null && file.getName().endsWith(".cryptslbin")) {
				rules.add(CryptSLRuleReader.readFromFile(file));
			}
		}
		if (rules.isEmpty())
			System.out.println("Did not find any rules to start the analysis for. \n It checked for rules in "+ rulesDirectory);
		
		return rules;
	}
	//-----------------------------------------------------------------------------------------------------------------
}
