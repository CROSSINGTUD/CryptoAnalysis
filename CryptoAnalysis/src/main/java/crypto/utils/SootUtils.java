package crypto.utils;

import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleDeclaredMethod;
import boomerang.scene.jimple.JimpleType;
import boomerang.scene.jimple.JimpleVal;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.StringConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SootUtils {

    public static List<JimpleType> getParameterTypes(DeclaredMethod declaredMethod) {
        if (!(declaredMethod instanceof JimpleDeclaredMethod)) {
            throw new RuntimeException("DeclaredMethod is not a JimpleDeclaredMethod");
        }

        JimpleDeclaredMethod jimpleDeclaredMethod = (JimpleDeclaredMethod) declaredMethod;
        SootMethod sootMethod = (SootMethod) jimpleDeclaredMethod.getDelegate();

        List<JimpleType> result = new ArrayList<>();
        for (Type sootType : sootMethod.getParameterTypes()) {
            JimpleType jimpleType = new JimpleType(sootType);

            result.add(jimpleType);
        }

        return result;
    }

    public static JimpleType getParameterType(DeclaredMethod declaredMethod, int position) {
        if (!(declaredMethod instanceof JimpleDeclaredMethod)) {
            throw new RuntimeException("DeclaredMethod is not a JimpleDeclaredMethod");
        }

        // Extract the static parameter type
        JimpleDeclaredMethod jimpleDeclaredMethod = (JimpleDeclaredMethod) declaredMethod;
        SootMethod sootMethod = (SootMethod) jimpleDeclaredMethod.getDelegate();
        Type sootType = sootMethod.getParameterType(position);

        return new JimpleType(sootType);
    }

    /**
     * Returns whether parent is a super type of child, i.e. if they
     * are the same, child implements or extends parent transitively.
     *
     * @param childClass		the child to check
     * @param parentClass	the parent to check against
     *
     * @return true, if parent is a super type of child
     */
    public static boolean isSubtype(String childClass, String parentClass) {
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

    public static Val toStringConstant(String string, Method method) {
        Value value = StringConstant.v(string);
        return new JimpleVal(value, method);
    }

    public static Val toIntConstant(int intValue, Method method) {
        Value value = IntConstant.v(intValue);
        return new JimpleVal(value, method);
    }

    public static Val toLongConstant(long longValue, Method method) {
        Value value = LongConstant.v(longValue);
        return new JimpleVal(value, method);
    }

    public static Val toArraySize(Val arrayAllocVal, Method method) {
        JimpleVal jimpleVal = (JimpleVal) arrayAllocVal;
        Value value = jimpleVal.getDelegate();

        NewArrayExpr newArrayExpr = (NewArrayExpr) value;
        Value size = newArrayExpr.getSize();

        return new JimpleVal(size, method);
    }

}
