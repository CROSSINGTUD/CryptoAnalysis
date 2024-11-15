package de.fraunhofer.iem.scanner;

import com.google.common.io.Files;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class ScannerSettings implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--appPath"},
            description = "The path to the jar file to be analyzed",
            required = true)
    private String appPath = null;

    @CommandLine.Option(
            names = {"--rulesDir"},
            description = "The path to the ruleset directory. Can be a simple directory or a ZIP file. If you are"
                    + " using a ZIP file, please make sure that the path ends with '.zip'",
            required = true)
    private String rulesDir = null;

    @CommandLine.Option(
            names = {"--framework"},
            description = "The framework to construct the call graph and the intermediate representation"
    )
    private String frameworkOption = null;

    @CommandLine.Option(
            names = {"--cg"},
            description = "The call graph to resolve method calls. Possible values are CHA, SPARK and SPARKLIB (default: CHA)")
    private String cg = null;

    @CommandLine.Option(
            names = {"--sootPath"},
            description = "The absolute path of the whole project")
    private String sootPath = "";

    @CommandLine.Option(
            names = {"--reportPath"},
            description = "Path for a directory to write the reports into")
    private String reportPath = null;

    @CommandLine.Option(
            names = {"--reportFormat"},
            split = ",",
            description = "The format of the report. Possible values are CMD, TXT, SARIF, CSV and CSV_SUMMARY (default: CMD)."
                    + " Multiple formats should be split with a comma (e.g. CMD,TXT,CSV)")
    private String[] reportFormat = null;

    @CommandLine.Option(
            names = {"--visualization"},
            description = "Enable visualization")
    private boolean visualization = false;

    @CommandLine.Option(
            names = {"--sparseStrategy"},
            description = "Strategy to sparsify Boomerang queries. Possible values are NONE, TYPE_BASED, and " +
                    "ALIAS_AWARE (default: NONE)"
    )
    private String sparseStrategyInput = null;

	@CommandLine.Option(
			names = {"--ignoreSections"},
			description = "Names of packages, classes and methods to be ignored during the analysis. This "
					+ "input expects path to a file containing one name per line. For example, "
					+ "'de.example.testClass' ignores the class 'testClass', 'de.example.exampleClass.exampleMethod "
					+ "ignores the method 'exampleMethod' in 'exampleClass', and 'de.example.*' ignores all classes "
					+ "and methods in the package 'example'. Using this option may increase the analysis performance. "
					+ "Note that constructors are methods that can be specified with '<init>'."
	)
	private String ignoreSectionsPath = null;

    @CommandLine.Option(
            names = {"--timeout"},
            description = "Timeout for seeds in milliseconds. If a seed exceeds this value, CryptoAnalysis aborts the " +
                    "typestate and extract parameter analysis and continues with the results computed so far. (default: 10000)"
    )
    private int timeout = 10000;

    public enum CallGraphAlgorithm {
        CHA, SPARK, SPARK_LIB,
    }

    public enum Framework {
        SOOT, SOOT_UP, OPAL
    }

    public enum SparseStrategy {
        NONE, TYPE_BASED, ALIAS_AWARE,
    }

    private CallGraphAlgorithm callGraphAlgorithm;
    private Framework framework;
    private Set<Reporter.ReportFormat> reportFormats;
    private Collection<String> ignoredSections;
    private SparseStrategy sparseStrategy;

    public ScannerSettings() {
        callGraphAlgorithm = CallGraphAlgorithm.CHA;
        reportFormats = new HashSet<>(List.of(Reporter.ReportFormat.CMD));
        framework = Framework.SOOT;
        ignoredSections = new ArrayList<>();
        sparseStrategy = SparseStrategy.NONE;
    }

    public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
        CommandLine parser = new CommandLine(this);
        parser.setOptionsCaseInsensitive(true);
        int exitCode = parser.execute(settings);

        if (frameworkOption != null) {
            parseFrameworkOption(frameworkOption);
        }

        if (cg != null) {
            parseControlGraphOption(cg);
        }

        if (reportFormat != null) {
            parseReportFormatValues(reportFormat);
        }

        if (ignoreSectionsPath != null) {
            parseIgnoredSections(ignoreSectionsPath);
        }

        if (sparseStrategyInput != null) {
            parseSparseStrategy(sparseStrategyInput);
        }

        if (timeout < 0) {
            throw new CryptoAnalysisParserException("Timeout should not be less than 0");
        }

        if (exitCode != ExitCode.OK) {
            throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
        }
    }

    private void parseFrameworkOption(String option) throws CryptoAnalysisParserException {
        String frameworkValue = option.toLowerCase();

        switch (frameworkValue) {
            case "soot":
                framework = Framework.SOOT;
                break;
            case "soot_up":
                framework = Framework.SOOT_UP;
                break;
            case "opal":
                framework = Framework.OPAL;
                break;
            default:
                throw new CryptoAnalysisParserException("Framework " + option + " is not supported");
        }
    }

    private void parseControlGraphOption(String value) throws CryptoAnalysisParserException {
        String CGValue = value.toLowerCase();

        switch (CGValue) {
            case "cha":
                callGraphAlgorithm = CallGraphAlgorithm.CHA;
                break;
            case "spark":
                callGraphAlgorithm = CallGraphAlgorithm.SPARK;
                break;
            case "sparklib":
                callGraphAlgorithm = CallGraphAlgorithm.SPARK_LIB;
                break;
            default:
                throw new CryptoAnalysisParserException("Incorrect value " + CGValue + " for --cg option. "
                        + "Available options are: CHA, SPARK and SPARKLIB.\n");
        }
    }


    private void parseReportFormatValues(String[] settings) throws CryptoAnalysisParserException {
        reportFormats.clear();

        for (String format : settings) {
            String reportFormatValue = format.toLowerCase();

            switch (reportFormatValue) {
                case "cmd":
                    reportFormats.add(Reporter.ReportFormat.CMD);
                    break;
                case "txt":
                    reportFormats.add(Reporter.ReportFormat.TXT);
                    break;
                case "sarif":
                    reportFormats.add(Reporter.ReportFormat.SARIF);
                    break;
                case "csv":
                    reportFormats.add(Reporter.ReportFormat.CSV);
                    break;
                case "csv_summary":
                    reportFormats.add(Reporter.ReportFormat.CSV_SUMMARY);
                    break;
                case "github_annotation":
                    reportFormats.add(Reporter.ReportFormat.GITHUB_ANNOTATION);
                    break;
                default:
                    throw new CryptoAnalysisParserException("Incorrect value " + reportFormatValue + " for --reportFormat option. "
                            + "Available options are: CMD, TXT, SARIF, CSV and CSV_SUMMARY.\n");
            }
        }
    }

    private void parseIgnoredSections(String path) throws CryptoAnalysisParserException {
        final File ignorePackageFile = new File(path);

        if (ignorePackageFile.isFile() && ignorePackageFile.canRead()) {
            try {
                List<String> lines = Files.readLines(ignorePackageFile, Charset.defaultCharset());
                ignoredSections.addAll(lines);
            } catch (IOException e) {
                throw new CryptoAnalysisParserException("Error while reading file " + ignorePackageFile + ": " + e.getMessage());
            }
        } else {
            throw new CryptoAnalysisParserException(ignorePackageFile + " is not a file or cannot be read");
        }
    }

    private void parseSparseStrategy(String strategy) {
        String strategyLowerCase = strategy.toLowerCase();

        switch (strategyLowerCase) {
            case "none":
                sparseStrategy = SparseStrategy.NONE;
                break;
            case "type_based":
                sparseStrategy = SparseStrategy.TYPE_BASED;
                break;
            case "alias_aware":
                sparseStrategy = SparseStrategy.ALIAS_AWARE;
                break;
            default:
                throw new CryptoAnalysisParserException(sparseStrategy + " is not a valid sparsification strategy");
        }
    }

    public String getApplicationPath() {
        return appPath;
    }

    public void setApplicationPath(String applicationPath) {
        this.appPath = applicationPath;
    }

    public String getRulesetPath() {
        return rulesDir;
    }

    public void setRulesetPath(String rulesetPath) {
        this.rulesDir = rulesetPath;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public CallGraphAlgorithm getCallGraph() {
        return callGraphAlgorithm;
    }

    public void setCallGraph(CallGraphAlgorithm callGraphAlgorithm) {
        this.callGraphAlgorithm = callGraphAlgorithm;
    }

    public String getSootPath() {
        return sootPath;
    }

    public void setSootPath(String sootPath) {
        this.sootPath = sootPath;
    }

	public String getReportDirectory() {
        return reportPath;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportPath = reportDirectory;
    }

    public Set<Reporter.ReportFormat> getReportFormats() {
        return reportFormats;
    }

    public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
        this.reportFormats = new HashSet<>(reportFormats);
    }

    public boolean isVisualization() {
        return visualization;
    }

    public void setVisualization(boolean visualization) {
        this.visualization = visualization;
    }

    public SparseStrategy getSparseStrategy() {
        return sparseStrategy;
    }

    public void setSparseStrategy(SparseStrategy strategy) {
        this.sparseStrategy = strategy;
    }

	public Collection<String> getIgnoredSections() {
		return ignoredSections;
	}

	public void setIgnoredSections(Collection<String> ignoredSections) {
		this.ignoredSections = new HashSet<>(ignoredSections);
	}

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
