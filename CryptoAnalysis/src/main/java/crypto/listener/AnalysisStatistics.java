/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
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
