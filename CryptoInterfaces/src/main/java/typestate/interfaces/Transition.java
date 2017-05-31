package typestate.interfaces;

import crypto.rules.StatementLabel;

public interface Transition<State>{
	State from();
	State to();
	StatementLabel getLabel();
}