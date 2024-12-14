package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLRule;
import java.util.Objects;

public class NoCallToError extends AbstractConstraintsError {

    public NoCallToError(IAnalysisSeed seed, Statement statement, CrySLRule rule) {
        super(seed, statement, rule);
    }

    @Override
    public String toErrorMarkerString() {
        return "Call to " + getErrorStatement() + " not allowed";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof NoCallToError;
    }

    @Override
    public String toString() {
        return "NoCallToError: " + toErrorMarkerString();
    }
}
