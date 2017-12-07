package crypto.analysis;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.BoomerangTimeoutException;
import boomerang.DefaultBoomerangOptions;
import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.boomerang.CogniCryptBoomerangOptions;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.typestate.CryptSLMethodToSootMethod;
import heros.utilities.DefaultValueMap;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import wpds.impl.Weight.NoWeight;

public abstract class CryptoScanner {

	private final LinkedList<IAnalysisSeed> worklist = Lists.newLinkedList();
	private final List<ClassSpecification> specifications = Lists.newLinkedList();
	private Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates = HashBasedTable.create();
	private Table<Statement, IAnalysisSeed, Set<EnsuredCryptSLPredicate>> existingPredicatesObjectBased = HashBasedTable.create();
	private Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicateObjectBased = HashBasedTable.create();
	private Set<Entry<CryptSLPredicate, CryptSLPredicate>> disallowedPredPairs = new HashSet<Entry<CryptSLPredicate, CryptSLPredicate>>();
	private CrySLAnalysisResultsAggregator resultsAggregator = new CrySLAnalysisResultsAggregator( null);
	

	private DefaultValueMap<Node<Statement,Val>, AnalysisSeedWithEnsuredPredicate> seedsWithoutSpec = new DefaultValueMap<Node<Statement,Val>, AnalysisSeedWithEnsuredPredicate>() {

		@Override
		protected AnalysisSeedWithEnsuredPredicate createItem(Node<Statement,Val> key) {
			return new AnalysisSeedWithEnsuredPredicate(CryptoScanner.this, key);
		}
	};
	private DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification> seedsWithSpec = new DefaultValueMap<AnalysisSeedWithSpecification, AnalysisSeedWithSpecification>() {

		@Override
		protected AnalysisSeedWithSpecification createItem(AnalysisSeedWithSpecification key) {
			return new AnalysisSeedWithSpecification(CryptoScanner.this, key.stmt(),key.var(), key.getSpec());
		}
	};

	public abstract BiDiInterproceduralCFG<Unit, SootMethod> icfg();

	public CrySLAnalysisResultsAggregator getAnalysisListener() {
		return resultsAggregator;
	};

	public abstract boolean isCommandLineMode();

	/**
	 * @return the existingPredicates
	 */
	public Set<EnsuredCryptSLPredicate> getExistingPredicates(Statement stmt, Val seed) {
		Set<EnsuredCryptSLPredicate> set = existingPredicates.get(stmt, seed);
		if (set == null) {
			set = Sets.newHashSet();
			existingPredicates.put(stmt, seed, set);
		}
		return set;
	}

	public CryptoScanner(List<CryptSLRule> specs) {
		CryptSLMethodToSootMethod.reset();
		for (CryptSLRule rule : specs) {
			specifications.add(new ClassSpecification(rule, this));
		}
	}

	public boolean addNewPred(IAnalysisSeed seedObj, Statement statement, Val seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(statement, seed);
		boolean added = set.add(ensPred);
		assert existingPredicates.get(statement, seed).contains(ensPred);
		if (added) {
			onPredicateAdded(seedObj, statement, seed, ensPred);
		}

		Set<EnsuredCryptSLPredicate> predsObjBased = existingPredicatesObjectBased.get(statement, seedObj);
		if (predsObjBased == null)
			predsObjBased = Sets.newHashSet();
		predsObjBased.add(ensPred);
		existingPredicatesObjectBased.put(statement, seedObj, predsObjBased);
		return added;
	}

