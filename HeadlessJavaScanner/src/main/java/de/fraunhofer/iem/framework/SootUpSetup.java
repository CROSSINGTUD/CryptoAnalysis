package de.fraunhofer.iem.framework;

import boomerang.scope.FrameworkScope;
import de.fraunhofer.iem.scanner.ScannerSettings;

public class SootUpSetup extends FrameworkSetup {

    public SootUpSetup(String applicationPath, ScannerSettings.CallGraphAlgorithm algorithm) {
        super(applicationPath, algorithm);
    }

    @Override
    public void initializeFramework() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public FrameworkScope createFrameworkScope() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
