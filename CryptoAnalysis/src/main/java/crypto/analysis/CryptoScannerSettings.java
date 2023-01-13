package crypto.analysis;

import java.util.HashSet;
import java.util.Set;

import crypto.exceptions.CryptoAnalysisParserException;

public class CryptoScannerSettings {
	
	private ControlGraph controlGraph = null;
	private RulesetPathType rulesetPathType = null;
	private String rulesetPathDir = null;
	private String rulesetPathZip = null;
	private String sootPath = "";
	private String applicationPath = null;
	private String softwareIdentifier = "";
	private String reportDirectory = null;
	private Set<ReportFormat> reportFormats;
	private boolean preAnalysis;
	private boolean visualization;
	private boolean providerDetectionAnalysis;
	private boolean includeStatistics;
	
	public CryptoScannerSettings() {
		setControlGraph(ControlGraph.CHA);
		setRulesetPathType(RulesetPathType.NONE);
		setPreAnalysis(false);
		setVisualization(false);
		setProviderDetectionAnalysis(false);
		setIncludeStatistics(true);
		
		reportFormats = new HashSet<>();
	}
	
	public ControlGraph getControlGraph() {
		return controlGraph;
	}

	public void setControlGraph(ControlGraph controlGraph) {
		this.controlGraph = controlGraph;
	}
	
	public RulesetPathType getRulesetPathType() {
		return rulesetPathType;
	}

	public void setRulesetPathType(RulesetPathType rulesetPathType) {
		this.rulesetPathType = rulesetPathType;
	}

	public String getRulesetPathDir() {
		return rulesetPathDir;
	}

	public void setRulesetPathDir(String rulesPath) {
		this.rulesetPathDir = rulesPath;
	}
	
	public String getRulesetPathZip() {
		return rulesetPathZip;
	}

	public void setRulesetPathZip(String rulesetPathZip) {
		this.rulesetPathZip = rulesetPathZip;
	}

	public String getSootPath() {
		return sootPath;
	}

