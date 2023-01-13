package crypto.reporting;

public class ReportStatistics {
	
	private String softwareID;
	private int seedObjectCount;
	private long analysisTime;
	private long callgraphTime;
	private int callgraphReachableMethods;
	private int callgraphReachableMethodsWithActiveBodies;
	private int dataflowVisitedMethods;
	
	public ReportStatistics() {
		this.softwareID = "Unknown";
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
