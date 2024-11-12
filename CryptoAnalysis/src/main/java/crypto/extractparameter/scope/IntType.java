package crypto.extractparameter.scope;

import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.WrappedClass;

import java.util.Arrays;

public class IntType implements Type {

	@Override
	public boolean isNullType() {
		return false;
	}

	@Override
	public boolean isRefType() {
		return false;
	}

	@Override
	public boolean isArrayType() {
		return false;
	}

	@Override
	public Type getArrayBaseType() {
		throw new RuntimeException("Int type has no array base type");
	}

	@Override
	public WrappedClass getWrappedClass() {
		throw new RuntimeException("Int type has no declaring class");
	}

	@Override
	public boolean doesCastFail(Type targetVal, Val target) {
		return false;
	}

	@Override
	public boolean isSubtypeOf(String type) {
		return false;
	}

	@Override
	public boolean isSupertypeOf(String subType) {
		return false;
	}

	@Override
	public boolean isBooleanType() {
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] {"int"});
	}

	@Override
	public String toString() {
		return "int";
	}
}
