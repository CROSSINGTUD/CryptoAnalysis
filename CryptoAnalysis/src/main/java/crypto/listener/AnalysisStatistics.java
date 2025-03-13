package crypto.listener;

import boomerang.scope.CallGraph;

public class AnalysisStatistics {

    private String analysisTime;
    private String callGraphTime;
    private String typestateTime;
    private CallGraph callGraph;

    public AnalysisStatistics() {}

    public String getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(String analysisTime) {
        this.analysisTime = analysisTime;
    }

    public String getCallGraphTime() {
        return callGraphTime;
    }

    public void setCallGraphTime(String callGraphTime) {
        this.callGraphTime = callGraphTime;
    }

    public String getTypestateTime() {
        return typestateTime;
    }

    public void setTypestateTime(String typestateTime) {
        this.typestateTime = typestateTime;
    }

    public void setCallGraph(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    public int getReachableMethods() {
        return callGraph.getReachableMethods().size();
    }

    public int getEdges() {
        return callGraph.size();
    }

    public int getEntryPoints() {
        return callGraph.getEntryPoints().size();
    }
}
