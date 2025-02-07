package crypto.utils;

import crysl.rule.CrySLMethod;
import java.util.Collection;
import java.util.Map;

public class CrySLUtils {

    public static String getIndexAsString(int index) {
        return switch (index) {
            case -1 -> "Return value";
            case 0 -> "First parameter";
            case 1 -> "Second parameter";
            case 2 -> "Third parameter";
            case 3 -> "Fourth parameter";
            case 4 -> "Fifth parameter";
            case 5 -> "Sixth parameter";
            default -> (index + 1) + "th parameter";
        };
    }

    public static String formatMethodNames(Collection<CrySLMethod> methods) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        Collection<String> methodNames =
                methods.stream().map(CrySLUtils::formatMethodName).toList();
        String formattedNames = String.join(", ", methodNames);
        builder.append(formattedNames);

        builder.append("}");

        return builder.toString();
    }

    public static String formatMethodName(CrySLMethod method) {
        StringBuilder builder = new StringBuilder();
        builder.append(method.getShortMethodName());
        builder.append("(");

        if (!method.getParameters().isEmpty()) {
            for (Map.Entry<String, String> param : method.getParameters()) {
                builder.append(param.getValue());
                builder.append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append(")");
        return builder.toString();
    }
}
