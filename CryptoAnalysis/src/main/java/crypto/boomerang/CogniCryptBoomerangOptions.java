package crypto.boomerang;

import boomerang.DefaultBoomerangOptions;
import boomerang.scene.AllocVal;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;

import java.util.Optional;

public class CogniCryptBoomerangOptions extends DefaultBoomerangOptions {

	@Override
	public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
	
		if (stmt.containsInvokeExpr()) {
			if (stmt.isAssign()) {
				Val leftOp = stmt.getLeftOp();
				Val rightOp = stmt.getRightOp();

				if (leftOp.equals(fact)) {
					if(stmt.getInvokeExpr().getMethod().isNative())
						return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}
			}
			if (stmt.getInvokeExpr().getMethod().isConstructor()
					&& (stmt.getInvokeExpr().isInstanceInvokeExpr())) {
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
		return false;
	}

    @Override
	public int analysisTimeoutMS() {
		return 600000000;
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return true;
	}

	@Override
	public boolean allowMultipleQueries() {
		return true;
	}

}
