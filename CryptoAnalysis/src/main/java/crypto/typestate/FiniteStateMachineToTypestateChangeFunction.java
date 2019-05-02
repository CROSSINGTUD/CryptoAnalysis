package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.WeightedForwardQuery;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import crypto.analysis.CryptoScanner;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class FiniteStateMachineToTypestateChangeFunction extends TypeStateMachineWeightFunctions {

	private RefType analyzedType = null;

	private SootBasedStateMachineGraph fsm;

	public FiniteStateMachineToTypestateChangeFunction(SootBasedStateMachineGraph fsm) {
		for(MatcherTransition trans : fsm.getAllTransitions()){
			this.addTransition(trans);
		}
		for(SootMethod m : fsm.initialTransitonLabel()){
			if(m.isConstructor()){
				if (analyzedType == null){
					analyzedType = m.getDeclaringClass().getType();
				} else {
					// This code was added to detect unidentified outlying cases affected by the changes made for issue #47.
					if (analyzedType != m.getDeclaringClass().getType()){
                        try {
                            throw new Exception("The type of m.getDeclaringClass() does not appear to be consistent across fsm.initialTransitonLabel().");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
				}
			}
		}
		this.fsm = fsm;
	}


	@Override
	public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(SootMethod method, Unit unit) {
		Set<WeightedForwardQuery<TransitionFunction>> out = new HashSet<>();
		if (!(unit instanceof Stmt) || !((Stmt) unit).containsInvokeExpr())
			return out;
		InvokeExpr invokeExpr = ((Stmt) unit).getInvokeExpr();
		SootMethod calledMethod = invokeExpr.getMethod();
		if (!fsm.initialTransitonLabel().contains(calledMethod))
			return out;
		if (calledMethod.isStatic()) {
			if(unit instanceof AssignStmt){
				AssignStmt stmt = (AssignStmt) unit;
				out.add(createQuery(stmt,method,new AllocVal(stmt.getLeftOp(), method, stmt.getRightOp(), new Statement(stmt,method))));
			} else if(unit instanceof Stmt && ((Stmt) unit).containsInvokeExpr()) {
				Stmt stmt = (Stmt) unit;
				out.add(createQuery(stmt,method,new AllocVal(stmt.getInvokeExpr(), method, stmt.getInvokeExpr(), new Statement(stmt,method))));
			}
		} else if (invokeExpr instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			out.add(createQuery(unit,method,new AllocVal(iie.getBase(), method,iie, new Statement((Stmt) unit,method))));
		}
		return out;
	}

	private WeightedForwardQuery<TransitionFunction> createQuery(Unit unit, SootMethod method, AllocVal allocVal) {
		return new WeightedForwardQuery<TransitionFunction>(new Statement((Stmt)unit,method), allocVal, fsm.getInitialWeight(new Statement((Stmt)unit,method)));
	}


	@Override
	protected State initialState() {
		throw new RuntimeException("Should never be called!");
	}
	
	
}
