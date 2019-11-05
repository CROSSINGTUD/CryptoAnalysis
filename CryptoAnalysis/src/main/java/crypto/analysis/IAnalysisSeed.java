package crypto.analysis;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import boomerang.WeightedForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.predicates.PredicateHandler;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public abstract class IAnalysisSeed extends WeightedForwardQuery<TransitionFunction> {

	protected final CryptoScanner cryptoScanner;
	protected final PredicateHandler predicateHandler;
	private String objectId;

	public IAnalysisSeed(CryptoScanner scanner, Statement stmt, Val fact, TransitionFunction func){
		super(stmt,fact, func);
		this.cryptoScanner = scanner;
		this.predicateHandler = scanner.getPredicateHandler();
	}
	abstract void execute();

	public SootMethod getMethod(){
		return stmt().getMethod();
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
	
	public abstract Set<Node<Statement, Val>> getDataFlowPath();
	
}
