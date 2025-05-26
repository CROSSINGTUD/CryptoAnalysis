/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.android;

import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class AndroidSettings implements Callable<Integer> {

    @CommandLine.Option(
            names = {"--apkFile"},
            description = {"The absolute path to the .apk file"},
            required = true)
    private String apkFile = null;

    @CommandLine.Option(
            names = {"--platformDirectory"},
            description = "The absolute path to the android SDK platforms",
            required = true)
    private String platformDirectory = null;

    @CommandLine.Option(
            names = {"--rulesDir"},
            description = {
                "The path to ruleset directory. Can be a simple directory or a ZIP archive"
            },
            required = true)
    private String rulesetDirectory = null;

    @CommandLine.Option(
            names = {"--cg"},
            description = {"The call graph algorithm"})
    private String cgAlgorithm = null;

    @CommandLine.Option(
            names = {"--reportPath"},
            description = "Path to a directory where the reports are stored")
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

    public enum CallGraphAlgorithm {
        CHA,
        RTA,
        VTA,
        SPARK
    }

    private CallGraphAlgorithm callGraphAlgorithm;
    private Collection<Reporter.ReportFormat> reportFormats;

    public AndroidSettings() {
        callGraphAlgorithm = CallGraphAlgorithm.CHA;
        reportFormats = Set.of(Reporter.ReportFormat.CMD);
    }

    public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
        CommandLine parser = new CommandLine(this);
        parser.setOptionsCaseInsensitive(true);
        int exitCode = parser.execute(settings);

        if (cgAlgorithm != null) {
            callGraphAlgorithm = parseCallGraphAlgorithm(cgAlgorithm);
        }

        if (reportFormat != null) {
            reportFormats = parseReportFormatValues(reportFormat);
        }

        if (visualization && reportPath == null) {
            throw new CryptoAnalysisParserException(
                    "If visualization is enabled, the reportPath has to be set");
        }

        if (exitCode != CommandLine.ExitCode.OK) {
            throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
        }
    }

    private CallGraphAlgorithm parseCallGraphAlgorithm(String algorithm) {
        return switch (algorithm.toLowerCase()) {
            case "cha" -> CallGraphAlgorithm.CHA;
            case "rta" -> CallGraphAlgorithm.RTA;
            case "vta" -> CallGraphAlgorithm.VTA;
            case "spark" -> CallGraphAlgorithm.SPARK;
            default ->
                    throw new CryptoAnalysisParserException(
                            "Invalid call graph algorithm. Possible values are {CHA, RTA, VTA, SPARK");
        };
    }

    private Collection<Reporter.ReportFormat> parseReportFormatValues(String[] settings) {
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
                            "Incorrect value "
                                    + reportFormatValue
                                    + " for --reportFormat option. "
                                    + "Available options are: CMD, TXT, SARIF, CSV and CSV_SUMMARY.\n");
            }
        }

        return formats;
    }

    public String getApkFile() {
        return apkFile;
    }

    public void setApkFile(String apkFile) {
        this.apkFile = apkFile;
    }

    public String getPlatformDirectory() {
        return platformDirectory;
    }

    public void setPlatformDirectory(String platformDirectory) {
        this.platformDirectory = platformDirectory;
    }

    public String getRulesetDirectory() {
        return rulesetDirectory;
    }

    public void setRulesetDirectory(String rulesetDirectory) {
        this.rulesetDirectory = rulesetDirectory;
    }

    public CallGraphAlgorithm getCallGraphAlgorithm() {
        return callGraphAlgorithm;
    }

    public void setCallGraphAlgorithm(CallGraphAlgorithm algorithm) {
        this.callGraphAlgorithm = algorithm;
    }

    public Collection<Reporter.ReportFormat> getReportFormats() {
        return reportFormats;
    }

    public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
        this.reportFormats = new HashSet<>(reportFormats);
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public boolean isVisualization() {
        return visualization;
    }

    public void setVisualization(boolean visualization) {
        this.visualization = visualization;
    }

    @Override
    public Integer call() throws Exception {
        return 0;
    }
}
