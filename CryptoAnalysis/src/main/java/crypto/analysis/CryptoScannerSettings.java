package crypto.analysis;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class CryptoScannerSettings implements Callable<Integer> {

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
	private boolean providerdetection = false;

	@CommandLine.Option(
			names = {"--dstats"},
			description = "Disable the output of analysis statistics in the reports")
	private boolean includeStatistics = true;

	@CommandLine.Option(
			names = {"--forbiddenPredicates"},
			description = "Facilitates the specification of forbidden predicates. Any "
					+ "occurrence will be flagged by the analysis. This input expects a "
					+ "path to a file containing one predicate per line")
	private String forbiddenPredicatesPath = null;

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

	private ControlGraph controlGraph;
	private RulesetPathType rulesetPathType;
	private Set<ReportFormat> reportFormats;
	private Collection<String> forbiddenPredicates;
	private Collection<String> ignoredSections;

	public enum ControlGraph {
		CHA, SPARK, SPARKLIB,
	}

	public enum ReportFormat {
		CMD, TXT, SARIF, CSV, CSV_SUMMARY, GITHUB_ANNOTATION
	}

	public enum RulesetPathType {
		DIR, ZIP, NONE
	}
	
	public CryptoScannerSettings() {
		controlGraph = ControlGraph.CHA;
		rulesetPathType = RulesetPathType.NONE;
		reportFormats = new HashSet<>(Arrays.asList(ReportFormat.CMD));
		forbiddenPredicates = new ArrayList<>();
		ignoredSections = new ArrayList<>();
	}

	public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
		CommandLine parser = new CommandLine(this);
		parser.setOptionsCaseInsensitive(true);
		int exitCode = parser.execute(settings);
		
		if (this.isZipFile(rulesDir)) {
			this.rulesetPathType = RulesetPathType.ZIP;
		} else {
			this.rulesetPathType = RulesetPathType.DIR;
		}

		if (cg != null) {
			parseControlGraphValue(cg);
		}

		if (reportFormat != null) {
			parseReportFormatValues(reportFormat);
		}

		if (forbiddenPredicatesPath != null) {
			parseForbiddenPredicates(forbiddenPredicatesPath);
		}

		if (ignoreSectionsPath != null) {
			parseIgnoredSections(ignoreSectionsPath);
		}

		if (exitCode != ExitCode.OK) {
			throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
		}
	}

	public ControlGraph getControlGraph() {
		return controlGraph;
	}

	public RulesetPathType getRulesetPathType() {
		return rulesetPathType;
	}

	public String getRulesetPathDir() {
		return rulesDir;
	}

	public String getSootPath() {
		return sootPath;
	}

	public String getApplicationPath() {
		return appPath;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getReportDirectory() {
		return reportPath;
	}
	
	public Set<ReportFormat> getReportFormats() {
		return reportFormats;
	}

	public boolean isPreAnalysis() {
		return preanalysis;
	}

	public boolean isVisualization() {
		return visualization;
	}

	public boolean isProviderDetectionAnalysis() {
		return providerdetection;
	}
	
	public boolean isIncludeStatistics() {
		return includeStatistics;
	}

	public Collection<String> getForbiddenPredicates() {
		return forbiddenPredicates;
	}

	public Collection<String> getIgnoredSections() {
		return ignoredSections;
	}
	
	private void parseControlGraphValue(String value) throws CryptoAnalysisParserException {
		String CGValue = value.toLowerCase();
		
		switch(CGValue) {
			case "cha":
				controlGraph = ControlGraph.CHA;
				break;
			case "spark":
				controlGraph = ControlGraph.SPARK;
				break;
			case "sparklib":
				controlGraph = ControlGraph.SPARKLIB;
				break;
			default:
				throw new CryptoAnalysisParserException("Incorrect value " + CGValue + " for --cg option. "
						+ "Available options are: CHA, SPARK and SPARKLIB.\n");
		}
	}
	
	private void parseReportFormatValues(String[] settings) throws CryptoAnalysisParserException {
		// reset the report formats
		this.reportFormats = new HashSet<>();
		
		for (String format : settings) {
			String reportFormatValue = format.toLowerCase();
			
			switch (reportFormatValue) {
				case "cmd":
					reportFormats.add(ReportFormat.CMD);
					break;
				case "txt":
					reportFormats.add(ReportFormat.TXT);
					break;
				case "sarif":
					reportFormats.add(ReportFormat.SARIF);
					break;
				case "csv":
					reportFormats.add(ReportFormat.CSV);
					break;
				case "csv_summary":
					reportFormats.add(ReportFormat.CSV_SUMMARY);
					break;
				case "github_annotation":
					reportFormats.add(ReportFormat.GITHUB_ANNOTATION);
					break;
				default:
					throw new CryptoAnalysisParserException("Incorrect value " + reportFormatValue + " for --reportFormat option. "
							+ "Available options are: CMD, TXT, SARIF, CSV and CSV_SUMMARY.\n");
			}
		}
	}

	private boolean isZipFile(String path) throws CryptoAnalysisParserException {
		File file = new File(path);

		// Copied from https://stackoverflow.com/questions/33934178/how-to-identify-a-zip-file-in-java
		int fileSignature = 0;

		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			fileSignature = raf.readInt();
		} catch (IOException e) {
			return false;
		}
		return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
	}

	private void parseForbiddenPredicates(String path) throws CryptoAnalysisParserException {
		File forbPredFile = new File(path);

		// Reset forbiddenPredicates
		forbiddenPredicates = new ArrayList<>();

		if (forbPredFile.isFile() && forbPredFile.canRead()) {
			try {
				List<String> lines = Files.readLines(forbPredFile, Charset.defaultCharset());
				forbiddenPredicates.addAll(lines);
			} catch (IOException e) {
				throw new CryptoAnalysisParserException("Error while reading file " + forbPredFile + ": " + e.getMessage());
			}
		} else {
			throw new CryptoAnalysisParserException(forbPredFile + " is not a file or cannot be read");
		}
	}

	private void parseIgnoredSections(String path) throws CryptoAnalysisParserException {
		final File ignorePackageFile = new File(path);

		// Reset ignoredPackages
		ignoredSections = new ArrayList<>();

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

	@Override
	public Integer call() throws Exception {
		return 0;
	}
	
}
