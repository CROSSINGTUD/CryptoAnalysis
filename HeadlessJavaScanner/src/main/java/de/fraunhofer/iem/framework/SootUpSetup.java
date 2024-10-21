package de.fraunhofer.iem.framework;

import boomerang.scene.CallGraph;
import crypto.rules.CrySLRule;
import de.fraunhofer.iem.scanner.AnalysisSettings;

import java.util.Collection;

public class SootUpSetup implements FrameworkSetup {

    @Override
    public void initializeFramework(String applicationPath, AnalysisSettings.CallGraphAlgorithm algorithm) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        throw new RuntimeException("Not implemented");
    }
}
