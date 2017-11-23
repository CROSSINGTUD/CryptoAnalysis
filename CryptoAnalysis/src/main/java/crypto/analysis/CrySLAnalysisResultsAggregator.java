package crypto.analysis;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.util.StmtWithMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.ExtendedIDEALAnaylsis.AdditionalBoomerangQuery;
import heros.InterproceduralCFG;
import soot.SootMethod;
import soot.Unit;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

public class CrySLAnalysisResultsAggregator{

	private File analyzedFile;
	private InterproceduralCFG<Unit, SootMethod> icfg;
	
	protected Set<IAnalysisSeed> analysisSeeds = Sets.newHashSet();
	protected Set<IAnalysisSeed> typestateTimeouts = Sets.newHashSet();
	protected Multimap<IAnalysisSeed, StmtWithMethod> reportedTypestateErros = HashMultimap.create();
	protected Multimap<ClassSpecification, StmtWithMethod> callToForbiddenMethod = HashMultimap.create();
	protected Multimap<AnalysisSeedWithSpecification, ISLConstraint> checkedConstraints = HashMultimap.create();
	protected Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> missingPredicatesObjectBased = HashMultimap.create();
	protected Multimap<AnalysisSeedWithSpecification, ISLConstraint> internalConstraintViolations = HashMultimap.create();
	protected Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates = HashBasedTable.create();
	protected Multimap<Node<Statement,Val>, Entry<CryptSLPredicate, CryptSLPredicate>> predicateContradictions = HashMultimap.create();
	protected Stopwatch taintWatch = Stopwatch.createUnstarted();
	protected Stopwatch typestateWatch = Stopwatch.createUnstarted();
	protected Stopwatch boomerangWatch = Stopwatch.createUnstarted();
	protected Stopwatch constraintWatch = Stopwatch.createUnstarted();
	protected Stopwatch predicateWatch = Stopwatch.createUnstarted();
	protected Stopwatch totalAnalysisTime = Stopwatch.createUnstarted();
	protected Multimap<IAnalysisSeed, Long> seedToTypestateAnalysisTime = HashMultimap.create();
	protected Multimap<IAnalysisSeed, Long> seedToTaintAnalysisTime = HashMultimap.create();
	protected Multimap<IAnalysisSeed, Long> seedToBoomerangAnalysisTime = HashMultimap.create();
	protected Multimap<IAnalysisSeed, Long> seedToConstraintTime = HashMultimap.create();
	protected Multimap<IAnalysisSeed, Long> seedToPredicateTime = HashMultimap.create();
	protected CrySLResultsReporter crr = new CrySLResultsReporter();
	
	public CrySLAnalysisResultsAggregator(InterproceduralCFG<Unit, SootMethod> icfg, File analyzedFile) {
		this.icfg = icfg;
		this.analyzedFile = analyzedFile;
	}
	
	public void onSeedFinished(IAnalysisSeed seed, WeightedBoomerang<TransitionFunction> solver) {
		crr.onSeedFinished(seed, solver);
	}

