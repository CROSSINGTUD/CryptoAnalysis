package crypto.utils;

import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ErrorUtils {

    public static Map<String, Integer> getErrorCounts(Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        Map<String, Integer> errorCounts = new HashMap<>();

        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell : errorCollection.cellSet()) {
            for (AbstractError error : cell.getValue()) {
                String errorClass = error.getClass().getSimpleName();
                errorCounts.put(errorClass, errorCounts.containsKey(errorClass) ? errorCounts.get(errorClass) + 1 : 1);
            }
        }

        return errorCounts;
    }

    public static int getErrorsOfTypeInMethod(String method, Class<?> errorClass, Table<WrappedClass, Method, Set<AbstractError>> errorCollection) {
        int result = 0;

        for (Table.Cell<WrappedClass, Method, Set<AbstractError>> cell : errorCollection.cellSet()) {
            String methodName = cell.getColumnKey().toString();

            if (!methodName.equals(method)) {
                continue;
            }

            for (AbstractError error : cell.getValue()) {
                String errorName = error.getClass().getSimpleName();

                if (errorName.equals(errorClass.getSimpleName())) {
                    result++;
                }
            }
        }

        return result;
    }

    public static List<AbstractError> orderErrorsByLineNumber(Collection<AbstractError> errors) {
        List<AbstractError> errorList = new ArrayList<>(errors);
        errorList.sort(Comparator.comparingInt(AbstractError::getLineNumber));

        return errorList;
    }
}
