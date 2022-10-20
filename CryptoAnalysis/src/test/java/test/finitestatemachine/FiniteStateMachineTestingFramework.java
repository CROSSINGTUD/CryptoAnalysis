package test.finitestatemachine;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import org.junit.Before;
import test.IDEALCrossingTestingFramework;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class FiniteStateMachineTestingFramework{

	private StateMachineGraph smg;
	private String crySLRule;
	private Ruleset ruleset;
	protected Order order;
	protected static int maxRepeat;
	
	public FiniteStateMachineTestingFramework(String crySLRule, Ruleset ruleset) {
		this.crySLRule = crySLRule;
		this.ruleset = ruleset;
	}
	
	// uncomment "@Test" to test the StatemachineBuilder.
	// These tests require a lot of memory and runtime and are thus excluded to not run each time.
	// Further, the tests solely test the StatemachineBuilder and thereby only require to be executed when changing the Builder.
	//@Test
	public void simulate() {
		if(order != null) {
			benchmark();
		}
	}

	public void benchmark() {
		// valid paths
		maxRepeat = 1;
		List<List<String>> allPossiblePathsMaxRepeat1 = order.get();
		maxRepeat = 3;
		List<List<String>> allPossiblePathsMaxRepeat3 = order.get();
		for(List<String> path: allPossiblePathsMaxRepeat3) {
			assertInSMG(path);
		}
		// invalid paths
		assertRandomInvalidPaths(allPossiblePathsMaxRepeat1, allPossiblePathsMaxRepeat3);
	}
	
	public void assertRandomInvalidPaths(List<List<String>> pathsWithSmallRepeat, List<List<String>> pathsWithLargeRepeat) {
		for(List<String> path: pathsWithSmallRepeat) {
			if(path.size()>1) {
				for(int i=0; i<10; i++) {
					List<String> events = Lists.newArrayList(path);
					switch((new Random()).nextInt(2)){
						case 0:
							// delete an event
							int rand = (new Random()).nextInt(events.size());
							events.remove(rand);
							break;
						case 1:
							// switch two events
							int rand1 = (new Random()).nextInt(events.size());
							String event = events.remove(rand1);
							int rand2 = (new Random()).nextInt(events.size());
							events.add(rand2, event);
							break;
					}
					if(!pathsWithLargeRepeat.contains(events)) {
						assertNotInSMG(events);
					}
				}
			}
		}
	}
	
	public void assertInSMG(List<String> methodPath) {
		if(!isPathOfMethodsInSMG(methodPath)) {
			throw new AssertionError("Order of calls are not in SMG but should be: " + methodPath.toString());
		};
	}
	
	public void assertNotInSMG(List<String> methodPath) {
		if(!methodPath.isEmpty() && isPathOfMethodsInSMG(methodPath)) {
			// the initial state is always accepting.
			throw new AssertionError("Order of calls are in SMG but should probably not be: " + methodPath.toString());
		};
	}
	
	private boolean isPathOfMethodsInSMG(List<String> methodPath) {
		final Set<StateNode> current = Sets.newHashSet();
		current.add(smg.getInitialTransition().getLeft());
		for(String event: methodPath) {
			List<TransitionEdge> matchingEdges = smg.getAllTransitions().stream().filter(edge -> current.contains(edge.getLeft()) && edge.getLabel().stream().anyMatch(label -> label.getName().contains(event))).collect(Collectors.toList());
			if(matchingEdges.size() == 0) {
				// found no matching edge
				return false;
			}
			current.clear();
			matchingEdges.forEach(edge -> current.add(edge.getRight()));
		}
		return current.stream().anyMatch(node -> node.getAccepting());
	}
	
	@Before
	public void createSMG() {
		if(this.smg == null) {
			try {
				this.smg = CrySLRulesetSelector.makeSingleRule(IDEALCrossingTestingFramework.RULES_BASE_DIR,  RuleFormat.SOURCE,  this.ruleset,  this.crySLRule).getUsagePattern();
			} catch (CryptoAnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//
	// Classes of type Order are able to generate all possible paths of events 
	// up to a certain recursion depth for * and +
	//
	
	public interface Order{
		List<List<String>> get();
	}
	
	public class Simple implements Order{
		private Order[] order;
		
		public Simple(Order... order){
			this.order = order;
		}
		
		public List<List<String>> get() {
			List<List<String>> result = Lists.newArrayList();
			result.add(Lists.newArrayList());
			for(Order o: order) {
				List<List<String>> possibleNextSteps = o.get();
				List<List<String>> possiblePathesWithNextSteps = Lists.newArrayList();
				for(List<String> possiblePathesUpToThisOrderIteration: result) {
					for(List<String> nextSteps: possibleNextSteps) {
						List<String> tmp = Lists.newArrayList(possiblePathesUpToThisOrderIteration);
						tmp.addAll(nextSteps);
						possiblePathesWithNextSteps.add(tmp);
					}
					
				}
				result = possiblePathesWithNextSteps;
			}
			return result;
		}
	}
	
	public class Or implements Order{
		private Order[] order;
		
		public Or(Order... order){
			this.order = order;
		}
		
		public List<List<String>> get() {
			List<List<String>> result = Lists.newArrayList();
			for(Order o: order) {
				result.addAll(o.get());
			}
			return result;
		}
	}
	
	public static class Plus implements Order{
		private Order order;
		
		public Plus(Order order){
			this.order = order;
		}
		
		public List<List<String>> get() {
			List<List<String>> result = Lists.newArrayList();
			result.add(Lists.newArrayList());
			for(int i=0; i<FiniteStateMachineTestingFramework.maxRepeat; i++) {
				List<List<String>> newPathes = Lists.newArrayList();
				for(List<String> possibleStartPaths: result) {
					for(List<String> possibleRepeats: order.get()) {
						List<String> clone = Lists.newArrayList(possibleStartPaths);
						clone.addAll(possibleRepeats);
						newPathes.add(clone);
					}
				}
				result.addAll(newPathes);
			}
			result.remove(0); // empty path should not be in result
			return result;
		}
	}
	
	public class Opt implements Order{
		private Order order;
		
		public Opt(Order order){
			this.order = order;
		}
		
		public List<List<String>> get() {
			List<List<String>> result = Lists.newArrayList();
			result.add(Lists.newArrayList());
			result.addAll(order.get());
			return result;
		}
	}
	
	public class Star implements Order{
		private Order order;
		
		public Star(Order order){
			this.order = new Opt(new Plus(order));
		}
		
		public List<List<String>> get() {
			return order.get();
		}
	}
	
	public class E implements Order{
		private String event;
		
		public E(String event){
			this.event = event;
		}
		
		public List<List<String>> get() {
			List<List<String>> result = Lists.newArrayList();
			result.add(Lists.newArrayList(event));
			return result;
		}
	}

}

