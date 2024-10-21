package de.fraunhofer.iem.framework;

import boomerang.scene.CallGraph;
import crypto.rules.CrySLRule;
import de.fraunhofer.iem.scanner.AnalysisSettings;

import java.util.Collection;

public interface FrameworkSetup {

    void initializeFramework(String applicationPath, AnalysisSettings.CallGraphAlgorithm algorithm);

    CallGraph constructCallGraph(Collection<CrySLRule> rules);
}
