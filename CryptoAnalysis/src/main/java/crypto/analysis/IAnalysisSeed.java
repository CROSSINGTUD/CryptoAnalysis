package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import crypto.analysis.errors.AbstractError;
import crypto.predicates.PredicateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import typestate.TransitionFunction;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class IAnalysisSeed {

	protected static final Logger LOGGER = LoggerFactory.getLogger(IAnalysisSeed.class);

	protected final CryptoScanner scanner;
	protected final PredicateHandler predicateHandler;
	protected final Collection<AbstractError> errorCollection;
	protected final ForwardBoomerangResults<TransitionFunction> analysisResults;

	private final Statement origin;
	private final Val fact;
	private String objectId;
	private boolean secure = true;

	public IAnalysisSeed(CryptoScanner scanner, Statement origin, Val fact, ForwardBoomerangResults<TransitionFunction> results) {
		this.scanner = scanner;
		this.origin = origin;
		this.fact = fact;
		this.analysisResults = results;

		this.predicateHandler = scanner.getPredicateHandler();
		this.errorCollection = new ArrayList<>();
	}
	public abstract void execute();

	public Method getMethod(){
		return origin.getMethod();
	}

	public Statement getOrigin() {
		return origin;
	}

	public Val getFact() {
		return fact;
	}

	public Type getType() {
		return fact.getType();
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public ForwardBoomerangResults<TransitionFunction> getAnalysisResults() {
		return analysisResults;
	}

	public void addError(AbstractError e) {
		this.errorCollection.add(e);
	}

	public List<AbstractError> getErrors(){
		return new ArrayList<>(errorCollection);
	}

	public CryptoScanner getScanner() {
		return scanner;
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
		IAnalysisSeed other = (IAnalysisSeed) obj;

		if (!origin.equals(other.getOrigin())) return false;
        return fact.equals(other.getFact());
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((fact == null) ? 0 : fact.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return fact.getVariableName() + " at " + origin;
	}
	
}
