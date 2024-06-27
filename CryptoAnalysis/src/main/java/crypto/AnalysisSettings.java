package crypto;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.io.Files;
import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class AnalysisSettings implements Callable<Integer> {

	@CommandLine.Option(
			names = {"--appPath"},
			description = "The path to the jar file to be analyzed",
			required = true)
	private String appPath = null;
	
	@CommandLine.Option(
			names = {"--rulesDir"},
			description = "The path to the ruleset directory. Can be a simple directory or a ZIP file. If you are"
					+ "using a ZIP file, please make sure that the path ends with '.zip'",
			required = true)
	private String rulesDir = null;

	@CommandLine.Option(
			names = {"--cg"},
			description = "The call graph to resolve method calls. Possible values are CHA, SPARK and SPARKLIB (default: CHA)")
	private String cg = null;

	@CommandLine.Option(
			names = {"--sootPath"},
			description = "The absolute path of the whole project")
	private String sootPath = "";

	@CommandLine.Option(
			names = {"--identifier"},
			description = "An identifier for the analysis to label output files")
	private String identifier = "";

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
			names = {"--preanalysis"},
			description = "Enable a preanalysis")
	private boolean preanalysis = false;

	@CommandLine.Option(
			names = {"--visualization"},
			description = "Enable visualization")
	private boolean visualization = false;

	@CommandLine.Option(
			names = {"--providerdetection"},
			description = "Enable provider detection")
	private boolean providerDetection = false;

	@CommandLine.Option(
			names = {"--dstats"},
			description = "Disable the output of analysis statistics in the reports")
	private boolean includeStatistics = true;

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

	public enum AnalysisCallGraph {
		CHA, SPARK, SPARK_LIB,
	}

	private AnalysisCallGraph analysisCallGraph;
	private Set<Reporter.ReportFormat> reportFormats;
	private Collection<String> ignoredSections;
	
	public AnalysisSettings() {
		analysisCallGraph = AnalysisCallGraph.CHA;
		reportFormats = new HashSet<>(Arrays.asList(Reporter.ReportFormat.CMD));
		ignoredSections = new ArrayList<>();
	}

	public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
		CommandLine parser = new CommandLine(this);
		parser.setOptionsCaseInsensitive(true);
		int exitCode = parser.execute(settings);

		if (cg != null) {
			parseControlGraphValue(cg);
		}

		if (reportFormat != null) {
			parseReportFormatValues(reportFormat);
		}

		if (ignoreSectionsPath != null) {
			parseIgnoredSections(ignoreSectionsPath);
		}

		if (exitCode != ExitCode.OK) {
			throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
		}
	}



	private void parseControlGraphValue(String value) throws CryptoAnalysisParserException {
		String CGValue = value.toLowerCase();

		switch(CGValue) {
			case "cha":
				analysisCallGraph = AnalysisCallGraph.CHA;
				break;
			case "spark":
				analysisCallGraph = AnalysisCallGraph.SPARK;
				break;
			case "sparklib":
				analysisCallGraph = AnalysisCallGraph.SPARK_LIB;
				break;
			default:
				throw new CryptoAnalysisParserException("Incorrect value " + CGValue + " for --cg option. "
						+ "Available options are: CHA, SPARK and SPARKLIB.\n");
		}
	}


	private void parseReportFormatValues(String[] settings) throws CryptoAnalysisParserException {
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

	public AnalysisCallGraph getCallGraph() {
		return analysisCallGraph;
	}

	public void setCallGraph(AnalysisCallGraph analysisCallGraph) {
		this.analysisCallGraph = analysisCallGraph;
	}

	public String getSootPath() {
		return sootPath;
	}

	public void setSootPath(String sootPath) {
		this.sootPath = sootPath;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public boolean isPreAnalysis() {
		return preanalysis;
	}

	public void setPreAnalysis(boolean preAnalysis) {
		this.preanalysis = preAnalysis;
	}

	public boolean isVisualization() {
		return visualization;
	}

	public void setVisualization(boolean visualization) {
		this.visualization = visualization;
	}

	public boolean isProviderDetectionAnalysis() {
		return providerDetection;
	}

	public void setProviderDetection(boolean providerDetection) {
		this.providerDetection = providerDetection;
	}
	
	public boolean isIncludeStatistics() {
		return includeStatistics;
	}

	public void setIncludeStatistics(boolean includeStatistics) {
		this.includeStatistics = includeStatistics;
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
