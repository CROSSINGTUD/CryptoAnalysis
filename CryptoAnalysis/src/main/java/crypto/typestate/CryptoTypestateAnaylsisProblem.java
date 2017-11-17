package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.jimple.Field;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.rules.StateMachineGraph;
import heros.utilities.DefaultValueMap;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public abstract class CryptoTypestateAnaylsisProblem extends WeightedBoomerang<TransitionFunction> {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private Multimap<CallSiteWithParamIndex,Unit> collectedValues = HashMultimap.create(); 
	private Set<Unit> invokedMethodsOnInstance = Sets.newHashSet();
	private DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery> additionalBoomerangQuery = new DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery>() {
		@Override
		protected AdditionalBoomerangQuery createItem(AdditionalBoomerangQuery key) {
			return key;
		}
	};

	public FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction(){
		if(this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(this);
		return this.changeFunction;
	}
	
	@Override
	protected WeightFunctions<Statement, Val, Statement, TransitionFunction> getForwardCallWeights() {
		return getOrCreateTypestateChangeFunction();
	}
	
	@Override
	protected WeightFunctions<Statement, Val, Statement, TransitionFunction> getBackwardCallWeights() {
		return new OneWeightFunctions<Statement, Val, Statement, TransitionFunction>(TransitionFunction.zero(),TransitionFunction.one());
	}
	
	@Override
	protected WeightFunctions<Statement, Val, Field, TransitionFunction> getForwardFieldWeights() {
		return new OneWeightFunctions<Statement, Val, Field, TransitionFunction>(TransitionFunction.zero(),TransitionFunction.one());
	}
	
	@Override
	protected WeightFunctions<Statement, Val, Field, TransitionFunction> getBackwardFieldWeights() {
		return new OneWeightFunctions<Statement, Val, Field, TransitionFunction>(TransitionFunction.zero(),TransitionFunction.one());
	}

	public abstract StateMachineGraph getStateMachine(); 
	
	@Override
	public void solve(Query query) {
		getOrCreateTypestateChangeFunction().injectQueryForSeed(query.asNode().stmt().getUnit().get());
		super.solve(query);
		CrySLAnalysisResultsAggregator reports = analysisListener();
		for(AdditionalBoomerangQuery q : additionalBoomerangQuery.keySet()){
			if(reports != null){
				reports.boomerangQueryStarted(query.asNode(),q);
			}
			q.solve();
			if(reports != null){
				reports.boomerangQueryFinished(query.asNode(),q);
			}
		}
	}
	
	public void addQueryAtCallsite(final String varNameInSpecification, final Stmt stmt,final int index) {
		Value parameter = stmt.getInvokeExpr().getArg(index);
		SootMethod method = CryptoTypestateAnaylsisProblem.this.icfg().getMethodOf(stmt);
		Statement s = new Statement(stmt,method);
		if(!(parameter instanceof Local)){
			collectedValues.put(new CallSiteWithParamIndex(s,new Val(parameter,method), index, varNameInSpecification), stmt);
			return;
		}
		AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(new AdditionalBoomerangQuery(s,new Val((Local) parameter, method)));
		query.addListener(new QueryListener() {
			@Override
			public void solved(AdditionalBoomerangQuery q, Set<Node<Statement,Val>> res) {
				for(Node<Statement, Val> v : res){
					collectedValues.put(new CallSiteWithParamIndex(s, v.fact(),index, varNameInSpecification), v.stmt().getUnit().get());
				}
			}
		});
	}
	
	public void addAdditionalBoomerangQuery(AdditionalBoomerangQuery q, QueryListener listener){
		AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(q);
		query.addListener(listener);
	}
	
	public class AdditionalBoomerangQuery extends BackwardQuery{
		public AdditionalBoomerangQuery(Statement stmt, Val variable) {
			super(stmt, variable);
		}
		protected boolean solved;
		private List<QueryListener> listeners = Lists.newLinkedList();
		private Set<Node<Statement,Val>> res;
		public void solve() {
			Boomerang boomerang = new Boomerang(){
				@Override
				public BiDiInterproceduralCFG<Unit,SootMethod> icfg() {
					return CryptoTypestateAnaylsisProblem.this.icfg();
				}
			};
			boomerang.solve(this);
			boomerang.getResults();
//			log("Solving query "+ accessGraph + " @ " + stmt);
			res = boomerang.getResults();
			for(QueryListener l : Lists.newLinkedList(listeners)){
				l.solved(this, res);
			}
			solved = true;
		}
		
		public void addListener(QueryListener q){
			if(solved){
				q.solved(this, res);
				return;
			}
			listeners.add(q);
		}
		private CryptoTypestateAnaylsisProblem getOuterType() {
			return CryptoTypestateAnaylsisProblem.this;
		}
	}
	
	public static interface QueryListener{
		public void solved(AdditionalBoomerangQuery q, Set<Node<Statement,Val>> res);
	}
	
	public Multimap<CallSiteWithParamIndex, Unit> getCollectedValues(){
		return collectedValues;
	}

	public void log(String string) {
//		System.out.println(string);
	}

	public Collection<Unit> getInvokedMethodOnInstance(){
		return invokedMethodsOnInstance;
	}

	public void methodInvokedOnInstance(Unit method) {
		invokedMethodsOnInstance.add(method);
	}

	public abstract CrySLAnalysisResultsAggregator analysisListener();
	
	
}