	private void onPredicateAdded(IAnalysisSeed seedObj, Statement statement, Val seed, EnsuredCryptSLPredicate ensPred) {
		if (statement.isCallsite()) {
			InvokeExpr ivexpr = ((Stmt) statement.getUnit().get()).getInvokeExpr();
			if (ivexpr instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ivexpr;
				SootMethod method = iie.getMethod();
				SootMethod callerMethod = statement.getMethod();
				Value base = iie.getBase();
				boolean paramMatch = false;
				for (Value arg : iie.getArgs()) {
					if (seed.value() != null && seed.value().equals(arg))
						paramMatch = true;
				}
				if (paramMatch) {
					for (final ClassSpecification specification : specifications) {
						if (specification.getInvolvedMethods().contains(method)) {
							Boomerang boomerang = new Boomerang(new CogniCryptBoomerangOptions() {
								@Override
								public Optional<AllocVal> getAllocationVal(SootMethod m, Stmt stmt, Val fact,
										BiDiInterproceduralCFG<Unit, SootMethod> icfg) {
									if(stmt.containsInvokeExpr() && stmt instanceof AssignStmt){
										AssignStmt as = (AssignStmt) stmt;
										if(as.getLeftOp().equals(fact.value())){
											if(icfg.getCalleesOfCallAt(stmt).isEmpty())
												return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp()));
											//TODO replace by check if stmt is a seed of specification
											if(stmt.toString().contains("getInstance")){
												return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp()));
											}
										}
									}
									return super.getAllocationVal(m, stmt, fact, icfg);
								}
							}){
								@Override
								public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
									return CryptoScanner.this.icfg();
								}
							};
							Val val = new Val(base, callerMethod);
							BackwardQuery backwardQuery = new BackwardQuery(statement, val);
							resultsAggregator.boomerangQueryStarted(seedObj, backwardQuery);
							try{
								boomerang.solve(backwardQuery);
							} catch(BoomerangTimeoutException e){
								resultsAggregator.boomerangQueryTimeout(backwardQuery);
							}
							resultsAggregator.boomerangQueryFinished(seedObj, backwardQuery);
							Table<Statement, Val, NoWeight> results = boomerang.getResults(backwardQuery);
							for (Cell<Statement, Val, NoWeight> p : results.cellSet()) {
								AnalysisSeedWithSpecification seedWithSpec = getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(CryptoScanner.this, p.getRowKey(),p.getColumnKey(), specification));
								seedWithSpec.addEnsuredPredicate(ensPred);
							}
						}
					}
				}
			}

		}
	}

	public boolean deleteNewPred(Statement stmt, Val seed, EnsuredCryptSLPredicate ensPred) {
		Set<EnsuredCryptSLPredicate> set = getExistingPredicates(stmt, seed);
		boolean deleted = set.remove(ensPred);
		assert !existingPredicates.get(stmt, seed).contains(ensPred);
		return deleted;
	}

	public void scan() {
		getAnalysisListener().beforeAnalysis();
		initialize();
		System.out.println("Discovered "+worklist.size() + " analysis seeds!");
		while (!worklist.isEmpty()) {
			IAnalysisSeed curr = worklist.poll();
			getAnalysisListener().discoveredSeed(curr);
			curr.execute();
		}
//		IDebugger<TypestateDomainValue<StateNode>> debugger = debugger();
//		if (debugger instanceof CryptoVizDebugger) {
//			CryptoVizDebugger ideVizDebugger = (CryptoVizDebugger) debugger;
//			ideVizDebugger.addEnsuredPredicates(this.existingPredicates);
//		}
		checkForContradictions();
		for (AnalysisSeedWithSpecification seed : seedsWithSpec.values()) {
			Set<CryptSLPredicate> missingPredicates = seed.getMissingPredicates();
			getAnalysisListener().missingPredicates(seed, missingPredicates);
		}
		getAnalysisListener().ensuredPredicates(this.existingPredicates, expectedPredicateObjectBased, computeMissingPredicates());
		getAnalysisListener().afterAnalysis();
//		debugger().afterAnalysis();
	}

	private Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> computeMissingPredicates() {
		Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> res = HashBasedTable.create();
		for (Cell<Statement, IAnalysisSeed, Set<CryptSLPredicate>> c : expectedPredicateObjectBased.cellSet()) {
			Set<EnsuredCryptSLPredicate> exPreds = existingPredicatesObjectBased.get(c.getRowKey(), c.getColumnKey());
			if (c.getValue() == null)
				continue;
			HashSet<CryptSLPredicate> expectedPreds = new HashSet<>(c.getValue());
			if (exPreds == null) {
				exPreds = Sets.newHashSet();
			}
			for (EnsuredCryptSLPredicate p : exPreds) {
				expectedPreds.remove(p.getPredicate());
			}
			if (!expectedPreds.isEmpty()) {
				res.put(c.getRowKey(), c.getColumnKey(), expectedPreds);
			}
		}
		return res;
	}

	private void checkForContradictions() {
		for (Statement generatingPredicateStmt : expectedPredicateObjectBased.rowKeySet()) {
			for (Entry<Val, Set<EnsuredCryptSLPredicate>> exPredCell : existingPredicates.row(generatingPredicateStmt).entrySet()) {
				Set<String> preds = new HashSet<String>();
				for (EnsuredCryptSLPredicate exPred : exPredCell.getValue()) {
					preds.add(exPred.getPredicate().getPredName());
				}
				for (Entry<CryptSLPredicate, CryptSLPredicate> disPair : disallowedPredPairs) {
					if (preds.contains(disPair.getKey().getPredName()) && preds.contains(disPair.getValue().getPredName())) {
						getAnalysisListener().predicateContradiction(new Node<Statement,Val>(generatingPredicateStmt, exPredCell.getKey()), disPair);
					}
				}
			}

		}

	}

	private void initialize() {
		for (ClassSpecification spec : getClassSpecifictions()) {
			spec.checkForForbiddenMethods();
			if (!isCommandLineMode() && !spec.isLeafRule())
				continue;

			for (Query seed : spec.getInitialSeeds()) {
				getOrCreateSeedWithSpec(new AnalysisSeedWithSpecification(this, seed.stmt(),seed.var(),spec));
			}
		}
	}

	public List<ClassSpecification> getClassSpecifictions() {
		return specifications;
	}

	protected void addToWorkList(IAnalysisSeed analysisSeedWithSpecification) {
		worklist.add(analysisSeedWithSpecification);
	}

	public AnalysisSeedWithEnsuredPredicate getOrCreateSeed(Node<Statement,Val> factAtStatement) {
		boolean addToWorklist = false;
		if (!seedsWithoutSpec.containsKey(factAtStatement))
			addToWorklist = true;

		AnalysisSeedWithEnsuredPredicate seed = seedsWithoutSpec.getOrCreate(factAtStatement);
		if (addToWorklist)
			addToWorkList(seed);
		return seed;
	}

	public AnalysisSeedWithSpecification getOrCreateSeedWithSpec(AnalysisSeedWithSpecification factAtStatement) {
		boolean addToWorklist = false;
		if (!seedsWithSpec.containsKey(factAtStatement))
			addToWorklist = true;
		AnalysisSeedWithSpecification seed = seedsWithSpec.getOrCreate(factAtStatement);
		if (addToWorklist)
			addToWorkList(seed);
		return seed;
	}

	public void addDisallowedPredicatePair(CryptSLPredicate cryptSLPredicate, CryptSLPredicate cons) {
		disallowedPredPairs.add(new SimpleEntry<CryptSLPredicate, CryptSLPredicate>(cryptSLPredicate, cons));
	}

	public void expectPredicate(IAnalysisSeed object, Statement stmt, CryptSLPredicate predToBeEnsured) {
		for (Unit succ : icfg().getSuccsOf(stmt.getUnit().get())) {
			Set<CryptSLPredicate> set = expectedPredicateObjectBased.get(succ, object);
			if (set == null)
				set = Sets.newHashSet();
			set.add(predToBeEnsured);
			expectedPredicateObjectBased.put(new Statement((Stmt)succ,stmt.getMethod()), object, set);
		}
	}
	
	public Debugger<TransitionFunction> debugger() {
		return new Debugger<>();
	}
}
