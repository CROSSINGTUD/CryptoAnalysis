package crypto.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.scene.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class CrySLResultsReporter  {

	private List<ICrySLResultsListener> listeners;

	public CrySLResultsReporter() {
		listeners = new ArrayList<ICrySLResultsListener>();
	}

	public boolean addReportListener(ICrySLResultsListener listener) {
		return listeners.add(listener);
	}

	public boolean removeReportListener(CrySLAnalysisListener listener) {
		return listeners.remove(listener);
	}

	public void collectedValues(AnalysisSeedWithSpecification seed, Multimap<CallSiteWithParamIndex, ExtractedValue> parametersToValues) {
		for (ICrySLResultsListener listen : listeners) {
			listen.collectedValues(seed, parametersToValues);
		}
	}

	public void discoveredSeed(IAnalysisSeed curr) {
		for (ICrySLResultsListener listen : listeners) {
			listen.discoveredSeed(curr);
		}
	}

	public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates, Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates, Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
		for (ICrySLResultsListener listen : listeners) {
			if (listen instanceof CrySLAnalysisListener) {
				((CrySLAnalysisListener) listen).ensuredPredicates(existingPredicates, expectedPredicates, missingPredicates);
			}
		}
	}

	public void beforeAnalysis() {
		for (ICrySLResultsListener listen : listeners) {
			if (listen instanceof CrySLAnalysisListener) {
				((CrySLAnalysisListener) listen).beforeAnalysis();
			}
		}
	}

	public void afterAnalysis() {
		for (ICrySLResultsListener listen : listeners) {
			if (listen instanceof CrySLAnalysisListener) {
				((CrySLAnalysisListener) listen).afterAnalysis();
			}
		}
	}
	
	public void onSeedTimeout(Node<ControlFlowGraph.Edge, Val> seed) {
		for (ICrySLResultsListener listen : listeners) {
			listen.onSeedTimeout(seed);
		}
	}


	public void onSecureObjectFound(IAnalysisSeed seed) {
		for (ICrySLResultsListener listen : listeners) {
			listen.onSecureObjectFound(seed);
		}
	}

	public void addProgress(int processedSeeds, int workListsize) {
		for (ICrySLResultsListener listen : listeners) {
			listen.addProgress(processedSeeds,workListsize);
		}
		
	}
	
}
