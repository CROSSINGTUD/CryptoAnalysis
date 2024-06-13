package crypto.extractparameter;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scene.AllocVal;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DeclaredMethod;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleType;
import boomerang.scene.jimple.JimpleVal;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import crypto.analysis.CryptoScanner;
import crypto.boomerang.CogniCryptIntAndStringBoomerangOptions;
import crypto.rules.CrySLMethod;
import crypto.typestate.LabeledMatcherTransition;
import crypto.typestate.MatcherTransitionCollection;
import heros.utilities.DefaultValueMap;
import soot.Scene;
import wpds.impl.Weight.NoWeight;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class ExtractParameterAnalysis {

	private Map<ControlFlowGraph.Edge, DeclaredMethod> allCallsOnObject;
	private Collection<LabeledMatcherTransition> events = Sets.newHashSet();
	private CryptoScanner cryptoScanner;
	private Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues = HashMultimap.create();
	private Collection<CallSiteWithParamIndex> querySites = Sets.newHashSet();
	private Multimap<CallSiteWithParamIndex, Type> propagatedTypes = HashMultimap.create();
	private DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery> additionalBoomerangQuery = new DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery>() {
		@Override
		protected AdditionalBoomerangQuery createItem(AdditionalBoomerangQuery key) {
			return key;
		}
	};

	public ExtractParameterAnalysis(CryptoScanner cryptoScanner, Map<ControlFlowGraph.Edge, DeclaredMethod> allCallsOnObject, MatcherTransitionCollection matcherTransitions) {
		this.cryptoScanner = cryptoScanner;
		this.allCallsOnObject = allCallsOnObject;
		this.events.addAll(matcherTransitions.getAllTransitions());
	}

	public void run() {
		for (Map.Entry<ControlFlowGraph.Edge, DeclaredMethod> stmt : allCallsOnObject.entrySet()) {
			Statement statement = stmt.getKey().getStart();

			if (!statement.containsInvokeExpr()) {
				continue;
			}

			DeclaredMethod declaredMethod = stmt.getValue();
			for (LabeledMatcherTransition e : events) {
				Optional<CrySLMethod> matchingMethod = e.getMatching(declaredMethod);

                matchingMethod.ifPresent(crySLMethod -> injectQueryAtCallSite(crySLMethod, stmt.getKey()));
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

	private void injectQueryAtCallSite(CrySLMethod match, ControlFlowGraph.Edge callSite) {
		// TODO edge to getStart()
		int index = 0;
		for (Entry<String, String> param : match.getParameters())
			addQueryAtCallSite(param.getKey(), callSite, index++);
	}

	public void addQueryAtCallSite(String varNameInSpecification, ControlFlowGraph.Edge stmt, int index) {
		Statement statement = stmt.getStart();

		if (!statement.containsInvokeExpr()) {
			return;
		}

		Val parameter = statement.getInvokeExpr().getArg(index);
		if (!parameter.isLocal()) {
			CallSiteWithParamIndex cs = new CallSiteWithParamIndex(stmt, parameter, index, varNameInSpecification);
			collectedValues.put(cs, new ExtractedValue(stmt, parameter));
			querySites.add(cs);
			return;
		}

		Collection<Statement> predecessors = statement.getMethod().getControlFlowGraph().getPredsOf(statement);
		for (Statement pred : predecessors) {
			AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(new AdditionalBoomerangQuery(new ControlFlowGraph.Edge(pred, statement), parameter));
			// TODO Edge to getStart()
			CallSiteWithParamIndex callSiteWithParamIndex = new CallSiteWithParamIndex(stmt, parameter, index, varNameInSpecification);
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
                        // TODO ExtractValue constructor: Edge to Statement
                        extractedValue = new ExtractedValue(v.cfgEdge(), allocVal.getAllocVal());
                    } else {
                        extractedValue = new ExtractedValue(v.cfgEdge(), v.var());
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

	public void addAdditionalBoomerangQuery(AdditionalBoomerangQuery q, QueryListener listener) {
		AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(q);
		query.addListener(listener);
	}

	public class AdditionalBoomerangQuery extends BackwardQuery {
		public AdditionalBoomerangQuery(ControlFlowGraph.Edge stmt, Val variable) {
			super(stmt, variable);
		}

		protected boolean solved;
		private List<QueryListener> listeners = Lists.newLinkedList();
		private BackwardBoomerangResults<NoWeight> res;

		public void solve() {
			Boomerang boomerang = new Boomerang(cryptoScanner.callGraph(), cryptoScanner.getDataFlowScope(), new CogniCryptIntAndStringBoomerangOptions());
			res = boomerang.solve(this);
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

	private static interface QueryListener {
		public void solved(AdditionalBoomerangQuery q, BackwardBoomerangResults<NoWeight> res);
	}

}
