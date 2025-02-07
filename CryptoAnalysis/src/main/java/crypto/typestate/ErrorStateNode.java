package crypto.typestate;

import typestate.finiteautomata.State;

/**
 * Final error state that cannot be left. All ingoing transitions should originate from a {@link
 * ReportingErrorStateNode}.
 */
public class ErrorStateNode implements State {

    public static final String LABEL = "ERR (final)";

    private static ErrorStateNode errorState;

    private ErrorStateNode() {}

    public static ErrorStateNode getInstance() {
        if (errorState == null) {
            errorState = new ErrorStateNode();
        }

        return errorState;
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
        return LABEL;
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ErrorStateNode;
    }
}
