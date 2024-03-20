package crypto.reporting;

import crypto.rules.CrySLRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class extends the class {@link Reporter} by generating a report and writing it into a text file.
 */
public class TXTReporter extends Reporter {

	private static final Logger LOG = LoggerFactory.getLogger(TXTReporter.class);
	
	/** The report of the analysis */
	private String analysisReport;
	
	/** name of the analysis report */
	private static final String REPORT_NAME = "CryptoAnalysis-Report.txt";
	
	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link TXTReporter}, which
	 * can be used to create a text file containing the analysis report.
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
	public TXTReporter(String reportDir, String softwareID, List<CrySLRule> rules, long callgraphConstructionTime, boolean includeStatistics) {
		super((reportDir != null ? new File(reportDir) : new File(System.getProperty("user.dir"))), softwareID, rules, callgraphConstructionTime, includeStatistics);
	}
	
	@Override
	public void handleAnalysisResults() {
		if (includeStatistics()) {
			this.analysisReport = ReporterHelper.generateReport(getRules(), getObjects(), this.secureObjects, this.errorMarkers, this.errorMarkerCount, getStatistics());	
		} else {
			this.analysisReport = ReporterHelper.generateReport(getRules(), getObjects(), this.secureObjects, this.errorMarkers, this.errorMarkerCount, null);
		}

		try {
			FileWriter writer = new FileWriter(getOutputFolder() + File.separator + REPORT_NAME);
			writer.write(this.analysisReport);
			writer.close();
			
			/*for (WrappedClass c : this.errorMarkers.rowKeySet()) {
				FileOutputStream streamOut = new FileOutputStream(new File(getOutputFolder() + File.separator + c.toString() + ".jimple"));
				PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
				Printer.v().printTo(c, writerOut);
				
				writerOut.flush();
				streamOut.close();
				writerOut.close();
			}*/
			
			LOG.info("Text Report generated to file : "+ getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOG.error("Could not write to file " + getOutputFolder().getAbsolutePath() + File.separator+ REPORT_NAME, e);
		}
	}
	
}
