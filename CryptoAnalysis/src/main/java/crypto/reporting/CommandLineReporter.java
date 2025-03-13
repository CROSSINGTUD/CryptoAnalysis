/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.reporting;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crysl.rule.CrySLRule;
import java.util.Collection;
import java.util.Set;

public class CommandLineReporter extends Reporter {

    public CommandLineReporter(Collection<CrySLRule> ruleset) {
        super(ruleset);
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        String report = ReportGenerator.generateReport(seeds, ruleset, errorCollection, statistics);
        System.out.println(report);
    }
}
