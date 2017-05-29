package crypto.analysis;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import crypto.DSL.CryptSLRule;
import crypto.statemachine.CryptoTypestateAnaylsisProblem;
import crypto.statemachine.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import heros.solver.Pair;
import ideal.FactAtStatement;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

public class SpecificationManager {

	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private final IInfoflowCFG icfg;
	private final LinkedList<AnalysisSeedWithSpecification> worklist;
	private final ErrorReporter errorReporter;

	public static final File SPECIFICATION_DIR = new File("src/test/resources/");
	public SpecificationManager(IInfoflowCFG icfg, LinkedList<AnalysisSeedWithSpecification> worklist, ErrorReporter errorReporter){
		this.icfg = icfg;
		this.worklist = worklist;
		this.errorReporter = errorReporter;
	}
	
	public void addSpecification(CryptSLRule spec) {
		specifications.add(new ClassSpecification(spec, icfg, this, errorReporter));
	}
	
	public List<ClassSpecification> getClassSpecifiction() {
		return specifications;
	}
	public void onCallToReturnFlow(ClassSpecification classSpecification, AccessGraph d1, Unit callSite, AccessGraph d2) {
		if(callSite instanceof Stmt && ((Stmt) callSite).containsInvokeExpr()){
			InvokeExpr ivexpr = ((Stmt) callSite).getInvokeExpr();
			if(ivexpr instanceof InstanceInvokeExpr){
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ivexpr;
				SootMethod method = iie.getMethod();
				Value base = iie.getBase();
				

				for(final ClassSpecification specification :specifications){
					if(classSpecification.equals(specification))
						continue;
					if(specification.getAnalysisProblem().getOrCreateTypestateChangeFunction().getAllMatchedMethods().contains(method)){
						CryptoTypestateAnaylsisProblem problem = classSpecification.getAnalysisProblem();
						AdditionalBoomerangQuery query = problem.new AdditionalBoomerangQuery(d1, callSite, new AccessGraph((Local) base, base.getType()));
						classSpecification.getAnalysisProblem().addAdditionalBoomerangQuery(query,new CryptoTypestateAnaylsisProblem.QueryListener() {
							@Override
							public void solved(AdditionalBoomerangQuery q, AliasResults res) {
								for(Pair<Unit, AccessGraph> p : res.keySet()){
									worklist.add(new AnalysisSeedWithSpecification(new FactAtStatement(p.getO2().getSourceStmt(), p.getO2()), specification));
								}
							}
						});
					}
				}
			}
			
		}
	}
}
