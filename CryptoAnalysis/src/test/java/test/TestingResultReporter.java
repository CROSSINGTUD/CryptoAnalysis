package test;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.solver.AbstractBoomerangSolver;
import soot.Unit;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;
import wpds.impl.Transition;

public class TestingResultReporter{
	private Multimap<Unit, Assertion> stmtToResults = HashMultimap.create();
	public TestingResultReporter(Set<Assertion> expectedResults) {
		for(Assertion e : expectedResults){
			if(e instanceof ComparableResult)
				stmtToResults.put(((ComparableResult) e).getStmt(), e);
		}
	}

	public void onSeedFinished(Node<Statement,Val> seed,final Table<Statement, Val, TransitionFunction> res) {
		for(final Entry<Unit, Assertion> e : stmtToResults.entries()){
			if(e.getValue() instanceof ComparableResult){
				final ComparableResult<State,Val> expectedResults = (ComparableResult) e.getValue();
				for(Cell<Statement, Val, TransitionFunction> s : res.cellSet()){
						if(s.getRowKey().getUnit().get().equals(e.getKey())){
							for(ITransition trans : s.getValue().values()){
								if(trans.from() == null || trans.to() == null)
									continue;
									
								if(trans.from().isInitialState()){
									expectedResults.computedResults(trans.to());
								}
							}
					}
				}
			}
		}
	}




}
