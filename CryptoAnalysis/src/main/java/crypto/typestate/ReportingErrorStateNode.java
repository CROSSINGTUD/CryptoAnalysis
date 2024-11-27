package crypto.typestate;

import crysl.rule.CrySLMethod;
import java.util.Collection;
import typestate.finiteautomata.State;

public class ReportingErrorStateNode implements State {

    private final Collection<CrySLMethod> expectedCalls;

    public ReportingErrorStateNode(Collection<CrySLMethod> expectedCalls) {
        this.expectedCalls = expectedCalls;
    }

    public Collection<CrySLMethod> getExpectedCalls() {
        return expectedCalls;
    }

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

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReportingErrorStateNode;
    }
}
