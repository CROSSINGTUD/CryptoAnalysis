package crypto.cryslhandler;

import com.google.common.base.Splitter;
import crypto.HeadlessCryptoScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Appendable, virtual classpath extension, allowing to add custom elements, even after the actual classpath was already set.
 */
public class CrySLModelReaderClassPath {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlessCryptoScanner.class);

    private final static URL[] javaRuntimeClassPath;
    private final Collection<URL> virtualClassPath;

    public static final CrySLModelReaderClassPath JAVA_CLASS_PATH = new CrySLModelReaderClassPath();

    static {

        Collection<String> runtimeClassPath = Splitter.on(File.pathSeparatorChar).splitToList(System.getProperty("java.class.path"));
        javaRuntimeClassPath = runtimeClassPath.stream()
                .map((it) -> {
                    try {
                        return new File(it).toURI().toURL();
                    } catch (MalformedURLException e) {
                        LOGGER.warn("Unable to get URL from java classpath element '" + it + "'.", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(URL[]::new);
    }

    /**
     * Initializes a new instance with the current runtime's classpath.
     */
    private CrySLModelReaderClassPath() {
        virtualClassPath = Collections.emptySet();
    }

    /**
     * Initializes a new instance with the current runtime's classpath and the elements the given set.
     * 
     * @param virtualClassPath the virtual class path
     */
    public CrySLModelReaderClassPath(Collection<URL> virtualClassPath) {
        this.virtualClassPath = virtualClassPath;
    }

    /**
     * Creates a new instance with the current runtime's classpath and the elements the given set.
     * 
     * @param virtualClassPath the virtual class path
     * @return the model reader class path instance for the given virtual class path
     */
    public static CrySLModelReaderClassPath createFromPaths(Collection<Path> virtualClassPath) {
        Collection<URL> urlClassPath = virtualClassPath.stream()
                .map((it) -> {
                    try {
                        return it.toAbsolutePath().toUri().toURL();
                    } catch (MalformedURLException e) {
                        LOGGER.warn("Unable to get URL from virtual classpath element '" + it.toAbsolutePath() + "'.", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return new CrySLModelReaderClassPath(urlClassPath);
    }

    /**
     * Creates a new instance with the current runtime's classpath and the elements the given set.
     * 
     * @param virtualClassPath the virtual class path
     * @return the model reader class path instance for the given virtual class path
     */
    public static CrySLModelReaderClassPath createFromURIs(Collection<URI> virtualClassPath) {
        Collection<URL> urlClassPath = virtualClassPath.stream()
                .map((it) -> {
                    try {
                        return it.toURL();
                    } catch (MalformedURLException e) {
                        LOGGER.warn("Unable to get URL from virtual classpath element '" + it + "'.", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return new CrySLModelReaderClassPath(urlClassPath);
    }

    /**
     * Get the class path.
     * 
     * @return A copy of the current state of the classpath.
     */
    public URL[] getClassPath() {
        if (virtualClassPath.isEmpty())
            return javaRuntimeClassPath.clone();
        Collection<URL> classPath = new HashSet<>();
        classPath.addAll(Arrays.asList(javaRuntimeClassPath));
        classPath.addAll(virtualClassPath);
        return classPath.toArray(new URL[0]);
    }
}
