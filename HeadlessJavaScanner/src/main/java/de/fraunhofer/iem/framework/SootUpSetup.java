package de.fraunhofer.iem.framework;

import boomerang.scene.CallGraph;
import crysl.rule.CrySLRule;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.util.Collection;

public class SootUpSetup extends FrameworkSetup {

    public SootUpSetup(String applicationPath, ScannerSettings.CallGraphAlgorithm algorithm) {
        super(applicationPath, algorithm);
    }

    @Override
    public void initializeFramework() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
