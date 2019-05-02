package crypto.extractparameter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.internal.util.Maps;

import boomerang.ForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.AbstractBoomerangResults;
import boomerang.results.BackwardBoomerangResults;
import soot.Value;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight.NoWeight;

public class ExtractedValue {
	private final Statement stmt;
	private final Value val;
	private final ForwardQuery originalQuery;
	private Multimap<ForwardQuery, Node<Statement, Val>> dataFlowPath;

	public ExtractedValue(Statement stmt, Value val, ForwardQuery originalQuery,
			Multimap<ForwardQuery, Node<Statement, Val>> dataFlowPath) {
		this.stmt = stmt;
		this.val = val;
		this.originalQuery = originalQuery;
		this.dataFlowPath = dataFlowPath;
	}

	public Statement stmt() {
		return stmt;
	}

	public Value getValue() {
		return val;
	}

	@Override
	public String toString() {
		return "Extracted Value: " + val + " at " + stmt;
	}

	/**
	 * Returns the actual data-flow path for this extract value.
	 * 
	 * Example: {@code
	 * if(...) 
	 * 	x = "AES"; 
	 * 	y = x; 
	 * else 
	 * 	y = "DES"; (1)
	 * 
	 * Cipher.getInstance(y); (2)
	 * } 
	 * When this extracted value represents "DES" (this.val == "DES), the returned
	 * data-flow path will only contain the statements marked by (1) and (2).
	 * 
	 * @return
	 */
	public Collection<Node<Statement, Val>> getRelevantDataFlowPath() {
		return dataFlowPath.get(originalQuery);
	}

	/**
	 * Returns all data-flow paths, for all for this extract value.
	 * 
	 * Example: {@code
	 * if(...) 
	 * 	x = "AES"; (1)
	 * 	y = x; (2)
	 * else 
	 * 	y = "DES"; (3)
	 * 
	 * Cipher.getInstance(y);  (4)
	 * } 
	 * 
	 * When this extracted value represents "DES" (this.val == "DES), the returned data-flow
	 * path will contain the statements marked by (1)-(4).
	 * 
	 * @return
	 */
	public Multimap<ForwardQuery, Node<Statement, Val>> getAllDataFlowPaths() {
		return dataFlowPath;
	}

	public ForwardQuery getQuery() {
		return originalQuery;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtractedValue other = (ExtractedValue) obj;
		if (stmt == null) {
			if (other.stmt != null)
				return false;
		} else if (!stmt.equals(other.stmt))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

}
