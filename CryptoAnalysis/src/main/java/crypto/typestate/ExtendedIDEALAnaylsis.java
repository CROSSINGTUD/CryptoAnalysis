package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.BoomerangOptions;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.IAnalysisSeed;
import crypto.boomerang.CogniCryptBoomerangOptions;
import crypto.boomerang.CogniCryptIntAndStringBoomerangOptions;
import heros.utilities.DefaultValueMap;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedSolver;
import ideal.IDEALSeedTimeout;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.util.queue.QueueReader;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;
import wpds.impl.Weight.NoWeight;

public abstract class ExtendedIDEALAnaylsis {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private Multimap<CallSiteWithParamIndex, Statement> collectedValues = HashMultimap.create();
	private Set<Statement> invokedMethodsOnInstance = Sets.newHashSet();
	private DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery> additionalBoomerangQuery = new DefaultValueMap<AdditionalBoomerangQuery, AdditionalBoomerangQuery>() {
		@Override
		protected AdditionalBoomerangQuery createItem(AdditionalBoomerangQuery key) {
			return key;
		}
	};
	private final IDEALAnalysis<TransitionFunction> analysis;
	private Table<Statement, Val, TransitionFunction> results = HashBasedTable.create();
	
	public ExtendedIDEALAnaylsis(){
		analysis = new IDEALAnalysis<TransitionFunction>(new IDEALAnalysisDefinition<TransitionFunction>() {
			@Override
			public Collection<WeightedForwardQuery<TransitionFunction>> generate(SootMethod method, Unit stmt, Collection<SootMethod> calledMethod) {
				return getOrCreateTypestateChangeFunction().generateSeed(method, stmt, calledMethod);
			}

			@Override
			public WeightFunctions<Statement, Val, Statement, TransitionFunction> weightFunctions() {
				return getOrCreateTypestateChangeFunction();
			}

			@Override
			public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
				return ExtendedIDEALAnaylsis.this.icfg();
			}

			@Override
			public boolean enableStrongUpdates() {
				return true;
			}

			@Override
			public Debugger<TransitionFunction> debugger() {
				return ExtendedIDEALAnaylsis.this.debugger();
			}
			@Override
			public BoomerangOptions boomerangOptions() {
				return new CogniCryptBoomerangOptions();
			}
		});
	}

	private FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction() {
		if (this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(getStateMachine(), this);
		return this.changeFunction;
	}

	public abstract SootBasedStateMachineGraph getStateMachine();

	public void run(ForwardQuery query) {
		getOrCreateTypestateChangeFunction().injectQueryForSeed(query.stmt());

		CrySLResultsReporter reports = analysisListener();
		try {
			results = analysis.run(query).getResults();
		} catch (IDEALSeedTimeout e){
			System.err.println(e);
//			solver = (IDEALSeedSolver<TransitionFunction>) e.getSolver();
			if (reports != null && query instanceof IAnalysisSeed) {
				reports.onSeedTimeout(((IAnalysisSeed)query).asNode());
			}
		}
		for (AdditionalBoomerangQuery q : additionalBoomerangQuery.keySet()) {
			if (reports != null) {
				reports.boomerangQueryStarted(query, q);
			}
			q.solve();
			if (reports != null) {
				reports.boomerangQueryFinished(query, q);
			}
		}
	}


	public void addQueryAtCallsite(final String varNameInSpecification, final Statement stmt, final int index) {
		if(!stmt.isCallsite())
			return;
		Value parameter = stmt.getUnit().get().getInvokeExpr().getArg(index);
		if (!(parameter instanceof Local)) {
			collectedValues.put(
					new CallSiteWithParamIndex(stmt, new Val(parameter, stmt.getMethod()), index, varNameInSpecification), stmt);
			return;
		}
		AdditionalBoomerangQuery query = additionalBoomerangQuery
				.getOrCreate(new AdditionalBoomerangQuery(stmt, new Val((Local) parameter, stmt.getMethod())));
		query.addListener(new QueryListener() {
			@Override
			public void solved(AdditionalBoomerangQuery q, Table<Statement, Val, NoWeight> res) {
				for (Cell<Statement, Val, NoWeight> v : res.cellSet()) {
					collectedValues.put(new CallSiteWithParamIndex(stmt, v.getColumnKey(), index, varNameInSpecification),
							v.getRowKey());
				}
			}
		});
	}

	protected abstract BiDiInterproceduralCFG<Unit, SootMethod> icfg();
	protected abstract Debugger<TransitionFunction> debugger();

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
		private Table<Statement, Val, NoWeight> res;

		public void solve() {
			Boomerang boomerang = new Boomerang(new CogniCryptIntAndStringBoomerangOptions()) {
				@Override
				public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
					return ExtendedIDEALAnaylsis.this.icfg();
				}
			};
			boomerang.solve(this);
			boomerang.debugOutput();
			// log("Solving query "+ accessGraph + " @ " + stmt);
			res = boomerang.getResults(this);
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

		private ExtendedIDEALAnaylsis getOuterType() {
			return ExtendedIDEALAnaylsis.this;
		}
	}

	public static interface QueryListener {
		public void solved(AdditionalBoomerangQuery q, Table<Statement, Val, NoWeight> res);
	}

	public Multimap<CallSiteWithParamIndex, Statement> getCollectedValues() {
		return collectedValues;
	}

	public void log(String string) {
		// System.out.println(string);
	}

	public Collection<Statement> getInvokedMethodOnInstance() {
		return invokedMethodsOnInstance;
	}

	public void methodInvokedOnInstance(Statement method) {
		invokedMethodsOnInstance.add(method);
	}

	public abstract CrySLResultsReporter analysisListener();

//	public Collection<Query> computeInitialSeeds() {
	//TODO Why does this version not terminate?
//		return analysis.computeSeeds();
//	}

    public Set<WeightedForwardQuery<TransitionFunction>> computeInitialSeeds() {
        Set<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();
        ReachableMethods rm = Scene.v().getReachableMethods();
        QueueReader<MethodOrMethodContext> listener = rm.listener();
        while (listener.hasNext()) {
            MethodOrMethodContext next = listener.next();
            seeds.addAll(computeSeeds(next.method()));
        }
        return seeds;
    }

    private Collection<WeightedForwardQuery<TransitionFunction>> computeSeeds(SootMethod method) {
    	Collection<WeightedForwardQuery<TransitionFunction>> seeds = new HashSet<>();
        if (!method.hasActiveBody())
            return seeds;
        for (Unit u : method.getActiveBody().getUnits()) {
            Collection<SootMethod> calledMethods = (icfg().isCallStmt(u) ? icfg().getCalleesOfCallAt(u)
                    : new HashSet<SootMethod>());
            seeds.addAll( getOrCreateTypestateChangeFunction().generateSeed(method, u, calledMethods));
        }
        return seeds;
    }


	public Map<WeightedForwardQuery<TransitionFunction>, Table<Statement, Val, TransitionFunction>> run() {
		Map<WeightedForwardQuery<TransitionFunction>, Table<Statement,Val,TransitionFunction>> seedToSolver = Maps.newHashMap();
		for (Query s : computeInitialSeeds()) {
			if(s instanceof WeightedForwardQuery){
				WeightedForwardQuery seed = (WeightedForwardQuery) s;
				run((WeightedForwardQuery<TransitionFunction>)seed);
				seedToSolver.put(seed, getResults());
			}
		}
		return seedToSolver;
	}

	public Table<Statement, Val, TransitionFunction> getResults() {
		return results;
	}

}