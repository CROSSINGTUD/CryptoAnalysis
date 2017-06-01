package typestate.interfaces;

import java.util.List;

import crypto.rules.StatementLabel;

public interface Transition<State>{
	State from();
	State to();
	List<StatementLabel> getLabel();
}