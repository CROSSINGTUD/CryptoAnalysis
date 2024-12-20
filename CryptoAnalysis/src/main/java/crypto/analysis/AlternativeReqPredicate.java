package crypto.analysis;

import boomerang.scene.Statement;
import crysl.rule.CrySLPredicate;
import crysl.rule.ISLConstraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper class for required predicates with alternatives. Predicates from the REQUIRES section may
 * have the following form:
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...] || generatedPubKey[...] || generatedPrivKey[...];
 * }</pre>
 *
 * According to the CrySL specification, "generatedKey" is the base predicate that determines the
 * statement where the predicates should be ensured. If an alternative refers to some other
 * statement, it is ignored for this predicate.
 */
public class AlternativeReqPredicate implements ISLConstraint {

    private final RequiredCrySLPredicate basePredicate;
    private final Collection<CrySLPredicate> allAlternatives;
    private final Collection<RequiredCrySLPredicate> relAlternatives;

    public AlternativeReqPredicate(
            RequiredCrySLPredicate basePredicate,
            Collection<CrySLPredicate> allAlternatives,
            Collection<RequiredCrySLPredicate> relAlternatives) {
        this.basePredicate = basePredicate;
        this.allAlternatives = List.copyOf(allAlternatives);
        this.relAlternatives = Set.copyOf(relAlternatives);
    }

    public Collection<CrySLPredicate> getAllAlternatives() {
        return allAlternatives;
    }

    public Collection<RequiredCrySLPredicate> getRelAlternatives() {
        return relAlternatives;
    }

    public Statement getLocation() {
        return basePredicate.getLocation();
    }

    @Override
    public List<String> getInvolvedVarNames() {
        List<String> involvedVarNames = new ArrayList<>();
        for (CrySLPredicate alt : allAlternatives) {
            involvedVarNames.addAll(alt.getInvolvedVarNames());
        }
        return involvedVarNames;
    }

    @Override
    public String getName() {
        return allAlternatives.stream()
                .map(CrySLPredicate::getName)
                .collect(Collectors.joining(" OR "));
    }

    @Override
    public int hashCode() {
        return Objects.hash(allAlternatives, relAlternatives);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AlternativeReqPredicate other
                && Objects.equals(allAlternatives, other.getAllAlternatives())
                && Objects.equals(relAlternatives, other.getRelAlternatives());
    }

    @Override
    public String toString() {
        return allAlternatives.stream()
                        .map(CrySLPredicate::toString)
                        .collect(Collectors.joining(" OR "))
                + " (Rel: "
                + relAlternatives.stream()
                        .map(e -> e.getPred().toString())
                        .collect(Collectors.joining(", "))
                + ")";
    }
}
