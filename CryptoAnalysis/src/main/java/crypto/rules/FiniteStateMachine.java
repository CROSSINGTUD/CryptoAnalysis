package crypto.rules;

import java.util.Collection;


public interface FiniteStateMachine<State> {
	Transition<State> getInitialTransition();
	Collection<State> getAcceptingStates();
	Collection<? extends Transition<State>> getAllTransitions();
	
}
