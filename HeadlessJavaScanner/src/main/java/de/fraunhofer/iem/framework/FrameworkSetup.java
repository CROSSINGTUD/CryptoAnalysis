package de.fraunhofer.iem.framework;

import boomerang.scene.CallGraph;
import crypto.rules.CrySLRule;
import de.fraunhofer.iem.scanner.ScannerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class FrameworkSetup {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FrameworkSetup.class);

    protected final String applicationPath;
    protected final ScannerSettings.CallGraphAlgorithm callGraphAlgorithm;

    protected FrameworkSetup(String applicationPath, ScannerSettings.CallGraphAlgorithm callGraphAlgorithm) {
        this.applicationPath = applicationPath;
        this.callGraphAlgorithm = callGraphAlgorithm;
    }

    public abstract void initializeFramework();

    public abstract CallGraph constructCallGraph(Collection<CrySLRule> rules);
}