	public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Unit> collectedValues) {
		crr.collectedValues(seed, collectedValues);
	}

	public void callToForbiddenMethod(ClassSpecification classSpecification, StmtWithMethod callSite, List<CryptSLMethod> alternatives) {
		callToForbiddenMethod.put(classSpecification, callSite);
		crr.callToForbiddenMethod(classSpecification, callSite,alternatives);
	}

	public void discoveredSeed(IAnalysisSeed curr) {
		analysisSeeds.add(curr);
		crr.discoveredSeed(curr);
	}

	protected StmtWithMethod createStmtWithMethodFor(Unit u) {
		return new StmtWithMethod(u, icfg.getMethodOf(u));
	}

	public void seedFinished(IAnalysisSeed seed) {
		if (seed instanceof AnalysisSeedWithEnsuredPredicate) {
			if (taintWatch.isRunning()) {
				taintWatch.stop();
				seedToTaintAnalysisTime.put(seed, taintWatch.elapsed(TimeUnit.MILLISECONDS));
			}
		} else {
			if (typestateWatch.isRunning()) {
				typestateWatch.stop();
				seedToTypestateAnalysisTime.put(seed, typestateWatch.elapsed(TimeUnit.MILLISECONDS));
				seedToBoomerangAnalysisTime.put(seed, boomerangWatch.elapsed(TimeUnit.MILLISECONDS));
			}
		}

		crr.seedFinished(seed);
	}

	public void seedStarted(IAnalysisSeed seed) {
		boomerangWatch.reset();
		constraintWatch.reset();
		predicateWatch.reset();
		if (seed instanceof AnalysisSeedWithEnsuredPredicate) {
			taintWatch.reset();
			taintWatch.start();
		} else {
			typestateWatch.reset();
			typestateWatch.start();
		}
		crr.seedStarted(seed);
	}

	public void boomerangQueryStarted(Query seed, AdditionalBoomerangQuery q) {
		boomerangWatch.start();
		crr.boomerangQueryStarted(seed, q);
	}

	public void boomerangQueryFinished(Query seed, AdditionalBoomerangQuery q) {
		if (boomerangWatch.isRunning()) {
			boomerangWatch.stop();
		}
		crr.boomerangQueryFinished(seed, q);
	}

	public void predicateContradiction(Node<Statement,Val> node, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
		predicateContradictions.put(node, disPair);
		crr.predicateContradiction(node, disPair);
	}

	public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {
		missingPredicatesObjectBased.putAll(seed, missingPredicates);
		crr.missingPredicates(seed, missingPredicates);
	}

	public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, StmtWithMethod unit) {
		internalConstraintViolations.put(analysisSeedWithSpecification, con);
		crr.constraintViolation(analysisSeedWithSpecification, con, unit);
	}

	public Multimap<AnalysisSeedWithSpecification, ISLConstraint> getCheckedConstraints() {
		return checkedConstraints;
	}
	
	public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt) {
		crr.typestateErrorEndOfLifeCycle(classSpecification, stmt);
	}
	
	public void checkedConstraints(AnalysisSeedWithSpecification seed, Collection<ISLConstraint> cons) {
		checkedConstraints.putAll(seed, cons);
		crr.checkedConstraints(seed, cons);
	}

	public void beforeAnalysis() {
		totalAnalysisTime.start();
		crr.beforeAnalysis();
	}

	public void afterAnalysis() {
		if (totalAnalysisTime.isRunning()) {
			totalAnalysisTime.stop();
		}
		crr.afterAnalysis();
	}

	public void beforeConstraintCheck(AnalysisSeedWithSpecification seed) {
		constraintWatch.start();
		crr.beforeConstraintCheck(seed);
	}

	public void afterConstraintCheck(AnalysisSeedWithSpecification seed) {
		if (constraintWatch.isRunning())
			constraintWatch.stop();
		seedToConstraintTime.put(seed, constraintWatch.elapsed(TimeUnit.MILLISECONDS));
		crr.afterConstraintCheck(seed);
	}

	public void beforePredicateCheck(AnalysisSeedWithSpecification seed) {
		predicateWatch.start();
		crr.beforePredicateCheck(seed);
	}

	public void afterPredicateCheck(AnalysisSeedWithSpecification seed) {
		if (predicateWatch.isRunning())
			predicateWatch.stop();
		seedToPredicateTime.put(seed, predicateWatch.elapsed(TimeUnit.MILLISECONDS));
		crr.afterPredicateCheck(seed);
	}

	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt, Set<SootMethod> expectedMethodCalls) {
		reportedTypestateErros.put(classSpecification, stmt);
		crr.typestateErrorAt(classSpecification, stmt, expectedMethodCalls);
	}

	public Multimap<IAnalysisSeed, StmtWithMethod> getTypestateErrors() {
		return reportedTypestateErros;
	}

	public Multimap<ClassSpecification, StmtWithMethod> getCallToForbiddenMethod() {
		return callToForbiddenMethod;
	}

	public Set<IAnalysisSeed> getAnalysisSeeds() {
		return analysisSeeds;
	}

	public Set<IAnalysisSeed> getTypestateTimeouts() {
		return typestateTimeouts;
	}

	public Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> getExpectedPredicates() {
		return this.expectedPredicates;
	}

	public Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> getMissingPredicates() {
		return this.missingPredicatesObjectBased;
	}

	public Multimap<AnalysisSeedWithSpecification, ISLConstraint> getInternalConstraintsViolations() {
		return this.internalConstraintViolations;
	}

	public Multimap<Node<Statement,Val>, Entry<CryptSLPredicate, CryptSLPredicate>> getPredicateContradictions() {
		return this.predicateContradictions;
	}

	public Multimap<IAnalysisSeed, Long> getBoomerangTime() {
		return seedToBoomerangAnalysisTime;
	}

	public Multimap<IAnalysisSeed, Long> getTaintAnalysisTime() {
		return seedToTaintAnalysisTime;
	}

	public Multimap<IAnalysisSeed, Long> getTypestateAnalysisTime() {
		return seedToTypestateAnalysisTime;
	}

	public Multimap<IAnalysisSeed, Long> getConstraintSolvingTime() {
		return seedToConstraintTime;
	}

	public Multimap<IAnalysisSeed, Long> getPredicateSolvingTime() {
		return seedToPredicateTime;
	}

	public long getTotalAnalysisTime(TimeUnit unit) {
		return totalAnalysisTime.elapsed(unit);
	}

	public void addReportListener(CrySLAnalysisListener reporter) {
		crr.addReportListener(reporter);
	}

	public void removeReportListener(CrySLAnalysisListener reporter) {
		crr.removeReportListener(reporter);
	}

	private String object(Node<Statement,Val> seed) {
		if (seed == null)
			return "";
		return String.format("%s\t %s\t %s", seed.stmt().getMethod(), seed.stmt(), seed.fact());
	}

	@Override
	public String toString() {
		String s = (analyzedFile != null ? "Report for File: " + analyzedFile : "");
		s += "\n================SEEDS=======================\n";
		s += "The following objects were analyzed with specifications: \n";
		s += String.format("%s\t %s\t %s\n", "Method", "Statement", "Variable");
		for (IAnalysisSeed seed : analysisSeeds) {
			if (seed instanceof AnalysisSeedWithSpecification)
				s += object(seed.asNode()) + "\n";
		}
		s += "The following objects were analyzed without specifications: \n";
		s += String.format("%s\t %s\t %s\n", "Method", "Statement", "Variable");
		for (IAnalysisSeed seed : analysisSeeds) {
			if (seed instanceof AnalysisSeedWithEnsuredPredicate)
				s += object(seed.asNode()) + "\n";
		}
		s += "\n\n================CALL TO FORBIDDEN METHODS==================\n";
		if (reportedTypestateErros.isEmpty()) {
			s += "No Calls to Forbidden Methods\n";
		} else {
			s += "The following methods are forbidden/deprecated and shall not be invoked\n";
			for (ClassSpecification spec : callToForbiddenMethod.keySet()) {
				s += "\tViolations of specification for type " + spec.getRule().getClassName() + " \n";
				for (StmtWithMethod m : callToForbiddenMethod.get(spec)) {
					s += "\t\tMethod " + m.getMethod() + " calls " + m.getStmt() + "\n";
				}
			}
		}
		s += "\n\n================REPORTED TYPESTATE ERRORS==================\n";
		if (reportedTypestateErros.isEmpty())
			s += "No Typestate Errors found\n";
		else {
			s += "The following objects are not used according to the ORDER specification of the rules \n";
			for (IAnalysisSeed seed : reportedTypestateErros.keySet()) {
				if (seed instanceof AnalysisSeedWithSpecification) {
					AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) seed;
					s += "\tSpecification type " + seedWithSpec.getSpec().getRule().getClassName() + "\n\t Object: \n";
					s += "\t\t" + object(seed.asNode()) + "\n";
					for (StmtWithMethod stmtsWithMethod : reportedTypestateErros.get(seedWithSpec))
						s += "\t\t\t " + stmtsWithMethod.getStmt() + " in method " + stmtsWithMethod.getMethod() + " \n";
				}
			}
		}
		s += "\n\n================REPORTED MISSING PREDICATES==================\n";
		if (missingPredicatesObjectBased.isEmpty())
			s += "No Missing Predicates found\n";
		else {
			s += "The following REQUIRED PREDICATES are missing \n";
			for (AnalysisSeedWithSpecification seed : missingPredicatesObjectBased.keySet()) {
				s += "\tSpecification type " + seed.getSpec().getRule().getClassName() + "\n\t Object: \n";
				s += "\t\t" + object(seed.asNode()) + "\n";
				s += "\t\t expects the following predicates: \n";
				for (CryptSLPredicate pred : missingPredicatesObjectBased.get(seed)) {
					s += "\t\t\t" + pred + "\n";
				}
			}
		}
		s += "\n\n================REPORTED VIOLATED INTERNAL CONSTRAINTS==================\n";
		if (internalConstraintViolations.isEmpty())
			s += "No Internal Constraint Violation found\n";
		else {
			s += "The following CONSTRAINTS are violated \n";
			for (AnalysisSeedWithSpecification seed : internalConstraintViolations.keySet()) {
				s += "\tSpecification type " + seed.getSpec().getRule().getClassName() + "\n\t Object: \n";
				s += "\t\t" + object(seed.asNode()) + "\n";
				s += "\t\t the analysis extracted the following statements that create the values \n";
				for (Entry<CallSiteWithParamIndex, Unit> e : seed.getExtractedValues().entries()) {
					s += "\t\t\t" + e.getKey().getVarName() + " => " + e.getValue() + "\n";
				}
				s += "\t\t that is mapped to the following value assignment \n";
				for (Entry<String, String> e : ConstraintSolver.convertToStringMultiMap(seed.getExtractedValues()).entries()) {
					s += "\t\t\t" + e.getKey() + " => " + e.getValue() + " \n";
				}
				s += "\t\t and contradicts following constraints(s) \n";
				for (ISLConstraint constraint : internalConstraintViolations.get(seed)) {
					s += "\t\t\t" + constraint + "\n";
				}
			}
		}

		s += "\n\n================REPORTED PREDICATE CONTRADICTION ==================\n";
		if (predicateContradictions.isEmpty())
			s += "No two predicates contradict\n";
		else {
			s += "The following object(s) holds two predicates that contradict \n";
			for (Node<Statement, Val> seed : predicateContradictions.keySet()) {
				s += "\t Object " + object(seed) + "\n\t the two predicates contradict\n";
				for (Entry<CryptSLPredicate, CryptSLPredicate> contradiction : predicateContradictions.get(seed)) {
					s += "\t\t" + contradiction.getKey() + " and " + contradiction.getValue() + "\n";
				}
			}
		}
//		s += "\n\n================TIMEOUTS==================\n";
//		if (typestateTimeouts.isEmpty())
//			s += "No Seeds timed out\n";
//		else {
//			s += "The analysis for the following seed object(s) timed out (Budget: 30 seconds) \n";
//			for (IFactAtStatement seed : typestateTimeouts) {
//				s += "\t" + object(seed) + "\n";
//			}
//		}
		return s;
	}

	public void ensuredPredicates(Table<Unit, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
		crr.ensuredPredicates(existingPredicates, expectedPredicates, missingPredicates);
	}


}
