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
import com.google.common.base.Stopwatch;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisOpalScope;
import de.fraunhofer.iem.cryptoanalysis.scope.CryptoAnalysisScope;
import de.fraunhofer.iem.scanner.ScannerSettings;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opalj.br.Method;
import org.opalj.br.analyses.Project;
import org.opalj.br.analyses.Project$;
import org.opalj.br.analyses.cg.InitialEntryPointsKey;
import org.opalj.log.DevNullLogger$;
import org.opalj.log.GlobalLogContext$;
import org.opalj.log.OPALLogger;
import org.opalj.tac.cg.AllocationSiteBasedPointsToCallGraphKey$;
import org.opalj.tac.cg.CHACallGraphKey$;
import org.opalj.tac.cg.CallGraph;
import org.opalj.tac.cg.CallGraphKey;
import org.opalj.tac.cg.RTACallGraphKey$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.ArraySeq;

public class OpalSetup extends FrameworkSetup {

    private Project<URL> project;

    public OpalSetup(
            String applicationPath,
            ScannerSettings.CallGraphAlgorithm algorithm,
            DataFlowScope dataFlowScope) {
        super(applicationPath, algorithm, dataFlowScope);
    }

    public Project<URL> getProject() {
        return project;
    }

    @Override
    public void initializeFramework() {
        OPALLogger.updateLogger(GlobalLogContext$.MODULE$, DevNullLogger$.MODULE$);

        LOGGER.info("Setting up Opal...");
        Stopwatch watch = Stopwatch.createStarted();

        project = Project.apply(new File(applicationPath));

        // Update the project's config to set the test method as the (single) entry point
        // See
        // https://github.com/opalj/opal/blob/ff01c1c9e696946a88b090a52881a41445cf07f1/DEVELOPING_OPAL/tools/src/main/scala/org/opalj/support/info/CallGraph.scala#L406
        Config config = project.config();
        ArraySeq<Method> methods = project.allMethodsWithBody();

        Config updatedConfig = updateConfigWithEntryPoints(config, methods);
        project = Project$.MODULE$.recreate(project, updatedConfig, true);

        watch.stop();
        LOGGER.info("Opal setup done in {}", watch);
    }

    @Override
    public CryptoAnalysisScope createFrameworkScope() {
        CallGraphKey callGraphKey = getCallGraphAlgorithm();
        CallGraph callGraph = project.get(callGraphKey);

        ArraySeq<Method> entryPoints = project.allMethodsWithBody();
        return new CryptoAnalysisOpalScope(project, callGraph, entryPoints.toSet(), dataFlowScope);
    }

    public Config updateConfigWithEntryPoints(Config config, Seq<Method> entryPoints) {
        String key = InitialEntryPointsKey.ConfigKeyPrefix() + "entryPoints";
        List<Object> currentValues = config.getList(key).unwrapped();

        Iterator<Method> methodIterator = entryPoints.iterator();
        while (methodIterator.hasNext()) {
            Method method = methodIterator.next();

            Map<String, String> configValue = new HashMap<>();
            configValue.put(
                    "declaringClass", method.classFile().thisType().toJava().replace(".", "/"));
            configValue.put("name", method.name());

            currentValues.add(ConfigValueFactory.fromMap(configValue));
            config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues));
        }

        config =
                config.withValue(
                        InitialEntryPointsKey.ConfigKey() + "analysis",
                        ConfigValueFactory.fromAnyRef(
                                "org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder"));

        return config;
    }

    private CallGraphKey getCallGraphAlgorithm() {
        switch (callGraphAlgorithm) {
            case CHA -> {
                return CHACallGraphKey$.MODULE$;
            }
            case RTA -> {
                return RTACallGraphKey$.MODULE$;
            }
            case ALLOC_SITE_BASED -> {
                return AllocationSiteBasedPointsToCallGraphKey$.MODULE$;
            }
            default ->
                    throw new RuntimeException(
                            "Opal does not support call graph algorithm " + callGraphAlgorithm);
        }
    }
}
