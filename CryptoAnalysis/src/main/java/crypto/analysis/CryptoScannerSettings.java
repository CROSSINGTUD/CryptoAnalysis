package crypto.analysis;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crypto.exceptions.CryptoAnalysisParserException;
import picocli.CommandLine;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class CryptoScannerSettings implements Runnable {

	@CommandLine.Option(names = {"--appPath"}, required = true)
	private String appPath = "";
	
	@CommandLine.Option(names = {"--rulesDir"}, required = true)
	private String rulesDir = "";
	
	@CommandLine.Option(names = {"--cg"})
	private String cg = "";
	
	@CommandLine.Option(names = {"--sootPath"})
	private String sootPath = "";
	
	@CommandLine.Option(names = {"--identifier"})
	private String identifier = "";

	@CommandLine.Option(names = {"--reportPath"})
	private String reportPath = "";
	
	@CommandLine.Option(names = {"--reportFormat"}, split = ",")
	private String[] reportFormat;
	
	@CommandLine.Option(names = {"--preanalysis"})
	private boolean preanalysis = false;
	
	@CommandLine.Option(names = {"--visualization"})
	private boolean visualization = false;
	
	@CommandLine.Option(names = {"--providerdetection"})
	private boolean providerdetection = false;
	
	@CommandLine.Option(names = {"--dstats"})
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

	public void parseSettingsFromCLI(String[] settings) {
		CommandLine parser = new CommandLine(this);
		parser.setCaseInsensitiveEnumValuesAllowed(true);
		parser.execute(settings);
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

	@Override
	public void run() {
		if (!cg.equals("")) {
			try {
				parseControlGraphValue(cg);
			} catch (CryptoAnalysisParserException e) {
				System.out.println(e.getMessage());
			}
		}

		if (reportFormat != null) {
			try {
				parseReportFormatValues(reportFormat);
			} catch (CryptoAnalysisParserException e) {
				System.out.println(e.getMessage());
			}
		}
		
		if (!rulesDir.equals("")) {
			if (this.isZipFile(rulesDir)) {
				this.rulesetPathType = RulesetPathType.ZIP;
			} else {
				this.rulesetPathType = RulesetPathType.DIR;
			}
		}
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
	
	/**
	 * This method parses the specified report formats and returns the number of given report formats until
	 * the next flag from the command line is found (e.g. "--reportformat CMD TXT CSV --<new flag>" will store
	 * the formats CMD, TXT and CSV and return the value 3).
	 * 
	 * @param settings The command line input.
	 * @param position The position of the --reportformat flag in the command line input
	 * @return numFormats The number of specified formats. If a format is given twice, it is also
	 *                    counted twice.
	 * @throws CryptoAnalysisParserException if a reportformat value is not supported
	 */
	
	private void parseReportFormatValues(String[] settings) throws CryptoAnalysisParserException {
		for (int i = 0; i < settings.length; i++) {
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
		}
	}
	
	private boolean isZipFile(String path) {
		return false;
	}
		
//		// settings should be the command line input and position the index of --reportFormat
//		int numFormats = 0;
//		
//		for (int i = position + 1; i < settings.length; i++) {
//			
//			// new argument is specified
//			if (settings[i].startsWith("-")) {
//				break;
//			}
//			
//			String reportFormatValue = settings[i].toLowerCase();
//			
//			switch (reportFormatValue) {
//				case "cmd":
//					reportFormats.add(ReportFormat.CMD);
//					break;
//				case "txt":
//					reportFormats.add(ReportFormat.TXT);
//					break;
//				case "sarif":
//					reportFormats.add(ReportFormat.SARIF);
//					break;
//				case "csv":
//					reportFormats.add(ReportFormat.CSV);
//					break;
//				case "csv_summary":
//					reportFormats.add(ReportFormat.CSV_SUMMARY);
//					break;
//				default:
//					throw new CryptoAnalysisParserException("Incorrect value " + reportFormatValue + " for --reportFormat option. "
//							+ "Available options are: CMD, TXT, SARIF, CSV and CSV_SUMMARY.\n");
//			}
//			
//			numFormats++;
//		}
//		
//		return numFormats;
//	}
	
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
