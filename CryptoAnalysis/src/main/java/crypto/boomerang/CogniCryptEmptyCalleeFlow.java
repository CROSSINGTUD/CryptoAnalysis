package crypto.boomerang;

import boomerang.customize.EmptyCalleeFlow;
import boomerang.jimple.Val;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import wpds.interfaces.State;

import java.util.Collection;
import java.util.Collections;

public abstract class CogniCryptEmptyCalleeFlow extends EmptyCalleeFlow {

    @Override
    public Collection<? extends State> getEmptyCalleeFlow(SootMethod caller, Stmt callSite, Val value,
                                                          Stmt returnSite) {
        if(isSystemArrayCopy(callSite.getInvokeExpr().getMethod())){
            return systemArrayCopyFlow(caller, callSite, value, returnSite);
        }
        if (isMethodBodyExcluded(callSite.getInvokeExpr().getMethod())){
            return calleesExcludedFlow(caller, callSite, value, returnSite);
        }
        return Collections.emptySet();
    }

    protected boolean isMethodBodyExcluded(SootMethod method) {
        SootClass declaringClass = method.getDeclaringClass();
        return Scene.v().isExcluded(declaringClass);
    }

    protected abstract Collection<? extends State> calleesExcludedFlow(SootMethod caller, Stmt callSite, Val value,
                                                                       Stmt returnSite);
}
