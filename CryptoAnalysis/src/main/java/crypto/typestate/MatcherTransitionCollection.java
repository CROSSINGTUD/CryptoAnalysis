package crypto.typestate;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import typestate.TransitionFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MatcherTransitionCollection {

    private final StateMachineGraph smg;
    private final Set<LabeledMatcherTransition> transitions;
    private final Set<LabeledMatcherTransition> initialTransitions;

    private MatcherTransitionCollection(StateMachineGraph smg) {
        this.smg = smg;

        transitions = new HashSet<>();
        initialTransitions = new HashSet<>();

        initializeExistingTransitions();
        initializeErrorTransitions();
    }

    public static MatcherTransitionCollection makeCollection(StateMachineGraph smg) {
        return new MatcherTransitionCollection(smg);
    }

    public static MatcherTransitionCollection makeOne() {
        StateMachineGraph smg = new StateMachineGraph();
        StateNode node = new StateNode("0", true, true);

        smg.addNode(node);
        smg.createNewEdge(Collections.emptyList(), node, node);

        return new MatcherTransitionCollection(smg);
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge stmt) {
        TransitionFunction defaultTransition = getDefaultTransitionFunction(stmt);
        Statement statement = stmt.getStart();

        if (!statement.containsInvokeExpr()) {
            return defaultTransition;
        }
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        if (!invokeExpr.isInstanceInvokeExpr() && !statement.isAssign()) {
            return defaultTransition;
        }

        for (LabeledMatcherTransition transition : initialTransitions) {
            if (invokeExpr.isInstanceInvokeExpr()) {
                if (transition.getMatching(statement.getInvokeExpr().getMethod()).isPresent()) {
                    return new TransitionFunction(transition, Collections.singleton(stmt));
                }
            } else if (statement.isAssign()) {
                if (transition.getMatching(statement.getInvokeExpr().getMethod()).isPresent()) {
                    return new TransitionFunction(transition, Collections.singleton(stmt));
                }
            }
        }
        return defaultTransition;
    }

    private TransitionFunction getDefaultTransitionFunction(ControlFlowGraph.Edge stmt) {
        for (LabeledMatcherTransition matcherTransition : initialTransitions) {
            if (matcherTransition.from().isInitialState() && matcherTransition.to().toString().equals("0")) {
                Collection<Statement> preds = stmt.getMethod().getControlFlowGraph().getPredsOf(stmt.getStart());

                for (Statement pred : preds) {
                    ControlFlowGraph.Edge edge = new ControlFlowGraph.Edge(pred, stmt.getStart());
                    return new TransitionFunction(matcherTransition, Collections.singleton(edge));
                }
            }
        }
        return new TransitionFunction(initialTransitions, Collections.singleton(stmt));
    }

    public Collection<LabeledMatcherTransition> getAllTransitions() {
        return transitions;
    }

    private void initializeExistingTransitions() {
        for (TransitionEdge edge : smg.getAllTransitions()) {
            WrappedState from = createWrappedState(edge.from());
            WrappedState to = createWrappedState(edge.to());

            LabeledMatcherTransition matcherTransition = new LabeledMatcherTransition(from, edge.getLabel(), to);
            transitions.add(matcherTransition);

            if (smg.getInitialTransitions().contains(edge)) {
                initialTransitions.add(matcherTransition);
            }
        }
    }

    private void initializeErrorTransitions() {
        Set<CrySLMethod> allMethods = new HashSet<>();
        for (TransitionEdge edge : smg.getAllTransitions()) {
            allMethods.addAll(edge.getLabel());
        }

        for (StateNode node : smg.getNodes()) {
            // Collect the methods that are on an outgoing edge
            Collection<CrySLMethod> existingMethods = new HashSet<>();
            for (TransitionEdge edge : smg.getAllOutgoingEdges(node)) {
                existingMethods.addAll(edge.getLabel());
            }

            // Remove the existing methods; all remaining methods lead to an error state
            Collection<CrySLMethod> remainingMethods = new HashSet<>(allMethods);
            remainingMethods.removeAll(existingMethods);

            // Create the error state, where typestate errors are reported
            WrappedState state = createWrappedState(node);
            ReportingErrorStateNode repErrorState = new ReportingErrorStateNode(existingMethods);
            LabeledMatcherTransition repErrorTransition = new LabeledMatcherTransition(state, remainingMethods, repErrorState);
            transitions.add(repErrorTransition);

            // Once in an error state, never leave it again
            ErrorStateNode errorState = new ErrorStateNode();
            LabeledMatcherTransition errorTransition = new LabeledMatcherTransition(repErrorState, allMethods, errorState);
            transitions.add(errorTransition);
        }
    }

    private WrappedState createWrappedState(StateNode node) {
        return WrappedState.of(node, node.equals(smg.getStartNode()));
    }
}
