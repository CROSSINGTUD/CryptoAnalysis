/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import crypto.exceptions.CryptoAnalysisException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;

public class ClassPathHandler {

    private static URLClassLoader classLoader;

    private ClassPathHandler() {}

    public static void initialize(String additionalClassPath) {
        if (classLoader != null) {
            throw new IllegalStateException("ClassLoader is already initialized");
        }

        String[] paths = additionalClassPath.split(File.pathSeparator);
        Collection<URL> urls =
                Arrays.stream(paths)
                        .map(
                                p -> {
                                    try {
                                        return new File(p).toURI().toURL();
                                    } catch (MalformedURLException e) {
                                        throw new CryptoAnalysisException(
                                                "Error while parsing the classpath: "
                                                        + e.getMessage());
                                    }
                                })
                        .toList();

        classLoader =
                new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
    }

    public static URLClassLoader getClassLoader() {
        if (classLoader == null) {
            throw new IllegalStateException("ClassLoader is not initialized");
        }

        return classLoader;
    }

    public static void reset() {
        classLoader = null;
    }
}
