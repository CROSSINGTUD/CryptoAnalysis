package crypto.reporting;

import java.io.File;
import java.util.List;
import crypto.rules.CrySLRule;

public class CommandLineReporter extends Reporter {

	/**
	 * The analysis report
	 */
	private String analysisReport;

	/**
	 * Creates {@link CommandLineReporter} a constructor with reportDir and rules as parameter
	 * 
	 * @param reportDir a {@link String} path giving the location of the report directory
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
	 */
	public CommandLineReporter(String reportDir, List<CrySLRule> rules) {
		super((reportDir != null ? new File(reportDir) : null), "", rules, -1, false);
	}
	
	/**
	 * Creates {@link CommandLineReporter} a constructor with the softwareID, the rules and the
	 * callgraph construction time as parameter
	 * 
	 * @param softwareID Identifier for the software
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
	 * @param callgraphConstructionTime Time for the callgraph construction in milliseconds
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
