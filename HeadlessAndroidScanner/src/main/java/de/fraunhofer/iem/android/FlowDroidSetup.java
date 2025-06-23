/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.fraunhofer.iem.android;

import boomerang.scope.DataFlowScope;
import boomerang.scope.soot.BoomerangPretransformer;
import com.google.common.base.Stopwatch;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisSootScope;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

public class FlowDroidSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDroidSetup.class);

    private final File apkFile;
    private final File platformDir;
    private final AndroidSettings.CallGraphAlgorithm cgAlgorithm;
    private SetupApplication flowDroid;

    public FlowDroidSetup(
            String apkFile, String platformDir, AndroidSettings.CallGraphAlgorithm cgAlgorithm) {
        this.apkFile = new File(apkFile);
        this.platformDir = new File(platformDir);
        this.cgAlgorithm = cgAlgorithm;
    }

    public void setupFlowDroid() {
        LOGGER.info("Setting up FlowDroid...");
        Stopwatch stopwatch = Stopwatch.createStarted();

        flowDroid = initializeFlowDroid();

        stopwatch.stop();
        LOGGER.info("FlowDroid setup done in {} ", stopwatch);
    }

    public CryptoAnalysisScope createFrameworkScope(DataFlowScope dataFlowScope) {
        flowDroid.constructCallgraph();

        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();

        CallGraph callGraph = Scene.v().getCallGraph();
        Collection<SootMethod> entryPoints = Scene.v().getEntryPoints();
        return new CryptoAnalysisSootScope(Scene.v(), callGraph, entryPoints, dataFlowScope);
    }

    private SetupApplication initializeFlowDroid() {
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();

        InfoflowConfiguration.CallgraphAlgorithm algorithm = initCallGraphAlgorithm();
        config.setCallgraphAlgorithm(algorithm);
        config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        config.getAnalysisFileConfig().setAndroidPlatformDir(platformDir);
        config.getAnalysisFileConfig().setTargetAPKFile(apkFile);
        config.setMergeDexFiles(true);
        config.setTaintAnalysisEnabled(false);
        config.setEnableLineNumbers(true);
        // config.setSootIntegrationMode(InfoflowConfiguration.SootIntegrationMode.UseExistingInstance);

        SetupApplication app = new SetupApplication(config);
        app.setSootConfig(initSootConfig());

        return app;
    }

    private InfoflowConfiguration.CallgraphAlgorithm initCallGraphAlgorithm() {
        return switch (cgAlgorithm) {
            case CHA -> InfoflowConfiguration.CallgraphAlgorithm.CHA;
            case RTA -> InfoflowConfiguration.CallgraphAlgorithm.RTA;
            case VTA -> InfoflowConfiguration.CallgraphAlgorithm.VTA;
            case SPARK -> InfoflowConfiguration.CallgraphAlgorithm.SPARK;
        };
    }

    private SootConfigForAndroid initSootConfig() {
        return new SootConfigForAndroid() {
            @Override
            public void setSootOptions(Options options, InfoflowConfiguration config) {
                options.setPhaseOption("jb", "use-original-names:true");
                options.setPhaseOption("jb.sils", "enabled:false");
                options.setPhaseOption("jb.dae", "enabled:false");

                options.set_include(new ArrayList<>());
                options.set_exclude(List.of("android.*", "androidx.*"));
            }
        };
    }
}
