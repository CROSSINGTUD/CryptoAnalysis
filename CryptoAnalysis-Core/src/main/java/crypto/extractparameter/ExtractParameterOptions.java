package crypto.extractparameter;

import boomerang.scene.AllocVal;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.jimple.IntAndStringBoomerangOptions;
import boomerang.scene.jimple.JimpleVal;
import crypto.definition.ExtractParameterDefinition;
import crypto.extractparameter.transformation.OperatorTransformation;
import crypto.extractparameter.transformation.StringTransformation;
import crypto.extractparameter.transformation.WrapperTransformation;

import java.util.Optional;

/**
 * Created by johannesspath on 23.12.17.
 */
public class ExtractParameterOptions extends IntAndStringBoomerangOptions {

	private final ExtractParameterDefinition definition;

	public ExtractParameterOptions(ExtractParameterDefinition definition) {
		this.definition = definition;
	}

	@Override
	public Optional<AllocVal> getAllocationVal(Method m, Statement stmt, Val fact) {
		if (stmt.isAssign()) {
			Val leftOp = stmt.getLeftOp();
			Val rightOp = stmt.getRightOp();

			if (!leftOp.equals(fact)) {
				return Optional.empty();
			}

			if (stmt.containsInvokeExpr()) {
				StringTransformation stringTransformation = new StringTransformation(definition);
				Optional<AllocVal> extractedStringValue = stringTransformation.evaluateExpression(stmt);

				if (extractedStringValue.isPresent()) {
					return extractedStringValue;
				}

				WrapperTransformation wrapperTransformation = new WrapperTransformation(definition);
				Optional<AllocVal> extractedWrapperValue = wrapperTransformation.evaluateExpression(stmt);

				if (extractedWrapperValue.isPresent()) {
					return extractedWrapperValue;
				}

				// TODO Move this into transformation package
				DeclaredMethod method = stmt.getInvokeExpr().getMethod();
				String sig = method.getSignature();

				if (sig.equals("<java.math.BigInteger: java.math.BigInteger valueOf(long)>")) {
					Val arg = stmt.getInvokeExpr().getArg(0);
					return Optional.of(new AllocVal(leftOp, stmt, arg));
				}

				if (stmt.getInvokeExpr().getMethod().isNative()) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}
			} else {
				OperatorTransformation operatorTransformation = new OperatorTransformation(definition);
				Optional<AllocVal> extractedOperatorValue = operatorTransformation.evaluateExpression(stmt);

				if (extractedOperatorValue.isPresent()) {
					return extractedOperatorValue;
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

				// Strings are initialized with a concrete value
				if (rightOp.isNewExpr()) {
					Type type = rightOp.getNewExprType();

					if (type.toString().equals("java.lang.String")) {
						return Optional.empty();
					}
				}

				if (isAllocationVal(rightOp)) {
					return Optional.of(new AllocVal(leftOp, stmt, rightOp));
				}
			}
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
		return definition.getTimeout();
	}
	
	@Override
	public boolean trackStaticFieldAtEntryPointToClinit() {
		return true;
	}
}
