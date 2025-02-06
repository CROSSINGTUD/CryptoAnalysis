package test.assertions.states;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public interface StateResult {

    Val getVal();

    Statement getStmt();

    void computedStatesAtStatement(Collection<State> states);
}
