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
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.WPAUpdateListener;

public class TestingResultReporter{
	private Multimap<Unit, Assertion> stmtToResults = HashMultimap.create();
	public TestingResultReporter(Set<Assertion> expectedResults) {
		for(Assertion e : expectedResults){
			if(e instanceof ComparableResult)
				stmtToResults.put(((ComparableResult) e).getStmt(), e);
		}
	}

	public void onSeedFinished(Node<Statement,Val> seed,final AbstractBoomerangSolver<TransitionFunction> seedSolver) {
		for(final Entry<Unit, Assertion> e : stmtToResults.entries()){
			if(e.getValue() instanceof ComparableResult){
				final ComparableResult<State,Val> expectedResults = (ComparableResult) e.getValue();
				for(Entry<Transition<Statement, INode<Val>>, TransitionFunction> s : seedSolver.getTransitionsToFinalWeights().entrySet()){
					Transition<Statement, INode<Val>> t = s.getKey();
					TransitionFunction w = s.getValue();
					if((t.getStart() instanceof GeneratedState)  || !t.getStart().fact().equals(expectedResults.getVal()))
						continue;
					if(t.getLabel().getUnit().isPresent()){
						if(t.getLabel().getUnit().get().equals(e.getKey())){
							for(ITransition trans : w.values()){
								if(trans.from().isInitialState()){
									expectedResults.computedResults(trans.to());
								}
							}
						}
					}
				}
			}
		}
		
      WeightedPAutomaton<Statement, INode<Val>, TransitionFunction> aut = new WeightedPAutomaton<Statement, INode<Val>, TransitionFunction>(null) {
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
          public TransitionFunction getZero() {
              return seedSolver.getCallAutomaton().getZero();
          }

          @Override
          public TransitionFunction getOne() {
              return seedSolver.getCallAutomaton().getOne();
          }
      };
      
      for(Entry<Transition<Statement, INode<Val>>,TransitionFunction> s : seedSolver.getTransitionsToFinalWeights().entrySet()){
          Transition<Statement, INode<Val>> t = s.getKey();
          TransitionFunction w = s.getValue();
          aut.addWeightForTransition(t, w);
      }
      
      System.out.println(aut.toDotString());
	}




}
