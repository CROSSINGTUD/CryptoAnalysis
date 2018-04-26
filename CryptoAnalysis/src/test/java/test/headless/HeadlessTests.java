package test.headless;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class HeadlessTests {
	private int errorCount;
	private CrySLAnalysisListener errorCountingAnalysisListener;
	private File tempDir;
	private File tempSourcesDir;
	private File tempClassesDir;
	@Before
	public void setup(){
		errorCountingAnalysisListener = new CrySLAnalysisListener() {
			@Override
			public void unevaluableConstraint(AnalysisSeedWithSpecification seed, ISLConstraint con, Statement location) {
			}
			
			@Override
			public void reportError(AbstractError error) {
				errorCount++;
			}
			
			@Override
			public void predicateContradiction(Node<Statement, Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
			}
			
			@Override
			public void onSeedTimeout(Node<Statement, Val> seed) {
			}
			
			@Override
			public void onSeedFinished(IAnalysisSeed seed, Table<Statement, Val, TransitionFunction> solver) {
			}
			
			@Override
			public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates,
					Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
					Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
				
			}
			
			@Override
			public void discoveredSeed(IAnalysisSeed curr) {
				
			}
			
			@Override
			public void collectedValues(AnalysisSeedWithSpecification seed,
					Multimap<CallSiteWithParamIndex, Statement> collectedValues) {
			}
			
			@Override
			public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
					Collection<ISLConstraint> relConstraints) {
			}
			
			@Override
			public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
			}
			
			@Override
			public void boomerangQueryStarted(Query seed, BackwardQuery q) {
			}
			
			@Override
			public void boomerangQueryFinished(Query seed, BackwardQuery q) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}
			
			@Override
			public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}
			
			@Override
			public void beforeAnalysis() {
			}
			
			@Override
			public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}
			
			@Override
			public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
			}
			
			@Override
			public void afterAnalysis() {
			}
		};
	}
	@Test
	public void oracleExample(){
		createAnalysisFor("OracleExample");
	}
	private void createAnalysisFor(String packageName) {
		compile(packageName);
	}
	
	private void compile(String packageName) {
		tempDir = new File(
				"testTempDir");
		if(tempDir.exists())
			tempDir.delete();
		tempDir.mkdir();
		tempSourcesDir = new File(
				"testTempDir"+File.separator+"sources"+File.separator+"target"+File.separator+packageName);
		tempClassesDir = new File(
				"testTempDir"+File.separator+"classes");
		try {
			FileUtils.copyDirectory(new File("src"+File.separator+"test"+File.separator+"java"+File.separator+"target"+File.separator+packageName),tempSourcesDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 ProcessBuilder pb =
				   new ProcessBuilder("javac", "$(find . -name \"*.java\")", "-d",tempClassesDir.getAbsolutePath());
		try {
			pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@After
	public void deleteTempDir(){
		
	}
}
