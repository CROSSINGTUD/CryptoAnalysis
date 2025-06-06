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
import boomerang.scope.soot.BoomerangPretransformer;
import boomerang.scope.soot.SootFrameworkScope;
import boomerang.scope.soot.jimple.JimpleMethod;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

public class SootTestSetup implements TestSetup {

    private SootMethod testMethod = null;

    @Override
    public void initialize(String classPath, String className, String testName) {
        G.reset();

        Options.v().set_whole_program(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_output_format(Options.output_format_none);

        Options.v().setPhaseOption("cg.cha", "on");

        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb.dtr", "enabled:false");
        Options.v().setPhaseOption("jb.sils", "enabled:false");
        Options.v().setPhaseOption("jb.dae", "enabled:false");
        Options.v().setPhaseOption("jb.uce", "enabled:false");
        Options.v().setPhaseOption("jb.cbf", "enabled:false");

        Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + classPath);

        SootClass sootTestCaseClass = Scene.v().forceResolve(className, SootClass.BODIES);
        sootTestCaseClass.setApplicationClass();

        String signature = "void " + testName + "()";
        testMethod = sootTestCaseClass.getMethod(signature);

        if (testMethod == null) {
            throw new RuntimeException("Could not load test method " + signature);
        }

        Scene.v().loadNecessaryClasses();

        List<SootMethod> entryPoints = new ArrayList<>();
        for (SootClass sootClass : Scene.v().getClasses()) {
            if (sootClass.getName().startsWith(sootTestCaseClass.getName())) {
                sootClass.setApplicationClass();

                for (SootMethod method : sootClass.getMethods()) {
                    if (method.isStaticInitializer()) {
                        entryPoints.add(method);
                    }
                }
            }
        }

        entryPoints.add(testMethod);
        Scene.v().setEntryPoints(entryPoints);
    }

    @Override
    public Method getTestMethod() {
        return JimpleMethod.of(testMethod, Scene.v());
    }

    @Override
    public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
        PackManager.v().getPack("cg").apply();

        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();

        return new SootFrameworkScope(
                Scene.v(),
                Scene.v().getCallGraph(),
                Collections.singleton(testMethod),
                dataFlowScope);
    }
}
