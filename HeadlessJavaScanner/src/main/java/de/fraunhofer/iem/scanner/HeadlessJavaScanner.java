package de.fraunhofer.iem.scanner;

import boomerang.Query;
import boomerang.debugger.Debugger;
import boomerang.debugger.IDEVizDebugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisException;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import crypto.reporting.ReporterFactory;
import crypto.rules.CrySLRule;
import de.fraunhofer.iem.framework.FrameworkSetup;
import de.fraunhofer.iem.framework.OpalSetup;
import de.fraunhofer.iem.framework.SootSetup;
import de.fraunhofer.iem.framework.SootUpSetup;
import de.fraunhofer.iem.scanner.ScannerSettings.CallGraphAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typestate.TransitionFunction;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HeadlessJavaScanner extends CryptoScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessJavaScanner.class);

    private final ScannerSettings settings;
    private FrameworkSetup frameworkSetup;

    public HeadlessJavaScanner(String applicationPath, String rulesetDirectory) {
        settings = new ScannerSettings();

        settings.setApplicationPath(applicationPath);
        settings.setRulesetPath(rulesetDirectory);
        settings.setReportFormats(new HashSet<>());
    }

    private HeadlessJavaScanner(ScannerSettings settings) {
        this.settings = settings;
    }

    public static HeadlessJavaScanner createFromCLISettings(String[] args) throws CryptoAnalysisParserException {
        ScannerSettings scannerSettings = new ScannerSettings();
        scannerSettings.parseSettingsFromCLI(args);

        return new HeadlessJavaScanner(scannerSettings);
    }

    @Override
    public String getRulesetPath() {
		return settings.getRulesetPath();
    }

    @Override
    protected CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        return frameworkSetup.constructCallGraph(rules);
    }

    @Override
    public DataFlowScope createDataFlowScope(Collection<CrySLRule> ruleset) {
        return new CryptoAnalysisDataFlowScope(ruleset, getIgnoredSections());
    }

    @Override
    public Debugger<TransitionFunction> debugger(Query query) {
        if (settings.isVisualization()) {

            if (settings.getReportDirectory() == null) {
                LOGGER.error("The visualization requires the --reportPath option. Disabling visualization...");
                return new Debugger<>();
            }

            File vizFile = new File(settings.getReportDirectory() + File.separator + "viz" + File.separator + query.var().getVariableName() + ".json");
            boolean created = vizFile.getParentFile().mkdirs();

            if (!created) {
                LOGGER.error("Could not create directory {}. Disabling visualization...", vizFile.getAbsolutePath());
                return new Debugger<>();
            }

            return new IDEVizDebugger<>(vizFile);
        }

        return new Debugger<>();
    }

    @Override
    public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
        switch (settings.getSparseStrategy()) {
            case NONE:
                return SparseCFGCache.SparsificationStrategy.NONE;
            case TYPE_BASED:
                return SparseCFGCache.SparsificationStrategy.TYPE_BASED;
            case ALIAS_AWARE:
                return SparseCFGCache.SparsificationStrategy.ALIAS_AWARE;
            default:
                LOGGER.error("Could not set sparsification strategy {}. Defaulting to NONE...", settings.getSparseStrategy());
                return SparseCFGCache.SparsificationStrategy.NONE;
        }
    }

    @Override
    public int getTimeout() {
        return settings.getTimeout();
    }

    public void run() {
        // Setup Framework
        frameworkSetup = setupFramework();
        frameworkSetup.initializeFramework();
        additionalFrameworkSetup();

        // Initialize fields and reporters
        super.initialize();
        Collection<Reporter> reporters = ReporterFactory.createReporters(getReportFormats(), getReportDirectory(), super.getRuleset());

        // Run the analysis
        super.scan();

        // Report the errors
        for (Reporter reporter : reporters) {
            reporter.createAnalysisReport(super.getDiscoveredSeeds(), super.getCollectedErrors(), super.getStatistics());
        }
    }

    private FrameworkSetup setupFramework() {
        switch (settings.getFramework()) {
            case SOOT:
                return new SootSetup(settings.getApplicationPath(), settings.getCallGraph(), settings.getSootPath());
            case SOOT_UP:
                return new SootUpSetup(settings.getApplicationPath(), settings.getCallGraph());
            case OPAL:
                return new OpalSetup(settings.getApplicationPath(), settings.getCallGraph());
            default:
                throw new CryptoAnalysisException("Framework " + settings.getFramework().name() + " is not supported");
        }
    }

    public String getApplicationPath() {
        return settings.getApplicationPath();
    }

    public ScannerSettings.Framework getFramework() {
        return settings.getFramework();
    }

    public void setFramework(ScannerSettings.Framework framework) {
        settings.setFramework(framework);
    }

    public CallGraphAlgorithm getCallGraphAlgorithm() {
        return settings.getCallGraph();
    }

    public void setCallGraphAlgorithm(CallGraphAlgorithm callGraphAlgorithm) {
        settings.setCallGraph(callGraphAlgorithm);
    }

    public String getSootClassPath() {
        return settings.getSootPath();
    }

    public void setSootClassPath(String sootClassPath) {
        settings.setSootPath(sootClassPath);
    }

    public String getReportDirectory() {
        return settings.getReportDirectory();
    }

    public void setReportDirectory(String reportDirectory) {
        settings.setReportDirectory(reportDirectory);
    }

    public Set<Reporter.ReportFormat> getReportFormats() {
        return settings.getReportFormats();
    }

    public void setReportFormats(Reporter.ReportFormat... formats) {
        setReportFormats(Arrays.asList(formats));
    }

    public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
        settings.setReportFormats(reportFormats);
    }

    public boolean isVisualization() {
        return settings.isVisualization();
    }

    public void setVisualization(boolean visualization) {
        settings.setVisualization(visualization);
    }

    public Collection<String> getIgnoredSections() {
        return settings.getIgnoredSections();
    }

    public void setIgnoredSections(Collection<String> ignoredSections) {
        settings.setIgnoredSections(ignoredSections);
    }

    public ScannerSettings.SparseStrategy getSparseStrategy() {
        return settings.getSparseStrategy();
    }

    public void setSparseStrategy(ScannerSettings.SparseStrategy strategy) {
        settings.setSparseStrategy(strategy);
    }

    public void setTimeout(int timeout) {
        settings.setTimeout(timeout);
    }

    public void additionalFrameworkSetup() {}
}
