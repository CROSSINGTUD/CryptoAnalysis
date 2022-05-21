package crypto.analysis;
import crypto.exceptions.CryptoAnalysisParserException;
import picocli.CommandLine;

@CommandLine.Command
public class CryptoScannerSettings implements Runnable {

	private ControlGraph controlGraph = null;
	private RulesetPathType rulesetPathType = null;
	private String rulesetPathDir = null;
	private String rulesetPathZip = null;
	private String sootPath = "";
	private String applicationPath = null;
	private String softwareIdentifier = "";
	private String reportDirectory = null;
	private ReportFormat reportFormat = null;
	private boolean preAnalysis;
	private boolean visualization;
	private boolean providerDetectionAnalysis;

	public enum ControlGraph {
		CHA, SPARK, SPARKLIB,
	}

	public enum ReportFormat {
		TXT, SARIF, CSV
	}

	public enum RulesetPathType {
		DIR, ZIP, NONE
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

	public ReportFormat getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(ReportFormat reportFormat) {
		this.reportFormat = reportFormat;
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

	public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
		setControlGraph(ControlGraph.CHA);
		setRulesetPathType(RulesetPathType.NONE);
		setPreAnalysis(false);
		setVisualization(false);
		setProviderDetectionAnalysis(false);
		CommandLine.run(this, settings);
	}

	@CommandLine.Option(names = {"--reportPath"})
	private String reportPath;
	@CommandLine.Option(names = {"--appPath"}, required = true)
	private String appPath;
	@CommandLine.Option(names = {"--rulesDir"})
	private String rulesDir;
	@CommandLine.Option(names = {"--cg"})
	private String cg;
	@CommandLine.Option(names = {"--sootpath"})
	private String sootpath;
	@CommandLine.Option(names = {"--identifier"})
	private String identifier;
	@CommandLine.Option(names = {"--reportformat"})
	private String reportformat;
	@CommandLine.Option(names = {"--preanalysis"})
	private boolean preanalysis;
	@CommandLine.Option(names = {"--visualization"})
	private boolean visualizations;
	@CommandLine.Option(names = {"--providerdetection"})
	private boolean providerdetection;


	@Override
	public void run() {
		System.out.println("The popular git command");
		System.out.println("Committing files in the staging area, how wonderful?");
		if (reportPath != null) {
			setReportDirectory(reportPath);
			System.out.println("The commit message is " + reportPath);
		}
		if (rulesDir != null) {
			setRulesetPathType(RulesetPathType.DIR);
			setRulesetPathDir(rulesDir);
		}

		if (appPath != null) {
			//setApplicationPath(appPath);
			this.applicationPath = appPath;
		}
		if (sootpath != null) {
			setSootPath(sootpath);
		}

		if (cg != null) {
			try {
				parseControlGraphValue(cg);
			} catch (CryptoAnalysisParserException e) {
				e.printStackTrace();
			}
		}
		if (identifier != null) {
			setSoftwareIdentifier(identifier);
		}

		if (reportformat != null) {
			try {
				parseReportFormatValue(reportformat);
			} catch (CryptoAnalysisParserException e) {
				System.out.println("Parser failed  wrong report format!");
			}
		}
		if (preanalysis != false) {
			setPreAnalysis(true);
		}
		if (visualizations != false) {
			setVisualization(true);
		}
		if (providerdetection != false) {
			setProviderDetectionAnalysis(true);
		}


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
				throw new CryptoAnalysisParserException("Incorrect value "+CGValue+" for --cg option. "
						+ "Available options are: CHA, SPARK and SPARKLIB.\n");
		}
	}
	
	private void parseReportFormatValue(String value) throws CryptoAnalysisParserException {
		String reportFormatValue = value.toLowerCase();
		switch(reportFormatValue) {
			case "txt":
				setReportFormat(ReportFormat.TXT);
				break;
			case "sarif":
				setReportFormat(ReportFormat.SARIF);
				break;
			case "csv":
				setReportFormat(ReportFormat.CSV);
				break;
			default:
				throw new CryptoAnalysisParserException("Incorrect value "+reportFormatValue+" for --reportFormat option. "
						+ "Available options are: TXT, SARIF and CSV.\n");
		}
	}
	
	private static void showErrorMessage() throws CryptoAnalysisParserException {
		String errorMessage = "An error occurred while trying to parse the CLI arguments.\n"
				+"The default command for running CryptoAnalysis is: \n"+
				"java -cp <jar_location_of_cryptoanalysis> crypto.HeadlessCryptoScanner \\\r\n"+ 
				" 		--rulesDir <absolute_path_to_crysl_source_code_format_rules> \\\r\n" + 
				"       --appPath <absolute_application_path>\n";
		throw new CryptoAnalysisParserException(errorMessage);
	}
		
	private static void showErrorMessage(String arg) throws CryptoAnalysisParserException {
		String errorMessage = "An error occured while trying to parse the CLI argument: "+arg+".\n"
				+"The default command for running CryptoAnalysis is: \n"
				+ "java -cp <jar_location_of_cryptoanalysis> crypto.HeadlessCryptoScanner \\\r\n" + 
				"      --rulesDir <absolute_path_to_crysl_rules> \\\r\n" + 
				"      --appPath <absolute_application_path>\n"
				+ "\nAdditional arguments that can be used are:\n"
				+ "--cg <selection_of_call_graph_for_analysis (CHA, SPARK, SPARKLIB)>\n"
				+ "--sootPath <absolute_path_of_whole_project>\n"
				+ "--identifier <identifier_for_labelling_output_files>\n"
				+ "--reportPath <directory_location_for_cognicrypt_report>\n"
				+ "--reportFormat <format of cognicrypt_report (TXT, SARIF, CSV)>\n"
				+ "--preanalysis (enables pre-analysis)\n"
				+ "--visualization (enables the visualization, but also requires --reportPath option to be set)\n"
				+ "--providerDetection (enables provider detection analysis)\n";
		throw new CryptoAnalysisParserException(errorMessage);
	}
	
}
