package crypto.reporting;

/**
 * This class is used by the class {@link Reporter} to store all statistics, which are relevant for the analysis.
 * 
 * Currently the following statistics are supported:
 * - softwareID: Identifier of the analyzed software. This value can be set by using the --identifier flag.
 * - seedObjectCount: Number of seed objects.
 * - analysisTime: The time in milliseconds for the actual analysis (e.g. without the initialization of the analysis
 *                 and the construction of the callgraph)
 * - callgraphTime: The time in milliseconds to construct the callgraph.
 * - callgraphReachableMethods: The number of reachable methods in the callgraph.
 * - callgraphReachableMethodsWithActiveBodies: The number of reachable methods with active bodies in the callgraph.
 * - dataflowVisitedMethods: The number of visited methods in the dataflows.
 */
public class ReportStatistics {
	
	private String softwareID;
	private int seedObjectCount;
	private long analysisTime;
	private long callgraphTime;
	private int callgraphReachableMethods;
	private int callgraphReachableMethodsWithActiveBodies;
	private int dataflowVisitedMethods;
	
	/**
	 * Creates an instance to store all relevant statistics for an analysis. The softwareID is initialized with
	 * an empty string and all numeric variables are initialized with -1. The corresponding set methods should be
	 * used to update the statistic values.
	 */
	public ReportStatistics() {
		this.softwareID = "";
		this.seedObjectCount = -1;
		this.analysisTime = -1;
		this.callgraphTime = -1;
		this.callgraphReachableMethods = -1;
		this.callgraphReachableMethodsWithActiveBodies = -1;
		this.dataflowVisitedMethods = -1;
	}
	
	public void setSoftwareID(String softwareID) {
		this.softwareID = softwareID;
	}
	
	public String getSoftwareID() {
		return softwareID;
	}
	
	public void setSeedObjectCount(int seedObjectCount) {
		this.seedObjectCount = seedObjectCount;
	}
	
	public int getSeedObjectCount() {
		return seedObjectCount;
	}
	
	public void setAnalysisTime(long analysisTime) {
		this.analysisTime = analysisTime;
	}
	
	public long getAnalysisTime() {
		return analysisTime;
	}
	
	public void setCallgraphTime(long callgraphTime) {
		this.callgraphTime = callgraphTime;
	}
	
	public long getCallgraphTime() {
		return callgraphTime;
	}
	
	public void setCallgraphReachableMethods(int callgraphReachableMethods) {
		this.callgraphReachableMethods = callgraphReachableMethods;
	}
	
	public int getCallgraphReachableMethods() {
		return callgraphReachableMethods;
	}
	
	public void setCallgraphReachableMethodsWithActiveBodies(int callgraphReachableMethodsWithActiveBodies) {
		this.callgraphReachableMethodsWithActiveBodies = callgraphReachableMethodsWithActiveBodies;
	}
	
	public int getCallgraphReachableMethodsWithActiveBodies() {
		return callgraphReachableMethodsWithActiveBodies;
	}
	
	public void setDataflowVisitedMethods(int dataflowVisitedMethods) {
		this.dataflowVisitedMethods = dataflowVisitedMethods;
	}
	
	public int getDataflowVisitedMethods() {
		return dataflowVisitedMethods;
	}

}
