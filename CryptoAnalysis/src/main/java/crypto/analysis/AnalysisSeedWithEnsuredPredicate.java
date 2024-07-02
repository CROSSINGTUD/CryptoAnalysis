package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import com.google.common.collect.Sets;
import com.google.common.collect.Table.Cell;
import typestate.TransitionFunction;

import java.util.Set;

public class AnalysisSeedWithEnsuredPredicate extends IAnalysisSeed {

	private final Set<EnsuredCrySLPredicate> ensuredPredicates = Sets.newHashSet();

	public AnalysisSeedWithEnsuredPredicate(CryptoScanner scanner, Statement statement, Val fact, ForwardBoomerangResults<TransitionFunction> results) {
		super(scanner, statement, fact, results);
	}

	public static AnalysisSeedWithEnsuredPredicate makeSeedForComparison(CryptoScanner scanner, Statement statement, Val fact) {
		return new AnalysisSeedWithEnsuredPredicate(scanner, statement, fact, null);
	}

	@Override
	public void execute() {
		if (analysisResults == null) {
			return;
		}
		scanner.getAnalysisReporter().onSeedStarted(this);

		for (EnsuredCrySLPredicate pred : ensuredPredicates) {
			ensurePredicates(pred);
		}

		scanner.getAnalysisReporter().onSeedFinished(this);
	}

	public void addEnsuredPredicate(EnsuredCrySLPredicate pred) {
		if (ensuredPredicates.add(pred)) {
			ensurePredicates(pred);
		}
	}

	private void ensurePredicates(EnsuredCrySLPredicate pred) {
		for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> c : analysisResults.asStatementValWeightTable().cellSet()) {
			predicateHandler.addNewPred(this, c.getRowKey().getStart(), c.getColumnKey(), pred);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "AnalysisSeedWithoutSpec [" + super.toString() + "]";
	}

}
