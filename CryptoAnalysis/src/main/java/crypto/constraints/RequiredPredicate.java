package crypto.constraints;

import boomerang.scene.Statement;
import crysl.rule.CrySLPredicate;

public record RequiredPredicate(CrySLPredicate predicate, Statement statement, int index) {}
