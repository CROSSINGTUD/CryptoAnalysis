package crypto.typestate;

import boomerang.WeightedForwardQuery;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.InterfaceInvokeExpr;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FiniteStateMachineToTypestateChangeFunction extends TypeStateMachineWeightFunctions {

	//private static final Logger LOGGER = LoggerFactory.getLogger(FiniteStateMachineToTypestateChangeFunction.class);
	
	//private Type analyzedType = null;

	private SootBasedStateMachineGraph fsm;

	public FiniteStateMachineToTypestateChangeFunction(SootBasedStateMachineGraph fsm) {
		for(MatcherTransition trans : fsm.getAllTransitions()){
			this.addTransition(trans);
		}
		/*for (Method m : fsm.initialTransitionLabel()) {
			if(m.isConstructor()){
				if (analyzedType == null){
					analyzedType = m.getDeclaringClass().getType();
				} else {
					// This code was added to detect unidentified outlying cases affected by the changes made for issue #47.
					if (analyzedType != m.getDeclaringClass().getType()){
						//LOGGER.error("The type of m.getDeclaringClass() does not appear to be consistent across fsm.initialTransitonLabel().");
                    }
				}
			}
		}*/
		this.fsm = fsm;
	}


	@Override
	public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(ControlFlowGraph.Edge edge) {
		Set<WeightedForwardQuery<TransitionFunction>> out = new HashSet<>();
		Statement statement = edge.getStart();
		
		if (!(statement.containsInvokeExpr())) {
			return out;
		}
		
		InvokeExpr invokeExpr = statement.getInvokeExpr();
		DeclaredMethod declaredMethod = invokeExpr.getMethod();
		Method method = statement.getMethod();
		
		//if (!fsm.initialTransitionLabel().contains(declaredMethod)) {
		if (!isMethodOnInitialTransition(declaredMethod)) {
			return out;
		}
		
		if (declaredMethod.isStatic()) {
			if (statement.isAssign()) {
				Val leftOp = statement.getLeftOp();
				Val rightOp = statement.getRightOp();
				out.add(createQuery(edge, new AllocVal(leftOp, statement, rightOp)));
			}
		} else if (invokeExpr.isInstanceInvokeExpr() && !(invokeExpr instanceof InterfaceInvokeExpr)){
			Val base = invokeExpr.getBase();

			out.add(createQuery(edge, new AllocVal(base, statement, base)));
		}
		return out;
	}

	private WeightedForwardQuery<TransitionFunction> createQuery(ControlFlowGraph.Edge edge, Val allocVal) {
		return new WeightedForwardQuery<>(edge, allocVal, fsm.getInitialWeight(edge));
	}

	private boolean isMethodOnInitialTransition(DeclaredMethod declaredMethod) {
		for (Method method : fsm.initialTransitionLabel()) {;

			if (!method.getName().equals(declaredMethod.getName())) {
				continue;
			}

			if (method.getParameterLocals().size() != declaredMethod.getInvokeExpr().getArgs().size()) {
				continue;
			}

			if (!doParameterTypesMatch(method, declaredMethod)) {
				continue;
			}

			return true;
		}
		return false;
	}

	private boolean doParameterTypesMatch(Method method, DeclaredMethod declaredMethod) {
		for (int i = 0; i < method.getParameterLocals().size(); i++) {
			Type methodParameter = method.getParameterLocal(i).getType();
			Type declaredMethodParameter = declaredMethod.getInvokeExpr().getArg(i).getType();

			if (!methodParameter.equals(declaredMethodParameter)) {
				return false;
			}
		}
		return true;
	}


	@Override
	protected State initialState() {
		throw new UnsupportedOperationException("This method should never be called.");
	}
	
	
}
