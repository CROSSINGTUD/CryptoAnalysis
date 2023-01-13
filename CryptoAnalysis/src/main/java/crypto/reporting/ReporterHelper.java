package crypto.reporting;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Table;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ErrorWithObjectAllocation;
import crypto.rules.CrySLRule;
import soot.SootClass;
import soot.SootMethod;

public class ReporterHelper{

	/** Generates analysis report content for {@link CommandLineReporter} CommandLineReporter and {@link TXTReporter} TXTReporter
	 * @param rules a {@link List} with {@link CrySLRule} rules
	 * @param objects a{@link Collection} with {@link IAnalysisSeed} objects
	 * @param secureObjects a {@link List} with {@link IAnalysisSeed} secureObjects
	 * @param errorMarkers a {@link Table} containing {@link SootClass},{@link SootMethod} 
	 * and a {@link Set} of {@link AbstractError} of the errors found during analysis
	 * @param errorMarkerCount a {@link Map} containing {@link Class} class of error and 
	 * {@link Integer} number of errors
	 * @return report {@link String} of the analysis
	 */
	public static String generateReport(List<CrySLRule> rules, Collection<IAnalysisSeed> objects, 
			List<IAnalysisSeed> secureObjects, Table<SootClass, SootMethod, Set<AbstractError>> errorMarkers, 
			Map<Class, Integer> errorMarkerCount, ReportStatistics statistics) {
		String report = "";

		report += "Ruleset: \n";
		for (CrySLRule r : rules) {
			report += String.format("\t%s\n", r.getClassName());
		}

		report += "\n";

		report += "Analyzed Objects: \n";
		for (IAnalysisSeed r : objects) {
			report += String.format("\tObject:\n");
			report += String.format("\t\tVariable: %s\n", r.var().value());
			report += String.format("\t\tType: %s\n", r.getType());
			report += String.format("\t\tStatement: %s\n", r.stmt().getUnit().get());
			report += String.format("\t\tMethod: %s\n", r.getMethod());
			report += String.format("\t\tSHA-256: %s\n", r.getObjectId());
			report += String.format("\t\tSecure: %s\n", secureObjects.contains(r));
		}
		
		report += "\n";
		
		for (SootClass c : errorMarkers.rowKeySet()) {
			report += String.format("Findings in Java Class: %s\n", c.getName());
			
			for (Entry<SootMethod, Set<AbstractError>> e : errorMarkers.row(c).entrySet()) {
				report += String.format("\n\t in Method: %s\n", e.getKey().getSubSignature());
				
				for (AbstractError marker : e.getValue()) {
					report += String.format("\t\t%s violating CrySL rule for %s", marker.getClass().getSimpleName(), marker.getRule().getClassName());
					
					if(marker instanceof ErrorWithObjectAllocation) {
						report += String.format(" (on Object #%s)\n", ((ErrorWithObjectAllocation) marker).getObjectLocation().getObjectId());
					} else {
						report += "\n";
					}
					
					report += String.format("\t\t\t%s\n", marker.toErrorMarkerString());
					report += String.format("\t\t\tat statement: %s\n", marker.getErrorLocation().getUnit().get());
					report += String.format("\t\t\tat line: %d\n\n", marker.getErrorLocation().getUnit().get().getJavaSourceStartLineNumber());
				}
			}
			
			report += "\n";
		}
		
		report += "======================= CryptoAnalysis Summary ==========================\n";
		report += String.format("\tNumber of CrySL rules: %s\n", rules.size());
		report += String.format("\tNumber of Objects Analyzed: %s\n", objects.size());
		
		if (errorMarkers.rowKeySet().isEmpty()) {
			report += "No violation of any of the rules found.\n";
		} else {
			report += "\n\tCryptoAnalysis found the following violations. For details see description above.\n";
			
			for (Entry<Class, Integer> e : errorMarkerCount.entrySet()) {
				report += String.format("\t%s: %s\n", e.getKey().getSimpleName(), e.getValue());
			}
		}
		
		if (statistics != null) {
			// Additional analysis statistics
			report += "\n\tAdditional analysis statistics:\n";
			report += String.format("\t\tSoftwareID: %s\n", statistics.getSoftwareID());
			report += String.format("\t\tSeedObjectCount: %d\n", statistics.getSeedObjectCount());
			report += String.format("\t\tCryptoAnalysisTime (in ms): %d\n", statistics.getAnalysisTime());
			report += String.format("\t\tCallgraphConstructionTime (in ms): %d\n", statistics.getCallgraphTime());
			report += String.format("\t\tCallgraphReachableMethods: %d\n", statistics.getCallgraphReachableMethods());
			report += String.format("\t\tCallgraphReachableMethodsWithActiveBodies: %d\n", statistics.getCallgraphReachableMethodsWithActiveBodies());
			report += String.format("\t\tDataflowVisitedMethods: %d\n", statistics.getDataflowVisitedMethods());	
		}

		report += "=========================================================================";
		
		return report;
	}
	
}
