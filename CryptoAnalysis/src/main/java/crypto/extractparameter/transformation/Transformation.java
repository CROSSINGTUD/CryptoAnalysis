package crypto.extractparameter.transformation;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.definition.Definitions;
import crypto.extractparameter.ExtendedBoomerangOptions;
import java.util.ArrayList;
import java.util.List;
import wpds.impl.Weight;

/**
 * Main class for transforming the allocation sites when using Boomerang to extract relevant
 * parameters. When running Boomerang, one can use the function {@link
 * Transformation#isTransformationExpression(Statement)} to check if there is an implemented
 * transformation for the given statement. Note that transformations require an invoke expression to
 * run. After checking corresponding statements, one can use {@link
 * Transformation#transformAllocationSite(AllocVal, Definitions.BoomerangOptionsDefinition)} to
 * transform Boomerang's detected allocations sites, if an implementation is available.
 */
public abstract class Transformation {

    protected record Signature(
            String declaringClass, String returnValue, String name, List<String> params) {
        public Signature(String declaringClass, String returnValue, String name) {
            this(declaringClass, returnValue, name, new ArrayList<>());
        }
    }

    public static boolean isTransformationExpression(Statement statement) {
        if (!statement.containsInvokeExpr()) {
            return false;
        }

        Signature signature = invokeExprToSignature(statement.getInvokeExpr());

        if (StringTransformation.isStringTransformation(signature)) {
            return true;
        }

        if (WrapperTransformation.isWrapperTransformation(signature)) {
            return true;
        }

        return false;
    }

    private static Signature invokeExprToSignature(InvokeExpr invokeExpr) {
        DeclaredMethod method = invokeExpr.getMethod();

        List<String> params = method.getParameterTypes().stream().map(Object::toString).toList();
        return new Signature(
                method.getDeclaringClass().getName(),
                method.getReturnType().toString(),
                method.getName(),
                params);
    }

    public static Multimap<Val, Type> transformAllocationSite(
            AllocVal allocVal, Definitions.BoomerangOptionsDefinition definition) {
        Statement allocStatement = allocVal.getAllocStatement();

        if (!allocStatement.containsInvokeExpr()) {
            Val allocSite = allocVal.getAllocVal();

            // Check for basic transformation operators (e.g. length)
            if (OperatorTransformation.isOperatorTransformation(allocSite)) {
                OperatorTransformation transformation = new OperatorTransformation(definition);

                return transformation.evaluateOperator(allocStatement, allocSite);
            }

            // For direct values (e.g. constants), we take their type
            Multimap<Val, Type> allocMap = HashMultimap.create();
            allocMap.put(allocSite, allocSite.getType());

            return allocMap;
        }

        Signature signature = invokeExprToSignature(allocStatement.getInvokeExpr());
        if (StringTransformation.isStringTransformation(signature)) {
            Transformation transformation = new StringTransformation(definition);

            return transformation.evaluateExpression(allocStatement, signature);
        } else if (WrapperTransformation.isWrapperTransformation(signature)) {
            Transformation transformation = new WrapperTransformation(definition);

            return transformation.evaluateExpression(allocStatement, signature);
        } else if (MiscellaneousTransformation.isMiscellaneousTransformation(signature)) {
            Transformation transformation = new MiscellaneousTransformation(definition);

            return transformation.evaluateExpression(allocStatement, signature);
        }

        return HashMultimap.create();
    }

    protected final Definitions.BoomerangOptionsDefinition definition;

    protected Transformation(Definitions.BoomerangOptionsDefinition definition) {
        this.definition = definition;
    }

    protected Multimap<AllocVal, Type> computeAllocSites(Statement statement, Val val) {
        Multimap<AllocVal, Type> boomerangResults = HashMultimap.create();

        for (Statement pred : statement.getMethod().getControlFlowGraph().getPredsOf(statement)) {
            ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, statement);

            BackwardQuery query = BackwardQuery.make(edge, val);
            Boomerang boomerang =
                    new Boomerang(
                            definition.callGraph(),
                            definition.dataFlowScope(),
                            new ExtendedBoomerangOptions(
                                    definition.timeout(), definition.strategy()));
            BackwardBoomerangResults<Weight.NoWeight> results = boomerang.solve(query);

            for (ForwardQuery forwardQuery : results.getAllocationSites().keySet()) {
                if (!(forwardQuery.var() instanceof AllocVal allocVal)) {
                    continue;
                }

                boomerangResults.putAll(allocVal, results.getPropagationType());
            }
        }

        return boomerangResults;
    }

    protected Multimap<Val, Type> extractAllocValues(Multimap<AllocVal, Type> allocSites) {
        Multimap<Val, Type> result = HashMultimap.create();

        for (AllocVal allocVal : allocSites.keySet()) {
            Multimap<Val, Type> transformedAllocSites =
                    Transformation.transformAllocationSite(allocVal, definition);

            result.putAll(transformedAllocSites);
        }

        return result;
    }

    protected abstract Multimap<Val, Type> evaluateExpression(
            Statement statement, Signature signature);
}
