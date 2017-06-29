package crypto.analysis;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import boomerang.AliasFinder;
import boomerang.AliasResults;
import boomerang.BoomerangOptions;
import boomerang.accessgraph.AccessGraph;
import boomerang.allocationsitehandler.PrimitiveTypeAndReferenceType;
import boomerang.cfg.IExtendedICFG;
import boomerang.context.AllCallersRequester;
import boomerang.pointsofindirection.AllocationSiteHandlers;
import crypto.rules.CryptSLRule;
import crypto.rules.StateNode;
import crypto.typestate.CryptSLMethodToSootMethod;
import heros.solver.Pair;
import heros.utilities.DefaultValueMap;
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
	
	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates = HashBasedTable.create();
	private DefaultValueMap<IFactAtStatement, AnalysisSeedWithEnsuredPredicate> seedsWithoutSpec = new DefaultValueMap<IFactAtStatement, AnalysisSeedWithEnsuredPredicate>() {
		@Override
		protected AnalysisSeedWithEnsuredPredicate createItem(IFactAtStatement key) {
			return new AnalysisSeedWithEnsuredPredicate(CryptoScanner.this,key,icfg().getMethodOf(key.getStmt()));
		}
	};
	private DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification> seedsWithSpec = new DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification>() {
		@Override
		protected AnalysisSeedWithSpecification createItem(AnalysisSeedWithSpecification key) {
			return new AnalysisSeedWithSpecification(CryptoScanner.this,new FactAtStatement(key.getStmt(), key.getFact()),icfg().getMethodOf(key.getStmt()), key.getSpec());
		}
	};
	
	

	public abstract IExtendedICFG icfg();
	public abstract CryptSLAnalysisListener analysisListener();
	public abstract boolean isCommandLineMode();
	
	
	
	/**
	 * @return the existingPredicates
	 */
	public Set<EnsuredCryptSLPredicate> getExistingPredicates(Unit stmt, AccessGraph seed) {
		Set<EnsuredCryptSLPredicate> set = existingPredicates.get(stmt,seed);
		if(set == null){
			set = Sets.newHashSet();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}


	public CryptoScanner(List<CryptSLRule> specs){
		CryptSLMethodToSootMethod.reset();
		for (CryptSLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}		
	}
	
	public boolean addNewPred(Unit stmt, AccessGraph seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(stmt, seed);
		boolean added = set.add(ensPred);
		assert existingPredicates.get(stmt,seed).contains(ensPred); 
		if(added){
			onPredicateAdded(stmt, seed, ensPred);
		}

		return added;
	}
	
	private void onPredicateAdded(Unit stmt, AccessGraph seed, EnsuredCryptSLPredicate ensPred) {
		if (stmt instanceof Stmt && ((Stmt) stmt).containsInvokeExpr()) {
			InvokeExpr ivexpr = ((Stmt) stmt).getInvokeExpr();
			if (ivexpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ivexpr;
				SootMethod method = iie.getMethod();
				Value base = iie.getBase();
				boolean paramMatch = false;
				for (Value arg : iie.getArgs()) {
					if (seed.getBase() != null && seed.getBase().equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for (final ClassSpecification specification : specifications) {
						if (specification.getAnalysisProblem().getOrCreateTypestateChangeFunction()
								.getEdgeLabelMethods().contains(method)) {
							AliasFinder boomerang = new AliasFinder(new BoomerangOptions() {
								@Override
								public IExtendedICFG icfg() {
									return CryptoScanner.this.icfg();
								}
								
								@Override
								public AllocationSiteHandlers allocationSiteHandlers() {
									return new PrimitiveTypeAndReferenceType();
								}				
							});
							boomerang.startQuery();
							AccessGraph baseAccessGraph = new AccessGraph((Local) base, base.getType());
							AliasResults res = boomerang.findAliasAtStmt(baseAccessGraph, stmt, new AllCallersRequester());
							for (Pair<Unit, AccessGraph> p : res.keySet()) {
								AnalysisSeedWithSpecification seedWithSpec = getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(CryptoScanner.this,
										new FactAtStatement(p.getO2().getSourceStmt(), p.getO2().deriveWithoutAllocationSite()),
										icfg().getMethodOf(p.getO2().getSourceStmt()), specification));
								seedWithSpec.addEnsuredPredicate(ensPred);
							}
						}
					}
				}
			}

		}
	}


	public boolean deleteNewPred(Unit stmt, AccessGraph seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(stmt, seed);
		boolean deleted = set.remove(ensPred);
		assert !existingPredicates.get(stmt,seed).contains(ensPred); 
		return deleted;
	}
	
	public void scan(){
		initialize();
		while(!worklist.isEmpty()){
			IAnalysisSeed curr = worklist.poll();
			analysisListener().discoveredSeed(curr);
			curr.execute();
			if(!curr.isSolved())
				worklist.add(curr);
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
			if(!isCommandLineMode() && !spec.isLeafRule())
				continue;

			for(IFactAtStatement seed : spec.getInitialSeeds()){
				addToWorkList(new AnalysisSeedWithSpecification(this, seed, icfg().getMethodOf(seed.getStmt()),spec));
			}
		}
	}

	
	
	public List<ClassSpecification> getClassSpecifictions() {
		return specifications;
	}


	protected void addToWorkList(IAnalysisSeed analysisSeedWithSpecification) {
		worklist.add(analysisSeedWithSpecification);
	}


	public IDebugger<TypestateDomainValue<StateNode>> debugger() {
		return new NullDebugger<>();
	}


	public AnalysisSeedWithEnsuredPredicate getOrCreateSeed(FactAtStatement factAtStatement) {
		boolean addToWorklist = false;
		if(!seedsWithoutSpec.containsKey(factAtStatement))
			addToWorklist = true;
		AnalysisSeedWithEnsuredPredicate seed = seedsWithoutSpec.getOrCreate(factAtStatement);
		if(addToWorklist)
			addToWorkList(seed);
		return seed;
	}
	
	public AnalysisSeedWithSpecification getOrCreateSeedWithSpec(AnalysisSeedWithSpecification factAtStatement) {
		boolean addToWorklist = false;
		if(!seedsWithSpec.containsKey(factAtStatement))
			addToWorklist = true;
		AnalysisSeedWithSpecification seed = seedsWithSpec.getOrCreate(factAtStatement);
		if(addToWorklist)
			addToWorkList(seed);
		return seed;
	}
}
