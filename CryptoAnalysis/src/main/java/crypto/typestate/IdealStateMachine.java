/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.typestate;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import crysl.rule.CrySLMethod;
import crysl.rule.StateMachineGraph;
import crysl.rule.StateNode;
import crysl.rule.TransitionEdge;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import typestate.TransitionFunction;
import typestate.TransitionFunctionImpl;

/**
 * State machine that wraps a {@link StateMachineGraph} from a {@link crysl.rule.CrySLRule} and
 * transforms it into a corresponding state machine that is compatible with IDEal. It models all
 * transitions from the given graph. Additionally, it adds a transition from each node to a
 * corresponding {@link ReportingErrorStateNode} to signalize that the analysis has to report an
 * error when in this state. From all reporting nodes, additional transitions are added to the final
 * {@link ErrorStateNode} where it remains, but no error is reported.
 */
public class IdealStateMachine {

    private final StateMachineGraph smg;
    private final Collection<LabeledMatcherTransition> transitions;
    private final Collection<LabeledMatcherTransition> initialTransitions;

    private IdealStateMachine(StateMachineGraph smg) {
        this.smg = smg;

        transitions = new HashSet<>();
        initialTransitions = new HashSet<>();

        initializeExistingTransitions();
        initializeErrorTransitions();
    }

    /**
     * Create a state machine that is compatible with IDEal from a {@link StateMachineGraph}
     *
     * @param smg the state machine graph
     * @return the constructed state machine with additional error transitions
     */
    public static IdealStateMachine makeStateMachine(StateMachineGraph smg) {
        return new IdealStateMachine(smg);
    }

    /**
     * Create a state machine that has only one state (initial and accepting) that is never left.
     * This machine can be used for seeds without an existing rule/state machine graph.
     *
     * @return the state machine with a single state
     */
    public static IdealStateMachine makeOne() {
        StateMachineGraph smg = new StateMachineGraph();
        StateNode node = new StateNode("0", true, true);

        smg.addNode(node);
        smg.createNewEdge(Collections.emptySet(), node, node);

        return new IdealStateMachine(smg);
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge edge) {
        Statement statement = edge.getStart();

        if (!statement.containsInvokeExpr()) {
            WrappedState startNode = createWrappedState(smg.getStartNode());
            ErrorStateNode errorStateNode = ErrorStateNode.getInstance();
            LabeledMatcherTransition transition =
                    new LabeledMatcherTransition(startNode, Collections.emptySet(), errorStateNode);

            return new TransitionFunctionImpl(transition, Collections.singleton(edge));
        }

        InvokeExpr invokeExpr = statement.getInvokeExpr();
        DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
        for (LabeledMatcherTransition transition : initialTransitions) {
            if (transition.getMatching(declaredMethod).isPresent()) {
                return new TransitionFunctionImpl(transition, Collections.singleton(edge));
            }
        }

        // Seeds may be generated from other seeds, e.g. SecretKey key = generator.generateKey().
        // In this case, we do a transition -1 -> 0
        if (statement.isAssignStmt() && !invokeExpr.isStaticInvokeExpr()) {
            for (LabeledMatcherTransition transition : initialTransitions) {
                if (transition.to().toString().equals("0")) {
                    return new TransitionFunctionImpl(transition, Collections.singleton(edge));
                }
            }
        }

        // If no initial transition exists, we go directly to a reporting error state
        Collection<CrySLMethod> expectedInitialMethods = new HashSet<>();
        for (LabeledMatcherTransition transition : initialTransitions) {
            expectedInitialMethods.addAll(transition.getMethods());
        }

        WrappedState startNode = createWrappedState(smg.getStartNode());
        ReportingErrorStateNode errorState = new ReportingErrorStateNode(expectedInitialMethods);

        LabeledMatcherTransition transition =
                new LabeledMatcherTransition(startNode, expectedInitialMethods, errorState);
        return new TransitionFunctionImpl(transition, Collections.singleton(edge));
    }

    public Collection<LabeledMatcherTransition> getAllTransitions() {
        return transitions;
    }

    private void initializeExistingTransitions() {
        for (TransitionEdge edge : smg.getAllTransitions()) {
            WrappedState from = createWrappedState(edge.from());
            WrappedState to = createWrappedState(edge.to());

            LabeledMatcherTransition matcherTransition =
                    new LabeledMatcherTransition(from, edge.getLabel(), to);
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
            LabeledMatcherTransition repErrorTransition =
                    new LabeledMatcherTransition(state, remainingMethods, repErrorState);
            transitions.add(repErrorTransition);

            // Once in an error state, never leave it again
            ErrorStateNode errorState = ErrorStateNode.getInstance();
            LabeledMatcherTransition errorTransition =
                    new LabeledMatcherTransition(repErrorState, allMethods, errorState);
            transitions.add(errorTransition);
        }

        // Loop for final error state
        ErrorStateNode errorState = ErrorStateNode.getInstance();
        LabeledMatcherTransition transition =
                new LabeledMatcherTransition(errorState, allMethods, errorState);
        transitions.add(transition);
    }

    private WrappedState createWrappedState(StateNode node) {
        return WrappedState.of(node, node.equals(smg.getStartNode()));
    }
}
