package crypto.analysis;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import typestate.TypestateDomainValue;
import typestate.interfaces.ISLConstraint;

public class CogniCryptCLIReporter implements CryptSLAnalysisListener{
	private Set<IAnalysisSeed> analysisSeeds = Sets.newHashSet();
	private Set<IAnalysisSeed> typestateTimeouts = Sets.newHashSet();
	private Multimap<IAnalysisSeed,StmtWithMethod> reportedTypestateErros = HashMultimap.create();
	private Multimap<ClassSpecification,StmtWithMethod> callToForbiddenMethod = HashMultimap.create();
	private Multimap<AnalysisSeedWithSpecification,ISLConstraint> checkedConstraints = HashMultimap.create();
	private InterproceduralCFG<Unit, SootMethod> icfg;
	private Multimap<AnalysisSeedWithSpecification, CryptSLPredicate> missingPredicatesObjectBased = HashMultimap.create();
	private Multimap<AnalysisSeedWithSpecification, ISLConstraint> internalConstraintViolations = HashMultimap.create();
	private Table<Unit, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates = HashBasedTable.create();
	private Multimap<IFactAtStatement, Entry<CryptSLPredicate,CryptSLPredicate>> predicateContradictions = HashMultimap.create();
	private Stopwatch taintWatch = Stopwatch.createUnstarted();
	private Stopwatch typestateWatch = Stopwatch.createUnstarted();
	private Stopwatch boomerangWatch = Stopwatch.createUnstarted();
	private Multimap<IAnalysisSeed, Long> seedToTypestateAnalysisTime = HashMultimap.create();
	private Multimap<IAnalysisSeed, Long> seedToTaintAnalysisTime = HashMultimap.create();
	private Multimap<IAnalysisSeed, Long> seedToBoomerangAnalysisTime = HashMultimap.create();
	
	private File analyzedFile;
	
	public CogniCryptCLIReporter(InterproceduralCFG<Unit, SootMethod> icfg, File analyzedFile) {
		this.icfg = icfg;
		this.analyzedFile = analyzedFile;
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
		if(seed instanceof IAnalysisSeed){
			typestateTimeouts.add((IAnalysisSeed) seed);
		}
	}
	
	public Set<IAnalysisSeed> getTypestateTimeouts() {
		return typestateTimeouts;
	}
	
	private String object(IFactAtStatement seed){
		if(seed == null)
			return "";
		return String.format("%s\t %s\t %s",icfg.getMethodOf(seed.getStmt()), seed.getStmt(),seed.getFact());
	}
	
