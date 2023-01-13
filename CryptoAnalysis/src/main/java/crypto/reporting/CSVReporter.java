package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import java.util.Set;

import crypto.analysis.errors.AbstractError;
import crypto.rules.CrySLRule;
import soot.SootClass;
import soot.SootMethod;

public class CSVReporter extends Reporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVReporter.class);
	
	private static final String CSV_SEPARATOR = ";";
	private static final String REPORT_NAME = "CryptoAnalysis-Report.csv";
	
	private List<String> headers;
	private List<String> contents;
	
	private enum Headers {
		ErrorID, ErrorType, ViolatingClass, Class, Method, LineNumber, Statement, Message
	}
	
	private enum StatisticHeaders {
		SoftwareID, SeedObjectCount, CryptoAnalysisTime_ms, CallGraphTime_ms, CallGraphReachableMethods,
		CallGraphReachableMethods_ActiveBodies, DataflowVisitedMethod
	}
	
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
			
			if (includeStatistics()) {
				// Additional analysis statistics
				writer.write("\nAdditional analysis statistics:\n");
				writer.write(String.format("SoftwareID: %s\n", statistics.getSoftwareID()));
				writer.write(String.format("SeedObjectCount: %d\n", statistics.getSeedObjectCount()));
				writer.write(String.format("CryptoAnalysisTime: %d\n", statistics.getAnalysisTime()));
				writer.write(String.format("CallgraphConstructionTime: %d\n", statistics.getCallgraphTime()));
				writer.write(String.format("CallgraphReachableMethods: %d\n", statistics.getCallgraphReachableMethods()));
				writer.write(String.format("CallgraphReachableMethodsWithActiveBodies: %d\n", statistics.getCallgraphReachableMethodsWithActiveBodies()));
				writer.write(String.format("DataflowVisitedMethods: %d\n", statistics.getDataflowVisitedMethods()));	
			}
			
			writer.close();
			LOGGER.info("CSV Report generated to file : " + getOutputFolder().getAbsolutePath() + File.separator+ REPORT_NAME);
		} catch (IOException e) {
			LOGGER.error("Could not write to " + getOutputFolder().getAbsolutePath() + File.separator + REPORT_NAME, e);
		}
	}

}
