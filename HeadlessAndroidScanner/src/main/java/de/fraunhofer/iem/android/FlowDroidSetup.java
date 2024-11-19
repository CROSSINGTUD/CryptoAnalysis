package de.fraunhofer.iem.android;

import boomerang.scene.CallGraph;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Stopwatch;
import crypto.preanalysis.TransformerSetup;
import crypto.rules.CrySLRule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.EntryPoints;
import soot.G;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.infoflow.cfg.LibraryClassPatcher;
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

        initializeSoot();
        flowDroid = initializeFlowDroid();

        stopwatch.stop();
        LOGGER.info("FlowDroid setup done in {} ", stopwatch);
    }

    public CallGraph constructCallGraph(Collection<CrySLRule> rules) {
        flowDroid.constructCallgraph();
        TransformerSetup.v().setupPreTransformer(rules);

        return new SootCallGraph();
    }

    /**
     * Basic setup as done in <a
     * href="https://github.com/secure-software-engineering/FlowDroid/blob/ab9faf1e67c6163972ae7d5a82da9b5a9dd1fcf8/soot-infoflow-android/src/soot/jimple/infoflow/android/SetupApplication.java#L1218">FlowDroid</a>
     */
    private void initializeSoot() {
        G.reset();

        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_whole_program(true);
        Options.v().set_process_dir(Collections.singletonList(apkFile.getAbsolutePath()));
        Options.v().set_android_jars(platformDir.getAbsolutePath());
        Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
        Options.v().set_keep_offset(false);
        Options.v().set_keep_line_number(true);
        Options.v().set_throw_analysis(Options.throw_analysis_dalvik);
        Options.v().set_process_multiple_dex(true);
        Options.v().set_ignore_resolution_errors(true);
        Options.v().set_include(new ArrayList<>());
        Options.v().set_exclude(List.of("android.*", "androidx.*"));

        Options.v().setPhaseOption("jb.sils", "enabled:false");

        Options.v().set_soot_classpath(getClassPath());

        Scene.v().loadNecessaryClasses();
        Scene.v().setEntryPoints(getEntryPoints());

        LibraryClassPatcher patcher = new LibraryClassPatcher();
        patcher.patchLibraries();
    }

    private String getClassPath() {
        return Scene.v()
                .getAndroidJarPath(platformDir.getAbsolutePath(), apkFile.getAbsolutePath());
    }

    private List<SootMethod> getEntryPoints() {
        return new ArrayList<>(EntryPoints.v().methodsOfApplicationClasses());
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
        config.setSootIntegrationMode(InfoflowConfiguration.SootIntegrationMode.UseExistingInstance);

        SetupApplication app = new SetupApplication(config);
        //app.setSootConfig(initSootConfig());

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