	public void setSootPath(String sootClasspath) {
		this.sootPath = sootClasspath;
	}

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationClasspath) {
		this.applicationPath = applicationClasspath;
	}

	public String getSoftwareIdentifier() {
		return softwareIdentifier;
	}

	public void setSoftwareIdentifier(String softwareIdentifier) {
		this.softwareIdentifier = softwareIdentifier;
	}

	public String getReportDirectory() {
		return reportDirectory;
	}

	public void setReportDirectory(String reportDirectory) {
		this.reportDirectory = reportDirectory;
	}
	
	public Set<ReportFormat> getReportFormats() {
		return reportFormats;
	}

	public boolean isPreAnalysis() {
		return preAnalysis;
	}

	public void setPreAnalysis(boolean preAnalysis) {
		this.preAnalysis = preAnalysis;
	}

	public boolean isVisualization() {
		return visualization;
	}

	public void setVisualization(boolean visualization) {
		this.visualization = visualization;
	}

	public boolean isProviderDetectionAnalysis() {
		return providerDetectionAnalysis;
	}

	public void setProviderDetectionAnalysis(boolean providerDetectionAnalysis) {
		this.providerDetectionAnalysis = providerDetectionAnalysis;
	}
	
	public boolean isIncludeStatistics() {
		return includeStatistics;
	}
	
	public void setIncludeStatistics(boolean includeStatistics) {
		this.includeStatistics = includeStatistics;
	}
	
	public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
		int mandatorySettings = 0;
		
		if(settings == null) {
			showErrorMessage();
		}
		
		for(int i = 0; i < settings.length; i++) {
			switch(settings[i].toLowerCase()) {
				case "--cg":
					parseControlGraphValue(settings[i+1]);
					i++;
					break;
				case "--rulesdir":
					if(this.rulesetPathType != RulesetPathType.NONE) {
						throw new CryptoAnalysisParserException("An error occured while parsing --rulesDir option. "
								+ "There should be only one option between --rulesDir and --rulesZip.");
					}
					setRulesetPathType(RulesetPathType.DIR);
					setRulesetPathDir(settings[i+1]);
					i++;
					mandatorySettings++;
					break;
				case "--ruleszip":
					if(this.rulesetPathType != RulesetPathType.NONE) {
						throw new CryptoAnalysisParserException("An error occured while parsing --rulesDir option. "
								+ "There should be only one option between --rulesDir and --rulesZip.");
					}
					setRulesetPathType(RulesetPathType.ZIP);
					setRulesetPathZip(settings[i+1]);
					mandatorySettings++;
					i++;
					break;
				case "--apppath":
					setApplicationPath(settings[i+1]);
					i++;
					mandatorySettings++;
					break;
				case "--sootpath":
					setSootPath(settings[i+1]);
					i++;
					break;
				case "--identifier":
					setSoftwareIdentifier(settings[i+1]);
					i++;
					break;
				case "--reportpath":
					setReportDirectory(settings[i+1]);
					i++;
					break;
				case "--reportformat":
					int formats = parseReportFormatValues(settings, i);
					i += formats;
					break;
				case "--preanalysis":
					setPreAnalysis(true);
					break;
				case "--visualization":
					setVisualization(true);
					break;
				case "--providerdetection":
					setProviderDetectionAnalysis(true);
					break;
				case "--dstats":
					setIncludeStatistics(false);
					break;
				default:
					showErrorMessage(settings[i]);		
			}
		}
		
		if(mandatorySettings != 2) {
			showErrorMessage();
		}
	}
	
	public enum ControlGraph {
		CHA, SPARK, SPARKLIB,
	}
	
	public enum ReportFormat {
		CMD, TXT, SARIF, CSV, CSV_SUMMARY
	}
	
	public enum RulesetPathType {
		DIR, ZIP, NONE
	}
	
	private void parseControlGraphValue(String value) throws CryptoAnalysisParserException {
		String CGValue = value.toLowerCase();
		
		switch(CGValue) {
			case "cha":
				setControlGraph(ControlGraph.CHA);
				break;
			case "spark":
				setControlGraph(ControlGraph.SPARK);
				break;
			case "sparklib":
				setControlGraph(ControlGraph.SPARKLIB);
				break;
			default:
				throw new CryptoAnalysisParserException("Incorrect value " + CGValue + " for --cg option. "
						+ "Available options are: CHA, SPARK and SPARKLIB.\n");
		}
	}
	
	private int parseReportFormatValues(String[] settings, int position) throws CryptoAnalysisParserException {
		// settings should be the command line input and position the index of --reportFormat
		int numFormats = 0;
		
		for (int i = position + 1; i < settings.length; i++) {
			
			// new argument
			if (settings[i].startsWith("-")) {
				break;
			}
			
			String reportFormatValue = settings[i].toLowerCase();
			
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
			
			numFormats++;
		}
		
		return numFormats;
	}
	
	private static void showErrorMessage() throws CryptoAnalysisParserException {
		String errorMessage = "An error occurred while trying to parse the CLI arguments.\n"
				+ "The default command for running CryptoAnalysis is: \n"
				+ "java -cp <jar_location_of_cryptoanalysis> crypto.HeadlessCryptoScanner \\\r\n"
				+ " 	  --rulesDir <absolute_path_to_crysl_source_code_format_rules> \\\r\n"
				+ "       --appPath <absolute_application_path>\n";
		throw new CryptoAnalysisParserException(errorMessage);
	}
		
	private static void showErrorMessage(String arg) throws CryptoAnalysisParserException {
		String errorMessage = "An error occured while trying to parse the CLI argument: " + arg + ".\n"
				+ "The default command for running CryptoAnalysis is: \n"
				+ "java -cp <jar_location_of_cryptoanalysis> crypto.HeadlessCryptoScanner \\\r\n"
				+ "      --rulesDir <absolute_path_to_crysl_rules> \\\r\n"
				+ "      --appPath <absolute_application_path>\n"
				+ "\nAdditional arguments that can be used are:\n"
				+ "--cg <selection_of_call_graph_for_analysis (CHA, SPARK, SPARKLIB)>\n"
				+ "--sootPath <absolute_path_of_whole_project>\n"
				+ "--identifier <identifier_for_labelling_output_files>\n"
				+ "--reportPath <directory_location_for_cognicrypt_report>\n"
				+ "--reportFormat <format of cognicrypt_report (CMD, TXT, SARIF, CSV, CSV_SUMMARY)>\n"
				+ "--preanalysis (enables pre-analysis)\n"
				+ "--visualization (enables the visualization, but also requires --reportPath option to be set)\n"
				+ "--providerDetection (enables provider detection analysis)\n"
				+ "--dstats (disable the statistic information in the reports)\n";
		throw new CryptoAnalysisParserException(errorMessage);
	}
	
}
