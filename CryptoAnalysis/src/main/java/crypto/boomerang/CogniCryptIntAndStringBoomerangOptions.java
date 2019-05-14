package crypto.boomerang;

import com.google.common.base.Optional;

import boomerang.IntAndStringBoomerangOptions;
import boomerang.callgraph.ObservableICFG;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

/**
 * Created by johannesspath on 23.12.17.
 */
public class CogniCryptIntAndStringBoomerangOptions extends IntAndStringBoomerangOptions {
	@Override
	public Optional<AllocVal> getAllocationVal(SootMethod m, Stmt stmt, Val fact,
			ObservableICFG<Unit, SootMethod> icfg) {
		if (stmt.containsInvokeExpr() && stmt instanceof AssignStmt) {
			AssignStmt as = (AssignStmt) stmt;
			if (as.getLeftOp().equals(fact.value())) {
				SootMethod method = as.getInvokeExpr().getMethod();
				String sig = method.getSignature();
				if (sig.equals("<java.math.BigInteger: java.math.BigInteger valueOf(long)>")) {
					Value arg = as.getInvokeExpr().getArg(0);
					return Optional.of(new AllocVal(as.getLeftOp(), m, arg, new Statement(stmt, m)));
				}
				if(sig.equals("<java.lang.String: char[] toCharArray()>")) {
					return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(stmt, m)));
				}
				if(sig.equals("<java.lang.String: byte[] getBytes()>")) {
					return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(stmt, m)));
				}
				
				if(as.getInvokeExpr().getMethod().isNative())
					return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(as, m)));

				if(Scene.v().isExcluded(as.getInvokeExpr().getMethod().getDeclaringClass()))
					return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(as, m)));

				if(!Scene.v().getCallGraph().edgesOutOf(stmt).hasNext()) {
					return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(as, m)));
				}
			}
		}
		if (stmt.containsInvokeExpr()) {
			if (stmt.getInvokeExpr().getMethod().isConstructor()
					&& (stmt.getInvokeExpr() instanceof InstanceInvokeExpr)) {
				InstanceInvokeExpr iie = (InstanceInvokeExpr) stmt.getInvokeExpr();
				Value base = iie.getBase();
				if (base.equals(fact.value())) {
					return Optional.of(new AllocVal(base, m, base, new Statement(stmt, m)));
				}
			}
		}

		if (!(stmt instanceof AssignStmt)) {
			return Optional.absent();
		}
		AssignStmt as = (AssignStmt) stmt;
		if (!as.getLeftOp().equals(fact.value())) {
			return Optional.absent();
		}
		if (as.getRightOp() instanceof StaticFieldRef) {
			StaticFieldRef sfr = (StaticFieldRef) as.getRightOp();
			if(sfr.getField().toString().equals("<java.security.spec.RSAKeyGenParameterSpec: java.math.BigInteger F4>")) {
				return Optional.of(new AllocVal(as.getLeftOp(), m, IntConstant.v(65537), new Statement(stmt, m)));
			}
			if(sfr.getField().toString().equals("<java.security.spec.RSAKeyGenParameterSpec: java.math.BigInteger F0>")) {
				return Optional.of(new AllocVal(as.getLeftOp(), m, IntConstant.v(3), new Statement(stmt, m)));
			}
		}
		if (as.getRightOp() instanceof LengthExpr) {
			return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(stmt, m)));
		}
//		if (as.containsInvokeExpr()) {
//			for (SootMethod callee : icfg.getCalleesOfCallAt(as)) {
//				for (Unit u : icfg.getEndPointsOf(callee)) {
//					if (u instanceof ReturnStmt && isAllocationVal(((ReturnStmt) u).getOp())) {
//						return Optional.of(
//								new AllocVal(as.getLeftOp(), m, ((ReturnStmt) u).getOp(), new Statement((Stmt) u, m)));
//					}
//				}
//			}
//		}
		if (isAllocationVal(as.getRightOp())) {
			return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(stmt, m)));
		}

		return Optional.absent();
	}

	@Override
	public boolean onTheFlyCallGraph() {
		return false;
	}

	@Override
	public boolean arrayFlows() {
		return true;
	}

	@Override
	public int analysisTimeoutMS() {
		return 5000;
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return false;
	}
}
