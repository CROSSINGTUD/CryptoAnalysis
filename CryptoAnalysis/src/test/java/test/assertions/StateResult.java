package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import java.util.Collection;
import typestate.finiteautomata.State;

public interface StateResult {

    Collection<Val> getVal();

    Statement getStmt();

    void computedResults(State state);
}
