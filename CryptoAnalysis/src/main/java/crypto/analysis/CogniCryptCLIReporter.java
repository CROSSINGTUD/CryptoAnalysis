package crypto.analysis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import boomerang.accessgraph.AccessGraph;
import boomerang.util.StmtWithMethod;
import crypto.rules.CryptSLPredicate;
import crypto.rules.StateNode;
import crypto.typestate.CallSiteWithParamIndex;
import crypto.typestate.CryptoTypestateAnaylsisProblem.AdditionalBoomerangQuery;
import crypto.typestate.ErrorStateNode;
import heros.InterproceduralCFG;
import ideal.AnalysisSolver;
import ideal.FactAtStatement;
import ideal.IFactAtStatement;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class CogniCryptCLIReporter implements CryptSLAnalysisListener{
	private Set<IAnalysisSeed> analysisSeeds = Sets.newHashSet();
	private Set<IFactAtStatement> typestateTimeouts = Sets.newHashSet();
	private Multimap<IAnalysisSeed,StmtWithMethod> reportedTypestateErros = HashMultimap.create();
	private Multimap<ClassSpecification,StmtWithMethod> callToForbiddenMethod = HashMultimap.create();
	private InterproceduralCFG<Unit, SootMethod> icfg;
	private Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> missingPredicatesObjectBased = HashMultimap.create();
	private Multimap<AnalysisSeedWithSpecification, ISLConstraint> missingInternalConstraints = HashMultimap.create();
	private Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates = HashBasedTable.create();
	private Multimap<IFactAtStatement, Entry<CryptSLPredicate,CryptSLPredicate>> predicateContradictions = HashMultimap.create();
	private Stopwatch taintWatch = Stopwatch.createUnstarted();
	private Stopwatch typestateWatch = Stopwatch.createUnstarted();
	private Stopwatch boomerangWatch = Stopwatch.createUnstarted();
	
	public CogniCryptCLIReporter(InterproceduralCFG<Unit, SootMethod> icfg) {
		this.icfg = icfg;
	}
	
	@Override
	public void onSeedFinished(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		for(SootMethod m : solver.getVisitedMethods()){
			if(!m.hasActiveBody())
				continue;
			for(Unit u : m.getActiveBody().getUnits()){
				Map<AccessGraph, TypestateDomainValue<StateNode>> resultsAt = solver.resultsAt(u);
				if(resultsAt == null)
					continue;
				for(Entry<AccessGraph, TypestateDomainValue<StateNode>> e : resultsAt.entrySet()){
					if(e.getValue().getStates().contains(ErrorStateNode.v()) && seed instanceof AnalysisSeedWithSpecification){
						typestateErrorAt((AnalysisSeedWithSpecification)seed, createStmtWithMethodFor(u));
					}
				}
			}
		}
		Multimap<Unit, AccessGraph> endPathOfPropagation = solver.getEndPathOfPropagation();
		for(Entry<Unit, AccessGraph> c : endPathOfPropagation.entries()){
			TypestateDomainValue<StateNode> resultAt = solver.resultAt(c.getKey(), c.getValue());
			if(resultAt == null)
				continue;
			
			for(StateNode n : resultAt.getStates()){
				if(!n.getAccepting()){
					typestateErrorAt((AnalysisSeedWithSpecification) seed, createStmtWithMethodFor(c.getKey()));
				}
			}
		}
	}
	
	private StmtWithMethod createStmtWithMethodFor(Unit u){
		return new StmtWithMethod(u,icfg.getMethodOf(u));
	}

	@Override
	public void collectedValues(AnalysisSeedWithSpecification seed,
			Multimap<CallSiteWithParamIndex, Unit> collectedValues) {
	}

	@Override
	public void callToForbiddenMethod(ClassSpecification classSpecification, Unit callSite) {
		callToForbiddenMethod.put(classSpecification, createStmtWithMethodFor(callSite));
	}
	
	public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, StmtWithMethod stmt){
		reportedTypestateErros.put(classSpecification, stmt);
	}
	@Override
	public void violateConstraint(ClassSpecification spec, Unit callSite) {
		
	}

	@Override
	public void discoveredSeed(IAnalysisSeed curr) {
		analysisSeeds.add(curr);
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

	@Override
	public void onSeedTimeout(IFactAtStatement seed) {
		typestateTimeouts.add(seed);
	}
	
	public Set<IFactAtStatement> getTypestateTimeouts() {
		return typestateTimeouts;
	}
	
	@Override
	public String toString() {
		String s = "================SEEDS=======================\n";
		s += Joiner.on("\n").join(analysisSeeds);
		s += "\n\n================CALL TO FORBIDDEN METHODS==================\n";
		s += Joiner.on("\n").join(callToForbiddenMethod.entries());

		s += "\n\n================REPORTED TYPESTATE ERRORS==================\n";
		s += Joiner.on("\n").join(reportedTypestateErros.entries());

		s += "\n\n================REPORTED MISSING PREDICATES==================\n";
		s += Joiner.on("\n").join(missingPredicatesObjectBased.asMap().entrySet());

		s += "\n\n================REPORTED VIOLATED INTERNAL CONSTRAINTS ==================\n";
		s += Joiner.on("\n").join(missingInternalConstraints.asMap().entrySet());
		
		s += "\n\n================REPORTED PREDICATE CONTRADICTION ==================\n";
		s += Joiner.on("\n").join(predicateContradictions.entries());
		
		s += "\n\n================Timeouts: ==================\n";
		s += Joiner.on("\n").join(typestateTimeouts);

		return s;
	}

	@Override
	public void ensuredPredicates(Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates,Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates,
			Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {
	}

	public Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> getExpectedPredicates() {
		return this.expectedPredicates;
	}
	public Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> getMissingPredicates() {
		return this.missingPredicatesObjectBased;
	}

	public Multimap<AnalysisSeedWithSpecification, ISLConstraint> getMissingInternalConstraints() {
		return this.missingInternalConstraints;
	}

	public Multimap<IFactAtStatement, Entry<CryptSLPredicate, CryptSLPredicate>> getPredicateContradictions() {
		return this.predicateContradictions;
	}

	@Override
	public void seedFinished(IAnalysisSeed seed) {
		if(seed instanceof AnalysisSeedWithEnsuredPredicate){
			taintWatch.stop();
		} else{
			typestateWatch.stop();
		}
	}

	@Override
	public void seedStarted(IAnalysisSeed seed) {
		if(seed instanceof AnalysisSeedWithEnsuredPredicate){
			taintWatch.start();
		} else{
			typestateWatch.start();
		}
	}

	@Override
	public void boomerangQueryStarted(IFactAtStatement seed, AdditionalBoomerangQuery q) {
		boomerangWatch.start();
	}

	@Override
	public void boomerangQueryFinished(IFactAtStatement seed, AdditionalBoomerangQuery q) {
		boomerangWatch.stop();
	}
	
	public long getBoomerangTime(TimeUnit desiredUnit){
		return boomerangWatch.elapsed(desiredUnit);
	}
	
	public long getTaintAnalysisTime(TimeUnit desiredUnit){
		return taintWatch.elapsed(desiredUnit);
	}
	
	public long getTypestateAnalysisTime(TimeUnit desiredUnit){
		return typestateWatch.elapsed(desiredUnit);
	}

	@Override
	public void predicateContradiction(Unit stmt, AccessGraph accessGraph, Entry<CryptSLPredicate, CryptSLPredicate> disPair) {
		predicateContradictions.put(new FactAtStatement(stmt,accessGraph), disPair);
	}

	@Override
	public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {
		missingPredicatesObjectBased.putAll(seed, missingPredicates);
	}

	@Override
	public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con) {
		missingInternalConstraints.put(analysisSeedWithSpecification, con);
	}

}
