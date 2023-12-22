package crypto.analysis;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import crypto.exceptions.CryptoAnalysisParserException;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class CryptoScannerSettings implements Callable<Integer> {

	@CommandLine.Option(
			names = {"--appPath"},
			description = "The path to the jar file to be analyzed",
			required = true)
	private String appPath;
	
	@CommandLine.Option(
			names = {"--rulesDir"},
			description = "The path to the ruleset directory. Can be a simple directory or a ZIP file. If you are"
					+ "using a ZIP file, please make sure that the path ends with '.zip'",
			required = true)
	private String rulesDir;
	
	@CommandLine.Option(
			names = {"--cg"},
			description = "The call graph to resolve method calls. Possible values are CHA, SPARK and SPARKLIB (default: CHA)")
	private String cg;
	
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
	private String reportPath;
	
	@CommandLine.Option(
			names = {"--reportFormat"},
			split = ",",
			description = "The format of the report. Possible values are CMD, TXT, SARIF, CSV and CSV_SUMMARY (default: CMD)."
					+ " Multiple formats should be split with a comma (e.g. CMD,TXT,CSV)")
	private String[] reportFormat;
	
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
	
	private ControlGraph controlGraph;
	private RulesetPathType rulesetPathType;
	private Set<ReportFormat> reportFormats;

	public enum ControlGraph {
		CHA, SPARK, SPARKLIB,
	}

	public enum ReportFormat {
		CMD, TXT, SARIF, CSV, CSV_SUMMARY
	}

	public enum RulesetPathType {
		DIR, ZIP, NONE
	}
	
	public CryptoScannerSettings() {
		controlGraph = ControlGraph.CHA;
		rulesetPathType = RulesetPathType.NONE;
		reportFormats = new HashSet<>(List.of(ReportFormat.CMD));
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
		
		if (exitCode != ExitCode.OK) {
			throw new CryptoAnalysisParserException("Error while parsing the CLI arugments");
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

	@Override
	public Integer call() throws Exception {
		return 0;
	}
	
}
