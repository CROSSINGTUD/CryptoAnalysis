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

        String cryslClassName = getDeclaringClassName(cryslMethod.getMethodName());
        String declaredClassName = declaredMethod.getDeclaringClass().getName();
        if (!cryslClassName.equals(declaredClassName) && !isSubtype(cryslClassName, declaredClassName)) {
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

            // For some reason Boomerang transforms boolean parameters to int values
            if (cryslParameter.equals("boolean") && declaredParameter.equals("int")) {
                continue;
            }

            // int and short correspond to the same type
            if (cryslParameter.equals("int") && declaredParameter.equals("short")) {
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

    private static boolean isSubtype(String childClass, String parentClass) {
        // Check for primitive types
        if (!(Scene.v().containsClass(childClass) || Scene.v().containsClass(parentClass))) {
            return false;
        }

        SootClass child = Scene.v().getSootClass(childClass);
        SootClass parent = Scene.v().getSootClass(parentClass);

        Collection<SootClass> fullHierarchy = getFullHierarchy(child, new HashSet<>());

        return fullHierarchy.contains(parent);
    }

    private static Collection<SootClass> getFullHierarchy(SootClass sourceClass, Set<SootClass> visited) {
        Set<SootClass> result = new HashSet<>();

        if (visited.contains(sourceClass)) {
            return result;
        }

        result.add(sourceClass);
        visited.add(sourceClass);

        // Super interfaces
        Collection<SootClass> interfaces = sourceClass.getInterfaces();
        for (SootClass intFace : interfaces) {
            result.addAll(getFullHierarchy(intFace, visited));
        }

        if (sourceClass.isInterface()) {
            // Super interfaces
            Collection<SootClass> superInterfaces = Scene.v().getActiveHierarchy().getSuperinterfacesOf(sourceClass);

            for (SootClass superInterface : superInterfaces) {
                result.addAll(getFullHierarchy(superInterface, visited));
            }
        } else {
            // Super classes
            Collection<SootClass> superClasses = Scene.v().getActiveHierarchy().getSuperclassesOf(sourceClass);

            for (SootClass superClass : superClasses) {
                result.addAll(getFullHierarchy(superClass, visited));
            }
        }

        return result;
    }
}
