package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import boomerang.WeightedForwardQuery;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import soot.MethodOrMethodContext;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class FiniteStateMachineToTypestateChangeFunction extends TypeStateMachineWeightFunctions {

	private static final Logger LOGGER = LoggerFactory.getLogger(FiniteStateMachineToTypestateChangeFunction.class);
	
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
						LOGGER.error("The type of m.getDeclaringClass() does not appear to be consistent across fsm.initialTransitonLabel().");
                    }
				}
			}
		}
		this.fsm = fsm;
	}

	@Override
	public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(SootMethod method, Unit unit) {
		Set<WeightedForwardQuery<TransitionFunction>> out = new HashSet<>();
		
		if(unit instanceof AssignStmt && ((AssignStmt) unit).getRightOp() instanceof StaticFieldRef) {
			AssignStmt stmt = (AssignStmt) unit;
			StaticFieldRef value = (StaticFieldRef) stmt.getRightOp();
			SootClass dClass = value.getFieldRef().declaringClass();
			ReachableMethods rm = Scene.v().getReachableMethods();
			QueueReader<MethodOrMethodContext> listener = rm.listener();
			while (listener.hasNext()) {
				MethodOrMethodContext next = listener.next();
				SootMethod m = next.method();
				if(m.getDeclaringClass().equals(dClass) && m.getName().equals("<clinit>")) {
					Collection<WeightedForwardQuery<TransitionFunction>> nestedSeeds = new HashSet<>();
			        if (!m.hasActiveBody())
			            return out;
			        
			        if(findSeedSource(m, value))
			        	out.add(createQuery(stmt,method,new AllocVal(stmt.getLeftOp(), method, stmt.getRightOp(), new Statement(stmt,method))));
				}
			}
		}
		
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
			}
		} else if (invokeExpr instanceof InstanceInvokeExpr){
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			out.add(createQuery(unit,method,new AllocVal(iie.getBase(), method,iie, new Statement((Stmt) unit,method))));
		}
		return out;
	}

	private boolean findSeedSource(SootMethod m, Value value) {
		for (Unit u : m.getActiveBody().getUnits()) {
        	if(u instanceof JAssignStmt) {
        		Value val = ((AssignStmt) u).getLeftOp();
        		if(val.toString().equals(value.toString())) {
        			Value rval = ((AssignStmt) u).getRightOp();
        			if(rval instanceof JStaticInvokeExpr) {
        				SootMethod calledMethod = ((JStaticInvokeExpr) rval).getMethod();
        				if (fsm.initialTransitonLabel().contains(calledMethod))
            				return true;
        				else
        					return false;
        			}
        			return findSeedSource(m, rval);
        		}
        	}
		}
		return false;
	}

	private WeightedForwardQuery<TransitionFunction> createQuery(Unit unit, SootMethod method, AllocVal allocVal) {
		return new WeightedForwardQuery<TransitionFunction>(new Statement((Stmt)unit,method), allocVal, fsm.getInitialWeight(new Statement((Stmt)unit,method)));
	}


	@Override
	protected State initialState() {
		throw new UnsupportedOperationException("This method should never be called.");
	}
	
	
}
