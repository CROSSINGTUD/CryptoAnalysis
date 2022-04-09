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

public class FiniteStateMachineTestingFramework{

	private StateMachineGraph smg;
	private String crySLRule;
	private Ruleset ruleset;
	
	public FiniteStateMachineTestingFramework(String crySLRule, Ruleset ruleset) {
		this.crySLRule = crySLRule;
		this.ruleset = ruleset;
	}
	
	public void assertInSMG(String methods) {
		if(!isPathOfMethodsInSMG(methods)) {
			throw new AssertionError("Order of calls are not in SMG but should be: " + methods);
		};
	}
	
	public void assertNotInSMG(String methods) {
		if(isPathOfMethodsInSMG(methods)) {
			throw new AssertionError("Order of calls are in SMG but should not be: " + methods);
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
	
	public class Plus implements Order{
		private Order order;
		
		public Plus(Order order){
			this.order = order;
		}
		
		public List<String> get() {
			List<String> result = Lists.newArrayList();
			for(int i=(new Random()).nextInt(5)+1; i>0; i--) {
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

