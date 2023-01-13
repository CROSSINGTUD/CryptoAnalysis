package crypto.reporting;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

public abstract class Reporter extends ErrorMarkerListener {
	
	private File outputFolder;
	private List<CrySLRule> rules;
	private boolean includeStatistics;
	
	protected final ReportStatistics statistics = new ReportStatistics();
	protected final Stopwatch analysisWatch = Stopwatch.createUnstarted();
	protected final Collection<IAnalysisSeed> objects = new HashSet<>();
	protected final Set<SootMethod> dataflowReachableMethods = new HashSet<>();
	
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
		this.analysisWatch.stop();
		
		this.statistics.setSeedObjectCount(this.objects.size());
		this.statistics.setAnalysisTime(this.analysisWatch.elapsed(TimeUnit.MILLISECONDS));
		this.statistics.setDataflowVisitedMethods(this.dataflowReachableMethods.size());
		
		handleAnalysisResults();
	}
	
	public abstract void handleAnalysisResults();

}
