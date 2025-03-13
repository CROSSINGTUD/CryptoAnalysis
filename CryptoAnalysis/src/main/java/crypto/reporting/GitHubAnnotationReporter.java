package crypto.reporting;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crypto.utils.ErrorUtils;
import crysl.rule.CrySLRule;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GitHubAnnotationReporter extends CommandLineReporter {

    /**
     * Path to relate paths in the analyzed jar and the source tree.
     *
     * <p>Example: Relating the class {@code "example.IncompleteOperationErrorExample"} to the
     * source file {@code
     * "CryptoAnalysisTargets/CogniCryptDemoExample/src/main/java/example/IncompleteOperationErrorExample.java"}
     * requires a {@code basePath} of {@code
     * "CryptoAnalysisTargets/CogniCryptDemoExample/src/main/java"}.
     */
    private final String basePath;

    /**
     * The constructor to initialize all attributes. Since this class is abstract, all subclasses
     * have to call this constructor. TODO describe
     *
     * @param ruleset the ruleset used in the analysis
     */
    public GitHubAnnotationReporter(Collection<CrySLRule> ruleset) {
        super(ruleset);

        basePath = getInput("basePath");
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        System.out.println("::group::Annotations");

        // report errors on individual lines
        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell :
                errorCollection.cellSet()) {
            WrappedClass clazz = cell.getRowKey();
            Path path = classToSourcePath(clazz);

            boolean sourceExists = Files.exists(path);

            for (AbstractError error : cell.getValue()) {
                String title =
                        error.getClass().getSimpleName()
                                + " violating CrySL rule for "
                                + error.getRule().getClassName();

                Integer line = error.getErrorStatement().getStartLineNumber();
                if (line == -1) {
                    line = null;
                }

                Integer column = error.getErrorStatement().getStartColumnNumber();
                if (column == -1) {
                    column = null;
                }

                if (sourceExists) {
                    GitHubAnnotationReporter.ActionsAnnotation.printAnnotation(
                            error.toErrorMarkerString(),
                            title,
                            path.toString(),
                            line,
                            null,
                            column,
                            null);
                } else {
                    // fall back to a "global" annotation when the corresponding source file could
                    // not be found
                    StringBuilder message = new StringBuilder(error.toErrorMarkerString());

                    if (line != null) {
                        message.append(System.lineSeparator()).append("at line ").append(line);
                    }

                    if (column != null) {
                        message.append(System.lineSeparator()).append("at column ").append(column);
                    }

                    message.append(System.lineSeparator())
                            .append(System.lineSeparator())
                            .append("Corresponding source file could not be found (at ")
                            .append(path)
                            .append("). The base path might be set incorrectly.");

                    GitHubAnnotationReporter.ActionsAnnotation.printAnnotation(
                            message.toString(), title, null, null, null, null, null);
                }
            }
        }

        // report summary
        StringBuilder summary = new StringBuilder();

        summary.append(String.format("Number of CrySL rules: %s\n", ruleset.size()));
        summary.append(String.format("Number of Objects Analyzed: %s\n", seeds.size()));

        Map<String, Integer> errorCounts = ErrorUtils.getErrorCounts(errorCollection);
        int errorCount = errorCounts.values().stream().reduce(0, Integer::sum);
        summary.append(String.format("Number of violations: %s\n", errorCount));

        if (statistics != null) {
            summary.append("\n");
            summary.append("Total Analysis Time: ")
                    .append(statistics.getAnalysisTime())
                    .append("\n");
            summary.append("Call Graph Construction Time: ")
                    .append(statistics.getCallGraphTime())
                    .append("\n");
            summary.append("Typestate Analysis Time: ")
                    .append(statistics.getTypestateTime())
                    .append("\n");
            summary.append("Reachable Methods: ")
                    .append(statistics.getReachableMethods())
                    .append("\n");
            summary.append("Edges in Call Graph: ").append(statistics.getEdges()).append("\n");
            summary.append("Entry Points: ").append(statistics.getEntryPoints()).append("\n");
        }

        // GitHub only displays 10 error annotations and silently drops the rest.
        // https://github.com/orgs/community/discussions/26680
        // https://github.com/orgs/community/discussions/68471
        if (errorCount > 10) {
            String missingAnnotationsMessage =
                    "There are more violations than the GitHub annotations interface displays. Please check the log for additional violations.";

            System.out.println("::warning ::" + missingAnnotationsMessage);
            summary.append("\nWarning: ").append(missingAnnotationsMessage).append("\n");
        }

        setSummary(summary.toString());

        System.out.println("::endgroup::");

        super.createAnalysisReport(seeds, errorCollection, statistics);

        if (errorCount != 0) {
            System.exit(1);
        }
    }

    private Path classToSourcePath(WrappedClass clazz) {
        return Paths.get(
                basePath, clazz.getFullyQualifiedName().replace('.', File.separatorChar) + ".java");
    }

    /**
     * Gets an input variable when running as a GitHub Action. Input values are read from
     * environment variables with the format specified <a
     * href="https://docs.github.com/en/actions/creating-actions/metadata-syntax-for-github-actions#inputs">here</a>.
     * This method automatically handles converting the input name to the correct environment
     * variable name.
     *
     * @param name the name of the input parameter
     * @return the value of the input, or null if the input doesn't exist
     */
    private static String getInput(String name) {
        name = name.replaceAll(" ", "_");
        name = "INPUT_" + name.toUpperCase(Locale.ROOT);

        return System.getenv(name);
    }

    /**
     * Sets a <a
     * href="https://docs.github.com/actions/using-workflows/workflow-commands-for-github-actions#adding-a-job-summary">summary</a>
     * for this GitHub Actions step. Supports <a href="https://github.github.com/gfm/">GitHub
     * flavored Markdown</a>.
     *
     * @param summary the content of the summary
     */
    private static void setSummary(String summary) {
        String filePath = System.getenv("GITHUB_STEP_SUMMARY");
        try {
            Files.write(
                    Paths.get(filePath),
                    summary.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.SYNC);
        } catch (NullPointerException | IOException e) {
            throw new IllegalStateException(
                    "Exception while trying to write to GITHUB_STEP_SUMMARY file. Make sure you are running inside GitHub Actions.",
                    e);
        }
    }

    /**
     * This class is used to output GitHub Actions Annotations.
     *
     * <p>It should be used when executing inside a GitHub Actions workflow. It will print out
     * commands to the standard output that get interpreted as annotations by the GitHub Actions
     * runner. The corresponding <a
     * href="https://docs.github.com/en/actions/using-workflows/workflow-commands-for-github-actions">documentation</a>
     * is not very exhaustive, so some parts of this class were created by looking at the official
     * <a href="https://github.com/actions/toolkit/tree/main/packages/core">typescript package</a>
     * and the <a href="https://github.com/actions/runner/">actions runner source</a>.
     */
    private static class ActionsAnnotation {
        /**
         * Prints a GitHub Actions annotation.
         *
         * @param message the message that this annotation displays
         * @param title optional; the title of the annotation
         * @param file optional; the file in which the annotation should be displayed
         * @param startLine optional; the line at which the annotation should be displayed, starting
         *     at 1
         * @param endLine optional; the end (inclusive) of the multi-line annotation; {@code
         *     startLine} needs to be set as well when setting this value
         * @param startColumn optional; the column at which the annotation should be displayed,
         *     starting at 1; {@code startLine} needs to be set as well when setting this value; not
         *     supported for multi-line annotations. This means {@code endLine} needs to have the
         *     same value of {@code startLine} or be not set at all.
         * @param endColumn optional; the end (inclusive) column at which the annotation should be
         *     displayed
         * @throws IllegalArgumentException when the requirements specified above are not met
         */
        public static void printAnnotation(
                @Nonnull String message,
                @Nullable String title,
                @Nullable String file,
                @Nullable Integer startLine,
                @Nullable Integer endLine,
                @Nullable Integer startColumn,
                @Nullable Integer endColumn)
                throws IllegalArgumentException {
            // Implementing the validation logic from the GitHub Actions runner found at:
            // https://github.com/actions/runner/blob/21b49c542cdb8caf3c6217db6286fdefecb5f025/src/Runner.Worker/ActionCommandManager.cs#L672
            // We re-implement this here so it shows up as an exception instead of the annotation
            // being
            // modified by the
            // runner and only having a debug log message when running the action.
            Objects.requireNonNull(message);

            if (endLine != null && startLine == null) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startLine` needs to be set if `endLine` was provided.");
            }
            if (endColumn != null && startColumn == null) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startColumn` needs to be set if `endColumn` was provided.");
            }
            if (startColumn != null && startLine == null) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startLine` needs to be set if `startColumn` was provided.");
            }
            if (endLine != null && !Objects.equals(startLine, endLine) && startColumn != null) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startColumn` can't be set when `startLine` and `endLine` have different values.");
            }
            if (startLine != null && endLine != null && startLine > endLine) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startLine` can't have a higher value than `endLine`.");
            }
            if (startColumn != null && endColumn != null && startColumn > endColumn) {
                throw new IllegalArgumentException(
                        "Invalid annotation. `startColumn` can't have a higher value than `endColumn`.");
            }

            print(message, title, file, startLine, endLine, startColumn, endColumn);
        }

        /**
         * Prints the annotation to the standard output. The properties and the message are escaped
         * properly.
         */
        private static void print(
                @Nonnull String message,
                @Nullable String title,
                @Nullable String file,
                @Nullable Integer startLine,
                @Nullable Integer endLine,
                @Nullable Integer startColumn,
                @Nullable Integer endColumn) {
            StringBuilder builder = new StringBuilder();

            builder.append("::").append("error").append(' ');

            StringJoiner properties = new StringJoiner(",");
            if (title != null) {
                properties.add("title=" + escapeParameter(title));
            }
            if (file != null) {
                properties.add("file=" + escapeParameter(file));
            }
            if (startLine != null) {
                properties.add("line=" + escapeParameter(String.valueOf(startLine)));
            }
            if (endLine != null) {
                properties.add("endLine=" + escapeParameter(String.valueOf(endLine)));
            }
            if (startColumn != null) {
                properties.add("col=" + escapeParameter(String.valueOf(startColumn)));
            }
            if (endColumn != null) {
                properties.add("endColumn=" + escapeParameter(String.valueOf(endColumn)));
            }
            builder.append(properties);

            builder.append("::").append(escapeMessage(message));

            System.out.println(builder);
        }

        private static String escapeParameter(String property) {
            // not documented, but works and is used by the official toolkit
            // https://github.com/actions/toolkit/blob/a6bf8726aa7b78d4fc8111359cca5d538527b239/packages/core/src/command.ts#L87
            return property.replaceAll("%", "%25")
                    .replaceAll("\r", "%0D")
                    .replaceAll("\n", "%0A")
                    .replaceAll(":", "%3A")
                    .replaceAll(",", "%2C");
        }

        private static String escapeMessage(String data) {
            // not documented, but works and is used by the official toolkit
            // https://github.com/actions/toolkit/blob/a6bf8726aa7b78d4fc8111359cca5d538527b239/packages/core/src/command.ts#L80
            return data.replaceAll("%", "%25").replaceAll("\r", "%0D").replaceAll("\n", "%0A");
        }
    }
}
