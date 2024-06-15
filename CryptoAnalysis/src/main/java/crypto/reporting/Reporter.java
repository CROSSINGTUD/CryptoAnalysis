package crypto.reporting;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import boomerang.scene.Method;
import com.google.common.base.Stopwatch;

import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;
import typestate.TransitionFunction;

/**
 * Superclass for all reporters.
 * 
 * This class is used to define and implement the basic parts, which all reporter should be able to support. This includes
 * the computation of all statistics for the analysis and the basic logic for methods defined in the {@link crypto.analysis.ICrySLResultsListener}.
 * 
 * This class is abstract. Subclasses have to call the constructor and overwrite the method handleAnalysisResults(), which is called
 * after the analysis is finished.
 */
public abstract class Reporter extends ErrorMarkerListener {
	
	private File outputFolder;
	private List<CrySLRule> rules;
	private boolean includeStatistics;
	
	/** An instance of {@link ReportStatistics} to store all relevant analysis statistics */
	protected final ReportStatistics statistics = new ReportStatistics();
	
	/** The stopwatch to measure to time for the actual analysis */
	protected final Stopwatch analysisWatch = Stopwatch.createUnstarted();
	
	/** A {@link Collection} to store and count all analyzed objects */
	protected final Collection<IAnalysisSeed> objects = new HashSet<>();
	
	/** A {@link Set} to store and count all reachable methods in the dataflow */
	protected final Set<Method> dataflowReachableMethods = new HashSet<>();
	
	/**
	 * The constructor to initialize all attributes. Since this class is abstract, all subclasses
	 * have to call this constructor.
	 * 
	 * @param outputFolder A {@link File} for the location of the report directory.
	 *                  The reportPath should end without an ending file separator.
	 * @param softwareID A {@link String} for the analyzed software.
	 * @param rules A {@link List} of {@link CrySLRule} containing the rules the program is analyzed with.
	 * @param callgraphConstructionTime The time in milliseconds for the construction of the callgraph.
	 * @param includeStatistics Set this value to true, if the analysis report should contain some
	 *                          analysis statistics (e.g. the callgraph construction time). If this value is set
	 *                          to false, no statistics will be output. 
	 */
	public Reporter(File outputFolder, String softwareID, List<CrySLRule> rules, long callgraphConstructionTime, boolean includeStatistics) {
		this.outputFolder = outputFolder;
		this.rules = rules;
		this.includeStatistics = includeStatistics;
		
		this.statistics.setSoftwareID(softwareID);
		this.statistics.setCallgraphTime(callgraphConstructionTime);
		
		// Compute reachable methods and visited methods
		ReachableMethods reachableMethods = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
		Set<SootMethod> visited = new HashSet<>();
		
		int callgraphReachableMethodsWithActiveBodies = 0;
		
		while (listener.hasNext()) {
			MethodOrMethodContext next = listener.next();
			visited.add(next.method());
			
			if (next.method().hasActiveBody()) {
				callgraphReachableMethodsWithActiveBodies++;
			}
		}
		
		this.statistics.setCallgraphReachableMethods(visited.size());
		this.statistics.setCallgraphReachableMethodsWithActiveBodies(callgraphReachableMethodsWithActiveBodies);
	}
	
	public File getOutputFolder() {
		return outputFolder;
	}
	
	public List<CrySLRule> getRules() {
		return rules;
	}
	
	public boolean includeStatistics() {
		return includeStatistics;
	}
	
	public ReportStatistics getStatistics() {
		return statistics;
	}
	
	public Collection<IAnalysisSeed> getObjects() {
		return objects;
	}
	
	@Override
	public void beforeAnalysis() {
		this.analysisWatch.start();
	}
	
	@Override
	public void discoveredSeed(IAnalysisSeed object) {
		this.objects.add(object);
	}
	
	@Override
	public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> forwardResults) {
		this.dataflowReachableMethods.addAll(forwardResults.getStats().getCallVisitedMethods());
	}
	
	@Override
	public void afterAnalysis() {
		//this.analysisWatch.stop();
		
		this.statistics.setSeedObjectCount(this.objects.size());
		this.statistics.setAnalysisTime(this.analysisWatch.elapsed(TimeUnit.MILLISECONDS));
		this.statistics.setDataflowVisitedMethods(this.dataflowReachableMethods.size());
		
		handleAnalysisResults();
	}
	
	/**
	 * This method is called after the analysis is finished and all statistics have been computed. A subclass
	 * can override this method to extend the actions after the analysis, e.g. creating an analysis report
	 * and write it into a file.
	 */
	public abstract void handleAnalysisResults();

}
