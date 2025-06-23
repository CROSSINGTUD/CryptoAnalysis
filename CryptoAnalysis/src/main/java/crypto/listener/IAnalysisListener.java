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

import boomerang.BackwardQuery;
import boomerang.scope.CallGraph;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import java.util.Collection;

public interface IAnalysisListener {

    void beforeAnalysis();

    void afterAnalysis();

    void beforeCallGraphConstruction();

    void afterCallGraphConstruction(CallGraph callGraph);

    void beforeTypestateAnalysis();

    void afterTypestateAnalysis();

    void beforeTriggeringBoomerangQuery(BackwardQuery query);

    void afterTriggeringBoomerangQuery(BackwardQuery query);

    void onDiscoveredSeeds(Collection<IAnalysisSeed> discoveredSeeds);

    void onSeedStarted(IAnalysisSeed analysisSeed);

    void onSeedFinished(IAnalysisSeed analysisSeed);

    void onTypestateAnalysisTimeout(IAnalysisSeed analysisSeed);

    void onExtractParameterAnalysisTimeout(Val parameter, Statement statement);

    void beforeConstraintsCheck(IAnalysisSeed analysisSeed);

    void afterConstraintsCheck(IAnalysisSeed analysisSeed, int violatedConstraints);

    void beforePredicateCheck();

    void afterPredicateCheck();

    void onReportedError(IAnalysisSeed analysisSeed, AbstractError error);

    void addProgress(int current, int total);
}
