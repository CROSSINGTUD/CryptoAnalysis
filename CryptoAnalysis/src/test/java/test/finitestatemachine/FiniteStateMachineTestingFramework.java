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
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

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
		assert isPathOfMethodsInSMG(methods);
	}
	
	public void assertNotInSMG(String methods) {
		assert !isPathOfMethodsInSMG(methods);
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

}

