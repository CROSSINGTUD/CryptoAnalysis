package crypto.constraints;

import boomerang.scope.Statement;
import crysl.rule.CrySLPredicate;
import java.util.Collection;

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
 *
 * @param statement the statement the alternatives are required
 * @param allAlternatives all alternatives that are specified in the rule
 * @param predicates the actual predicates that are expected to be ensured at the given statement
 */
public record AlternativeReqPredicate(
        Statement statement,
        Collection<CrySLPredicate> allAlternatives,
        Collection<RequiredPredicate> predicates)
        implements IRequiredPredicate {}
