package de.fraunhofer.iem;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import crypto.analysis.*;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLPredicate;
import crypto.typestate.CallSiteWithParamIndex;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.interfaces.ISLConstraint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by johannesspath on 07.12.17.
 */
public class ErrorListener extends CrySLAnalysisListener {

    private final MyMojo myMojo;
    int error = 0;

    public ErrorListener(MyMojo myMojo) {
        this.myMojo = myMojo;
    }

    public boolean hasErrors(){
        return error > 0;
    }

    public void beforeAnalysis() {

    }

    public void afterAnalysis() {

    }

    public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {

    }

    public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {

    }

    public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {

    }

    public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {

    }

    public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
        myMojo.getLog().info("SEEED STARTS");

    }

    public void boomerangQueryStarted(Query seed, BackwardQuery q) {

    }

    public void boomerangQueryFinished(Query seed, BackwardQuery q) {

    }

    public void typestateErrorAt(AnalysisSeedWithSpecification classSpecification, Statement stmt, Collection<SootMethod> expectedCalls) {
        error++;
    }

    public void typestateErrorEndOfLifeCycle(AnalysisSeedWithSpecification classSpecification, Statement stmt) {

    }

    public void callToForbiddenMethod(ClassSpecification classSpecification, Statement callSite, List<CryptSLMethod> alternatives) {

    }

    public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCryptSLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> missingPredicates) {

    }

    public void predicateContradiction(Node<Statement, Val> node, Map.Entry<CryptSLPredicate, CryptSLPredicate> disPair) {

    }

    public void missingPredicates(AnalysisSeedWithSpecification seed, Set<CryptSLPredicate> missingPredicates) {

    }

    public void constraintViolation(AnalysisSeedWithSpecification analysisSeedWithSpecification, ISLConstraint con, Statement unit) {

    }

    public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification, Collection<ISLConstraint> relConstraints) {

    }

    public void onSeedTimeout(Node<Statement, Val> seed) {

    }

    public void onSeedFinished(IAnalysisSeed seed, WeightedBoomerang<TransitionFunction> solver) {

    }

    public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, Statement> collectedValues) {

    }

    public void discoveredSeed(IAnalysisSeed curr) {

    }
}
