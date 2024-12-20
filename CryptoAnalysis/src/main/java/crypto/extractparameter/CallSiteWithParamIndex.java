package crypto.extractparameter;

import boomerang.scene.Statement;

public record CallSiteWithParamIndex(Statement statement, int index, String varName) {

    @Override
    public String toString() {
        return varName + " at " + statement + " and " + index;
    }
}
