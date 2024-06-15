package crypto.preanalysis;

import boomerang.scene.jimple.BoomerangPretransformer;
import crypto.rules.CrySLRule;

import java.util.Collection;

public class TransformerSetup {

    private static TransformerSetup instance;

    public void setupPreTransformer(Collection<CrySLRule> rules) {
        // Transformer related to the analysis
        setupCastTransformer();
        setupEmptyStatementTransformer(rules);
        setupExceptionAwareTransformer(rules);

        // Transformer related to Boomerang
        setupBoomerangTransformer();
    }

    public void setupCastTransformer() {
        CastTransformer.v().reset();
        CastTransformer.v().apply();
    }

    public void setupEmptyStatementTransformer(Collection<CrySLRule> rules) {
        EmptyStatementTransformer transformer = new EmptyStatementTransformer(rules);
        transformer.apply();
    }

    public void setupExceptionAwareTransformer(Collection<CrySLRule> rules) {
        for (CrySLRule rule : rules) {
            ExceptionAwareTransformer transformer = new ExceptionAwareTransformer(rule);
            transformer.apply();
        }
    }

    public void setupBoomerangTransformer() {
        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();
    }

    public static TransformerSetup v() {
        if (instance == null) {
            instance = new TransformerSetup();
        }
        return instance;
    }

    public void reset() {
        instance = null;
    }
}
