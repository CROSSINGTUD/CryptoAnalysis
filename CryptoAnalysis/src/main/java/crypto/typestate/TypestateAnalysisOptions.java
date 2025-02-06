package crypto.typestate;

import boomerang.DefaultBoomerangOptions;
import boomerang.scene.AllocVal;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Optional;

public class TypestateAnalysisOptions extends DefaultBoomerangOptions {

    private final ForwardSeedQuery query;
    private final int timeout;

    public TypestateAnalysisOptions(ForwardSeedQuery query, int timeout) {
        this.query = query;
        this.timeout = timeout;
    }

    @Override
    public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
        Statement statement = query.cfgEdge().getStart();
        Val var = ((AllocVal) query.var()).getDelegate();

        if (stmt.equals(statement)) {
            if (stmt.isAssign()) {
                Val leftOp = stmt.getLeftOp();
                Val rightOp = stmt.getRightOp();
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            } else {
                AllocVal allocVal = new AllocVal(var, stmt, var);

                return Optional.of(allocVal);
            }
        }

        return super.getAllocationVal(m, stmt, fact);
    }

    @Override
    public StaticFieldStrategy getStaticFieldStrategy() {
        return StaticFieldStrategy.FLOW_SENSITIVE;
    }

    @Override
    public boolean allowMultipleQueries() {
        return true;
    }

    @Override
    public int analysisTimeoutMS() {
        return timeout;
    }
}
