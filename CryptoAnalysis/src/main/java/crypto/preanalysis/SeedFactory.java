package crypto.preanalysis;

import boomerang.Query;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import crypto.rules.CrySLRule;
import crypto.typestate.FiniteStateMachineToTypestateChangeFunction;
import crypto.typestate.MatcherTransitionCollection;
import soot.SootMethod;
import soot.Unit;

import java.util.List;
import java.util.Set;

public class SeedFactory {

	private List<FiniteStateMachineToTypestateChangeFunction> idealAnalysisDefs = Lists.newLinkedList();
	private Set<Query> seeds = Sets.newHashSet();

	public SeedFactory(List<CrySLRule> rules) {
		for(CrySLRule rule : rules){
			idealAnalysisDefs.add(new FiniteStateMachineToTypestateChangeFunction(MatcherTransitionCollection.makeCollection(rule.getUsagePattern())));
		}
	}

	public void generate(SootMethod method, Unit unit) {
		for(FiniteStateMachineToTypestateChangeFunction defs : idealAnalysisDefs){
			// TODO Refactor
			//Collection<WeightedForwardQuery<TransitionFunction>> found = defs.generateSeed(method, unit);
			//seeds.addAll(found);
		}
	}
	
	public boolean hasSeeds(){
		return !seeds.isEmpty();
	}
}
