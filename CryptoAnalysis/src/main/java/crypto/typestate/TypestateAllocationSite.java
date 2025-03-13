package crypto.typestate;

import boomerang.options.DefaultAllocationSite;
import boomerang.scope.AllocVal;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Optional;

public class TypestateAllocationSite extends DefaultAllocationSite {

    private final ForwardSeedQuery query;

    public TypestateAllocationSite(ForwardSeedQuery query) {
        this.query = query;
    }

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement stmt, Val fact) {
        Statement statement = query.cfgEdge().getStart();
        Val var = ((AllocVal) query.var()).getDelegate();

        if (stmt.equals(statement)) {
            if (stmt.isAssignStmt()) {
                Val leftOp = stmt.getLeftOp();
                Val rightOp = stmt.getRightOp();
                AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);

                return Optional.of(allocVal);
            } else {
                AllocVal allocVal = new AllocVal(var, stmt, var);

                return Optional.of(allocVal);
            }
        }

        return super.getAllocationSite(method, stmt, fact);
    }
}
