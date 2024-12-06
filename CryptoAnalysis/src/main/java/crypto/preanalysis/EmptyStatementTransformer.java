package crypto.preanalysis;

import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.internal.AbstractStmt;

public class EmptyStatementTransformer extends PreTransformer {

    private static final String EMPTY_STATEMENT = "empty";
    private final Collection<String> ruleNames;

    public EmptyStatementTransformer(Collection<CrySLRule> rules) {
        this.ruleNames = new HashSet<>();

        for (CrySLRule rule : rules) {
            ruleNames.add(rule.getClassName());
        }
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
            return;
        }

        final UnitPatchingChain units = body.getUnits();
        units.snapshotIterator()
                .forEachRemaining(
                        unit -> {
                            if (isConstructorCall(unit) || isAssignmentSeed(unit)) {
                                units.insertAfter(new EmptyStatement(), unit);
                            }
                        });
    }

    private boolean isConstructorCall(Unit unit) {
        if (!(unit instanceof InvokeStmt)) {
            return false;
        }

        InvokeStmt invokeStmt = (InvokeStmt) unit;
        SootMethod sootMethod = invokeStmt.getInvokeExpr().getMethod();

        if (!sootMethod.isConstructor()) {
            return false;
        }

        // Seeds that originate from constructor calls
        InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
        if (!(invokeExpr instanceof InstanceInvokeExpr)) {
            return false;
        }

        InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
        String callingType = instanceInvokeExpr.getBase().getType().toString();

        return ruleNames.contains(callingType);
    }

    private boolean isAssignmentSeed(Unit unit) {
        if (!(unit instanceof AssignStmt)) {
            return false;
        }

        // Seeds that originate from assignments
        AssignStmt assignStmt = (AssignStmt) unit;
        Value leftSide = assignStmt.getLeftOp();
        String leftSideType = leftSide.getType().toString();

        return ruleNames.contains(leftSideType);
    }

    private static class EmptyStatement extends AbstractStmt {

        @Override
        public Object clone() {
            return new EmptyStatement();
        }

        @Override
        public boolean fallsThrough() {
            return true;
        }

        @Override
        public boolean branches() {
            return false;
        }

        @Override
        public void toString(UnitPrinter up) {
            up.literal(EMPTY_STATEMENT);
        }

        @Override
        public String toString() {
            return EMPTY_STATEMENT;
        }
    }
}
