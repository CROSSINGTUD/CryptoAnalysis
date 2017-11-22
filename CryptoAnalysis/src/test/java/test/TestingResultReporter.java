package test;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.solver.AbstractBoomerangSolver;
import soot.Unit;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import test.Assertion;
import test.ComparableResult;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.WPAUpdateListener;

public class TestingResultReporter<W extends Weight>{
	private Multimap<Unit, Assertion> stmtToResults = HashMultimap.create();
	public TestingResultReporter(Set<Assertion> expectedResults) {
		for(Assertion e : expectedResults){
			if(e instanceof ComparableResult)
				stmtToResults.put(((ComparableResult) e).getStmt(), e);
		}
	}

	public void onSeedFinished(Node<Statement,Val> seed,final AbstractBoomerangSolver<W> seedSolver) {
		for(final Entry<Unit, Assertion> e : stmtToResults.entries()){
			if(e.getValue() instanceof ComparableResult){
				final ComparableResult<W,Val> expectedResults = (ComparableResult) e.getValue();
//				System.out.println(Joiner.on("\n").join(seedSolver.getNodesToWeights().entrySet()));
				WeightedPAutomaton<Statement, INode<Val>, W> aut = new WeightedPAutomaton<Statement, INode<Val>, W>(null) {
					@Override
					public INode<Val> createState(INode<Val> d, Statement loc) {
						return null;
					}

					@Override
					public boolean isGeneratedState(INode<Val> d) {
						return false;
					}

					@Override
					public Statement epsilon() {
						return seedSolver.getCallAutomaton().epsilon();
					}

					@Override
					public W getZero() {
						return seedSolver.getCallAutomaton().getZero();
					}

					@Override
					public W getOne() {
						return seedSolver.getCallAutomaton().getOne();
					}
				};
				
//				for(Entry<Transition<Statement, INode<Val>>, TransitionFunction> s : seedSolver.getTransitionsToFinalWeights().entrySet()){
//					aut.addWeightForTransition(s.getKey(), s.getValue());
//					Transition<Statement, INode<Val>> node = s.getKey();
//					if((node.getStart() instanceof GeneratedState)  || !node.getStart().fact().equals(expectedResults.getVal()))
//						continue;
//					if(node.getLabel().getUnit().isPresent()){
//						if(node.getLabel().getUnit().get().equals(e.getKey())){
//							expectedResults.computedResults(s.getValue());
//						}
//					}
//				}
				seedSolver.getCallAutomaton().registerListener(new WPAUpdateListener<Statement, INode<Val>, W>() {
					
					@Override
					public void onWeightAdded(Transition<Statement, INode<Val>> t, W w,
							WeightedPAutomaton<Statement, INode<Val>, W> aut) {
						if((t.getStart() instanceof GeneratedState)  || !t.getStart().fact().equals(expectedResults.getVal()))
							return;
						if(t.getLabel().getUnit().isPresent()){
							if(t.getLabel().getUnit().get().equals(e.getKey())){
								expectedResults.computedResults(w);
							}
						}
					}
				});
				System.out.println("FINAL WEIGHT AUTOMATON");
				System.out.println(aut.toDotString());
			}
		}
	}


}
