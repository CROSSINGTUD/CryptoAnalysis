package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLPredicate;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import typestate.TransitionFunction;

public abstract class IAnalysisSeed {

    protected final CryptoScanner scanner;
    protected final Collection<AbstractError> errorCollection;
    protected final ForwardBoomerangResults<TransitionFunction> analysisResults;
    protected final Multimap<Statement, ExpectedPredicateOnSeed> expectedPredicates =
            HashMultimap.create();

    protected static final class ExpectedPredicateOnSeed {

        private final CrySLPredicate predicate;
        private final IAnalysisSeed seed;
        private final int paramIndex;

        public ExpectedPredicateOnSeed(
                CrySLPredicate predicate, IAnalysisSeed seed, int paramIndex) {
            this.predicate = predicate;
            this.seed = seed;
            this.paramIndex = paramIndex;
        }

        public CrySLPredicate getPredicate() {
            return predicate;
        }

        public IAnalysisSeed getSeed() {
            return seed;
        }

        public int getParamIndex() {
            return paramIndex;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[] {predicate, seed, paramIndex});
        }

        @Override
        public boolean equals(Object obj) {
            if (getClass() != obj.getClass()) return false;
            ExpectedPredicateOnSeed other = (ExpectedPredicateOnSeed) obj;

            return predicate.equals(other.getPredicate())
                    && seed.equals(other.getSeed())
                    && paramIndex == other.getParamIndex();
        }

        @Override
        public String toString() {
            return predicate + " for " + seed + " @ " + paramIndex;
        }
    }

    private final Statement origin;
    private final Val fact;
    private String objectId;
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
            predicates.add(expectedPredicateOnSeed.getPredicate());
        }

        return predicates;
    }

    public Collection<IAnalysisSeed> getDependantSeeds() {
        Collection<IAnalysisSeed> seeds = new HashSet<>();

        for (ExpectedPredicateOnSeed seed : expectedPredicates.values()) {
            if (this.equals(seed.getSeed())) {
                continue;
            }

            seeds.add(seed.getSeed());
        }

        return seeds;
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

    public String getObjectId() {
        if (objectId == null) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            this.objectId = new BigInteger(1, md.digest(this.toString().getBytes())).toString(16);
        }
        return this.objectId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (!(obj instanceof IAnalysisSeed)) return false;
        IAnalysisSeed other = (IAnalysisSeed) obj;

        if (!origin.equals(other.getOrigin())) return false;
        return fact.equals(other.getFact());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((fact == null) ? 0 : fact.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return fact.getVariableName() + " at " + origin;
    }
}
