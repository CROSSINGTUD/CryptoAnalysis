package crypto.preanalysis;

import java.util.HashMap;
import soot.BodyTransformer;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

public abstract class PreTransformer extends BodyTransformer {

    private boolean applied;

    public PreTransformer() {
        applied = false;
    }

    public final void apply() {
        if (isApplied()) {
            return;
        }

        /* Following the concept of Soot, each transformer is applied to each method
         * body individually. It may be more efficient to apply all transformers at once
         * to a method body.
         */
        ReachableMethods reachableMethods = Scene.v().getReachableMethods();
        QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
        while (listener.hasNext()) {
            SootMethod method = listener.next().method();
            if (method.hasActiveBody()) {
                internalTransform(method.getActiveBody(), "preTrans", new HashMap<>());
            }
        }
        applied = true;
    }

    public boolean isApplied() {
        return applied;
    }
}
