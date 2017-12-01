package crypto.typestate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import boomerang.*;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.debugger.Debugger;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisResultsAggregator;
import crypto.analysis.IAnalysisSeed;
import crypto.rules.StateMachineGraph;
import heros.utilities.DefaultValueMap;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedTimeout;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import wpds.impl.Transition;
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
	private WeightedBoomerang<TransitionFunction> solver;
	
	public ExtendedIDEALAnaylsis(){
		analysis = new IDEALAnalysis<TransitionFunction>(new IDEALAnalysisDefinition<TransitionFunction>() {
			@Override
			public Collection<AllocVal> generate(SootMethod method, Unit stmt, Collection<SootMethod> calledMethod) {
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
			public long analysisBudgetInSeconds() {
				return 0;
			}

			@Override
			public boolean enableStrongUpdates() {
				return true;
			}

			@Override
			public Debugger<TransitionFunction> debugger() {
				return ExtendedIDEALAnaylsis.this.debugger();
			}
		});
	}

	private FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction() {
		if (this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(getStateMachine(), this);
		return this.changeFunction;
	}

	public abstract SootBasedStateMachineGraph getStateMachine();

	public WeightedBoomerang<TransitionFunction> run(Query query) {
		getOrCreateTypestateChangeFunction().injectQueryForSeed(query.stmt());

		try {
			solver = analysis.run(query);
		} catch (IDEALSeedTimeout e){
			System.err.println(e);
			solver = (WeightedBoomerang<TransitionFunction>) e.getSolver();
		}
		CrySLAnalysisResultsAggregator reports = analysisListener();
		for (AdditionalBoomerangQuery q : additionalBoomerangQuery.keySet()) {
			if (reports != null) {
				reports.boomerangQueryStarted(query, q);
			}
			q.solve();
			if (reports != null) {
				reports.boomerangQueryFinished(query, q);
			}
		}
		return solver;
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
			Boomerang boomerang = new Boomerang(new IntAndStringBoomerangOptions(){
				@Override
				public Optional<AllocVal> getAllocationVal(SootMethod m, Stmt stmt, Val fact,
						BiDiInterproceduralCFG<Unit, SootMethod> icfg) {
					if(stmt.containsInvokeExpr() && stmt instanceof AssignStmt){
						AssignStmt as = (AssignStmt) stmt;
						if(as.getLeftOp().equals(fact.value())){
							if(icfg.getCalleesOfCallAt(stmt).isEmpty())
								return Optional.of(new AllocVal(as.getLeftOp(), m, as.getRightOp()));
						}
					}
					return super.getAllocationVal(m, stmt, fact, icfg);
				}
			}) {
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

	public abstract CrySLAnalysisResultsAggregator analysisListener();

	public Set<Node<Statement, AllocVal>> computeInitialSeeds() {
		return analysis.computeSeeds();
	}

	public Table<Statement, Val, TransitionFunction> getResults(IAnalysisSeed seed) {
		return solver.getResults(seed);
	}

	public Map<Node<Statement,AllocVal>, WeightedBoomerang<TransitionFunction>> run() {
		Map<Node<Statement,AllocVal>, WeightedBoomerang<TransitionFunction>> seedToSolver = Maps.newHashMap();
		for (Node<Statement, AllocVal> seed : computeInitialSeeds()) {
			seedToSolver.put(seed, run(new WeightedForwardQuery<>(seed.stmt(),seed.fact(),getStateMachine().getInitialWeight())));
		}
		return seedToSolver;
	}

}