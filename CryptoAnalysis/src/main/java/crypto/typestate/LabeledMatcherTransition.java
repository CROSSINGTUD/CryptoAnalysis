package crypto.typestate;

import java.util.List;

import crypto.rules.CrySLMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

	private final List<CrySLMethod> label;

	public LabeledMatcherTransition(State from, List<CrySLMethod> label, Parameter param, State to, Type type) {
		super(from, CrySLMethodToSootMethod.v().convert(label), param, to, type);
		this.label = label;
	}
	
	public List<CrySLMethod> label(){
		return label;
	}
}
