package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;
import soot.SootClass;
import soot.SootMethod;

/**
 * This class extends the class {@link Reporter} by generating an analysis report and write it into a
 * csv file.
 * 
 * Compared to the {@link CSVSummaryReporter}, this reporter writes each error from the analysis into
 * a single line. If the statistics are enabled, each line is extended by the corresponding statistic
 * fields. Since the statistics are computed for the whole analysis, each value for the different fields
 * are the same in all lines.
 */
public class CSVReporter extends Reporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVReporter.class);

	private static final String CSV_SEPARATOR = ";";
	private static final String REPORT_NAME = "CryptoAnalysis-Report.csv";
	
	private List<String> headers;
	private List<String> contents;
	
	/** Headers for the errors. These headers are always part of the analysis report. */
	private enum Headers {
		ErrorID, ErrorType, ViolatingClass, Class, Method, LineNumber, Statement, Message
	}
	
	/** 
	 * Headers for the  statistics. These headers are only part of the analysis report, if
	 * the corresponding parameter in the constructor is set to true.
	 */
	private enum StatisticHeaders {
		SoftwareID, SeedObjectCount, CryptoAnalysisTime_ms, CallGraphTime_ms, CallGraphReachableMethods,
		CallGraphReachableMethods_ActiveBodies, DataflowVisitedMethod
	}
	
	/**
	 * Subclass of {@link Reporter}. Creates an instance of {@link CSVReporter}, which
	 * can be used to create a csv file containing the analysis report.
	 * 
	 * @param reportDir A {@link String} path giving the location of the report directory.
	 *                  The reportPath should end without an ending file separator.
	 * @param softwareId A {@link String} for the analyzed software.
	 * @param rules A {@link List} of {@link CrySLRule} containing the rules the program is analyzed with.
	 * @param callGraphConstructionTime The time in milliseconds for the construction of the callgraph.
	 * @param includeStatistics Set this value to true, if the analysis report should contain some
	 *                          analysis statistics (e.g. the callgraph construction time). If this value is set
	 *                          to false, no statistics will be output. 
	 */
	public CSVReporter(String reportDir, String softwareId,  List<CrySLRule> rules, long callGraphConstructionTime, boolean includeStatistics) {
		super((reportDir != null ? new File(reportDir) : new File(System.getProperty("user.dir"))), softwareId, rules, callGraphConstructionTime, includeStatistics);
		
		headers = new ArrayList<>();
		contents = new ArrayList<>();
		
		for (Headers h : Headers.values()) {
			headers.add(h.toString());
		}
		
		if (includeStatistics()) {
			for (StatisticHeaders h : StatisticHeaders.values()) {
				headers.add(h.toString());
			}
		}
	}

	@Override
	public void handleAnalysisResults() {
		int idCount = 0;
		
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			String className = c.getName();
			
			for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				String methodName = e.getKey().getSubSignature();
				
				for (AbstractError marker : e.getValue()) {
					String errorType = marker.getClass().getSimpleName();
					String violatingClass = marker.getRule().getClassName();
					String errorMessage = marker.toErrorMarkerString();
					int lineNumber = marker.getErrorLocation().getUnit().get().getJavaSourceStartLineNumber();
					String statement = marker.getErrorLocation().getUnit().get().toString();
					
					String line = idCount + CSV_SEPARATOR + errorType + CSV_SEPARATOR + violatingClass + CSV_SEPARATOR + className + 
							CSV_SEPARATOR + methodName + CSV_SEPARATOR + lineNumber + CSV_SEPARATOR + statement + CSV_SEPARATOR + errorMessage;
					
					// Add the statistics to every single line of the report
					if (includeStatistics()) {
						line += CSV_SEPARATOR + getStatistics().getSoftwareID() + CSV_SEPARATOR + getStatistics().getSeedObjectCount() + CSV_SEPARATOR
								+ getStatistics().getAnalysisTime() + CSV_SEPARATOR + getStatistics().getCallgraphTime()
								+ CSV_SEPARATOR + getStatistics().getCallgraphReachableMethods() + CSV_SEPARATOR + getStatistics().getCallgraphReachableMethodsWithActiveBodies()
								+ CSV_SEPARATOR + getStatistics().getDataflowVisitedMethods();
					}
					
					contents.add(line);
					
					idCount++;
				}
			}
		}

		writeToFile();
	}
	
	private void writeToFile() {
		try {
			FileWriter writer = new FileWriter(getOutputFolder() + File.separator + REPORT_NAME);
			
			// write headers
			writer.write(Joiner.on(CSV_SEPARATOR).join(headers) + "\n");
			
			// write errors line by line
			for (String line : this.contents) {
				writer.write(line + "\n");
			}
			
			writer.close();
			LOGGER.info("CSV Report generated to file : " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME);
		} catch (IOException e) {
			LOGGER.error("Could not write to " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME, e);
		}
	}
}
