package crypto.boomerang;

import boomerang.DefaultBoomerangOptions;
import boomerang.scene.AllocVal;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

import java.util.Optional;

public class CogniCryptBoomerangOptions extends DefaultBoomerangOptions {
	@Override
	public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
	
		if (stmt.containsInvokeExpr()) {
			if (stmt instanceof AssignStmt) {
				AssignStmt as = (AssignStmt) stmt;
				if (as.getLeftOp().equals(fact.value())) {
					if(as.getInvokeExpr().getMethod().isNative())
						return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp(), new Statement(as, m)));
				}
			}
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
	public boolean isAllocationVal(Value val) {
		if (!trackStrings() && isStringAllocationType(val.getType())) {
			return false;
		}
		if (trackNullAssignments() && val instanceof NullConstant) {
			return true;
		}
		if (arrayFlows() && isArrayAllocationVal(val)) {
			return true;
		}
		if (trackStrings() && val instanceof StringConstant) {
			return true;
		}
		if (!trackAnySubclassOfThrowable() && isThrowableAllocationType(val.getType())) {
			return false;
		}

		return false;
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
