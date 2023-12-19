package crypto.reporting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class GitHubAnnotationReporter {
    /**
     * Gets an input variable when running as a GitHub Action. Input values are read from environment
     * variables with the format specified <a
     * href="https://docs.github.com/en/actions/creating-actions/metadata-syntax-for-github-actions#inputs">here</a>.
     * This method automatically handles converting the input name to the correct environment variable
     * name.
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
     * for this GitHub Actions step. Supports <a href="https://github.github.com/gfm/">GitHub flavored
     * Markdown</a>.
     *
     * @param summary the content of the summary
     */
    private static void setSummary(String summary) {
        String filePath = System.getenv("GITHUB_STEP_SUMMARY");
        try {
            Files.write(Paths.get(filePath), summary.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (NullPointerException | IOException e) {
            throw new IllegalStateException("Exception while trying to write to GITHUB_STEP_SUMMARY file. Make sure you are running inside GitHub Actions.", e);
        }
    }

    /**
     * This class is used to output GitHub Actions Annotations.
     *
     * <p>It should be used when executing inside a GitHub Actions workflow. It will print out commands
     * to the standard output that get interpreted as annotations by the GitHub Actions runner. The
     * corresponding <a
     * href="https://docs.github.com/en/actions/using-workflows/workflow-commands-for-github-actions">documentation</a>
     * is not very exhaustive, so some parts of this class were created by looking at the official <a
     * href="https://github.com/actions/toolkit/tree/main/packages/core">typescript package</a> and the
     * <a href="https://github.com/actions/runner/">actions runner source</a>.
     */
    private static class ActionsAnnotation {
        /**
         * Prints a GitHub Actions annotation.
         *
         * @param message     the message that this annotation displays
         * @param title       optional; the title of the annotation
         * @param file        optional; the file in which the annotation should be displayed
         * @param startLine   optional; the line at which the annotation should be displayed, starting at 1
         * @param endLine     optional; the end (inclusive) of the multi-line annotation; {@code startLine}
         *                    needs to be set as well when setting this value
         * @param startColumn optional; the column at which the annotation should be displayed, starting
         *                    at 1; {@code startLine} needs to be set as well when setting this value; not supported for
         *                    multi-line annotations. This means {@code endLine} needs to have the same value of {@code
         *                    startLine} or be not set at all.
         * @param endColumn   optional; the end (inclusive) column at which the annotation should be
         *                    displayed
         * @throws IllegalArgumentException when the requirements specified above are not met
         */
        public static void printAnnotation(@Nonnull String message, @Nullable String title, @Nullable String file, @Nullable Integer startLine, @Nullable Integer endLine, @Nullable Integer startColumn, @Nullable Integer endColumn) throws IllegalArgumentException {
            // Implementing the validation logic from the GitHub Actions runner found at:
            // https://github.com/actions/runner/blob/21b49c542cdb8caf3c6217db6286fdefecb5f025/src/Runner.Worker/ActionCommandManager.cs#L672
            // We re-implement this here so it shows up as an exception instead of the annotation being
            // modified by the
            // runner and only having a debug log message when running the action.
            Objects.requireNonNull(message);

            if (endLine != null && startLine == null) {
                throw new IllegalArgumentException("Invalid annotation. `startLine` needs to be set if `endLine` was provided.");
            }
            if (endColumn != null && startColumn == null) {
                throw new IllegalArgumentException("Invalid annotation. `startColumn` needs to be set if `endColumn` was provided.");
            }
            if (startColumn != null && startLine == null) {
                throw new IllegalArgumentException("Invalid annotation. `startLine` needs to be set if `startColumn` was provided.");
            }
            if (endLine != null && !Objects.equals(startLine, endLine) && startColumn != null) {
                throw new IllegalArgumentException("Invalid annotation. `startColumn` can't be set when `startLine` and `endLine` have different values.");
            }
            if (startLine != null && endLine != null && startLine > endLine) {
                throw new IllegalArgumentException("Invalid annotation. `startLine` can't have a higher value than `endLine`.");
            }
            if (startColumn != null && endColumn != null && startColumn > endColumn) {
                throw new IllegalArgumentException("Invalid annotation. `startColumn` can't have a higher value than `endColumn`.");
            }

            print(message, title, file, startLine, endLine, startColumn, endColumn);
        }

        /**
         * Prints the annotation to the standard output. The properties and the message are escaped
         * properly.
         */
        private static void print(@Nonnull String message, @Nullable String title, @Nullable String file, @Nullable Integer startLine, @Nullable Integer endLine, @Nullable Integer startColumn, @Nullable Integer endColumn) {
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
            return property.replaceAll("%", "%25").replaceAll("\r", "%0D").replaceAll("\n", "%0A").replaceAll(":", "%3A").replaceAll(",", "%2C");
        }

        private static String escapeMessage(String data) {
            // not documented, but works and is used by the official toolkit
            // https://github.com/actions/toolkit/blob/a6bf8726aa7b78d4fc8111359cca5d538527b239/packages/core/src/command.ts#L80
            return data.replaceAll("%", "%25").replaceAll("\r", "%0D").replaceAll("\n", "%0A");
        }
    }
}
