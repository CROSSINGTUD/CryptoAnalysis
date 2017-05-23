package crypto.analysis;

import java.io.File;
import java.util.LinkedList;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;

import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

public abstract class CryptoScanner {
	
	IInfoflowCFG icfg;
	LinkedList<AnalysisSeedWithSpecification> worklist = Lists.newLinkedList();
	

	private final SpecificationManager specManager;
	public CryptoScanner(){
		specManager = new SpecificationManager(icfg(), worklist, errorReporter());
		specManager.addSpecification(new File("Cipher.smg"));
		specManager.addSpecification(new File("KeyGenerator.smg"));
		specManager.addSpecification(new File("KeyPairGenerator.smg"));
//		specManager.addSpecification(new File("Mac.smg"));
		specManager.addSpecification(new File("MessageDigest.smg"));
		specManager.addSpecification(new File("PBEKeySpec.smg"));
//		specManager.addSpecification(new File("SecretKeyFactory.smg"));
	}
	
	
	public abstract IInfoflowCFG icfg();
	public abstract ErrorReporter errorReporter();

	public void scan(){
		initialize();
		Set<AnalysisSeedWithSpecification> visited = Sets.newHashSet();
		while(!worklist.isEmpty()){
			AnalysisSeedWithSpecification curr = worklist.poll();
			if(!visited.add(curr))
				continue;
			System.out.println(curr);
			curr.spec.runTypestateAnalysisForConcreteSeed(curr.factAtStmt);
		}
	}

	private void initialize() {
		for(ClassSpecification spec : specManager.getClassSpecifiction()){
			spec.checkForForbiddenMethods();
			if(!spec.isRootNode())
				continue;
			spec.runTypestateAnalysisForAllSeeds();
		}
	}
}
