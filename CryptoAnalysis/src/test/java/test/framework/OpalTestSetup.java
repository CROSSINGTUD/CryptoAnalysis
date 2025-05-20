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
import boomerang.scope.opal.OpalFrameworkScope;
import boomerang.scope.opal.tac.OpalMethod;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.opalj.br.ClassFile;
import org.opalj.br.MethodDescriptor$;
import org.opalj.br.ObjectType;
import org.opalj.br.analyses.Project;
import org.opalj.br.analyses.Project$;
import org.opalj.br.analyses.cg.InitialEntryPointsKey;
import org.opalj.log.DevNullLogger$;
import org.opalj.log.GlobalLogContext$;
import org.opalj.log.OPALLogger;
import org.opalj.tac.cg.CHACallGraphKey$;
import org.opalj.tac.cg.CallGraph;
import scala.Option;
import scala.jdk.javaapi.CollectionConverters;

public class OpalTestSetup implements TestSetup {

    private Project<URL> project;
    private org.opalj.br.Method testMethod;

    @Override
    public void initialize(String classPath, String className, String testName) {
        OPALLogger.updateLogger(GlobalLogContext$.MODULE$, DevNullLogger$.MODULE$);

        File[] testClassFiles = loadTestClassFiles(classPath, className);
        project = Project.apply(testClassFiles, new File[0]);

        // Load the class that contains the test method
        Option<ClassFile> testClass =
                project.classFile(ObjectType.apply(className.replace(".", "/")));
        if (testClass.isEmpty()) {
            throw new RuntimeException("Could not find class " + className);
        }

        // Search the test method in the test class
        Option<org.opalj.br.Method> method =
                testClass
                        .get()
                        .findMethod(testName, MethodDescriptor$.MODULE$.NoArgsAndReturnVoid());
        if (method.isEmpty()) {
            throw new RuntimeException(
                    "Could not find method " + testName + " in class " + className);
        }

        // Update the project's config to set the test method as the (single) entry point
        // See
        // https://github.com/opalj/opal/blob/ff01c1c9e696946a88b090a52881a41445cf07f1/DEVELOPING_OPAL/tools/src/main/scala/org/opalj/support/info/CallGraph.scala#L406
        Config config = project.config();

        String key = InitialEntryPointsKey.ConfigKeyPrefix() + "entryPoints";
        List<Object> currentValues = config.getList(key).unwrapped();

        Map<String, String> configValue = new HashMap<>();
        configValue.put(
                "declaringClass", method.get().classFile().thisType().toJava().replace(".", "/"));
        configValue.put("name", method.get().name());

        currentValues.add(ConfigValueFactory.fromMap(configValue));
        config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues));
        config =
                config.withValue(
                        InitialEntryPointsKey.ConfigKeyPrefix() + "analysis",
                        ConfigValueFactory.fromAnyRef(
                                "org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder"));
        project = Project$.MODULE$.recreate(project, config, true);

        testMethod = method.get();
    }

    @Override
    public Method getTestMethod() {
        return OpalMethod.of(testMethod, project);
    }

    @Override
    public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
        CallGraph callGraph = project.get(CHACallGraphKey$.MODULE$);

        return new OpalFrameworkScope(
                project,
                callGraph,
                CollectionConverters.asScala(Set.of(testMethod)).toSet(),
                dataFlowScope);
    }

    private File[] loadTestClassFiles(String classPath, String testClass) {
        Path path = Path.of(classPath);

        try (Stream<Path> stream = Files.walk(path)) {
            Stream<File> classPathFiles = stream.filter(Files::isRegularFile).map(Path::toFile);

            String packageName = testClass.substring(0, testClass.lastIndexOf("."));
            Stream<File> testClassFiles =
                    classPathFiles.filter(c -> isTestClass(c, classPath, packageName));

            return testClassFiles.toArray(File[]::new);
        } catch (IOException e) {
            throw new RuntimeException("Could not read classpath: " + e.getMessage());
        }
    }

    private boolean isTestClass(File file, String classpath, String packageName) {
        String path = file.getPath().replace("/", ".").replace("\\", ".");
        String formattedClassPath = classpath.replace("/", ".").replace("\\", ".");
        String formattedPath =
                path.replace(formattedClassPath, "").replace(".class", "").substring(1);

        return formattedPath.startsWith(packageName);
    }
}
