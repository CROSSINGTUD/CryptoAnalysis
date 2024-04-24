package crypto.analysis;

import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Method;
import boomerang.scene.Type;
import boomerang.scene.Val;
import crypto.analysis.errors.AbstractError;
import crypto.predicates.PredicateHandler;
import typestate.TransitionFunction;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public abstract class IAnalysisSeed {

	private final WeightedForwardQuery<TransitionFunction> forwardQuery;

	protected final CryptoScanner cryptoScanner;
	protected final PredicateHandler predicateHandler;
	protected final List<AbstractError> errorCollection;
	private String objectId;

	public IAnalysisSeed(CryptoScanner scanner, WeightedForwardQuery<TransitionFunction> forwardQuery){
		this.forwardQuery = forwardQuery;
		this.cryptoScanner = scanner;
		this.predicateHandler = scanner.getPredicateHandler();
		this.errorCollection = new ArrayList<>();
	}
	abstract void execute();

	public WeightedForwardQuery<TransitionFunction> getForwardQuery() {
		return forwardQuery;
	}

	public Method getMethod(){
		return forwardQuery.cfgEdge().getMethod();
	}

	public Val getFact() {
		return forwardQuery.var();
	}

	public ControlFlowGraph.Edge cfgEdge() {
		return forwardQuery.cfgEdge();
	}

	public Type getType() {
		return forwardQuery.getType();
	}

	public void addError(AbstractError e) {
		this.errorCollection.add(e);
	}

	public List<AbstractError> getErrors(){
		return new ArrayList<>(errorCollection);
	}
	
	public String getObjectId() {
		if(objectId == null) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			this.objectId = new BigInteger(1, md.digest(this.toString().getBytes())).toString(16);
		}
		return this.objectId;
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;

		if (!(obj instanceof IAnalysisSeed)) return false;
		IAnalysisSeed seed = (IAnalysisSeed) obj;

		return forwardQuery.equals(seed.getForwardQuery());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfgEdge() == null) ? 0 : cfgEdge().hashCode());
		result = prime * result + ((getFact() == null) ? 0 : getFact().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Seed: " + getFact().getVariableName() + " at " + cfgEdge().getStart();
	}
	
}
