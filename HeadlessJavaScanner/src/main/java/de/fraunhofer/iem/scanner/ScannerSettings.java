/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.scanner;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class ScannerSettings implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--appPath"},
            description = "The path to the jar file to be analyzed",
            required = true)
    private String appPath = null;

    @CommandLine.Option(
            names = {"--rulesDir"},
            description =
                    "The path to the ruleset directory. Can be a simple directory or a ZIP file. If you are"
                            + " using a ZIP file, please make sure that the path ends with '.zip'",
            required = true)
    private String rulesDir = null;

    @CommandLine.Option(
            names = {"--framework"},
            description =
                    "The framework to construct the call graph and the intermediate representation")
    private String frameworkOption = null;

    @CommandLine.Option(
            names = {"--cg"},
            description =
                    "The call graph to resolve method calls. Possible values are CHA, SPARK and SPARKLIB (default: CHA)")
    private String cg = null;

    @CommandLine.Option(
            names = {"--addClassPath"},
            description = "Add classes to the classpath")
    private String addClassPath = "";

    @CommandLine.Option(
            names = {"--reportPath"},
            description = "Path for a directory to write the reports into")
    private String reportPath = null;

    @CommandLine.Option(
            names = {"--reportFormat"},
            split = ",",
            description =
                    "The format of the report. Possible values are CMD, TXT, SARIF, CSV and CSV_SUMMARY (default: CMD)."
                            + " Multiple formats should be split with a comma (e.g. CMD,TXT,CSV)")
    private String[] reportFormat = null;

    @CommandLine.Option(
            names = {"--visualization"},
            description = "Visualize the errors (requires --reportPath to be set)")
    private boolean visualization = false;

    @CommandLine.Option(
            names = {"--sparseStrategy"},
            description =
                    "Strategy to sparsify Boomerang queries. Possible values are NONE, TYPE_BASED, and "
                            + "ALIAS_AWARE (default: NONE)")
    private String sparseStrategyInput = null;

    @CommandLine.Option(
            names = {"--ignoreSections"},
            description =
                    "Names of packages, classes and methods to be ignored during the analysis. This "
                            + "input expects path to a file containing one name per line. For example, "
                            + "'de.example.testClass' ignores the class 'testClass', 'de.example.exampleClass.exampleMethod "
                            + "ignores the method 'exampleMethod' in 'exampleClass', and 'de.example.*' ignores all classes "
                            + "and methods in the package 'example'. Using this option may increase the analysis performance. "
                            + "Note that constructors are methods that can be specified with '<init>'.")
    private String ignoreSectionsPath = null;

    @CommandLine.Option(
            names = {"--timeout"},
            description =
                    "Timeout for seeds in milliseconds. If a seed exceeds this value, CryptoAnalysis aborts the "
                            + "typestate and extract parameter analysis and continues with the results computed so far. (default: 10000)")
    private int timeout = 10000;

    public enum CallGraphAlgorithm {
        CHA,
        RTA,
        VTA,
        SPARK,
        SPARK_LIB,
    }

    public enum Framework {
        SOOT,
        SOOT_UP,
        OPAL
    }

    public enum SparseStrategy {
        NONE,
        TYPE_BASED,
        ALIAS_AWARE,
    }

    private final Multimap<Framework, CallGraphAlgorithm> supportedCGAlgorithms;

    private CallGraphAlgorithm callGraphAlgorithm;
    private Framework framework;
    private Collection<Reporter.ReportFormat> reportFormats;
    private Collection<String> ignoredSections;
    private SparseStrategy sparseStrategy;

    public ScannerSettings() {
        callGraphAlgorithm = CallGraphAlgorithm.CHA;
        reportFormats = Set.of(Reporter.ReportFormat.CMD);
        framework = Framework.SOOT;
        ignoredSections = new ArrayList<>();
        sparseStrategy = SparseStrategy.NONE;

        supportedCGAlgorithms = HashMultimap.create();
        supportedCGAlgorithms.putAll(
                Framework.SOOT,
                Set.of(
                        CallGraphAlgorithm.CHA,
                        CallGraphAlgorithm.RTA,
                        CallGraphAlgorithm.VTA,
                        CallGraphAlgorithm.SPARK,
                        CallGraphAlgorithm.SPARK_LIB));
        supportedCGAlgorithms.putAll(
                Framework.SOOT_UP, Set.of(CallGraphAlgorithm.CHA, CallGraphAlgorithm.RTA));
        supportedCGAlgorithms.putAll(
                Framework.OPAL, Set.of(CallGraphAlgorithm.CHA, CallGraphAlgorithm.RTA));
    }

    public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
        CommandLine parser = new CommandLine(this);
        parser.setOptionsCaseInsensitive(true);
        int exitCode = parser.execute(settings);

        if (frameworkOption != null) {
            framework = parseFrameworkOption(frameworkOption);
        }

        if (cg != null) {
            callGraphAlgorithm = parseCallGraphOption(cg);

            Collection<CallGraphAlgorithm> algorithms = supportedCGAlgorithms.get(framework);
            if (!algorithms.contains(callGraphAlgorithm)) {
                throw new CryptoAnalysisParserException(
                        "Framework "
                                + framework
                                + " does not support the call graph algorithm "
                                + callGraphAlgorithm);
            }
        }

        if (reportFormat != null) {
            reportFormats = parseReportFormatOption(reportFormat);
        }

        if (ignoreSectionsPath != null) {
            ignoredSections = parseIgnoredSectionOption(ignoreSectionsPath);
        }

        if (visualization && reportPath == null) {
            throw new CryptoAnalysisParserException(
                    "If visualization is enabled, the reportPath has to be set");
        }

        if (sparseStrategyInput != null) {
            sparseStrategy = parseSparseStrategyOption(sparseStrategyInput);
        }

        if (timeout < 0) {
            throw new CryptoAnalysisParserException("Timeout should not be less than 0");
        }

        if (exitCode != ExitCode.OK) {
            throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
        }
    }

    private Framework parseFrameworkOption(String option) throws CryptoAnalysisParserException {
        String frameworkValue = option.toLowerCase();

        return switch (frameworkValue) {
            case "soot" -> Framework.SOOT;
            case "sootup" -> Framework.SOOT_UP;
            case "opal" -> Framework.OPAL;
            default ->
                    throw new CryptoAnalysisParserException(
                            "Incorrect framework option: " + option);
        };
    }

    private CallGraphAlgorithm parseCallGraphOption(String option)
            throws CryptoAnalysisParserException {
        String callGraphValue = option.toLowerCase();

        return switch (callGraphValue) {
            case "cha" -> CallGraphAlgorithm.CHA;
            case "spark" -> CallGraphAlgorithm.SPARK;
            case "sparklib" -> CallGraphAlgorithm.SPARK_LIB;
            default ->
                    throw new CryptoAnalysisParserException(
                            "Incorrect call graph value: " + option);
        };
    }

    private Collection<Reporter.ReportFormat> parseReportFormatOption(String[] settings)
            throws CryptoAnalysisParserException {
        Collection<Reporter.ReportFormat> formats = new HashSet<>();

        for (String format : settings) {
            String reportFormatValue = format.toLowerCase();

            switch (reportFormatValue) {
                case "cmd":
                    formats.add(Reporter.ReportFormat.CMD);
                    break;
                case "txt":
                    formats.add(Reporter.ReportFormat.TXT);
                    break;
                case "sarif":
                    formats.add(Reporter.ReportFormat.SARIF);
                    break;
                case "csv":
                    formats.add(Reporter.ReportFormat.CSV);
                    break;
                case "csv_summary":
                    formats.add(Reporter.ReportFormat.CSV_SUMMARY);
                    break;
                case "github_annotation":
                    formats.add(Reporter.ReportFormat.GITHUB_ANNOTATION);
                    break;
                default:
                    throw new CryptoAnalysisParserException(
                            "Incorrect report format value: " + format);
            }
        }

        return formats;
    }

    private Collection<String> parseIgnoredSectionOption(String path)
            throws CryptoAnalysisParserException {
        Collection<String> result = new ArrayList<>();
        File ignorePackageFile = new File(path);

        if (ignorePackageFile.isFile() && ignorePackageFile.canRead()) {
            try {
                List<String> lines = Files.readLines(ignorePackageFile, Charset.defaultCharset());
                result.addAll(lines);
            } catch (IOException e) {
                throw new CryptoAnalysisParserException(
                        "Error while reading file " + ignorePackageFile + ": " + e.getMessage());
            }
        } else {
            throw new CryptoAnalysisParserException(
                    ignorePackageFile + " is not a file or cannot be read");
        }

        return result;
    }

    private SparseStrategy parseSparseStrategyOption(String option) {
        String strategyLowerCase = option.toLowerCase();

        return switch (strategyLowerCase) {
            case "none" -> SparseStrategy.NONE;
            case "type_based" -> SparseStrategy.TYPE_BASED;
            case "alias_aware" -> SparseStrategy.ALIAS_AWARE;
            default ->
                    throw new CryptoAnalysisParserException(
                            "Incorrect sparsification strategy: " + option);
        };
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

    public String getAddClassPath() {
        return addClassPath;
    }

    public void setAddClassPath(String addClassPath) {
        this.addClassPath = addClassPath;
    }

    public String getReportDirectory() {
        return reportPath;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportPath = reportDirectory;
    }

    public Collection<Reporter.ReportFormat> getReportFormats() {
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
