package crypto.extractparameter;

import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import java.util.Collection;

public record ExtractedValue(Val val, Statement initialStatement, Collection<Type> types) {

    @Override
    public String toString() {
        return "Extracted Value: " + val + " with types " + types;
    }
}
