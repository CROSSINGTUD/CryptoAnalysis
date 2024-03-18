package test;

import java.util.Map.Entry;
import java.util.Set;

import boomerang.scene.ControlFlowGraph;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.scene.Val;
import soot.Unit;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

public class TestingResultReporter {
	private Multimap<Unit, ComparableResult<State, Val>> allExpectedTypestateResults = HashMultimap.create();

	@SuppressWarnings("unchecked")
	public TestingResultReporter(Set<Assertion> expectedResults) {
		for (Assertion e : expectedResults) {
			if (e instanceof ComparableResult)
				allExpectedTypestateResults.put(((ComparableResult<State, Val>) e).getStmt(),
						(ComparableResult<State, Val>) e);
		}
	}

	public void onSeedFinished(Node<ControlFlowGraph.Edge, Val> seed, final Table<ControlFlowGraph.Edge, Val, TransitionFunction> res) {
		for (final Entry<Unit, ComparableResult<State, Val>> expectedResults : allExpectedTypestateResults.entries()) {
			for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> s : res.cellSet()) {
				Unit expectedUnit = expectedResults.getKey();
				Val expectedVal = expectedResults.getValue().getVal();

				Unit analysisResultUnit = s.getRowKey().getUnit().get();
				Val analysisResultFact = s.getColumnKey();
				if (analysisResultUnit.equals(expectedUnit) && analysisResultFact.equals(expectedVal)) {
					for (ITransition trans : s.getValue().values()) {
						if (trans.from() == null || trans.to() == null)
							continue;
						if (trans.from().isInitialState()) {
							expectedResults.getValue().computedResults(trans.to());
						}
					}
				}
			}
		}
	}

}
