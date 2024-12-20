package crypto.typestate;

import crysl.rule.CrySLMethod;
import java.util.Collection;
import typestate.finiteautomata.State;

public record ReportingErrorStateNode(Collection<CrySLMethod> expectedCalls) implements State {

    @Override
    public boolean isErrorState() {
        return true;
    }

    @Override
    public boolean isInitialState() {
        return false;
    }

    @Override
    public boolean isAccepting() {
        return false;
    }

    @Override
    public String toString() {
        return "ERR";
    }
}
