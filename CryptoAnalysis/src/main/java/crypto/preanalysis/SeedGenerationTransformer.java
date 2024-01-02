package crypto.preanalysis;

import crypto.rules.CrySLMethod;
import crypto.rules.CrySLRule;
import crypto.rules.TransitionEdge;
import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PhaseOptions;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.UnitPatchingChain;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JimpleLocal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This transformer extends the jimple bodies, if CryptoAnalysis would not
 * be able to generate seeds for some objects. Minimal programs as
 * 'Cipher c = Cipher.getInstance("AES");' are transformed to jimple code
 * of the form 'staticinvoke Cipher.getInstance([...])'. This transformer
 * introduces a new JimpleLocal 'Cipher0' and transforms the statement to
 * Cipher0 = staticinvoke Cipher.getInstance([...]). Without this transformation,
 * CryptoAnalysis would not be able to generate a seed for c and would not check
 * for incorrect usages.
 */
public class SeedGenerationTransformer extends BodyTransformer {

    public static void setup(Collection<CrySLRule> rules) {
        final String phaseName = "jtp.sgtr";
        PackManager.v().getPack("jtp").remove(phaseName);
        PackManager.v().getPack("jtp").add(new Transform(phaseName, new SeedGenerationTransformer(rules)));
        PhaseOptions.v().setPhaseOption(phaseName, "on");
    }

    private final Collection<CrySLRule> rules;
    private int counter;

    public SeedGenerationTransformer(Collection<CrySLRule> rules) {
        this.rules = rules;
        this.counter = 0;
    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        if (body.getMethod().getDeclaringClass().getName().startsWith("java.")) {
            return;
        }
        if (!body.getMethod().getDeclaringClass().isApplicationClass()) {
            return;
        }

        UnitPatchingChain units = body.getUnits();
        units.snapshotIterator().forEachRemaining(unit -> {
            if (!(unit instanceof InvokeStmt)) {
                return;
            }

            InvokeStmt invokeStmt = (InvokeStmt) unit;
            if (!(invokeStmt.getInvokeExpr() instanceof StaticInvokeExpr)) {
                return;
            }

            SootClass declaringClass = invokeStmt.getInvokeExpr().getMethod().getDeclaringClass();
            CrySLRule rule = getRuleForClass(declaringClass);

            if (rule == null) {
                return;
            }

            if (isSeedGeneratingStatement(invokeStmt.getInvokeExpr(), rule)) {
                // Create new Variable
                JimpleLocal leftSide = new JimpleLocal(declaringClass.getShortName() + counter, declaringClass.getType());
                body.getLocals().add(leftSide);
                counter++;

                units.swapWith(unit, new JAssignStmt(leftSide, invokeStmt.getInvokeExpr()));
            }
        });
    }

    private CrySLRule getRuleForClass(SootClass sootClass) {
        for (CrySLRule rule : rules) {
            if (rule.getClassName().equals(sootClass.getName())) {
                return rule;
            }
        }
        return null;
    }

    private boolean isSeedGeneratingStatement(InvokeExpr invokeExpr, CrySLRule rule) {
        Collection<TransitionEdge> initialTransitions = rule.getUsagePattern().getInitialTransitions();
        SootMethod method = invokeExpr.getMethod();

        for (TransitionEdge transition : initialTransitions) {
            Collection<CrySLMethod> labels = transition.getLabel();

            for (CrySLMethod label : labels) {
                if (doMethodsMatch(method, rule.getClassName(), label)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doMethodsMatch(SootMethod sootMethod, String ruleClassName, CrySLMethod crySLMethod) {
        String declaringSootMethodClass = sootMethod.getDeclaringClass().getName();
        if (!ruleClassName.equals(declaringSootMethodClass)) {
            return false;
        }

        if (!sootMethod.getName().equals(crySLMethod.getShortMethodName())) {
            return false;
        }

        if (!doParametersMatch(sootMethod.getParameterTypes(), crySLMethod.getParameters())) {
            return false;
        }

        return true;
    }

    private boolean doParametersMatch(List<Type> parameterTypes, List<Entry<String, String>> parameterLabels) {
        if (parameterTypes.size() != parameterLabels.size()) {
            return false;
        }

        for (int i = 0; i < parameterTypes.size(); i++) {
            if (parameterLabels.get(i).getValue().equals("AnyType")) {
                continue;
            }

            Type parameterType = parameterTypes.get(i);
            // Soot does not track generic types, so we are required to remove <...> from the parameter
            String parameterLabelType = parameterLabels.get(i).getValue().replaceAll("[<].*?[>]", "");

            if (!parameterType.toString().equals(parameterLabelType)) {
                return false;
            }
        }
        return true;
    }
}
