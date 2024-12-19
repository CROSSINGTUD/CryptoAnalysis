package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;

/** Super class for all errors from the ORDER section */
public abstract class AbstractOrderError extends AbstractError {

    public AbstractOrderError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule) {
        super(seed, errorStmt, rule);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
