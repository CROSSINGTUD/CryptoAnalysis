package de.fraunhofer.iem.scanner;

import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.sparse.SparseCFGCache;
import crypto.analysis.CryptoAnalysisDataFlowScope;
import crypto.analysis.CryptoScanner;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import de.fraunhofer.iem.framework.FrameworkSetup;
import de.fraunhofer.iem.framework.OpalSetup;
import de.fraunhofer.iem.framework.SootSetup;
import de.fraunhofer.iem.framework.SootUpSetup;
import de.fraunhofer.iem.scanner.ScannerSettings.CallGraphAlgorithm;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static HeadlessJavaScanner createFromCLISettings(String[] args)
            throws CryptoAnalysisParserException {
        ScannerSettings scannerSettings = new ScannerSettings();
        scannerSettings.parseSettingsFromCLI(args);

        return new HeadlessJavaScanner(scannerSettings);
    }

    @Override
    public String getRulesetPath() {
        return settings.getRulesetPath();
    }

    @Override
    protected CallGraph constructCallGraph() {
        return frameworkSetup.constructCallGraph(super.getRuleset());
    }

    @Override
    public DataFlowScope createDataFlowScope() {
        return new CryptoAnalysisDataFlowScope(super.getRuleset(), settings.getIgnoredSections());
    }

    @Override
    public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
        return switch (settings.getSparseStrategy()) {
            case NONE -> SparseCFGCache.SparsificationStrategy.NONE;
            case TYPE_BASED -> SparseCFGCache.SparsificationStrategy.TYPE_BASED;
            case ALIAS_AWARE -> SparseCFGCache.SparsificationStrategy.ALIAS_AWARE;
        };
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

        // Initialize fields
        super.initialize();

        // Run the analysis
        super.scan();

        // Report the errors
        super.createReports(getReportFormats(), getReportDirectory(), isVisualization());
    }

    private FrameworkSetup setupFramework() {
        return switch (settings.getFramework()) {
            case SOOT ->
                    new SootSetup(
                            settings.getApplicationPath(),
                            settings.getCallGraph(),
                            settings.getSootPath());
            case SOOT_UP -> new SootUpSetup(settings.getApplicationPath(), settings.getCallGraph());
            case OPAL -> new OpalSetup(settings.getApplicationPath(), settings.getCallGraph());
        };
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

    public Collection<Reporter.ReportFormat> getReportFormats() {
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
