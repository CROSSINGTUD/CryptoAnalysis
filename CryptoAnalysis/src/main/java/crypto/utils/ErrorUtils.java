/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.utils;

import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.errors.AbstractError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ErrorUtils {

    public static Map<String, Integer> getErrorCounts(
            Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        Map<String, Integer> errorCounts = new HashMap<>();

        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell :
                errorCollection.cellSet()) {
            for (AbstractError error : cell.getValue()) {
                String errorClass = error.getClass().getSimpleName();
                errorCounts.put(
                        errorClass,
                        errorCounts.containsKey(errorClass) ? errorCounts.get(errorClass) + 1 : 1);
            }
        }

        return errorCounts;
    }

    public static List<AbstractError> orderErrorsByLineNumber(Collection<AbstractError> errors) {
        List<AbstractError> errorList = new ArrayList<>(errors);
        errorList.sort(Comparator.comparingInt(AbstractError::getLineNumber));

        return errorList;
    }

    public static List<AnalysisSeedWithSpecification> orderSeedsByInitialStatement(
            Collection<AnalysisSeedWithSpecification> seeds) {
        List<AnalysisSeedWithSpecification> orderedSeeds = new ArrayList<>(seeds);
        orderedSeeds.sort(
                Comparator.comparing((AnalysisSeedWithSpecification s) -> s.getMethod().toString())
                        .thenComparingInt(s -> s.getInitialStatement().getLineNumber()));

        return orderedSeeds;
    }
}
