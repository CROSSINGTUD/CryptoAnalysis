package crypto.typestate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

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
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALSeedSolver;
import ideal.IDEALSeedTimeout;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.util.queue.QueueReader;
import sync.pds.solver.WeightFunctions;
import typestate.TransitionFunction;

public abstract class ExtendedIDEALAnaylsis {

	private FiniteStateMachineToTypestateChangeFunction changeFunction;
	private final IDEALAnalysis<TransitionFunction> analysis;
	private ForwardBoomerangResults<TransitionFunction> results;
	
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
			public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
				return ExtendedIDEALAnaylsis.this.debugger(solver);
			}
			@Override
			public BoomerangOptions boomerangOptions() {
				return new CogniCryptBoomerangOptions();
			}
		});
	}

	private FiniteStateMachineToTypestateChangeFunction getOrCreateTypestateChangeFunction() {
		if (this.changeFunction == null)
			this.changeFunction = new FiniteStateMachineToTypestateChangeFunction(getStateMachine());
		return this.changeFunction;
	}

	public abstract SootBasedStateMachineGraph getStateMachine();

	public void run(ForwardQuery query) {
		CrySLResultsReporter reports = analysisListener();
		try {
			results = analysis.run(query);
		} catch (IDEALSeedTimeout e){
//			System.err.println(e);
//			solver = (IDEALSeedSolver<TransitionFunction>) e.getSolver();
			if (reports != null && query instanceof IAnalysisSeed) {
				reports.onSeedTimeout(((IAnalysisSeed)query).asNode());
			}
		}
	}


	protected abstract BiDiInterproceduralCFG<Unit, SootMethod> icfg();
	protected abstract Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver);

	public void log(String string) {
		// System.out.println(string);
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


	public Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> run() {
		Map<WeightedForwardQuery<TransitionFunction>, ForwardBoomerangResults<TransitionFunction>> seedToSolver = Maps.newHashMap();
		for (Query s : computeInitialSeeds()) {
			if(s instanceof WeightedForwardQuery){
				WeightedForwardQuery<TransitionFunction> seed = (WeightedForwardQuery<TransitionFunction>) s;
				run((WeightedForwardQuery<TransitionFunction>)seed);
				if(getResults() != null){
					seedToSolver.put(seed, getResults());
				}
			}
		}
		return seedToSolver;
	}

	public ForwardBoomerangResults<TransitionFunction> getResults() {
		return results;
	}

}