	@Override
	public String toString() {
		String s = (analyzedFile != null ? "Report for File: " + analyzedFile : ""); 
		s += "\n================SEEDS=======================\n";
		s+= "The following objects were analyzed with specifications: \n";
		s+= String.format("%s\t %s\t %s\n","Method","Statement","Variable");
		for(IAnalysisSeed seed : analysisSeeds){
			if(seed instanceof AnalysisSeedWithSpecification)
				s += object(seed)+"\n";
		}
		s+= "The following objects were analyzed without specifications: \n";
		s+= String.format("%s\t %s\t %s\n","Method","Statement","Variable");
		for(IAnalysisSeed seed : analysisSeeds){
			if(seed instanceof AnalysisSeedWithEnsuredPredicate)
				s += object(seed)+"\n";
		}
		s += "\n\n================CALL TO FORBIDDEN METHODS==================\n";
		if(reportedTypestateErros.isEmpty()){
			s += "No Calls to Forbidden Methods\n";
		} else {
			s += "The following methods are forbidden/deprecated and shall not be invoked\n";
			for(ClassSpecification spec : callToForbiddenMethod.keySet()){
				s += "\tViolations of specification for type " + spec.getRule().getClassName() +" \n";
				for(StmtWithMethod m : callToForbiddenMethod.get(spec)){
					s+= "\t\tMethod " + m.getMethod()+ " calls "+ m.getStmt() + "\n";  
				}
			}	
		}
		s += "\n\n================REPORTED TYPESTATE ERRORS==================\n";
		if(reportedTypestateErros.isEmpty())
			s += "No Typestate Errors found\n";
		else{
			s += "The following objects are not used according to the ORDER specification of the rules \n";
			for(IAnalysisSeed seed: reportedTypestateErros.keySet()){
				if(seed instanceof AnalysisSeedWithSpecification){
					AnalysisSeedWithSpecification seedWithSpec = (AnalysisSeedWithSpecification) seed;
					s += "\tSpecification type " + seedWithSpec.getSpec().getRule().getClassName() +"\n\t Object: \n";
					s+="\t\t" + object(seed)+"\n";
					for(StmtWithMethod stmtsWithMethod : reportedTypestateErros.get(seedWithSpec))
						s+= "\t\t\t " +stmtsWithMethod.getStmt() + " in method " + stmtsWithMethod.getMethod() +" \n";
				}
			}
		}
		s += "\n\n================REPORTED MISSING PREDICATES==================\n";
		if(missingPredicatesObjectBased.isEmpty())
			s += "No Missing Predicates found\n";
		else{	
			s += "The following REQUIRED PREDICATES are missing \n";
			for(AnalysisSeedWithSpecification seed: missingPredicatesObjectBased.keySet()){
				s += "\tSpecification type " + seed.getSpec().getRule().getClassName() +"\n\t Object: \n";
				s+="\t\t" + object(seed)+"\n";
				s += "\t\t expects the following predicates: \n";
				for(CryptSLPredicate pred : missingPredicatesObjectBased.get(seed)){
					s += "\t\t\t"+pred+"\n";
				}
			}
		}
		s += "\n\n================REPORTED VIOLATED INTERNAL CONSTRAINTS==================\n";
		if(internalConstraintViolations.isEmpty())
			s += "No Internal Constraint Violation found\n";
		else{
			s += "The following CONSTRAINTS are violated \n";
			for(AnalysisSeedWithSpecification seed: internalConstraintViolations.keySet()){
				s += "\tSpecification type " + seed.getSpec().getRule().getClassName() +"\n\t Object: \n";
				s+="\t\t" + object(seed)+"\n";
				s += "\t\t the analysis extracted the following statements that create the values \n";
				for(Entry<CallSiteWithParamIndex, Unit> e : seed.getExtractedValues().entries()){
					s += "\t\t\t"+e.getKey().getVarName() +" => "+ e.getValue() +"\n";
				}
				s += "\t\t that is mapped to the following value assignment \n";
				for(Entry<String, String> e : ConstraintSolver.convertToStringMultiMap(seed.getExtractedValues()).entries()){
					s += "\t\t\t"+e.getKey() +" => "+ e.getValue() +" \n";
				}
				s += "\t\t and contradicts following constraints(s) \n";
				for(ISLConstraint constraint :internalConstraintViolations.get(seed)){
					s+= "\t\t\t"+constraint +"\n";
				}
			}
		}
		
		s += "\n\n================REPORTED PREDICATE CONTRADICTION ==================\n";
		if(predicateContradictions.isEmpty())
			s += "No two predicates contradict\n";
		else{
			s += "The following object(s) holds two predicates that contradict \n";
			for(IFactAtStatement seed: predicateContradictions.keySet()){
				s += "\t Object " +object(seed) +"\n\t the two predicates contradict\n";
				for(Entry<CryptSLPredicate, CryptSLPredicate> contradiction : predicateContradictions.get(seed)){
					s += "\t\t" + contradiction.getKey() +" and " +contradiction.getValue() + "\n";
				}
			}
		}
		s += "\n\n================TIMEOUTS==================\n";
		if(typestateTimeouts.isEmpty())
			s += "No Seeds timed out\n";
		else{
			s += "The analysis for the following seed object(s) timed out (Budget: 30 seconds) \n";
			for(IFactAtStatement seed: typestateTimeouts){
				s+="\t" + object(seed)+"\n";
			}
		}
		s += "\n\n================MAXIMAL ACCESS GRAPH==================\n";
		s += "Length: " + AccessGraph.MAX_FIELD_COUNT + " Instance: "+ AccessGraph.MAX_ACCESS_GRAPH;
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

	public Multimap<AnalysisSeedWithSpecification, ISLConstraint> getInternalConstraintsViolations() {
		return this.internalConstraintViolations;
	}

	public Multimap<IFactAtStatement, Entry<CryptSLPredicate, CryptSLPredicate>> getPredicateContradictions() {
		return this.predicateContradictions;
	}

	@Override
	public void seedFinished(IAnalysisSeed seed) {
		if(seed instanceof AnalysisSeedWithEnsuredPredicate){
			taintWatch.stop();
			seedToTaintAnalysisTime.put(seed, taintWatch.elapsed(TimeUnit.MILLISECONDS));
		} else{
			typestateWatch.stop();
			seedToTypestateAnalysisTime.put(seed, typestateWatch.elapsed(TimeUnit.MILLISECONDS));
			seedToBoomerangAnalysisTime.put(seed, boomerangWatch.elapsed(TimeUnit.MILLISECONDS));
		}
	}

	@Override
	public void seedStarted(IAnalysisSeed seed) {
		boomerangWatch.reset();
		if(seed instanceof AnalysisSeedWithEnsuredPredicate){
			taintWatch.reset();
			taintWatch.start();
		} else{
			typestateWatch.reset();
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
	
	public Multimap<IAnalysisSeed, Long> getBoomerangTime(){
		return seedToBoomerangAnalysisTime;
	}
	
	public Multimap<IAnalysisSeed, Long> getTaintAnalysisTime(){
		return seedToTaintAnalysisTime;
	}
	
	public Multimap<IAnalysisSeed, Long> getTypestateAnalysisTime(){
		return seedToTypestateAnalysisTime;
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
		internalConstraintViolations.put(analysisSeedWithSpecification, con);
	}

	public Multimap<AnalysisSeedWithSpecification, ISLConstraint> getCheckedConstraints() {
		return checkedConstraints;
	}

	@Override
	public void checkedConstraints(AnalysisSeedWithSpecification seed,
			Collection<ISLConstraint> cons) {
		checkedConstraints.putAll(seed, cons);
	}

}
