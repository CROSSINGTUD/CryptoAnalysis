package crypto.extractparameter.scope;

import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.WrappedClass;
import java.util.Arrays;

public class LongType implements Type {

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
        throw new RuntimeException("Long type has no array base");
    }

    @Override
    public WrappedClass getWrappedClass() {
        throw new RuntimeException("Long type has no declaring class");
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
        return Arrays.hashCode(new Object[] {"long"});
    }

    @Override
    public String toString() {
        return "long";
    }
}
