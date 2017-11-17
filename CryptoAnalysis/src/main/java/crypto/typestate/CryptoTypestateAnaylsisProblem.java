package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.AliasFinder;
import boomerang.AliasResults;
import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.WeightedBoomerang;
import boomerang.BoomerangOptions;
import boomerang.DefaultBoomerangOptions;
import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import boomerang.context.AllCallersRequester;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.pointsofindirection.AllocationSiteHandlers;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.PrimitiveTypeAndReferenceForCryptoType;
import crypto.rules.StateMachineGraph;
import crypto.rules.StateNode;
import heros.EdgeFunction;
import heros.solver.Pair;
import heros.utilities.DefaultValueMap;
import ideal.AnalysisSolver;
import ideal.IFactAtStatement;
import ideal.NonIdentityEdgeFlowHandler;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.TypestateAnalysisProblem;
import typestate.TypestateDomainValue;

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
	
	@Override
	public FiniteStateMachineToTypestateChangeFunction createTypestateChangeFunction() {
		return new FiniteStateMachineToTypestateChangeFunction(this);
	}

	public FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction(){
		if(this.changeFunction == null)
			this.changeFunction = createTypestateChangeFunction();
		return this.changeFunction;
	}

	public abstract StateMachineGraph getStateMachine(); 
	public NonIdentityEdgeFlowHandler<typestate.TypestateDomainValue<StateNode>> nonIdentityEdgeFlowHandler() {
		return new NonIdentityEdgeFlowHandler<TypestateDomainValue<StateNode>>() {

			@Override
			public void onCallToReturnFlow(AccessGraph d2, Unit callSite, AccessGraph d3, Unit returnSite,
					AccessGraph d1, EdgeFunction<TypestateDomainValue<StateNode>> func) {
			}

			@Override
			public void onReturnFlow(AccessGraph d2, Unit callSite, AccessGraph d3, Unit returnSite, AccessGraph d1,
					EdgeFunction<TypestateDomainValue<StateNode>> func) {
			}
		};
	};
	@Override
	public void onFinishWithSeed(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		CrySLAnalysisResultsAggregator reports = analysisListener();
		for(AdditionalBoomerangQuery q : additionalBoomerangQuery.keySet()){
			if(reports != null){
				reports.boomerangQueryStarted(seed,q);
			}
			q.solve();
			if(reports != null){
				reports.boomerangQueryFinished(seed,q);
			}
		}
	}
	
	@Override
	public void onStartWithSeed(IFactAtStatement seed, AnalysisSolver<TypestateDomainValue<StateNode>> solver) {
		getOrCreateTypestateChangeFunction().injectQueryForSeed(seed.getStmt());
	}
	public void addQueryAtCallsite(final String varNameInSpecification, final Stmt stmt,final int index,final AccessGraph d1) {
		Value parameter = stmt.getInvokeExpr().getArg(index);
		if(!(parameter instanceof Local)){
			collectedValues.put(new CallSiteWithParamIndex(stmt, d1,index, varNameInSpecification), stmt);
			return;
		}
		AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(new AdditionalBoomerangQuery(d1, stmt,new AccessGraph((Local) parameter, parameter.getType())));
		query.addListener(new QueryListener() {
			@Override
			public void solved(AdditionalBoomerangQuery q, Set<Node<Statement,Val>> res) {
				for(Node<Statement, Val> v : res){
					collectedValues.put(new CallSiteWithParamIndex(stmt, v.fact(),index, varNameInSpecification), v.stmt().getUnit().get());
				}
			}
		});
	}
	
	public void addAdditionalBoomerangQuery(AdditionalBoomerangQuery q, QueryListener listener){
		AdditionalBoomerangQuery query = additionalBoomerangQuery.getOrCreate(q);
		query.addListener(listener);
	}
	
	public class AdditionalBoomerangQuery {
		protected boolean solved;
		private final BackwardQuery query;
		private List<QueryListener> listeners = Lists.newLinkedList();
		private Set<Node<Statement,Val>> res;
		public AdditionalBoomerangQuery(Unit stmt, Val val){
		}
		public void solve() {
			Boomerang boomerang = new Boomerang(){
				@Override
				public BiDiInterproceduralCFG<Unit,SootMethod> icfg() {
					return CryptoTypestateAnaylsisProblem.this.icfg();
				}
			};
			boomerang.solve(query);
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
	
}