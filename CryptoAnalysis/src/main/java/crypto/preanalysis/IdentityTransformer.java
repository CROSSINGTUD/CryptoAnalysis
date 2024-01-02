package crypto.preanalysis;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Transform;
import soot.UnitPatchingChain;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JimpleLocal;

import java.util.Map;

/**
 * This transformer removes redundant jimple body statements that are introduced by
 * the jb.lp phase. These statements include identity statements as v = v and
 * v = v.method(). Without this transformer, Boomerang is not able to instantiate
 * correct queries.
 */
public class IdentityTransformer extends BodyTransformer {

    public static void setup() {
        String phaseName = "jtp.itr";
        PackManager.v().getPack("jtp").remove(phaseName);
        PackManager.v().getPack("jtp").add(new Transform(phaseName, new IdentityTransformer()));
        PhaseOptions.v().setPhaseOption(phaseName, "on");
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        if (body.getMethod().getDeclaringClass().getName().startsWith("java.")) {
            return;
        }
        if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
            return;
        }

        UnitPatchingChain units = body.getUnits();
        units.snapshotIterator().forEachRemaining(unit -> {
            if (!(unit instanceof AssignStmt)) {
                return;
            }

            AssignStmt assignStmt = (AssignStmt) unit;

            // Identity statements for variable v: v = v
            if (assignStmt.getLeftOp().equals(assignStmt.getRightOp())) {
                units.remove(unit);
            }

            if (!(assignStmt.getLeftOp() instanceof JimpleLocal)) {
                return;
            }

            if (!(assignStmt.getRightOp() instanceof InstanceInvokeExpr)) {
                return;
            }

            // Replace statement of the form: v = v.method() with v.method()
            JimpleLocal leftSide = (JimpleLocal) assignStmt.getLeftOp();
            InstanceInvokeExpr invokeExpr = (InstanceInvokeExpr) assignStmt.getRightOp();
            if (leftSide.equals(invokeExpr.getBase())) {
                units.swapWith(unit, new JInvokeStmt(invokeExpr));
            }
        });
    }
}
