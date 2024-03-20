package crypto.boomerang;

import boomerang.scene.AllocVal;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import boomerang.scene.jimple.IntAndStringBoomerangOptions;

import java.util.Optional;

/**
 * Created by johannesspath on 23.12.17.
 */
public class CogniCryptIntAndStringBoomerangOptions extends IntAndStringBoomerangOptions {

	@Override
	public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
		if (stmt.containsInvokeExpr() && stmt.isAssign()) {
			Val leftOp = stmt.getLeftOp();
			Val rightOp = stmt.getRightOp();

			if (leftOp.equals(fact)) {
				DeclaredMethod method = stmt.getInvokeExpr().getMethod();
				String sig = method.getSignature();

				if (sig.equals("<java.math.BigInteger: java.math.BigInteger valueOf(long)>")) {
					Val arg = stmt.getInvokeExpr().getArg(0);
					return Optional.of(new AllocVal(leftOp, stmt, arg));
				}

				if (sig.equals("<java.lang.String: char[] toCharArray()>")) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				if (sig.equals("<java.lang.String: byte[] getBytes()>")) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}
				
				if (stmt.getInvokeExpr().getMethod().isNative()) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				/*if (Scene.v().isExcluded(stmt.getInvokeExpr().getMethod().getDeclaringClass())) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				if (!Scene.v().getCallGraph().edgesOutOf(stmt).hasNext()) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}*/
			}
		}
		if (stmt.containsInvokeExpr()) {
			if (stmt.getInvokeExpr().getMethod().isConstructor() && (stmt.getInvokeExpr().isInstanceInvokeExpr())) {
				Val base = stmt.getInvokeExpr().getBase();

				if (base.equals(fact)) {
					return Optional.of(new AllocVal(base, stmt, base));
				}
			}
		}

		if (!(stmt.isAssign())) {
			return Optional.empty();
		}

		Val leftOp = stmt.getLeftOp();
		Val rightOp = stmt.getRightOp();
		if (!leftOp.equals(fact)) {
			return Optional.empty();
		}
		/*if (as.getRightOp() instanceof StaticFieldRef) {
			StaticFieldRef sfr = (StaticFieldRef) as.getRightOp();
			if (sfr.getField().toString().equals("<java.security.spec.RSAKeyGenParameterSpec: java.math.BigInteger F4>")) {
				return Optional.of(new AllocVal(as.getLeftOp(), m, IntConstant.v(65537), new Statement(stmt, m)));
			}
			if (sfr.getField().toString().equals("<java.security.spec.RSAKeyGenParameterSpec: java.math.BigInteger F0>")) {
				return Optional.of(new AllocVal(leftOp, stmt, new JimpleVal() IntConstant.v(3));
			}
		}*/
		if (rightOp.isLengthExpr()) {
			return Optional.of(new AllocVal(leftOp, stmt, rightOp));
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
		if (isAllocationVal(rightOp)) {
			return Optional.of(new AllocVal(leftOp, stmt, rightOp));
		}

		return Optional.empty();
	}

    @Override
	public int analysisTimeoutMS() {
		return 5000;
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return true;
	}
}
