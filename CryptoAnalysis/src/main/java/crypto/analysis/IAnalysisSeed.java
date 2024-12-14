package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.errors.AbstractError;
import crysl.rule.CrySLPredicate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import typestate.TransitionFunction;

public abstract class IAnalysisSeed {

    protected final CryptoScanner scanner;
    protected final Collection<AbstractError> errorCollection;
    protected final ForwardBoomerangResults<TransitionFunction> analysisResults;
    protected final Multimap<Statement, ExpectedPredicateOnSeed> expectedPredicates =
            HashMultimap.create();

    protected record ExpectedPredicateOnSeed(
            CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {}

    private final Statement origin;
    private final Val fact;
    private boolean secure = true;

    public IAnalysisSeed(
            CryptoScanner scanner,
            Statement origin,
            Val fact,
            ForwardBoomerangResults<TransitionFunction> results) {
        this.scanner = scanner;
        this.origin = origin;
        this.fact = fact;
        this.analysisResults = results;

        this.errorCollection = new HashSet<>();
    }

    public abstract void execute();

    public abstract void expectPredicate(
            Statement statement, CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex);

    protected Collection<CrySLPredicate> expectedPredicatesAtStatement(Statement statement) {
        Collection<CrySLPredicate> predicates = new HashSet<>();

        if (!expectedPredicates.containsKey(statement)) {
            return predicates;
        }

        Collection<ExpectedPredicateOnSeed> expectedPredicateOnSeeds =
                expectedPredicates.get(statement);
        for (ExpectedPredicateOnSeed expectedPredicateOnSeed : expectedPredicateOnSeeds) {
            predicates.add(expectedPredicateOnSeed.predicate());
        }

        return predicates;
    }

    public Method getMethod() {
        return origin.getMethod();
    }

    public Statement getOrigin() {
        return origin;
    }

    public Val getFact() {
        return fact;
    }

    public Type getType() {
        return fact.getType();
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public ForwardBoomerangResults<TransitionFunction> getAnalysisResults() {
        return analysisResults;
    }

    public void addError(AbstractError e) {
        this.errorCollection.add(e);
    }

    public Collection<AbstractError> getErrors() {
        return new HashSet<>(errorCollection);
    }

    public CryptoScanner getScanner() {
        return scanner;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IAnalysisSeed other
                && Objects.equals(origin, other.origin)
                && Objects.equals(fact, other.fact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, fact);
    }

    @Override
    public String toString() {
        return fact.getVariableName() + " at " + origin;
    }
}
