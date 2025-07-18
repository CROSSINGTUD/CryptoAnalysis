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
import boomerang.scope.Val;
import crypto.utils.MatcherUtils;
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
import typestate.finiteautomata.Transition;

/**
 * State machine that wraps a {@link StateMachineGraph} from a {@link crysl.rule.CrySLRule} and
 * transforms it into a corresponding state machine that is compatible with IDEal. It models all
 * transitions from the given graph. Additionally, it adds a transition from each node to a
 * corresponding {@link ReportingErrorStateNode} to signalize that the analysis has to report an
 * error when in this state. From all reporting nodes, additional transitions are added to the final
 * {@link ErrorStateNode} where it remains, but no error is reported.
 */
public class IdealStateMachine {

    private final Collection<CrySLMethod> events;
    private final StateMachineGraph smg;
    private final Collection<LabeledMatcherTransition> transitions;
    private final Collection<LabeledMatcherTransition> initialTransitions;

    private IdealStateMachine(Collection<CrySLMethod> events, StateMachineGraph smg) {
        this.events = events;
        this.smg = smg;

        transitions = new HashSet<>();
        initialTransitions = new HashSet<>();

        initializeExistingTransitions();
        initializeErrorTransitions();
    }

    /**
     * Create a state machine that is compatible with IDEal from a {@link StateMachineGraph}
     *
     * @param events all events from {@link crysl.rule.CrySLRule}
     * @param smg the state machine graph using the events
     * @return the constructed state machine with additional error transitions
     */
    public static IdealStateMachine makeStateMachine(
            Collection<CrySLMethod> events, StateMachineGraph smg) {
        return new IdealStateMachine(events, smg);
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

        return new IdealStateMachine(Collections.emptySet(), smg);
    }

    public TransitionFunction getInitialWeight(ControlFlowGraph.Edge edge) {
        Statement statement = edge.getStart();

        if (statement.isAssignStmt()) {
            Val rightOp = statement.getRightOp();

            if (rightOp.isNewExpr()) {
                WrappedState startNode = createWrappedState(smg.getStartNode());
                LabeledMatcherTransition idTransition =
                        new LabeledMatcherTransition(startNode, Collections.emptySet(), startNode);

                return new TransitionFunctionImpl(idTransition, statement);
            }

            if (statement.containsInvokeExpr()) {
                InvokeExpr invokeExpr = statement.getInvokeExpr();
                DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

                Set<Transition> matchingTransitions = new HashSet<>();
                for (LabeledMatcherTransition transition : initialTransitions) {
                    if (transition.getMatching(declaredMethod).isPresent()) {
                        matchingTransitions.add(transition);
                    }
                }

                if (!matchingTransitions.isEmpty()) {
                    return new TransitionFunctionImpl(matchingTransitions, statement);
                }

                Collection<CrySLMethod> matchingMethods =
                        MatcherUtils.getMatchingCryslMethodsToDeclaredMethod(
                                events, declaredMethod);
                if (matchingMethods.isEmpty()) {
                    // return new TransitionFunctionImpl(Set.copyOf(initialTransitions),
                    // Collections.singleton(edge));
                    for (LabeledMatcherTransition transition : initialTransitions) {
                        if (transition.to().toString().equals("0")) {
                            return new TransitionFunctionImpl(transition, statement);
                        }
                    }
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
        return new TransitionFunctionImpl(transition, statement);
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
        for (StateNode node : smg.getNodes()) {
            // Collect the methods that are on an outgoing edge
            Collection<CrySLMethod> existingMethods = new HashSet<>();
            for (TransitionEdge edge : smg.getAllOutgoingEdges(node)) {
                existingMethods.addAll(edge.getLabel());
            }

            // Remove the existing methods; all remaining methods lead to an error state
            Collection<CrySLMethod> remainingMethods = new HashSet<>(events);
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
                    new LabeledMatcherTransition(repErrorState, events, errorState);
            transitions.add(errorTransition);
        }

        // Loop for final error state
        ErrorStateNode errorState = ErrorStateNode.getInstance();
        LabeledMatcherTransition transition =
                new LabeledMatcherTransition(errorState, events, errorState);
        transitions.add(transition);
    }

    private WrappedState createWrappedState(StateNode node) {
        return WrappedState.of(node, node.equals(smg.getStartNode()));
    }
}
