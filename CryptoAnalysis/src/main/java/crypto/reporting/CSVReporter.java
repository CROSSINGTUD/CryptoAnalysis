/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.reporting;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.listener.AnalysisStatistics;
import crypto.utils.ErrorUtils;
import crysl.rule.CrySLRule;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVReporter extends Reporter {

    private static final String FILE_ENDING = ".csv";
    private static final String CSV_SEPARATOR = ";";

    /** Headers for the errors */
    private enum Headers {
        ErrorId,
        ErrorType,
        PrecedingErrors,
        SubsequentErrors,
        ViolatedRule,
        Class,
        Method,
        Statement,
        LineNumber,
        Message
    }

    public CSVReporter(String outputDir, Collection<CrySLRule> ruleset) throws IOException {
        super(outputDir, ruleset);
    }

    @Override
    public void createAnalysisReport(
            Collection<IAnalysisSeed> seeds,
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection,
            AnalysisStatistics statistics) {
        List<String> lineContents = new ArrayList<>();

        for (WrappedClass wrappedClass : errorCollection.rowKeySet()) {
            String className = wrappedClass.getFullyQualifiedName();

            for (Map.Entry<Method, Set<AbstractError>> entry :
                    errorCollection.row(wrappedClass).entrySet()) {
                String methodName = entry.getKey().toString();

                List<AbstractError> orderedErrors =
                        ErrorUtils.orderErrorsByLineNumber(entry.getValue());
                for (AbstractError error : orderedErrors) {
                    String preErrors =
                            String.join(
                                    ",",
                                    error.getPrecedingErrors().stream()
                                            .map(e -> String.valueOf(e.getErrorId()))
                                            .toList());
                    String subErrors =
                            String.join(
                                    ",",
                                    error.getSubsequentErrors().stream()
                                            .map(e -> String.valueOf(e.getErrorId()))
                                            .toList());

                    List<String> lineFields =
                            Arrays.asList(
                                    String.valueOf(error.getErrorId()), // id
                                    error.getClass().getSimpleName(), // error type
                                    preErrors, // preceding errors
                                    subErrors, // subsequent errors
                                    error.getRule().getClassName(), // violating class
                                    className, // class
                                    methodName, // method
                                    error.getErrorStatement().toString(), // statement
                                    String.valueOf(error.getLineNumber()), // line number
                                    sanitizeMessage(error.toErrorMarkerString()) // message
                                    );

                    String line = Joiner.on(CSV_SEPARATOR).join(lineFields);
                    lineContents.add(line);
                }
            }
        }

        writeToFile(lineContents);
    }

    private String sanitizeMessage(String message) {
        return message.replace("\\", "\\\\").replace("\n", "\\n").replace("\t", "\\t");
    }

    public void writeToFile(List<String> contents) {
        List<String> headers = new ArrayList<>();

        for (CSVReporter.Headers h : CSVReporter.Headers.values()) {
            headers.add(h.toString());
        }

        String header = Joiner.on(CSV_SEPARATOR).join(headers);

        String fileName = outputFile.getAbsolutePath() + File.separator + REPORT_NAME + FILE_ENDING;
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(header + "\n");

            // Write headers line by line
            for (String line : contents) {
                writer.write(line + "\n");
            }

            LOGGER.info("CSV report generated in {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Could not write CSV report to {}: {}", fileName, e.getMessage());
        }
    }
}
