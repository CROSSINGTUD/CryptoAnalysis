package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import java.util.Arrays;
import java.util.Collection;

public class ExtractedValue {

    private final Val val;
    private final Statement initialStatement;
    private final Collection<Type> types;

    public ExtractedValue(Val val, Statement initialStatement, Collection<Type> types) {
        this.val = val;
        this.initialStatement = initialStatement;
        this.types = types;
    }

    public Val getVal() {
        return val;
    }

    public Statement getInitialStatement() {
        return initialStatement;
    }

    public Collection<Type> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "Extracted Value: " + val + " with type " + types;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {val, initialStatement, types});
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        ExtractedValue other = (ExtractedValue) obj;
        if (val == null) {
            if (other.getVal() != null) return false;
        } else if (!val.equals(other.getVal())) {
            return false;
        }

        if (initialStatement == null) {
            if (other.getInitialStatement() != null) return false;
        } else if (!initialStatement.equals(other.getInitialStatement())) {
            return false;
        }

        if (types == null) {
            if (other.getTypes() != null) return false;
        } else if (!types.equals(other.getTypes())) {
            return false;
        }

        return true;
    }
}
