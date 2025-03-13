package crypto.constraints;

import boomerang.scope.Statement;
import crysl.rule.CrySLPredicate;

/**
 * Wrapper class for predicates from the REQUIRES section. This class only stores single predicates,
 * that is, predicates of the form
 *
 * <pre>{@code
 * REQUIRES
 *    generatedKey[...];
 * }</pre>
 *
 * If a predicate has alternatives, a {@link AlternativeReqPredicate} is used.
 *
 * @param predicate the predicate wrapped in this object
 * @param statement the statement where the predicate is required
 * @param index the statement's parameter index
 */
public record RequiredPredicate(CrySLPredicate predicate, Statement statement, int index)
        implements IRequiredPredicate {}
