package crypto.preanalysis;

import boomerang.preanalysis.BoomerangPretransformer;
import crypto.rules.CrySLRule;

import java.util.List;

public class TransformerSetup {

    private static TransformerSetup instance;

    public void setupPreTransformer(List<CrySLRule> rules) {
        // Transformer related to the analysis
        setupCastTransformer();
        setupExceptionAwareTransformer(rules);

        // Transformer related to Boomerang
        setupBoomerangTransformer();
    }

    public void setupCastTransformer() {
        CastTransformer.v().reset();
        CastTransformer.v().apply();
    }

    public void setupExceptionAwareTransformer(List<CrySLRule> rules) {
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
