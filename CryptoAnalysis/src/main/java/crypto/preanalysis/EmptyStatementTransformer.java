package crypto.preanalysis;

import soot.Body;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.AbstractStmt;

import java.util.Map;

public class EmptyStatementTransformer extends PreTransformer {

    private static EmptyStatementTransformer instance;
    private static final String EMPTY_STATEMENT = "empty";

    public EmptyStatementTransformer() {
        super();
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
            return;
        }

        final UnitPatchingChain units = body.getUnits();
        units.snapshotIterator().forEachRemaining(unit -> {
            if (isStaticAssignStatement(unit) || isConstructorCall(unit)) {
                units.insertAfter(new EmptyStatement(), unit);
            }
        });
    }

    private boolean isStaticAssignStatement(Unit unit) {
        if (!(unit instanceof AssignStmt)) {
            return false;
        }
        AssignStmt assignStmt = (AssignStmt) unit;

        Value rightSide = assignStmt.getRightOp();
        return rightSide instanceof StaticInvokeExpr;
    }

    private boolean isConstructorCall(Unit unit) {
        if (!(unit instanceof InvokeStmt)) {
            return false;
        }

        InvokeStmt invokeStmt = (InvokeStmt) unit;
        return invokeStmt.getInvokeExpr().getMethod().isConstructor();
    }

    public static EmptyStatementTransformer v() {
        if (instance == null) {
            instance = new EmptyStatementTransformer();
        }
        return instance;
    }

    public void reset() {
        instance = null;
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
