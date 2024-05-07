package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;

// TODO Fill with content
public class NoCallToError extends ErrorWithObjectAllocation {

    public NoCallToError(Statement statement, IAnalysisSeed seed, CrySLRule rule) {
        super(statement, rule, seed);
    }

    @Override
    public String toErrorMarkerString() {
        return "Call to " + getErrorStatement() + " not allowed";
    }

    @Override
    public void accept(ErrorVisitor visitor) {
        visitor.visit(this);
    }
}
