package crypto.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import crypto.rules.CrySLRule;
import soot.Printer;
import soot.SootClass;
import soot.util.EscapedWriter;

public class TXTReporter extends Reporter {

	private static final Logger LOG = LoggerFactory.getLogger(TXTReporter.class);
	
	/**
	 * The report of the analysis
	 */
	private String analysisReport;
	/**
	 * name of the analysis report
	 */
	private static final String REPORT_NAME = "CryptoAnalysis-Report.txt";
	
	/**
	 * Creates {@link TXTReporter} a constructor with reportDir and rules as parameter
	 * 
	 * @param reportDir A {@link String} path giving the location of the report directory
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
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
			
			for (SootClass c : this.errorMarkers.rowKeySet()) {
				FileOutputStream streamOut = new FileOutputStream(new File(getOutputFolder() + File.separator + c.toString() + ".jimple"));
				PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
				Printer.v().printTo(c, writerOut);
				
				writerOut.flush();
				streamOut.close();
				writerOut.close();
			}
			
			LOG.info("Text Report generated to file : "+ getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOG.error("Could not write to file " + getOutputFolder().getAbsolutePath() + File.separator+ REPORT_NAME, e);
		}
	}
	
}
