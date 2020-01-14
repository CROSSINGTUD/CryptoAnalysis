package crypto.extractparameter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.callgraph.ObservableICFG;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.BackwardBoomerangResults;
import crypto.analysis.CryptoScanner;
import crypto.boomerang.CogniCryptIntAndStringBoomerangOptions;
import crypto.rules.CrySLMethod;
import crypto.typestate.CrySLMethodToSootMethod;
import crypto.typestate.LabeledMatcherTransition;
import crypto.typestate.SootBasedStateMachineGraph;
import heros.utilities.DefaultValueMap;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import sync.pds.solver.nodes.Node;
import typestate.finiteautomata.MatcherTransition;
import wpds.impl.Weight.NoWeight;

public class ExtractParameterAnalysis {

	private Map<Statement,SootMethod> allCallsOnObject;
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

	public ExtractParameterAnalysis(CryptoScanner cryptoScanner, Map<Statement, SootMethod> allCallsOnObject, SootBasedStateMachineGraph fsm) {
		this.cryptoScanner = cryptoScanner;
		this.allCallsOnObject = allCallsOnObject;
		for(MatcherTransition m : fsm.getAllTransitions()) {
			if(m instanceof LabeledMatcherTransition) {
				this.events.add((LabeledMatcherTransition) m );
			}
		}
	}

	public void run() {
		for(Entry<Statement, SootMethod> callSiteWithCallee : allCallsOnObject.entrySet()) {
			Statement callSite = callSiteWithCallee.getKey();
			SootMethod declaredCallee = callSiteWithCallee.getValue();
			if(callSite.isCallsite()){
				for(LabeledMatcherTransition e : events) {
					if(e.matches(declaredCallee)) {
						injectQueryAtCallSite(e.label(),callSite);
					}
				}
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
	
	private void injectQueryAtCallSite(List<CrySLMethod> list, Statement callSite) {
		if(!callSite.isCallsite())
			return;
		for(CrySLMethod matchingDescriptor : list){
			for(SootMethod m : CrySLMethodToSootMethod.v().convert(matchingDescriptor)){
				SootMethod method = callSite.getUnit().get().getInvokeExpr().getMethod();
				if (!m.equals(method))
					continue;
				{
					int index = 0;
					for(Entry<String, String> param : matchingDescriptor.getParameters()){
						if(!param.getKey().equals("_")){
							soot.Type parameterType = method.getParameterType(index);
							if(parameterType.toString().equals(param.getValue())){
								addQueryAtCallsite(param.getKey(), callSite, index);
							}
						}
						index++;
					}
				}
			}
		}
	}

	public void addQueryAtCallsite(final String varNameInSpecification, final Statement stmt, final int index) {
		if(!stmt.isCallsite())
			return;
		Value parameter = stmt.getUnit().get().getInvokeExpr().getArg(index);
		if (!(parameter instanceof Local)) {
			Val parameterVal = new Val(parameter, stmt.getMethod());
			CallSiteWithParamIndex cs = new CallSiteWithParamIndex(stmt, parameterVal, index, varNameInSpecification);
			Set<Node<Statement,Val>> dataFlowPath = Sets.newHashSet();
			dataFlowPath.add(new Node<Statement, Val>(stmt, parameterVal));
			collectedValues.put(cs
					, new ExtractedValue(stmt,parameter, dataFlowPath));
			querySites.add(cs);
			return;
		}
		Val queryVal = new Val((Local) parameter, stmt.getMethod());
		
		for(Unit pred : cryptoScanner.icfg().getPredsOf(stmt.getUnit().get())) {
			AdditionalBoomerangQuery query = additionalBoomerangQuery
					.getOrCreate(new AdditionalBoomerangQuery(new Statement((Stmt)pred, stmt.getMethod()), queryVal));
			CallSiteWithParamIndex callSiteWithParamIndex = new CallSiteWithParamIndex(stmt, queryVal, index, varNameInSpecification);
			querySites.add(callSiteWithParamIndex);
			query.addListener(new QueryListener() {
				@Override
				public void solved(AdditionalBoomerangQuery q, BackwardBoomerangResults<NoWeight> res) {
					propagatedTypes.putAll(callSiteWithParamIndex, res.getPropagationType());
					for (ForwardQuery v : res.getAllocationSites().keySet()) {
						ExtractedValue extractedValue = null;
						if(v.var() instanceof AllocVal) {
							AllocVal allocVal = (AllocVal) v.var();
							extractedValue = new ExtractedValue(allocVal.allocationStatement(),allocVal.allocationValue(), res.getDataFlowPath(v));
						} else {
							extractedValue = new ExtractedValue(v.stmt(),v.var().value(), res.getDataFlowPath(v));
						}
						collectedValues.put(callSiteWithParamIndex,
								extractedValue);
						//Special handling for toCharArray method (required for NeverTypeOf constraint)
						if(v.stmt().isCallsite()) {
							String calledMethodSignature = v.stmt().getUnit().get().getInvokeExpr().getMethod().getSignature();
							if(calledMethodSignature.equals("<java.lang.String: char[] toCharArray()>")){
								propagatedTypes.put(callSiteWithParamIndex, Scene.v().getType("java.lang.String"));
							}
						}
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
		public AdditionalBoomerangQuery(Statement stmt, Val variable) {
			super(stmt, variable);
		}

		protected boolean solved;
		private List<QueryListener> listeners = Lists.newLinkedList();
		private BackwardBoomerangResults<NoWeight> res;

		public void solve() {
			Boomerang boomerang = new Boomerang(new CogniCryptIntAndStringBoomerangOptions()) {
				@Override
				public ObservableICFG<Unit, SootMethod> icfg() {
					return ExtractParameterAnalysis.this.cryptoScanner.icfg();
				}
			};
			res = boomerang.solve(this);
			for (QueryListener l : Lists.newLinkedList(listeners)) {
				l.solved(this, res);
			}
			solved = true;
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
