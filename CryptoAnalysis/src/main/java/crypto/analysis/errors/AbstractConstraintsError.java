package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;

/**
 * Super class for all errors that violate a constraint from the CONSTRAINTS and REQUIRES section
 */
public abstract class AbstractConstraintsError extends AbstractError {

    public AbstractConstraintsError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule) {
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
