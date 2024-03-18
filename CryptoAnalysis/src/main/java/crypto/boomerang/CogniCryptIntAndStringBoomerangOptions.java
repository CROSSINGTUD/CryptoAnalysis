package crypto.boomerang;

import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.jimple.IntAndStringBoomerangOptions;

import boomerang.scene.AllocVal;
import boomerang.scene.Val;
import soot.Scene;
import soot.SootMethod;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.LengthExpr;
import soot.jimple.StaticFieldRef;

import java.util.Optional;

/**
 * Created by johannesspath on 23.12.17.
 */
public class CogniCryptIntAndStringBoomerangOptions extends IntAndStringBoomerangOptions {
	@Override
	public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
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
	public int analysisTimeoutMS() {
		return 5000;
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return false;
	}
}
