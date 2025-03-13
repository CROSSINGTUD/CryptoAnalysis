package crypto.typestate;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.options.BoomerangOptions;
import boomerang.options.IAllocationSite;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.utils.MatcherUtils;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLPredicate;
import crysl.rule.CrySLRule;
import crysl.rule.ICrySLPredicateParameter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import wpds.impl.Weight;

public class TypestateAnalysisScope extends AnalysisScope {

    private final FrameworkScope frameworkScope;
    private final Map<String, RuleTransitions> ruleTransitions;

    public TypestateAnalysisScope(
            FrameworkScope frameworkScope, Map<String, RuleTransitions> ruleTransitions) {
        super(frameworkScope.getCallGraph());

        this.ruleTransitions = ruleTransitions;
        this.frameworkScope = frameworkScope;
    }

    @Override
    protected Collection<? extends Query> generate(ControlFlowGraph.Edge stmt) {
        Statement statement = stmt.getStart();

        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        // Check if method should not be analyzed TODO Move this to AnalysisScope
        if (frameworkScope.getDataFlowScope().isExcluded(statement.getMethod())) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getMethod();

        Collection<ForwardSeedQuery> discoveredSeeds = new HashSet<>();

        // Constructors
        if (declaredMethod.isConstructor()) {
            Collection<ForwardSeedQuery> constructorSeeds =
                    computeSeedsFromConstructor(stmt, statement);
            discoveredSeeds.addAll(constructorSeeds);
        }

        // Invoke statements from instances
        if (invokeExpr.isInstanceInvokeExpr()) {
            String baseType = invokeExpr.getBase().getType().toString();

            Collection<ForwardSeedQuery> instanceExprSeeds =
                    computeSeedsFromStatement(stmt, statement, baseType);
            discoveredSeeds.addAll(instanceExprSeeds);
        }

        // Static invoke statements
        if (invokeExpr.isStaticInvokeExpr()) {
            String declaringClassName = declaredMethod.getDeclaringClass().getFullyQualifiedName();

            Collection<ForwardSeedQuery> staticExprSeeds =
                    computeSeedsFromStatement(stmt, statement, declaringClassName);
            discoveredSeeds.addAll(staticExprSeeds);
        }

        return discoveredSeeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromConstructor(
            ControlFlowGraph.Edge edge, Statement stmt) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        Val base = stmt.getInvokeExpr().getBase();
        String baseType = base.getType().toString();

        if (!ruleTransitions.containsKey(baseType)) {
            return seeds;
        }

        AllocVal allocVal = new AllocVal(base, stmt, base);
        RuleTransitions rule = ruleTransitions.get(baseType);

        ForwardSeedQuery constructorSeed =
                ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, rule);
        seeds.add(constructorSeed);

        Collection<ForwardSeedQuery> paramSeeds = computeSeedsFromParameters(stmt, baseType);
        seeds.addAll(paramSeeds);

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromStatement(
            ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!ruleTransitions.containsKey(baseType)) {
            return seeds;
        }

        // Basic expression
        Collection<ForwardSeedQuery> basicSeeds = computeSeedsFromExpression(edge, stmt, baseType);
        seeds.addAll(basicSeeds);

        // Parameters
        Collection<ForwardSeedQuery> parameterSeeds = computeSeedsFromParameters(stmt, baseType);
        seeds.addAll(parameterSeeds);

        // Assign statements
        Collection<ForwardSeedQuery> assignmentSeeds =
                computeSeedsFromAssignment(edge, stmt, baseType);
        seeds.addAll(assignmentSeeds);

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromExpression(
            ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!stmt.isAssignStmt()) {
            return seeds;
        }

        // Only seeds from static expressions are relevant (e.g. getInstance()), others are from
        // constructors
        if (!stmt.getInvokeExpr().isStaticInvokeExpr()) {
            return seeds;
        }

