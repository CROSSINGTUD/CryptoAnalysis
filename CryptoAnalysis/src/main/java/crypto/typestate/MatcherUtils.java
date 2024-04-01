package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Val;
import boomerang.scene.WrappedClass;
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
        for (CrySLMethod method : allMethods) {
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

        // TODO check for class hierarchy
        String cryslMethodName = cryslMethod.getMethodName();
        String cryslClassName = getDeclaringClassName(cryslMethod.getMethodName());
        String declaredClassName = declaredMethod.getDeclaringClass().getName();
        if (!cryslClassName.equals(declaredClassName)) {
            return false;
        }

        if (!matchParameters(cryslMethod.getParameters(), declaredMethod.getInvokeExpr().getArgs())) {
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

    public static boolean matchParameters(List<Map.Entry<String, String>> cryslParameters, List<Val> declaredParameters) {
        if (cryslParameters.size() != declaredParameters.size()) {
            return false;
        }

        for (int i = 0; i < cryslParameters.size(); i++) {
            if (cryslParameters.get(i).getValue().equals("AnyType")) {
                continue;
            }

            // Soot does not track generic types, so we are required to remove <...> from the parameter
            String cryslParameter = cryslParameters.get(i).getValue().replaceAll("<.*?>", "");
            String declaredParameter = declaredParameters.get(i).getType().toString();

            // null type corresponds to any type
            if (declaredParameter.equals("null_type")) {
                continue;
            }

            if (cryslParameter.equals(declaredParameter)) {
                continue;
            }

            if (!isSubtype(declaredParameter, cryslParameter)) {
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

    public static boolean isSubtype(String childClass, String parentClass) {
        // Check for primitive types
        if (!(Scene.v().containsClass(childClass) || Scene.v().containsClass(parentClass))) {
            return false;
        }

        SootClass child = Scene.v().getSootClass(childClass);
        SootClass parent = Scene.v().getSootClass(parentClass);

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
