package test.assertions;

import java.util.Set;

import com.beust.jcommander.internal.Sets;

import boomerang.accessgraph.AccessGraph;
import crypto.analysis.EnsuredCryptSLPredicate;
import ideal.IFactAtStatement;
import soot.jimple.Stmt;
import test.Assertion;

public class HasConstraintAssertion implements Assertion {

	private Stmt stmt;
	private AccessGraph val;
	private Set<IFactAtStatement> allocations = Sets.newHashSet();
	private boolean satisfied;

	public HasConstraintAssertion(Stmt stmt, AccessGraph val) {
		this.stmt = stmt;
		this.val = val;
	}
	
	public AccessGraph getAccessGraph() {
		return val;
	}

	@Override
	public boolean isSatisfied() {
		return satisfied;
	}

	@Override
	public boolean isImprecise() {
		return false;
	}

	public void addObject(IFactAtStatement seed) {
		allocations.add(seed);
	}

	public Stmt getStmt() {
		return stmt;
	}

	public void reported(IFactAtStatement seed, EnsuredCryptSLPredicate pred) {
		satisfied = true;
	}

}
