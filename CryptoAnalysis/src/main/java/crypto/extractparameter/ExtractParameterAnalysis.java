package crypto.extractparameter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.BackwardBoomerangResults;
import crypto.analysis.CryptoScanner;
import crypto.boomerang.CogniCryptIntAndStringBoomerangOptions;
import crypto.rules.CryptSLMethod;
import crypto.typestate.CryptSLMethodToSootMethod;
import crypto.typestate.LabeledMatcherTransition;
import crypto.typestate.SootBasedStateMachineGraph;
import heros.utilities.DefaultValueMap;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import typestate.finiteautomata.MatcherTransition;
import wpds.impl.Weight.NoWeight;

public class ExtractParameterAnalysis {

	private Map<Statement,SootMethod> allCallsOnObject;
	private Collection<LabeledMatcherTransition> events = Sets.newHashSet();
	private CryptoScanner cryptoScanner;
	private Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues = HashMultimap.create();
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
//			if (reports != null) {
//				reports.boomerangQueryStarted(query, q);
//			}
			q.solve();
//			if (reports != null) {
//				reports.boomerangQueryFinished(query, q);
//			}
		}
	}
	public Multimap<CallSiteWithParamIndex, ExtractedValue> getCollectedValues() {
		return collectedValues;
	}
	private void injectQueryAtCallSite(List<CryptSLMethod> list, Statement callSite) {
		if(!callSite.isCallsite())
			return;
		for(CryptSLMethod matchingDescriptor : list){
			for(SootMethod m : CryptSLMethodToSootMethod.v().convert(matchingDescriptor)){
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
			collectedValues.put(
					new CallSiteWithParamIndex(stmt, new Val(parameter, stmt.getMethod()), index, varNameInSpecification), new ExtractedValue(stmt,parameter));
			return;
		}
		Val queryVal = new Val((Local) parameter, stmt.getMethod());
		AdditionalBoomerangQuery query = additionalBoomerangQuery
				.getOrCreate(new AdditionalBoomerangQuery(stmt, queryVal));
		query.addListener(new QueryListener() {
			@Override
			public void solved(AdditionalBoomerangQuery q, BackwardBoomerangResults<NoWeight> res) {
				for (ForwardQuery v : res.getAllocationSites().keySet()) {
					ExtractedValue extractedValue = null;
					if(v.var() instanceof AllocVal) {
						AllocVal allocVal = (AllocVal) v.var();
						extractedValue = new ExtractedValue(allocVal.allocationStatement(),allocVal.allocationValue());
					} else {
						extractedValue = new ExtractedValue(v.stmt(),v.var().value());
					}
					collectedValues.put(new CallSiteWithParamIndex(stmt, queryVal, index, varNameInSpecification),
							extractedValue);
				}
			}
		});
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
				public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
					return ExtractParameterAnalysis.this.cryptoScanner.icfg();
				}
			};
			res = boomerang.solve(this);
			boomerang.debugOutput();
			// log("Solving query "+ accessGraph + " @ " + stmt);
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

		private ExtractParameterAnalysis getOuterType() {
			return ExtractParameterAnalysis.this;
		}
	}

	private static interface QueryListener {
		public void solved(AdditionalBoomerangQuery q, BackwardBoomerangResults<NoWeight> res);
	}
	


}
