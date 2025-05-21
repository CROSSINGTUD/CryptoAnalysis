/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.framework;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.sootup.BoomerangPreInterceptor;
import boomerang.scope.sootup.SootUpFrameworkScope;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.util.ArrayList;
import java.util.List;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SootClassMember;
import sootup.core.model.SourceType;
import sootup.core.transform.BodyInterceptor;
import sootup.interceptors.TypeAssigner;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpSetup extends FrameworkSetup {

    private JavaView view;

    public SootUpSetup(
            String applicationPath,
            ScannerSettings.CallGraphAlgorithm algorithm,
            DataFlowScope dataFlowScope) {
        super(applicationPath, algorithm, dataFlowScope);
    }

    @Override
    public void initializeFramework() {
        List<BodyInterceptor> interceptors =
                List.of(new TypeAssigner(), new BoomerangPreInterceptor());
        AnalysisInputLocation inputLocation =
                new JavaClassPathAnalysisInputLocation(
                        applicationPath, SourceType.Application, interceptors);

        view = new JavaView(inputLocation);
    }

    @Override
    public FrameworkScope createFrameworkScope() {
        List<JavaSootMethod> entryPoints = new ArrayList<>();
        view.getClasses()
                .forEach(
                        c -> {
                            for (JavaSootMethod method : c.getMethods()) {
                                if (method.hasBody()) {
                                    entryPoints.add(method);
                                }
                            }
                        });

        CallGraphAlgorithm algorithm = getCallGraphAlgorithm(view);
        CallGraph callGraph =
                algorithm.initialize(
                        entryPoints.stream().map(SootClassMember::getSignature).toList());

        return new SootUpFrameworkScope(view, callGraph, entryPoints, dataFlowScope);
    }

    private CallGraphAlgorithm getCallGraphAlgorithm(JavaView view) {
        switch (callGraphAlgorithm) {
            case CHA -> {
                return new ClassHierarchyAnalysisAlgorithm(view);
            }
            default ->
                    throw new RuntimeException(
                            "SootUp does not support call graph algorithm " + callGraphAlgorithm);
        }
    }
}
