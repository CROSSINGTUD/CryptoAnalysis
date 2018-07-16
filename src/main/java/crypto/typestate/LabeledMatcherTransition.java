package crypto.typestate;

import java.util.List;

import crypto.rules.CryptSLMethod;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

	private final List<CryptSLMethod> label;

	public LabeledMatcherTransition(State from, List<CryptSLMethod> label, Parameter param, State to, Type type) {
		super(from, CryptSLMethodToSootMethod.v().convert(label), param, to, type);
		this.label = label;
	}
	
	public List<CryptSLMethod> label(){
		return label;
	}
}
