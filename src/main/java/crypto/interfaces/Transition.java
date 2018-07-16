package crypto.interfaces;

import java.util.List;

import crypto.rules.CryptSLMethod;

public interface Transition<State>{
	State from();
	State to();
	List<CryptSLMethod> getLabel();
}