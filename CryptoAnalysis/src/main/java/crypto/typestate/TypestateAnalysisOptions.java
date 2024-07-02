package crypto.typestate;

import boomerang.DefaultBoomerangOptions;

public class TypestateAnalysisOptions extends DefaultBoomerangOptions {

    private final int timeout;

    public TypestateAnalysisOptions(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public StaticFieldStrategy getStaticFieldStrategy() {
        return StaticFieldStrategy.FLOW_SENSITIVE;
    }

    @Override
    public boolean allowMultipleQueries() {
        return true;
    }

    @Override
    public int analysisTimeoutMS() {
        return timeout;
    }
}
