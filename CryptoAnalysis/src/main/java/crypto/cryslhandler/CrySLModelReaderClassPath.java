package crypto.cryslhandler;

import com.google.common.base.Splitter;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Appendable, virtual classpath extension, allowing to add custom elements, even after the actual classpath was already set.
 */
public class CrySLModelReaderClassPath {

    private static final AtomicBoolean sealed = new AtomicBoolean();
    private final static Set<URI> classPathElement;

    static {
        classPathElement = new HashSet<>();
        List<String> runtimeClassPath = Splitter.on(File.pathSeparatorChar).splitToList(System.getProperty("java.class.path"));
        for (String item : runtimeClassPath) {
            classPathElement.add(new File(item).toURI());
        }
    }

    /**
     * @return A copy of the current state of the classpath.
     */
    public static HashSet<URI> getClassPath() {
        return new HashSet<>(classPathElement);
    }

    /**
     * Adds an element to the virtual classpath.
     *
     * @param elementUri The element to add.
     * @throws IllegalStateException If the classpath was sealed for further inputs.
     */
    public static void addToClassPath(URI elementUri) {
        if (sealed.get())
            throw new IllegalStateException("Unable to add new element, after the classpath was sealed.");
        classPathElement.add(elementUri);
    }

    static void seal() {
        sealed.set(true);
    }
}
