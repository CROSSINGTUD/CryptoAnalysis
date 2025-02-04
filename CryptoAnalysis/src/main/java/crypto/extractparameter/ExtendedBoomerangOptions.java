package crypto.extractparameter;

import boomerang.DefaultBoomerangOptions;
import boomerang.scene.AllocVal;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.extractparameter.transformation.OperatorTransformation;
import crypto.extractparameter.transformation.Transformation;
import java.util.Optional;

public class ExtendedBoomerangOptions extends DefaultBoomerangOptions {

    private final int timeout;
    private final SparseCFGCache.SparsificationStrategy strategy;

    public ExtendedBoomerangOptions(int timeout, SparseCFGCache.SparsificationStrategy strategy) {
        this.timeout = timeout;
        this.strategy = strategy;
    }

    @Override
    public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
        /* Constructors are not assignments; they are simple invoke statements. Therefore, we
         * have to check if we have a corresponding transformation separately.
         */
        if (Transformation.isTransformationExpression(stmt)) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            DeclaredMethod declaredMethod = invokeExpr.getMethod();

            if (declaredMethod.isConstructor()) {
                Val base = invokeExpr.getBase();

                if (base.equals(fact)) {
                    AllocVal allocVal = new AllocVal(base, stmt, base);
                    return Optional.of(allocVal);
                }
            }
        }

        if (!stmt.isAssign()) {
            return Optional.empty();
        }

        Val leftOp = stmt.getLeftOp();
        Val rightOp = stmt.getRightOp();

        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }

        if (stmt.containsInvokeExpr()) {
            /* If we have an invoke expression, we check if it corresponds to an
             * implemented transformation
             */
            if (Transformation.isTransformationExpression(stmt)) {
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            }
        } else {
            if (OperatorTransformation.isOperatorTransformation(rightOp)) {
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            }

            /* Extract cast value from cast expressions, e.g.
             * int i = (int) 65000 -> AllocVal: 65000
             */
            if (rightOp.isCast()) {
                Val castOp = rightOp.getCastOp();

                if (isAllocationVal(castOp)) {
                    return Optional.of(new AllocVal(leftOp, stmt, castOp));
                }
            }

            /* Strings are initialized in two steps, where we need the second one:
             * r0 = new java.lang.String;
             * r0 = "value";
             */
            if (rightOp.isNewExpr()) {
                Type type = rightOp.getNewExprType();

                if (type.toString().equals("java.lang.String")) {
                    return Optional.empty();
                }
            }

            // Basic values: constants, array allocations and null
            if (isAllocationVal(rightOp)) {
                return Optional.of(new AllocVal(leftOp, stmt, rightOp));
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean isAllocationVal(Val val) {
        // Constants: var = <constant>
        if (val.isConstant()) {
            return true;
        }

        // null: var = null
        if (val.isNull()) {
            return true;
        }

        // arrays: var = new arr[..]
        if (val.isArrayAllocationVal()) {
            return true;
        }

        return val.isNewExpr();
    }

    @Override
    public int analysisTimeoutMS() {
        return timeout;
    }

    @Override
    public boolean trackStaticFieldAtEntryPointToClinit() {
        return true;
    }

    @Override
    public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
        return strategy;
    }
}
