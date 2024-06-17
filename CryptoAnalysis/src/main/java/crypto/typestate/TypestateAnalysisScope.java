package crypto.typestate;

import boomerang.Query;
import boomerang.scene.AllocVal;
import boomerang.scene.AnalysisScope;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.rules.ICrySLPredicateParameter;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.utils.MatcherUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class TypestateAnalysisScope extends AnalysisScope {

    private final Map<String, RuleTransitions> ruleTransitions;

    public TypestateAnalysisScope(CallGraph callGraph, Map<String, RuleTransitions> ruleTransitions) {
        super(callGraph);

        this.ruleTransitions = ruleTransitions;
    }

    @Override
    protected Collection<? extends Query> generate(ControlFlowGraph.Edge stmt) {
        Statement statement = stmt.getStart();

        if (!statement.containsInvokeExpr()) {
            return Collections.emptySet();
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getMethod();

        Collection<ForwardSeedQuery> discoveredSeeds = new HashSet<>();

        // Constructors
        if (declaredMethod.isConstructor()) {
            Collection<ForwardSeedQuery> constructorSeeds = computeSeedsFromConstructor(stmt, statement);
            discoveredSeeds.addAll(constructorSeeds);
        }

        // Invoke statements from instances
        if (invokeExpr.isInstanceInvokeExpr()) {
            String baseType = invokeExpr.getBase().getType().toString();

            Collection<ForwardSeedQuery> instanceExprSeeds = computeSeedsFromStatement(stmt, statement, baseType);
            discoveredSeeds.addAll(instanceExprSeeds);
        }

        // Static invoke statements
        if (invokeExpr.isStaticInvokeExpr()) {
            String declaringClassName = declaredMethod.getDeclaringClass().getName();

            Collection<ForwardSeedQuery> staticExprSeeds = computeSeedsFromStatement(stmt, statement, declaringClassName);
            discoveredSeeds.addAll(staticExprSeeds);
        }

        return discoveredSeeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromConstructor(ControlFlowGraph.Edge edge, Statement stmt) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        Val base = stmt.getInvokeExpr().getBase();
        String baseType = base.getType().toString();

        if (!ruleTransitions.containsKey(baseType)) {
            return seeds;
        }

        AllocVal allocVal = new AllocVal(base, stmt, base);
        RuleTransitions rule = ruleTransitions.get(baseType);

        ForwardSeedQuery constructorSeed = ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, rule);
        seeds.add(constructorSeed);

        Collection<ForwardSeedQuery> paramSeeds = computeSeedsFromParameters(edge, stmt, baseType);
        seeds.addAll(paramSeeds);

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromStatement(ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!ruleTransitions.containsKey(baseType)) {
            return seeds;
        }

        // Basic expression
        Collection<ForwardSeedQuery> basicSeeds = computeSeedsFromExpression(edge, stmt, baseType);
        seeds.addAll(basicSeeds);

        // Parameters
        Collection<ForwardSeedQuery> parameterSeeds = computeSeedsFromParameters(edge, stmt, baseType);
        seeds.addAll(parameterSeeds);

        // Assign statements
        Collection<ForwardSeedQuery> assignmentSeeds = computeSeedsFromAssignment(edge, stmt, baseType);
        seeds.addAll(assignmentSeeds);

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromExpression(ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!stmt.isAssign()) {
            return seeds;
        }

        // Only seeds from static expressions are relevant (e.g. getInstance()), others are from constructors
        if (!stmt.getInvokeExpr().isStaticInvokeExpr()) {
            return seeds;
        }

        RuleTransitions rightSideRule = ruleTransitions.get(baseType);
        Collection<CrySLMethod> methods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(rightSideRule.getRule(), stmt.getInvokeExpr().getMethod());

        if (methods.isEmpty()) {
            return seeds;
        }

        Val leftOp = stmt.getLeftOp();
        Val rightOp = stmt.getRightOp();
        AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

        String leftSideType = leftOp.getType().toString();
        RuleTransitions leftSideRule = ruleTransitions.get(leftSideType);

        ForwardSeedQuery seed = ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, leftSideRule);
        seeds.add(seed);

        return seeds;
    }

    private Collection<ForwardSeedQuery> computeSeedsFromParameters(ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        CrySLRule baseTypeRule = ruleTransitions.get(baseType).getRule();
        DeclaredMethod declaredMethod = stmt.getInvokeExpr().getMethod();
        Collection<CrySLMethod> methods = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(baseTypeRule, declaredMethod);

        for (CrySLMethod method : methods) {
            for (int i = 0; i < method.getParameters().size(); i++) {
                Map.Entry<String, String> param = method.getParameters().get(i);

                if (!isEnsuringPredicateParam(baseTypeRule, param.getKey())) {
                    continue;
                }

                Val paramVal = stmt.getInvokeExpr().getArg(i);
                AllocVal allocVal = new AllocVal(paramVal, stmt, paramVal);

                // There is a rule for the parameter type => Seed is computed somewhere else
                String paramValType = paramVal.getType().toString();
                if (ruleTransitions.containsKey(paramValType)) {
                    continue;
                }

                ForwardSeedQuery paramSeed = ForwardSeedQuery.makeQueryWithoutSpecification(edge, allocVal);
                seeds.add(paramSeed);
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

    private Collection<ForwardSeedQuery> computeSeedsFromAssignment(ControlFlowGraph.Edge edge, Statement stmt, String baseType) {
        Collection<ForwardSeedQuery> seeds = new HashSet<>();

        if (!stmt.isAssign()) {
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

                ForwardSeedQuery seed = ForwardSeedQuery.makeQueryWithSpecification(edge, allocVal, leftSideRule);
                seeds.add(seed);
            } else {
                // Case where no rule exists, e.g. byte[] bytes = key.getEncoded();
                ForwardSeedQuery seed = ForwardSeedQuery.makeQueryWithoutSpecification(edge, allocVal);
                seeds.add(seed);
            }
        }

        return seeds;
    }

    private boolean isSeedGeneratingAssignment(CrySLRule rule, DeclaredMethod declaredMethod) {
        Collection<CrySLMethod> converted = MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(rule, declaredMethod);

        for (CrySLMethod method : converted) {
            Map.Entry<String, String> targetObject = method.getRetObject();

            if (!targetObject.getValue().equals(CrySLMethod.VOID)) {
                return true;
            }
        }
        return false;
    }

}
