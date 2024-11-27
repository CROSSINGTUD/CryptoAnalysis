package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;

public class NoCallToError extends AbstractError {

    public NoCallToError(IAnalysisSeed seed, Statement statement, CrySLRule rule) {
        super(seed, statement, rule);
    }

    @Override
    public String toErrorMarkerString() {
        return "Call to " + getErrorStatement() + " not allowed";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "NoCallToError: " + toErrorMarkerString();
    }
}
