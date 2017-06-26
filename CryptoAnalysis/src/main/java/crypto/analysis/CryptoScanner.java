package crypto.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.synth.SynthSpinnerUI;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

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
	private Table<Unit, AnalysisSeedWithSpecification, Set<EnsuredCryptSLPredicate>> existingPredicates = HashBasedTable.create();
	
	/**
	 * @return the existingPredicates
	 */
	public Set<EnsuredCryptSLPredicate> getExistingPredicates(Unit stmt, AnalysisSeedWithSpecification seed) {
		Set<EnsuredCryptSLPredicate> set = existingPredicates.get(stmt,seed);
		if(set == null){
			set = Sets.newHashSet();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}


	public CryptoScanner(List<CryptSLRule> specs){
		for (CryptSLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}		
	}
	
	
	public abstract IExtendedICFG icfg();
	public abstract CryptSLAnalysisListener analysisListener();

	public boolean addNewPred(Unit stmt, AnalysisSeedWithSpecification seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(stmt, seed);
		boolean added = set.add(ensPred);
		assert existingPredicates.get(stmt,seed).contains(ensPred); 
		return added;
	}
	
	public boolean deleteNewPred(Unit stmt, AnalysisSeedWithSpecification seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(stmt, seed);
		boolean deleted = set.remove(ensPred);
		assert !existingPredicates.get(stmt,seed).contains(ensPred); 
		return deleted;
	}
	
	public void scan(){
		initialize();
		Set<AnalysisSeedWithSpecification> visited = Sets.newHashSet();
		while(!worklist.isEmpty()){
			AnalysisSeedWithSpecification curr = worklist.poll();
			if(!visited.add(curr))
				continue;
			analysisListener().discoveredSeed(curr);
			this.curr = curr;
			curr.execute();
		}
		IDebugger<TypestateDomainValue<StateNode>> debugger = debugger();
		if(debugger instanceof CryptoVizDebugger){
			CryptoVizDebugger ideVizDebugger = (CryptoVizDebugger) debugger;
			ideVizDebugger.addEnsuredPredicates(this.existingPredicates);
		}
		analysisListener().ensuredPredicates(this.existingPredicates);
		debugger().afterAnalysis();
	}

	private void initialize() {
		for(ClassSpecification spec : getClassSpecifictions()){
			spec.checkForForbiddenMethods();
			if(!spec.isRootNode())
				continue;

			for(IFactAtStatement seed : spec.getInitialSeeds()){
				addToWorkList(new AnalysisSeedWithSpecification(this, seed, icfg().getMethodOf(seed.getStmt()),spec));
			}
		}
	}

	
	
	public List<ClassSpecification> getClassSpecifictions() {
		return specifications;
	}

	public void onCallToReturnFlow(ClassSpecification classSpecification, AccessGraph d1, Unit callSite,
			AccessGraph d2) {
		if (callSite instanceof Stmt && ((Stmt) callSite).containsInvokeExpr()) {
			InvokeExpr ivexpr = ((Stmt) callSite).getInvokeExpr();
			if (ivexpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ivexpr;
				SootMethod method = iie.getMethod();
				Value base = iie.getBase();
				boolean paramMatch = false;
				for (Value arg : iie.getArgs()) {
					if (d2.getBase() != null && d2.getBase().equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for (final ClassSpecification specification : specifications) {
						if (classSpecification.equals(specification))
							continue;
						if (specification.getAnalysisProblem().getOrCreateTypestateChangeFunction()
								.getEdgeLabelMethods().contains(method)) {
							CryptoTypestateAnaylsisProblem problem = classSpecification.getAnalysisProblem();
							AdditionalBoomerangQuery query = problem.new AdditionalBoomerangQuery(d1, callSite,
									new AccessGraph((Local) base, base.getType()));
							classSpecification.getAnalysisProblem().addAdditionalBoomerangQuery(query,
									new CryptoTypestateAnaylsisProblem.QueryListener() {
										@Override
										public void solved(AdditionalBoomerangQuery q, AliasResults res) {
											for (Pair<Unit, AccessGraph> p : res.keySet()) {
												addToWorkList(new AnalysisSeedWithSpecification(CryptoScanner.this,
														new FactAtStatement(p.getO2().getSourceStmt(), p.getO2()),
														icfg().getMethodOf(p.getO2().getSourceStmt()), specification,
														curr));
											}
										}
									});
						}
					}
				}
			}

		}
	}

	protected void addToWorkList(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
		worklist.add(analysisSeedWithSpecification);
	}


	public IDebugger<TypestateDomainValue<StateNode>> debugger() {
		return new NullDebugger<>();
	}
}
