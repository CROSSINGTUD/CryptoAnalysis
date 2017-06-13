package crypto.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;

import boomerang.AliasResults;
import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.CryptSLRule;
import crypto.rules.StateNode;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import heros.solver.Pair;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import ideal.debug.IDebugger;
import ideal.debug.NullDebugger;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import typestate.TypestateDomainValue;

public abstract class CryptoScanner {
	
	private final LinkedList<AnalysisSeedWithSpecification> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private AnalysisSeedWithSpecification curr;

	public CryptoScanner(List<CryptSLRule> specs){
		for (CryptSLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}		
	}
	
	
	public abstract IExtendedICFG icfg();
	public abstract CryptSLAnalysisListener analysisListener();

	public void scan(){
		initialize();
		Set<AnalysisSeedWithSpecification> visited = Sets.newHashSet();
		while(!worklist.isEmpty()){
			AnalysisSeedWithSpecification curr = worklist.poll();
			if(!visited.add(curr))
				continue;
			this.curr = curr;
			curr.execute();
		}
		debugger().afterAnalysis();
	}

	private void initialize() {
		for(ClassSpecification spec : getClassSpecifictions()){
			spec.checkForForbiddenMethods();
			if(!spec.isRootNode())
				continue;

			for(IFactAtStatement seed : spec.getInitialSeeds()){
				worklist.add(new AnalysisSeedWithSpecification(this, seed, spec));
			}
		}
	}

	
	
	public List<ClassSpecification> getClassSpecifictions() {
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
									worklist.add(new AnalysisSeedWithSpecification(CryptoScanner.this, new FactAtStatement(p.getO2().getSourceStmt(), p.getO2()), specification, curr));
								}
							}
						});
					}
				}
			}
			
		}
	}

	public IDebugger<TypestateDomainValue<StateNode>> debugger() {
		return new NullDebugger<>();
	}
}