        RuleTransitions rightSideRule = ruleTransitions.get(baseType);
        Collection<CrySLMethod> methods =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                        rightSideRule.getRule(), stmt.getInvokeExpr().getMethod());

        if (methods.isEmpty()) {
            return seeds;
        }

        Val leftOp = stmt.getLeftOp();
        Val rightOp = stmt.getRightOp();
        AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

        String leftSideType = leftOp.getType().toString();
        if (ruleTransitions.containsKey(leftSideType)) {
            RuleTransitions leftSideRule = ruleTransitions.get(leftSideType);
            ForwardSeedQuery seed =
                    ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, leftSideRule);
            seeds.add(seed);
        } else {
            ForwardSeedQuery seed =
                    ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, rightSideRule);
            seeds.add(seed);
        }

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromParameters(
            Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        CrySLRule baseTypeRule = ruleTransitions.get(baseType).getRule();
        DeclaredMethod declaredMethod = stmt.getInvokeExpr().getMethod();
        Collection<CrySLMethod> methods =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(baseTypeRule, declaredMethod);

        for (CrySLMethod method : methods) {
            for (int i = 0; i < method.getParameters().size(); i++) {
                Map.Entry<String, String> param = method.getParameters().get(i);

                if (!isEnsuringPredicateParam(baseTypeRule, param.getKey())) {
                    continue;
                }

                Val paramVal = stmt.getInvokeExpr().getArg(i);

                // There is a rule for the parameter type => Seed is computed somewhere else
                String paramValType = paramVal.getType().toString();
                if (ruleTransitions.containsKey(paramValType)) {
                    continue;
                }

                for (Statement pred : stmt.getMethod().getControlFlowGraph().getPredsOf(stmt)) {
                    ControlFlowGraph.Edge backwardsEdge = new ControlFlowGraph.Edge(pred, stmt);

                    BoomerangOptions options =
                            BoomerangOptions.WITH_ALLOCATION_SITE(
                                    new SeedsWithoutSpecAllocationSite());

                    BackwardQuery backwardQuery = BackwardQuery.make(backwardsEdge, paramVal);
                    Boomerang boomerang = new Boomerang(frameworkScope, options);

                    BackwardBoomerangResults<Weight.NoWeight> results =
                            boomerang.solve(backwardQuery);

                    // TODO What happens when no value is found?
                    for (ForwardQuery query : results.getAllocationSites().keySet()) {
                        ForwardSeedQuery paramSeed =
                                ForwardSeedQuery.makeQueryWithoutSpecification(
                                        query.cfgEdge(), query.getAllocVal());
                        seeds.add(paramSeed);
                    }
                }
            }
        }

        return seeds;
    }

    private boolean isEnsuringPredicateParam(CrySLRule rule, String param) {
        for (CrySLPredicate ensuredPred : rule.getPredicates()) {
            for (ICrySLPredicateParameter predParam : ensuredPred.getParameters()) {
                // TODO Maybe also compare types?
                if (predParam.getName().equals(param)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromAssignment(
            ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!stmt.isAssignStmt()) {
            return seeds;
        }

        Val leftOp = stmt.getLeftOp();
        Val rightOp = stmt.getRightOp();

        RuleTransitions rightSideRule = ruleTransitions.get(baseType);
        if (isSeedGeneratingAssignment(rightSideRule.getRule(), stmt.getInvokeExpr().getMethod())) {
            AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);
            String leftClassName = leftOp.getType().toString();

            if (ruleTransitions.containsKey(leftClassName)) {
                // Case where rule exists, e.g. SecretKey key = kg.generateKey()
                RuleTransitions leftSideRule = ruleTransitions.get(leftClassName);

                ForwardSeedQuery seed =
                        ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, leftSideRule);
                seeds.add(seed);
            } else {
                // Case where no rule exists, e.g. byte[] bytes = key.getEncoded();
                ForwardSeedQuery seed =
                        ForwardSeedQuery.makeQueryWithoutSpecification(edge, allocVal);
                seeds.add(seed);
            }
        }

        return seeds;
    }

    private boolean isSeedGeneratingAssignment(CrySLRule rule, DeclaredMethod declaredMethod) {
        Collection<CrySLMethod> converted =
                MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(rule, declaredMethod);

        for (CrySLMethod method : converted) {
            Map.Entry<String, String> targetObject = method.getRetObject();

            if (!targetObject.getValue().equals(CrySLMethod.VOID)) {
                return true;
            }
        }
        return false;
    }

    private static class SeedsWithoutSpecAllocationSite implements IAllocationSite {

        @Override
        public Optional<AllocVal> getAllocationSite(Method method, Statement stmt, Val fact) {
            if (!stmt.isAssignStmt()) {
                return Optional.empty();
            }

            if (!stmt.getLeftOp().equals(fact)) {
                return Optional.empty();
            }

            Val leftOp = stmt.getLeftOp();
            Val rightOp = stmt.getRightOp();

            AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);
            return Optional.of(allocVal);
        }
    }
}
