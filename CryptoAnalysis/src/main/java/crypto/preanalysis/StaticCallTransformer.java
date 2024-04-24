package crypto.preanalysis;

import soot.Body;
import soot.UnitPatchingChain;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.AbstractStmt;

import java.util.Map;

public class StaticCallTransformer extends PreTransformer {

    private static StaticCallTransformer instance;
    private static final String EMPTY_STATEMENT = "empty";

    public StaticCallTransformer() {
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
            if (!(rightSide instanceof StaticInvokeExpr)) {
                return;
            }

            units.insertAfter(new EmptyStatement(), unit);
        });
    }

    public static StaticCallTransformer v() {
        if (instance == null) {
            instance = new StaticCallTransformer();
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
