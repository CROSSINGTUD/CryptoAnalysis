package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import typestate.TransitionFunction;

public interface ResultsHandler{
	void done(ForwardBoomerangResults<TransitionFunction> results);
}