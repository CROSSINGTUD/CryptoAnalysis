package crypto.predicates;

import boomerang.scope.Statement;
import crypto.analysis.AnalysisSeedWithSpecification;
import crysl.rule.CrySLPredicate;
import java.util.Objects;

/**
 * Super class for predicates that are propagated during the analysis. Each predicate is either an
 * {@link EnsuredPredicate} or an {@link UnEnsuredPredicate}. The former ones keep track of all
 * predicates from the ENSURES section where no violations from a rule are found. The latter ones
 * are propagated to keep track of predicates and seeds if there is a violation.
 */
public abstract class AbstractPredicate {

    private final CrySLPredicate predicate;
    private final AnalysisSeedWithSpecification generatingSeed;
    private final Statement statement;
    private final int index;

    public AbstractPredicate(
            AnalysisSeedWithSpecification generatingSeed,
            CrySLPredicate predicate,
            Statement statement,
            int index) {
        this.predicate = predicate;
        this.generatingSeed = generatingSeed;
        this.statement = statement;
        this.index = index;
    }

    public CrySLPredicate getPredicate() {
        return predicate;
    }

    public AnalysisSeedWithSpecification getGeneratingSeed() {
        return generatingSeed;
    }

    public Statement getStatement() {
        return statement;
    }

    public int getIndex() {
        return index;
    }

    public boolean equalsSimple(AbstractPredicate other) {
        return Objects.equals(predicate, other.getPredicate()) && index == other.getIndex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, generatingSeed, statement, index);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractPredicate other
                && Objects.equals(predicate, other.getPredicate())
                && Objects.equals(generatingSeed, other.getGeneratingSeed())
                && Objects.equals(statement, other.getStatement())
                && index == other.getIndex();
    }
}
