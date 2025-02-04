package crypto.predicates;

import boomerang.scene.Statement;
import crypto.analysis.IAnalysisSeed;
import crysl.rule.CrySLPredicate;

public record ExpectedPredicate(
        IAnalysisSeed seed, CrySLPredicate predicate, Statement statement, int paramIndex) {}
