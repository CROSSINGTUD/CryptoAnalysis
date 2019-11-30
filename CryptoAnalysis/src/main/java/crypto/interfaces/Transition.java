package crypto.interfaces;

import java.util.List;

import crypto.rules.CrySLMethod;

public interface Transition<State>{
	State from();
	State to();
	List<CrySLMethod> getLabel();
}