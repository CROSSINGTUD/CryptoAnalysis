package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import crypto.rules.CrySLMethod;
import crypto.rules.TransitionEdge;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FiniteStateMachineToTypestateChangeFunction extends TypeStateMachineWeightFunctions {

	private final MatcherTransitionCollection matcherTransitions;

	public FiniteStateMachineToTypestateChangeFunction(MatcherTransitionCollection matcherTransitions) {
		this.matcherTransitions = matcherTransitions;

		for (MatcherTransition transition : matcherTransitions.getAllTransitions()) {
			this.addTransition(transition);
		}
	}

	@Override
	public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(ControlFlowGraph.Edge edge) {
		Set<WeightedForwardQuery<TransitionFunction>> out = new HashSet<>();
		Statement statement = edge.getStart();
		
		if (!statement.containsInvokeExpr()) {
			return out;
		}
		
		InvokeExpr invokeExpr = statement.getInvokeExpr();
		DeclaredMethod declaredMethod = invokeExpr.getMethod();

		if (!isMethodOnInitialTransition(declaredMethod)) {
			return out;
		}
		
		if (declaredMethod.isStatic()) {
			if (statement.isAssign()) {
				Val leftOp = statement.getLeftOp();
				Val rightOp = statement.getRightOp();
				out.add(createQuery(edge, new AllocVal(leftOp, statement, rightOp)));
			}
		} else if (invokeExpr.isInstanceInvokeExpr() && invokeExpr.isSpecialInvokeExpr()){
			Val base = invokeExpr.getBase();

			out.add(createQuery(edge, new AllocVal(base, statement, base)));
		}
		return out;
	}

	private WeightedForwardQuery<TransitionFunction> createQuery(ControlFlowGraph.Edge edge, Val allocVal) {
		return new WeightedForwardQuery<>(edge, allocVal, matcherTransitions.getInitialWeight(edge));
	}

	private boolean isMethodOnInitialTransition(DeclaredMethod declaredMethod) {
		for (TransitionEdge edge : matcherTransitions.getInitialTransitions()) {
			for (CrySLMethod method : edge.getLabel()) {
				if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method, declaredMethod)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected State initialState() {
		throw new UnsupportedOperationException("This method should never be called.");
	}

}
