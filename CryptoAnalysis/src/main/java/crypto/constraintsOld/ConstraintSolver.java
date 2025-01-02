package crypto.constraintsOld;

import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Statement;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.RequiredCrySLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractParameterAnalysisOld;
import crypto.extractparameter.ExtractParameterDefinition;
import crypto.listener.AnalysisReporter;
import crysl.rule.CrySLConstraint;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import crysl.rule.ICrySLPredicateParameter;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ConstraintSolver {

    public static final Collection<String> predefinedPreds =
            Arrays.asList(
                    "callTo", "noCallTo", "neverTypeOf", "length", "notHardCoded", "instanceOf");

    private final AnalysisSeedWithSpecification seed;
    private final Collection<Statement> collectedCalls;
    private final Collection<ISLConstraint> relConstraints;
    private final Collection<ISLConstraint> requiredPredicates;
    private final ExtractParameterAnalysisOld parameterAnalysis;

    public ConstraintSolver(AnalysisSeedWithSpecification seed) {
        this.seed = seed;

        this.collectedCalls = new HashSet<>();
        for (ControlFlowGraph.Edge edge : seed.getAllCallsOnObject().keySet()) {
            collectedCalls.add(edge.getStart());
        }

        relConstraints = new HashSet<>();
        requiredPredicates = new HashSet<>();

        ExtractParameterDefinition definition =
                new ExtractParameterDefinition() {
                    @Override
                    public CallGraph getCallGraph() {
                        return seed.getScanner().getCallGraph();
                    }

                    @Override
                    public DataFlowScope getDataFlowScope() {
                        return seed.getScanner().getDataFlowScope();
                    }

                    @Override
                    public Collection<Statement> getCollectedCalls() {
                        return collectedCalls;
                    }

                    @Override
                    public CrySLRule getRule() {
                        return seed.getSpecification();
                    }

                    @Override
                    public AnalysisReporter getAnalysisReporter() {
                        return seed.getScanner().getAnalysisReporter();
                    }

                    @Override
                    public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
                        return seed.getScanner().getSparsificationStrategy();
                    }

                    @Override
                    public int getTimeout() {
                        return seed.getScanner().getTimeout();
                    }
                };
        parameterAnalysis = new ExtractParameterAnalysisOld(definition);
    }

    /**
     * Evaluate the constraints from the CONSTRAINTS section
     *
     * @return the errors that violate the constraints
     */
    public Collection<AbstractError> evaluateConstraints() {
        // Run Boomerang to find all allocation sites
        extractValuesFromCollectedCalls();
        partitionConstraints();

        return evaluateRelConstraints();
    }

    private void extractValuesFromCollectedCalls() {
        parameterAnalysis.run();
        seed.getScanner()
                .getAnalysisReporter()
                .collectedValues(seed, parameterAnalysis.getExtractedValues());
    }

    public CrySLRule getSpecification() {
        return seed.getSpecification();
    }

    public Collection<Statement> getCollectedCalls() {
        return collectedCalls;
    }

    public AnalysisSeedWithSpecification getSeed() {
        return seed;
    }

    public Collection<CallSiteWithExtractedValue> getCollectedValues() {
        return parameterAnalysis.getExtractedValues();
    }

    public Collection<ISLConstraint> getRelConstraints() {
        return relConstraints;
    }

    public Collection<ISLConstraint> getRequiredPredicates() {
        return requiredPredicates;
    }

    private Collection<AbstractError> evaluateRelConstraints() {
        Collection<AbstractError> violatedConstraints = new HashSet<>();

        for (ISLConstraint con : getRelConstraints()) {
            EvaluableConstraint currentConstraint = EvaluableConstraint.getInstance(con, this);
            // currentConstraint.evaluate();

            violatedConstraints.addAll(currentConstraint.getErrors());
        }
        return violatedConstraints;
    }

    /**
     * Partitions the constraints into relevant constraints from the CONSTRAINTS section and
     * required predicate constraints from the REQUIRES section
     */
    private void partitionConstraints() {
        for (ISLConstraint cons : seed.getSpecification().getConstraints()) {
            Collection<String> involvedVarNames = new HashSet<>(cons.getInvolvedVarNames());

            for (CallSiteWithExtractedValue callSite : this.getCollectedValues()) {
                CallSiteWithParamIndex callSiteWithParamIndex = callSite.callSiteWithParam();
                involvedVarNames.remove(callSiteWithParamIndex.varName());
            }

            if (!involvedVarNames.isEmpty()) {
                continue;
            }

            if (cons instanceof CrySLPredicate predicate) {
                if (predefinedPreds.contains(predicate.getPredName())) {
                    relConstraints.add(predicate);
                    continue;
                }

                Collection<RequiredCrySLPredicate> preds = retrieveValuesForPred(predicate);

                for (RequiredCrySLPredicate pred : preds) {
                    CrySLPredicate innerPred = pred.getPred();

                    if (innerPred != null) {
                        relConstraints.add(innerPred);
                        requiredPredicates.add(pred);
                    }
                }
            } else if (cons instanceof CrySLConstraint constraint) {
                ISLConstraint left = constraint.getLeft();

                if (left instanceof CrySLPredicate
                        && !predefinedPreds.contains(((CrySLPredicate) left).getPredName())) {

                    List<CrySLPredicate> allAlts = new ArrayList<>();
                    extractAlternativePredicates(constraint, allAlts);
                    Collections.reverse(allAlts);

                    if (allAlts.isEmpty()) {
                        continue;
                    }

                    // Use the left pred as the base predicate to determine the statement
                    Collection<RequiredCrySLPredicate> basePreds =
                            retrieveValuesForPred(allAlts.get(0));
                    for (RequiredCrySLPredicate reqPred : basePreds) {
                        Collection<RequiredCrySLPredicate> relAlts =
                                getRelevantPredicates(reqPred, allAlts);

                        AlternativeReqPredicate altPred =
                                new AlternativeReqPredicate(reqPred, allAlts, relAlts);
                        requiredPredicates.add(altPred);
                    }
                } else {
                    relConstraints.add(cons);
                }
            } else {
                relConstraints.add(cons);
            }
        }
    }

    private void extractAlternativePredicates(CrySLConstraint cons, List<CrySLPredicate> alts) {
        CrySLPredicate left = (CrySLPredicate) cons.getLeft();
        alts.add(left);

        ISLConstraint right = cons.getRight();
        if (right instanceof CrySLPredicate predicate) {
            alts.add(predicate);
        } else if (right instanceof CrySLConstraint constraint) {
            extractAlternativePredicates(constraint, alts);
        }
    }

    private Collection<RequiredCrySLPredicate> getRelevantPredicates(
            RequiredCrySLPredicate basePred, Collection<CrySLPredicate> predicates) {
        Collection<RequiredCrySLPredicate> result = new HashSet<>();

        for (CrySLPredicate pred : predicates) {
            Collection<RequiredCrySLPredicate> reqPreds = retrieveValuesForPred(pred);

            for (RequiredCrySLPredicate reqPred : reqPreds) {
                if (reqPred.getLocation().equals(basePred.getLocation())) {
                    result.add(reqPred);
                }
            }
        }

        return result;
    }

    private Collection<RequiredCrySLPredicate> retrieveValuesForPred(CrySLPredicate pred) {
        Collection<RequiredCrySLPredicate> result = new ArrayList<>();

        for (CallSiteWithExtractedValue callSite : this.getCollectedValues()) {
            CallSiteWithParamIndex cwpi = callSite.callSiteWithParam();

            for (ICrySLPredicateParameter p : pred.getParameters()) {
                // TODO: FIX Cipher rule
                if (p.getName().equals("transformation")) {
                    continue;
                }

                // Predicates with _ can have any type
                if (cwpi.varName().equals("_")) {
                    continue;
                }

                if (cwpi.varName().equals(p.getName())) {
                    result.add(new RequiredCrySLPredicate(pred, cwpi.statement(), cwpi.index()));
                }
            }
        }

        // Extract predicates with 'this' as parameter
        if (pred.getParameters().stream().anyMatch(param -> param.getName().equals("this"))) {
            RequiredCrySLPredicate reqPred = new RequiredCrySLPredicate(pred, seed.getOrigin(), -1);

            if (!result.contains(reqPred)) {
                result.add(reqPred);
            }
        }

        return result;
    }
}
