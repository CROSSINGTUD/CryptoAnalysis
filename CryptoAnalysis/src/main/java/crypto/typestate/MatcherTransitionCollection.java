package crypto.typestate;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Statement;
import crypto.rules.CrySLMethod;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MatcherTransitionCollection {

    private final StateMachineGraph smg;
    private final Set<LabeledMatcherTransition> transitions;
    private final Set<LabeledMatcherTransition> initialTransitions;

    public MatcherTransitionCollection(StateMachineGraph smg) {
        this.smg = smg;

        transitions = new HashSet<>();
        initialTransitions = new HashSet<>();

        initializeExistingTransitions();
        initializeErrorTransitions();
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge stmt) {
        TransitionFunction defaultTransition = new TransitionFunction(initialTransitions, Collections.singleton(stmt));
        Statement statement = stmt.getStart();

        if (!statement.containsInvokeExpr()) {
            return defaultTransition;
        }
        InvokeExpr invokeExpr = statement.getInvokeExpr();

        if (!(invokeExpr.isInstanceInvokeExpr()) && !(statement.isAssign())) {
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

    public Collection<TransitionEdge> getInitialTransitions() {
        return smg.getInitialTransitions();
    }

    public Set<LabeledMatcherTransition> getAllTransitions() {
        return transitions;
    }

    private void initializeExistingTransitions() {
        for (TransitionEdge edge : smg.getAllTransitions()) {
            WrappedState from = createWrappedState(edge.from());
            WrappedState to = createWrappedState(edge.to());

            LabeledMatcherTransition matcherTransition = new LabeledMatcherTransition(from, edge.getLabel(), MatcherTransition.Parameter.This, to, MatcherTransition.Type.OnCallOrOnCallToReturn);
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
            Set<CrySLMethod> existingMethods = new HashSet<>();
            for (TransitionEdge edge : smg.getAllOutgoingEdges(node)) {
                existingMethods.addAll(edge.getLabel());
            }

            // Remove the existing methods; all remaining methods lead to an error state
            Set<CrySLMethod> remainingMethods = new HashSet<>(allMethods);
            remainingMethods.removeAll(existingMethods);

            // Create the error state, where typestate errors are reported
            WrappedState state = createWrappedState(node);
            ReportingErrorStateNode repErrorState = new ReportingErrorStateNode(remainingMethods);
            LabeledMatcherTransition repErrorTransition = new LabeledMatcherTransition(state, remainingMethods, MatcherTransition.Parameter.This, repErrorState, MatcherTransition.Type.OnCallOrOnCallToReturn);
            transitions.add(repErrorTransition);

            // Once in an error state, never leave it again
            ErrorStateNode errorState = new ErrorStateNode();
            LabeledMatcherTransition errorTransition = new LabeledMatcherTransition(repErrorState, allMethods, MatcherTransition.Parameter.This, errorState, MatcherTransition.Type.OnCallOrOnCallToReturn);
            transitions.add(errorTransition);
        }
    }

    private WrappedState createWrappedState(StateNode node) {
        return new WrappedState(node, node.equals(smg.getStartNode()));
    }
}
