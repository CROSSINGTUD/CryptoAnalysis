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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.Type;
import crypto.analysis.ClassPathHandler;
import crysl.rule.CrySLMethod;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MatcherUtils {

    public static Collection<CrySLMethod> getMatchingCryslMethodsToDeclaredMethod(
            Collection<CrySLMethod> events, DeclaredMethod declaredMethod) {
        Collection<CrySLMethod> matchingMethods = new HashSet<>();
        for (CrySLMethod method : events) {
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
            String className = declaredMethod.getDeclaringClass().getFullyQualifiedName();
            declaredName = className.substring(className.lastIndexOf(".") + 1);
        }

        if (!cryslName.equals(declaredName)) {
            return false;
        }

        // Compare class names
        String cryslClassName = cryslMethod.getDeclaringClassName();
        String declaredClassName = declaredMethod.getDeclaringClass().getFullyQualifiedName();
        if (!isTypeOrSubType(cryslClassName, declaredClassName)) {
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

    public static boolean isTypeOrSubType(String subType, String superType) {
        try {
            Class<?> subClass = Class.forName(subType, true, ClassPathHandler.getClassLoader());
            Class<?> superClass = Class.forName(superType, true, ClassPathHandler.getClassLoader());

            return superClass.isAssignableFrom(subClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
