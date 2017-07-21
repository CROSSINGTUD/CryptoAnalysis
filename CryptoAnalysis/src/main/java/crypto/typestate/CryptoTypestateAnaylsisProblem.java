package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import boomerang.AliasFinder;
import boomerang.AliasResults;
import boomerang.BoomerangOptions;
import boomerang.accessgraph.AccessGraph;
import boomerang.allocationsitehandler.PrimitiveTypeAndReferenceType;
import boomerang.cfg.IExtendedICFG;
import boomerang.context.AllCallersRequester;
import boomerang.pointsofindirection.AllocationSiteHandlers;
import crypto.analysis.CryptSLAnalysisListener;
import crypto.analysis.CryptoScanner;
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
import typestate.TypestateAnalysisProblem;
import typestate.TypestateDomainValue;

public abstract class CryptoTypestateAnaylsisProblem extends TypestateAnalysisProblem<StateNode> {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private Multimap<CallSiteWithParamIndex,Unit> collectedValues = HashMultimap.create(); 
	private Set<SootMethod> invokedMethodsOnInstance = Sets.newHashSet();
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
		CryptSLAnalysisListener listener = analysisListener();
		for(AdditionalBoomerangQuery q : additionalBoomerangQuery.values()){
			if(listener != null){
				listener.boomerangQueryStarted(seed,q);
			}
			q.solve();
			if(listener != null){
				listener.boomerangQueryFinished(seed,q);
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
			public void solved(AdditionalBoomerangQuery q, AliasResults res) {
				for(Pair<Unit, AccessGraph> v : res.keySet()){
					if(v.getO2().hasAllocationSite())
						collectedValues.put(new CallSiteWithParamIndex(stmt, q.accessGraph,index, varNameInSpecification), v.getO2().getSourceStmt());
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
		protected final Unit stmt;
		private final AccessGraph accessGraph;
		private final AccessGraph context;
		private List<QueryListener> listeners = Lists.newLinkedList();
		private AliasResults res;
		public AdditionalBoomerangQuery(AccessGraph context, Unit stmt, AccessGraph ag){
			this.context = context;
			this.stmt = stmt;
			this.accessGraph = ag;
		}
		public void solve() {
			AliasFinder boomerang = new AliasFinder(new BoomerangOptions() {
				@Override
				public IExtendedICFG icfg() {
					return CryptoTypestateAnaylsisProblem.this.icfg();
				}
				
				@Override
				public AllocationSiteHandlers allocationSiteHandlers() {
					return new PrimitiveTypeAndReferenceForCryptoType();
				}				
			});
			boomerang.startQuery();
			log("Solving query "+ accessGraph + " @ " + stmt);
			res = boomerang.findAliasAtStmt(accessGraph, stmt, new AllCallersRequester());
			for(QueryListener l : Lists.newLinkedList(listeners)){
				l.solved(this, res);
			}
			log("Solved query "+ accessGraph + " @ " + stmt + " with "+  res);
			solved = true;
		}
		
		public void addListener(QueryListener q){
			if(solved){
				q.solved(this, res);
				return;
			}
			listeners.add(q);
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((accessGraph == null) ? 0 : accessGraph.hashCode());
			result = prime * result + ((context == null) ? 0 : context.hashCode());
			result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
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
			AdditionalBoomerangQuery other = (AdditionalBoomerangQuery) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (accessGraph == null) {
				if (other.accessGraph != null)
					return false;
			} else if (!accessGraph.equals(other.accessGraph))
				return false;
			if (context == null) {
				if (other.context != null)
					return false;
			} else if (!context.equals(other.context))
				return false;
			if (stmt == null) {
				if (other.stmt != null)
					return false;
			} else if (!stmt.equals(other.stmt))
				return false;
			return true;
		}
		private CryptoTypestateAnaylsisProblem getOuterType() {
			return CryptoTypestateAnaylsisProblem.this;
		}
	}
	
	public static interface QueryListener{
		public void solved(AdditionalBoomerangQuery q, AliasResults res);
	}
	
	public Multimap<CallSiteWithParamIndex, Unit> getCollectedValues(){
		return collectedValues;
	}

	public void log(String string) {
//		System.out.println(string);
	}

	public Collection<SootMethod> getInvokedMethodOnInstance(){
		return invokedMethodsOnInstance;
	}

	public void methodInvokedOnInstance(SootMethod method) {
		invokedMethodsOnInstance.add(method);
	}
	
	public CryptSLAnalysisListener analysisListener(){
		return null;
	}

}