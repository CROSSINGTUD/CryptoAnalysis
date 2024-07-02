package crypto.utils;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.WrappedClass;
import boomerang.scene.jimple.JimpleType;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import crypto.rules.TransitionEdge;
import soot.Scene;
import soot.SootClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatcherUtils {

    public static Collection<CrySLMethod> getMatchingCryslMethodsToDeclaredMethod(CrySLRule rule, DeclaredMethod declaredMethod) {
        Set<CrySLMethod> allMethods = new HashSet<>();
        for (TransitionEdge edge : rule.getUsagePattern().getAllTransitions()) {
            allMethods.addAll(edge.getLabel());
        }

        Set<CrySLMethod> matchingMethods = new HashSet<>();
        for (CrySLMethod method : rule.getEvents()) {
            if (matchCryslMethodAndDeclaredMethod(method, declaredMethod)) {
                matchingMethods.add(method);
            }
        }
        return matchingMethods;
    }

    public static boolean matchCryslMethodAndDeclaredMethod(CrySLMethod cryslMethod, DeclaredMethod declaredMethod) {
        // Compare method names
        String cryslName = cryslMethod.getShortMethodName();
        String declaredName = declaredMethod.getName();

        // Check for constructors: CryslMethod stores the actual class name, DeclaredMethod stores '<init>'
        if (declaredName.equals("<init>")) {
            WrappedClass wrappedClass = declaredMethod.getDeclaringClass();
            declaredName = ((SootClass) wrappedClass.getDelegate()).getShortName();
        }

        if (!cryslName.equals(declaredName)) {
            return false;
        }

        String cryslClassName = getDeclaringClassName(cryslMethod.getMethodName());
        String declaredClassName = declaredMethod.getDeclaringClass().getName();
        if (!cryslClassName.equals(declaredClassName) && !SootUtils.isSubtype(cryslClassName, declaredClassName)) {
            return false;
        }

        List<Map.Entry<String, String>> cryslParameters = cryslMethod.getParameters();
        List<JimpleType> declaredParameters = SootUtils.getParameterTypes(declaredMethod);
        if (!matchParameters(cryslParameters, declaredParameters)) {
            return false;
        }

        return true;
    }

    private static String getDeclaringClassName(String cryslMethodName) {
        if (Scene.v().containsClass(cryslMethodName)) {
            return cryslMethodName;
        }
        return cryslMethodName.substring(0, cryslMethodName.lastIndexOf("."));
    }

    public static boolean matchParameters(List<Map.Entry<String, String>> cryslParameters, List<JimpleType> declaredParameters) {
        if (cryslParameters.size() != declaredParameters.size()) {
            return false;
        }

        for (int i = 0; i < cryslParameters.size(); i++) {
            if (cryslParameters.get(i).getValue().equals("AnyType")) {
                continue;
            }

            // Soot does not track generic types, so we are required to remove <...> from the parameter
            String cryslParameter = cryslParameters.get(i).getValue().replaceAll("<.*?>", "");
            String declaredParameter = declaredParameters.get(i).toString();

            // null type corresponds to any type
            if (declaredParameter.equals("null_type")) {
                continue;
            }

            if (cryslParameter.equals(declaredParameter)) {
                continue;
            }

            if (!SootUtils.isSubtype(declaredParameter, cryslParameter)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isSubtype(WrappedClass childClass, WrappedClass parentClass) {
        SootClass child = (SootClass) childClass.getDelegate();
        SootClass parent = (SootClass) parentClass.getDelegate();

        if (child.equals(parent))
            return true;

        if (child.isInterface()) {
            return parent.isInterface() &&
                    Scene.v().getActiveHierarchy().isInterfaceSubinterfaceOf(child, parent);
        }
        return Scene.v().getActiveHierarchy().isClassSubclassOf(child, parent)
                || child.getInterfaces().contains(parent);
    }
}
