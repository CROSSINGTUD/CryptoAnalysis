package crypto.extractparameter;

import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Collection;

public record ExtractedValue(Val val, Statement initialStatement, Collection<Type> types) {

    @Override
    public String toString() {
        return "Extracted Value: " + val.getVariableName() + " with types " + types;
    }
}
