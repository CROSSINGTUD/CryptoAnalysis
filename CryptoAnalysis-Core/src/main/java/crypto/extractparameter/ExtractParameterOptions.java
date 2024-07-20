package crypto.extractparameter;

import boomerang.scene.AllocVal;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.jimple.IntAndStringBoomerangOptions;
import boomerang.scene.jimple.JimpleVal;
import soot.Scene;

import java.util.Optional;

/**
 * Created by johannesspath on 23.12.17.
 */
public class ExtractParameterOptions extends IntAndStringBoomerangOptions {

	private final int timeout;

	public ExtractParameterOptions(int timeout) {
		this.timeout = timeout;
	}

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

				// TODO Special handling for toCharArray required; trigger new BackwardQuery?
				if (sig.equals("<java.lang.String: char[] toCharArray()>")) {
					//Val rightBase = stmt.getInvokeExpr().getBase();
					//return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				if (sig.equals("<java.lang.String: byte[] getBytes()>")) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}
				
				if (stmt.getInvokeExpr().getMethod().isNative()) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				String className = stmt.getInvokeExpr().getMethod().getDeclaringClass().getName();
				if (Scene.v().isExcluded(className)) {
					//return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}

				/*if (!Scene.v().getCallGraph().edgesOutOf(stmt).hasNext()) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}*/
			}
		}
		if (stmt.containsInvokeExpr()) {
			if (stmt.getInvokeExpr().getMethod().isConstructor() && (stmt.getInvokeExpr().isInstanceInvokeExpr())) {
				Val base = stmt.getInvokeExpr().getBase();

				if (base.equals(fact)) {
					//return Optional.of(new AllocVal(base, stmt, base));
				}
			}
		}

		if (!stmt.isAssign()) {
			return Optional.empty();
		}

		Val leftOp = stmt.getLeftOp();
		Val rightOp = stmt.getRightOp();
		if (!leftOp.equals(fact)) {
			return Optional.empty();
		}

		// Extract static fields
		if (rightOp instanceof JimpleVal) {
			JimpleVal jimpleRightOp = (JimpleVal) rightOp;

			if (jimpleRightOp.isStaticFieldRef()) {
				AllocVal allocVal = new AllocVal(leftOp, stmt, rightOp);
				return Optional.of(allocVal);

			}
		}

		// Extract cast value from cast expressions (e.g. (int) 65000 -> 65000)
		if (rightOp.isCast()) {
			Val castOp = rightOp.getCastOp();

			if (isAllocationVal(castOp)) {
				return Optional.of(new AllocVal(leftOp, stmt, castOp));
			}
		}

		// Extract the length value of length expressions
		if (rightOp.isLengthExpr()) {
			return Optional.of(new AllocVal(leftOp, stmt, rightOp));
		}

		// Strings are initialized with a concrete value
		if (rightOp.isNewExpr()) {
			Type type = rightOp.getNewExprType();

			if (type.toString().equals("java.lang.String")) {
				return Optional.empty();
			}
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
	public boolean isAllocationVal(Val val) {
		if (val.isConstant()) {
			return true;
		}
		if (!trackStrings() && val.isStringBufferOrBuilder()) {
			return false;
		}
		if (trackNullAssignments() && val.isNull()) {
			return true;
		}
		if (getArrayStrategy() != ArrayStrategy.DISABLED && val.isArrayAllocationVal()) {
			return true;
		}
		if (trackStrings() && val.isStringConstant()) {
			return true;
		}
		if (!trackAnySubclassOfThrowable() && val.isThrowableAllocationType()) {
			return false;
		}

		return val.isNewExpr();
	}

    @Override
	public int analysisTimeoutMS() {
		return timeout;
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return true;
	}
}
