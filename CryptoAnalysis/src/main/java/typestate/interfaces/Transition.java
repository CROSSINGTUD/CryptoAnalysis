package typestate.interfaces;

public interface Transition<State>{
	State from();
	State to();
	String label();
}