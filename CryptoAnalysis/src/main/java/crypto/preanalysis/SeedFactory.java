package crypto.preanalysis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import boomerang.Query;
import boomerang.WeightedForwardQuery;
import crypto.rules.CrySLRule;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import crypto.typestate.SootBasedStateMachineGraph;
import soot.SootMethod;
import soot.Unit;
import typestate.TransitionFunction;

public class SeedFactory {

	private List<FiniteStateMachineToTypestateChangeFunction> idealAnalysisDefs = Lists.newLinkedList();
	private Set<Query> seeds = Sets.newHashSet();

	public SeedFactory(List<CrySLRule> rules) {
		for(CrySLRule rule : rules){
			idealAnalysisDefs.add(new FiniteStateMachineToTypestateChangeFunction(new SootBasedStateMachineGraph(rule.getUsagePattern())));
		}
	}

	public void generate(SootMethod method, Unit unit) {
		for(FiniteStateMachineToTypestateChangeFunction defs : idealAnalysisDefs){
			Collection<WeightedForwardQuery<TransitionFunction>> found = defs.generateSeed(method, unit);
			seeds.addAll(found);
		}
	}
	
	public boolean hasSeeds(){
		return !seeds.isEmpty();
	}
}
