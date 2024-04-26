package test.assertions;

import boomerang.scene.Statement;
import boomerang.scene.Val;
import typestate.finiteautomata.State;

import java.util.Collection;

public interface StateResult {

    Collection<Val> getVal();

    Statement getStmt();

    void computedResults(State state);
}
