package crypto.rules;

import boomerang.scene.Statement;

public abstract class CrySLLiteral implements ISLConstraint {

    private Statement location;

    protected CrySLLiteral() {}

    public Statement getLocation() {
        return location;
    }
}
