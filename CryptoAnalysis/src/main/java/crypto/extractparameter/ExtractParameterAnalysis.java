package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleType;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.rules.CrySLMethod;
import crypto.typestate.LabeledMatcherTransition;
import crypto.typestate.MatcherTransitionCollection;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import wpds.impl.Weight.NoWeight;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ExtractParameterAnalysis {

	private final Collection<LabeledMatcherTransition> events = Sets.newHashSet();
	private final Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues = HashMultimap.create();
	private final Collection<CallSiteWithParamIndex> querySites = Sets.newHashSet();
	private final Multimap<CallSiteWithParamIndex, Type> propagatedTypes = HashMultimap.create();
	private final DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery> additionalBoomerangQuery = new DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery>() {
		@Override
		protected AdditionalBoomerangQuery createItem(AdditionalBoomerangQuery key) {
			return key;
		}
	};

	private final AnalysisSeedWithSpecification seed;

	public ExtractParameterAnalysis(AnalysisSeedWithSpecification seed) {
		this.seed = seed;

		MatcherTransitionCollection matcherTransitions = MatcherTransitionCollection.makeCollection(seed.getSpecification().getUsagePattern());
		this.events.addAll(matcherTransitions.getAllTransitions());
	}

	public void run() {
		for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> stmt : seed.getAllCallsOnObject().entrySet()) {
			Statement statement = stmt.getKey().getStart();

			if (!statement.containsInvokeExpr()) {
				continue;
			}

			DeclaredMethod declaredMethod = stmt.getValue();
			for (LabeledMatcherTransition e : events) {
				Optional<CrySLMethod> matchingMethod = e.getMatching(declaredMethod);

                matchingMethod.ifPresent(crySLMethod -> injectQueryAtCallSite(crySLMethod, statement));
			}
		}

		for (AdditionalBoomerangQuery q : additionalBoomerangQuery.keySet()) {
			q.solve();
		}
	}

	public Multimap<CallSiteWithParamIndex, ExtractedValue> getCollectedValues() {
		return collectedValues;
	}

	public Multimap<CallSiteWithParamIndex, Type> getPropagatedTypes() {
		return propagatedTypes;
	}

	public Collection<CallSiteWithParamIndex> getAllQuerySites() {
		return querySites;
	}

	private void injectQueryAtCallSite(CrySLMethod match, Statement callSite) {
		int index = 0;
		for (Map.Entry<String, String> param : match.getParameters())
			addQueryAtCallSite(param.getKey(), callSite, index++);
	}

	public void addQueryAtCallSite(String varNameInSpecification, Statement statement, int index) {
		if (!statement.containsInvokeExpr()) {
			return;
		}

		Val parameter = statement.getInvokeExpr().getArg(index);
		if (!parameter.isLocal()) {
			CallSiteWithParamIndex cs = new CallSiteWithParamIndex(statement, parameter, index, varNameInSpecification);
			collectedValues.put(cs, new ExtractedValue(statement, parameter));
			querySites.add(cs);
			return;
		}

		Collection<Statement> predecessors = statement.getMethod().getControlFlowGraph().getPredsOf(statement);
		for (Statement pred : predecessors) {
			AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(new AdditionalBoomerangQuery(new ControlFlowGraph.Edge(pred, statement), parameter));
			CallSiteWithParamIndex callSiteWithParamIndex = new CallSiteWithParamIndex(statement, parameter, index, varNameInSpecification);
			querySites.add(callSiteWithParamIndex);
			query.addListener((q, res) -> {
                propagatedTypes.putAll(callSiteWithParamIndex, res.getPropagationType());

				// If the allocation site could not be extracted, add the zero value for indication
				if (res.getAllocationSites().keySet().isEmpty()) {
					ExtractedValue zeroValue = new ExtractedValue(callSiteWithParamIndex.stmt(), Val.zero());
					collectedValues.put(callSiteWithParamIndex, zeroValue);
					return;
				}

                for (ForwardQuery v : res.getAllocationSites().keySet()) {
					ExtractedValue extractedValue;
                    if (v.var() instanceof AllocVal) {
                        AllocVal allocVal = (AllocVal) v.var();
                        extractedValue = new ExtractedValue(v.cfgEdge().getStart(), allocVal.getAllocVal());
                    } else {
                        extractedValue = new ExtractedValue(v.cfgEdge().getStart(), v.var());
                    }
                    collectedValues.put(callSiteWithParamIndex, extractedValue);

					// TODO This seems to be odd; char[] is not a String
                    // Special handling for toCharArray method (required for NeverTypeOf constraint)
					Statement allocStmt = v.cfgEdge().getStart();
					if (!allocStmt.isAssign()) {
						continue;
					}

					Val rightOp = allocStmt.getRightOp();
					if (rightOp.getVariableName().contains("<java.lang.String: char[] toCharArray()>")) {
						propagatedTypes.put(callSiteWithParamIndex, new JimpleType(Scene.v().getType("java.lang.String")));
					}
                }
            });
		}
	}

	private class AdditionalBoomerangQuery extends BackwardQuery {

		private final Collection<QueryListener> listeners = Lists.newLinkedList();
		private BackwardBoomerangResults<NoWeight> res;
		private boolean solved;

		public AdditionalBoomerangQuery(ControlFlowGraph.Edge stmt, Val variable) {
			super(stmt, variable);
		}

		public void solve() {
			ExtractParameterOptions options = new ExtractParameterOptions(seed.getScanner().getTimeout());
			Boomerang boomerang = new Boomerang(seed.getScanner().callGraph(), seed.getScanner().getDataFlowScope(), options);
			res = boomerang.solve(this);

			if (res.isTimedout()) {
				seed.getScanner().getAnalysisReporter().onExtractParameterAnalysisTimeout(seed, var(), cfgEdge().getTarget());
			}

			for (QueryListener l : Lists.newLinkedList(listeners)) {
				l.solved(this, res);
			}
			solved = true;
			boomerang.unregisterAllListeners();
		}

		public void addListener(QueryListener q) {
			if (solved) {
				q.solved(this, res);
				return;
			}
			listeners.add(q);
		}

	}

	private interface QueryListener {
		void solved(AdditionalBoomerangQuery q, BackwardBoomerangResults<NoWeight> res);
	}

}
