package crypto.reporting;

import java.io.File;
import java.util.List;
import crypto.rules.CrySLRule;

/**
 * This class extends the class {@link Reporter} by generating an analysis report and print it to the command line.
 */
public class CommandLineReporter extends Reporter {

	/**The analysis report */
	private String analysisReport;

	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link CommandLineReporter} with reportDir and rules as parameter
	 * 
	 * @param reportDir a {@link String} path giving the location of the report directory
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
	 */
	public CommandLineReporter(String reportDir, List<CrySLRule> rules) {
		super((reportDir != null ? new File(reportDir) : null), "", rules, -1, false);
	}
	
	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link CommandLineReporter}, which
	 * can be used to print an analysis report to stdout.
	 * 
	 * @param reportDir A {@link String} path giving the location of the report directory.
	 *                  The reportPath should end without an ending file separator.
	 * @param softwareID A {@link String} for the analyzed software.
	 * @param rules A {@link List} of {@link CrySLRule} containing the rules the program is analyzed with.
	 * @param callgraphConstructionTime The time in milliseconds for the construction of the callgraph.
	 * @param includeStatistics Set this value to true, if the analysis report should contain some
	 *                          analysis statistics (e.g. the callgraph construction time). If this value is set
	 *                          to false, no statistics will be output. 
	 */
	public CommandLineReporter(String softwareID, List<CrySLRule> rules, long callgraphConstructionTime, boolean includeStatistics) {
		super(null, softwareID, rules, callgraphConstructionTime, includeStatistics);
	}
	
	@Override
	public void handleAnalysisResults() {
		if (includeStatistics()) {
			this.analysisReport = ReporterHelper.generateReport(getRules(), getObjects(), this.secureObjects, this.errorMarkers, this.errorMarkerCount, getStatistics());
		} else {
			this.analysisReport = ReporterHelper.generateReport(getRules(), getObjects(), this.secureObjects, this.errorMarkers, this.errorMarkerCount, null);
		}
		
		System.out.println(analysisReport);
	}
}
