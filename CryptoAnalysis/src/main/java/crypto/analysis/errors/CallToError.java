package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;

import java.util.Collection;

// TODO Adapt messages
public class CallToError extends ErrorWithObjectAllocation {

    private final Collection<CrySLMethod> requiredMethods;

    public CallToError(Statement statement, IAnalysisSeed seed, CrySLRule rule, Collection<CrySLMethod> requiredMethods) {
        super(statement, rule, seed);

        this.requiredMethods = requiredMethods;
    }

    @Override
    public String toErrorMarkerString() {
        return "Call to " + requiredMethods + " is missing";
    }

    @Override
    public void accept(ErrorVisitor visitor) {
        visitor.visit(this);
    }
}
