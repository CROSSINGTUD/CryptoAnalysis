/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package test.framework;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.sootup.BoomerangPreInterceptor;
import boomerang.scope.sootup.SootUpFrameworkScope;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SootClassMember;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpTestSetup implements TestSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(SootUpTestSetup.class);

    private JavaView view;
    private JavaSootMethod testMethod;

    @Override
    public void initialize(String classPath, String className, String testName) {
        LOGGER.info("Setting up SootUp...");

        List<BodyInterceptor> interceptors = List.of(new BoomerangPreInterceptor());

        AnalysisInputLocation inputLocation =
                new JavaClassPathAnalysisInputLocation(
                        classPath, SourceType.Application, interceptors);
        view = new JavaView(inputLocation);

        // Load the test class
        ClassType classType = view.getIdentifierFactory().getClassType(className);
        Optional<JavaSootClass> testClass = view.getClass(classType);
        if (testClass.isEmpty()) {
            throw new RuntimeException("Could not find class " + className);
        }

        // Load the test method
        MethodSignature testMethodSignature =
                view.getIdentifierFactory()
                        .getMethodSignature(className, testName, "void", Collections.emptyList());
        Optional<JavaSootMethod> testMethodOpt =
                testClass.get().getMethod(testMethodSignature.getSubSignature());
        if (testMethodOpt.isEmpty()) {
            throw new RuntimeException(
                    "Could not find method " + testName + " in class " + className);
        }

        testMethod = testMethodOpt.get();
    }

    @Override
    public Method getTestMethod() {
        return JimpleUpMethod.of(testMethod, view);
    }

    @Override
    public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
        Collection<JavaSootMethod> entryPoints = Set.of(testMethod);
        CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);

        CallGraph callGraph =
                cha.initialize(entryPoints.stream().map(SootClassMember::getSignature).toList());
        return new SootUpFrameworkScope(view, callGraph, entryPoints, dataFlowScope);
    }
}
