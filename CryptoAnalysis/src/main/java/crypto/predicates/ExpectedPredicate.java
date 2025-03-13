package crypto.predicates;

import boomerang.scope.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLPredicate;

public record ExpectedPredicate(
        IAnalysisSeed seed, CrySLPredicate predicate, Statement statement, int paramIndex) {}
