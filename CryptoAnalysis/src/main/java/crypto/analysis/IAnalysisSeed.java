package crypto.analysis;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import boomerang.WeightedForwardQuery;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Val;
import crypto.analysis.errors.AbstractError;
import crypto.predicates.PredicateHandler;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public abstract class IAnalysisSeed extends WeightedForwardQuery<TransitionFunction> {

	protected final CryptoScanner cryptoScanner;
	protected final PredicateHandler predicateHandler;
	protected final List<AbstractError> errorCollection;
	private String objectId;

	public IAnalysisSeed(CryptoScanner scanner, ControlFlowGraph.Edge stmt, Val fact, TransitionFunction func){
		super(stmt,fact, func);
		this.cryptoScanner = scanner;
		this.predicateHandler = scanner.getPredicateHandler();
		this.errorCollection = new ArrayList<>();
	}
	abstract void execute();

	public SootMethod getMethod(){
		return stmt().getMethod();
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
				e.printStackTrace();
				return null;
			}
			this.objectId = new BigInteger(1, md.digest(this.toString().getBytes())).toString(16);
		}
		return this.objectId;
		
	}
	
	public abstract Set<Node<ControlFlowGraph.Edge, Val>> getDataFlowPath();
	
}
