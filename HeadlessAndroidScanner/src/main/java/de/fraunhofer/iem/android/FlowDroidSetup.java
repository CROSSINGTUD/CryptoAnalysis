package de.fraunhofer.iem.android;

import boomerang.scene.CallGraph;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import crypto.preanalysis.TransformerSetup;
import crypto.rules.CrySLRule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.options.Options;

public class FlowDroidSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDroidSetup.class);

    private final File apkFile;
    private final File platformDir;
    private SetupApplication flowDroid;

    public FlowDroidSetup(String apkFile, String platformDir) {
        this.apkFile = new File(apkFile);
        this.platformDir = new File(platformDir);
    }

    public void setupFlowDroid() {
        LOGGER.info("Setting up FlowDroid...");
        Stopwatch stopwatch = Stopwatch.createStarted();

        flowDroid = initializeFlowDroid();

        stopwatch.stop();
        LOGGER.info("FlowDroid setup done in {} ", stopwatch);
    }

    public CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        flowDroid.constructCallgraph();
        TransformerSetup.v().setupPreTransformer(rules);

        return new SootCallGraph();
    }

    private SetupApplication initializeFlowDroid() {
        InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.CHA);
        config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
        config.getAnalysisFileConfig().setAndroidPlatformDir(platformDir.getAbsolutePath());
        config.getAnalysisFileConfig().setTargetAPKFile(apkFile.getAbsolutePath());
        config.setMergeDexFiles(true);
        config.setTaintAnalysisEnabled(false);
        config.setEnableLineNumbers(true);
        // config.setSootIntegrationMode(InfoflowConfiguration.SootIntegrationMode.UseExistingInstance);

        SetupApplication app = new SetupApplication(config);
        app.setSootConfig(initSootConfig());

        return app;
    }

    private SootConfigForAndroid initSootConfig() {
        return new SootConfigForAndroid() {
            @Override
            public void setSootOptions(Options options, InfoflowConfiguration config) {
                options.setPhaseOption("jb.sils", "enabled:false");

                options.set_include(new ArrayList<>());
                options.set_exclude(List.of("android.*", "androidx.*"));
            }
        };
    }
}
