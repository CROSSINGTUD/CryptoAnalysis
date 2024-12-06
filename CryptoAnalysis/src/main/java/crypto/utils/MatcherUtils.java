package crypto.utils;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Type;
import crysl.rule.CrySLMethod;
import crysl.rule.CrySLRule;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MatcherUtils {

    public static Collection<CrySLMethod> getMatchingCryslMethodsToDeclaredMethod(
            CrySLRule rule, DeclaredMethod declaredMethod) {
        Collection<CrySLMethod> matchingMethods = new HashSet<>();
        for (CrySLMethod method : rule.getEvents()) {
            if (matchCryslMethodAndDeclaredMethod(method, declaredMethod)) {
                matchingMethods.add(method);
            }
        }
        return matchingMethods;
    }

    public static boolean matchCryslMethodAndDeclaredMethod(
            CrySLMethod cryslMethod, DeclaredMethod declaredMethod) {
        // Compare method names
        String cryslName = cryslMethod.getShortMethodName();
        String declaredName = declaredMethod.getName();

        // Check for constructors: CryslMethod stores the actual class name, DeclaredMethod stores
        // '<init>'
        if (declaredMethod.isConstructor()) {
            // Strip the plain class name: java.lang.Object -> Object
            String className = declaredMethod.getDeclaringClass().getName();
            declaredName = className.substring(className.lastIndexOf(".") + 1);
        }

        if (!cryslName.equals(declaredName)) {
            return false;
        }

        // Compare class names
        String cryslClassName = cryslMethod.getDeclaringClassName();
        Type declaringClassType = declaredMethod.getDeclaringClass().getType();
        if (!cryslClassName.equals(declaringClassType.toString())
                && !declaringClassType.isSupertypeOf(cryslClassName)) {
            return false;
        }

        // Compare parameters
        List<Map.Entry<String, String>> cryslParameters = cryslMethod.getParameters();
        List<Type> declaredParameters = new ArrayList<>(declaredMethod.getParameterTypes());

        return matchParameters(cryslParameters, declaredParameters);
    }

    public static boolean matchParameters(
            List<Map.Entry<String, String>> cryslParameters, List<Type> declaredParameters) {
        if (cryslParameters.size() != declaredParameters.size()) {
            return false;
        }

        for (int i = 0; i < cryslParameters.size(); i++) {
            if (cryslParameters.get(i).getValue().equals(CrySLMethod.ANY_TYPE)) {
                continue;
            }

            // Soot does not track generic types, so we are required to remove <...> from the
            // parameter
            String cryslParameter = cryslParameters.get(i).getValue().replaceAll("<.*?>", "");
            Type parameterType = declaredParameters.get(i);
            String declaredParameter = parameterType.toString();

            // TODO Deal with null type
            // null type corresponds to any type
            if (declaredParameter.equals("null_type")) {
                continue;
            }

            if (cryslParameter.equals(declaredParameter)) {
                continue;
            }

            if (!parameterType.isSupertypeOf(cryslParameter)) {
                return false;
            }
        }
        return true;
    }
}
