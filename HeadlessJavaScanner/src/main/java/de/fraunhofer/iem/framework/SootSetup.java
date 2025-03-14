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
import boomerang.scope.soot.BoomerangPretransformer;
import boomerang.scope.soot.SootFrameworkScope;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import crypto.exceptions.CryptoAnalysisException;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import soot.EntryPoints;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

public class SootSetup extends FrameworkSetup {

    private final String sootClassPath;
    private final DataFlowScope dataFlowScope;

    public SootSetup(
            String applicationPath,
            ScannerSettings.CallGraphAlgorithm algorithm,
            String sootClassPath,
            DataFlowScope dataFlowScope) {
        super(applicationPath, algorithm);

        this.sootClassPath = sootClassPath;
        this.dataFlowScope = dataFlowScope;
    }

    @Override
    public void initializeFramework() {
        LOGGER.info("Setting up Soot...");
        Stopwatch watch = Stopwatch.createStarted();

        G.reset();
        Options.v().set_whole_program(true);

        switch (callGraphAlgorithm) {
            case CHA:
                Options.v().setPhaseOption("cg.cha", "on");
                break;
            case SPARK_LIB:
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg", "library:any-subtype");
                break;
            case SPARK:
                Options.v().setPhaseOption("cg.spark", "on");
                break;
            default:
                throw new CryptoAnalysisException(
                        "Call Graph algorithm "
                                + callGraphAlgorithm.name()
                                + " for Soot not supported");
        }

        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_keep_line_number(true);

        /* This phase is new in soot 4.3.0 and manipulates the jimple code in a
         * way that CryptoAnalysis is not able to find seeds in some cases (see
         * https://github.com/CROSSINGTUD/CryptoAnalysis/issues/293). Therefore,
         * it is disabled.
         */
        Options.v().setPhaseOption("jb.sils", "enabled:false");
        // Options.v().setPhaseOption("jb", "use-original-names:true");

        // JAVA 8
        if (getJavaVersion() < 9) {
            Options.v().set_prepend_classpath(true);
            Options.v().set_soot_classpath(sootClassPath + File.pathSeparator + pathToJCE());
        }
        // JAVA VERSION 9 && IS A CLASSPATH PROJECT
        else if (getJavaVersion() >= 9 && !isModularProject(applicationPath)) {
            Options.v()
                    .set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + sootClassPath);
        }
        // JAVA VERSION 9 && IS A MODULEPATH PROJECT
        else if (getJavaVersion() >= 9 && isModularProject(applicationPath)) {
            Options.v().set_prepend_classpath(true);
            Options.v().set_soot_modulepath(sootClassPath);
        }

        Options.v().set_process_dir(Arrays.asList(applicationPath.split(File.pathSeparator)));
        Options.v().set_include(new ArrayList<>());
        Options.v().set_exclude(new ArrayList<>());
        Options.v().set_full_resolver(true);
        Scene.v().loadNecessaryClasses();
        Scene.v().setEntryPoints(getEntryPoints());

        watch.stop();
        LOGGER.info("Soot setup done in {}", watch);
    }

    @Override
    public FrameworkScope createFrameworkScope() {
        PackManager.v().getPack("cg").apply();

        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();

        CallGraph callGraph = Scene.v().getCallGraph();
        Collection<SootMethod> entryPoints = Scene.v().getEntryPoints();
        return new SootFrameworkScope(Scene.v(), callGraph, entryPoints, dataFlowScope);
    }

    private List<SootMethod> getEntryPoints() {
        List<SootMethod> entryPoints = Lists.newArrayList();

        entryPoints.addAll(EntryPoints.v().application());
        entryPoints.addAll(EntryPoints.v().methodsOfApplicationClasses());

        return entryPoints;
    }

    private static String pathToJCE() {
        // When whole program mode is disabled, the classpath misses jce.jar
        return System.getProperty("java.home")
                + File.separator
                + "lib"
                + File.separator
                + "jce.jar";
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    private boolean isModularProject(String applicationPath) {
        File dirName = new File(applicationPath);
        String moduleFile = dirName + File.separator + "module-info.class";
        return new File(moduleFile).exists();
    }
}
