package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import typestate.finiteautomata.State;

public interface StateResult {

    Val getVal();

    Statement getStmt();

    void computedResults(State state);
}
