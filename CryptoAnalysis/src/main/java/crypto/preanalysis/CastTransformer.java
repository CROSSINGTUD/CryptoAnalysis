package crypto.preanalysis;

import soot.Body;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;

import java.util.Map;

/**
 * This transformer removes explicit cast expressions from the jimple code.
 * Since Soot 4.3.0, it transforms the expression 'int a = 65000' into the jimple
 * statement '$v = (int) 65000'. However, Boomerang is not able to extract the
 * value 65000 from the statement because of the explicit cast operation (int).
 * For the analysis, this operation is irrelevant because the variables always
 * have the correct type (i.e. $v is of type int), so we can remove the cast
 * operation.
 */
public class CastTransformer extends PreTransformer {

    private static CastTransformer instance;

    public CastTransformer() {
        super();
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
            return;
        }

        final UnitPatchingChain units = body.getUnits();
        units.snapshotIterator().forEachRemaining(unit -> {
            if (!(unit instanceof AssignStmt)) {
                return;
            }
            AssignStmt assignStmt = (AssignStmt) unit;

            Value rightSide = assignStmt.getRightOp();
            if (!(rightSide instanceof CastExpr)) {
                return;
            }

            CastExpr castExpr = (CastExpr) rightSide;
            if (!(castExpr.getOp() instanceof Constant)) {
                return;
            }
            Constant constant = (Constant) castExpr.getOp();

            assignStmt.setRightOp(constant);
        });
    }

    public static CastTransformer v() {
        if (instance == null) {
            instance = new CastTransformer();
        }
        return instance;
    }

    public void reset() {
        instance = null;
    }

}
