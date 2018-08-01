package crypto.boomerang;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Stmt;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

import java.util.Collection;
import java.util.Collections;

public class CogniCryptBackwardEmptyCalleeFlow extends CogniCryptEmptyCalleeFlow {

    @Override
    protected Collection<? extends State> systemArrayCopyFlow(SootMethod caller, Stmt callSite, Val value,
                                                              Stmt returnSite) {
        if(value.equals(new Val(callSite.getInvokeExpr().getArg(2),caller))){
            Value arg = callSite.getInvokeExpr().getArg(0);
            return Collections.singleton(new Node<Statement, Val>(new Statement(returnSite, caller), new Val(arg,caller)));
        }
        return Collections.emptySet();
    }

    @Override
    protected Collection<? extends State> calleesExcludedFlow(SootMethod caller, Stmt callSite, Val value, Stmt returnSite) {
        return Collections.singleton(new Node<>(new Statement(returnSite, caller), value));
    }
}
