package test.finitestatemachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.internal.util.Lists;
import com.google.inject.internal.util.Sets;

import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.cryslhandler.CrySLModelReader;
import crypto.cryslhandler.CryslReaderUtils;
import crypto.exceptions.CryptoAnalysisException;
import crypto.rules.CrySLRule;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.darmstadt.tu.crossing.crySL.Order;
import test.IDEALCrossingTestingFramework;

public abstract class FiniteStateMachineTestingFramework{

	private StateMachineGraph smg;
	private String crySLRule;
	private Ruleset ruleset;
	protected Order order;
	private Set<String> validPathsWithMaxRepeat2;
	private Set<String> validPathsWithMaxRepeat6;
	
	public FiniteStateMachineTestingFramework(String crySLRule, Ruleset ruleset) {
		this.crySLRule = crySLRule;
		this.ruleset = ruleset;
		this.validPathsWithMaxRepeat2 = Sets.newHashSet();
		this.validPathsWithMaxRepeat6 = Sets.newHashSet();
	}
	
	/**
	 * This test based on stochastic assumptions.
	 */
	@Test
	public void simulate() {
		if(order != null) {
			benchmark();
		}
	}
	
	private void benchmark() {
		benchmarkValidPaths(1000);
		benchmarkNonValidPaths();
	}
	
	public void benchmarkValidPaths(int rounds) {
		Plus.maxRepeat = 1;
		for(int i=0; i<rounds*10; i++) {
			String randomPath = String.join(",", order.get());
			validPathsWithMaxRepeat2.add(randomPath);
			validPathsWithMaxRepeat6.add(randomPath);
			assertInSMG(randomPath);
		}
		Plus.maxRepeat = 3;
		for(int i=0; i<rounds*500; i++) {
			String randomPath = String.join(",", order.get());
			validPathsWithMaxRepeat6.add(randomPath);
			assertInSMG(randomPath);
		}
	}
	
	public void benchmarkNonValidPaths() {
		for(String path: validPathsWithMaxRepeat2) {
			List<String> eventsForPath = Lists.newArrayList(Arrays.asList(path.split(",")));
			if(eventsForPath.size()>1) {
				for(int i=0; i<10; i++) {
					List<String> events = Lists.newArrayList(eventsForPath);
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
					String newPath = String.join(",", events);
					if(!validPathsWithMaxRepeat6.contains(newPath)) {
						assertNotInSMG(newPath);
					}
				}
			}
		}
	}
	
	public void assertInSMG(String methods) {
		if(!isPathOfMethodsInSMG(methods)) {
			throw new AssertionError("Order of calls are not in SMG but should be: " + methods);
		};
	}
	
	public void assertNotInSMG(String methods) {
		if(!methods.isEmpty() && isPathOfMethodsInSMG(methods)) {
			// the initial state is always accepting.
			throw new AssertionError("Order of calls are in SMG but should probably not be: " + methods);
		};
	}
	
	private boolean isPathOfMethodsInSMG(String methods) {
		String[] events = methods.split(",");
		final Set<StateNode> current = Sets.newHashSet();
		current.add(smg.getInitialTransition().getLeft());
		for(String event: events) {
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
	
	public interface Order{
		List<String> get();
	}
	
	public class Simple implements Order{
		private Order[] order;
		
		public Simple(Order... order){
			this.order = order;
		}
		
		public List<String> get() {
			List<String> result = Lists.newArrayList();
			for(Order o: order) {
				result.addAll(o.get());
			}
			return result;
		}
	}
	
	public class Or implements Order{
		private Order[] order;
		
		public Or(Order... order){
			this.order = order;
		}
		
		public List<String> get() {
			return order[(new Random()).nextInt(order.length)].get();
		}
	}
	
	public static class Plus implements Order{
		private Order order;
		static int maxRepeat = 2;
		
		public Plus(Order order){
			this.order = order;
		}
		
		public List<String> get() {
			List<String> result = Lists.newArrayList();
			for(int i=(new Random()).nextInt(maxRepeat)+1; i>0; i--) {
				result.addAll(order.get());
			}
			return result;
		}
	}
	
	public class Opt implements Order{
		private Order order;
		
		public Opt(Order order){
			this.order = order;
		}
		
		public List<String> get() {
			List<String> result = Lists.newArrayList();
			if((new Random()).nextInt(2)==0) {
				result.addAll(order.get());
			}
			return result;
		}
	}
	
	public class Star implements Order{
		private Order order;
		
		public Star(Order order){
			this.order = new Opt(new Plus(order));
		}
		
		public List<String> get() {
			return order.get();
		}
	}
	
	public class E implements Order{
		private String event;
		
		public E(String event){
			this.event = event;
		}
		
		public List<String> get() {
			return Lists.newArrayList(event);
		}
	}

}

