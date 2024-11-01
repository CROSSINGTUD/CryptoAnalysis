package de.fraunhofer.iem.framework;

import boomerang.scene.CallGraph;
import crypto.rules.CrySLRule;
import de.fraunhofer.iem.scanner.ScannerSettings;

import java.util.Collection;

public class OpalSetup extends FrameworkSetup {

    public OpalSetup(String applicationPath, ScannerSettings.CallGraphAlgorithm algorithm) {
        super(applicationPath, algorithm);
    }

    @Override
    public void initializeFramework() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        throw new RuntimeException("Not implemented yet");
    }
}
