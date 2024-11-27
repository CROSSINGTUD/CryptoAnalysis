package crypto.analysis;

import boomerang.Query;
import boomerang.debugger.Debugger;
import boomerang.scene.CallGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Method;
import boomerang.scene.WrappedClass;
import boomerang.scene.sparse.SparseCFGCache;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.listener.AnalysisPrinter;
import crypto.listener.AnalysisReporter;
import crypto.listener.AnalysisStatistics;
import crypto.listener.ErrorCollector;
import crypto.listener.IAnalysisListener;
import crypto.listener.IErrorListener;
import crypto.listener.IResultsListener;
import crysl.CrySLParser;
import crysl.rule.CrySLRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import typestate.TransitionFunction;

public abstract class CryptoScanner {

    private final AnalysisReporter analysisReporter;
    private final AnalysisPrinter analysisPrinter;
    private final ErrorCollector errorCollector;
    private final Map<IAnalysisSeed, IAnalysisSeed> discoveredSeeds;
    private final PredicateHandler predicateHandler;

    private CallGraph callGraph;
    private Collection<CrySLRule> ruleset;
    private DataFlowScope dataFlowScope;

    protected CryptoScanner() {
        this.analysisReporter = new AnalysisReporter();
        this.discoveredSeeds = new HashMap<>();
        this.predicateHandler = new PredicateHandler(this);

        analysisPrinter = new AnalysisPrinter();
        addAnalysisListener(analysisPrinter);

        errorCollector = new ErrorCollector();
        addErrorListener(errorCollector);
    }

    protected final void initialize() {
        // Read the ruleset
        analysisReporter.beforeReadingRuleset(getRulesetPath());
        try {
            CrySLParser parser = new CrySLParser();
            ruleset = parser.parseRulesFromDirectory(getRulesetPath());
        } catch (IOException e) {
            throw new CryptoAnalysisException("Could not read rules: " + e.getMessage());
        }
        analysisReporter.afterReadingRuleset(getRulesetPath(), ruleset);

        // Construct the call graph
        analysisReporter.beforeCallGraphConstruction();
        callGraph = constructCallGraph();
        analysisReporter.afterCallGraphConstruction(callGraph);

        // Initialize the dataflow scope
        dataFlowScope = createDataFlowScope();
    }

    protected final void scan() {
        // Check whether the fields have been initialized correctly
        if (ruleset == null) {
            throw new CryptoAnalysisException(
                    "Cannot start the scan. The ruleset must not be null");
        }
        if (callGraph == null) {
            throw new CryptoAnalysisException(
                    "Cannot start the scan. The call graph must not be null");
        }
        if (dataFlowScope == null) {
            throw new CryptoAnalysisException(
                    "Cannot start the scan. The dataflow scope must not be null");
        }

        // Start analysis
        analysisReporter.beforeAnalysis();

        SeedGenerator generator = new SeedGenerator(this, ruleset);
        List<IAnalysisSeed> seeds = new ArrayList<>(generator.computeSeeds());
        analysisReporter.onDiscoveredSeeds(seeds);

        for (IAnalysisSeed seed : seeds) {
            discoveredSeeds.put(seed, seed);
        }

        for (int i = 0; i < seeds.size(); i++) {
            seeds.get(i).execute();
            analysisReporter.addProgress(i + 1, seeds.size());
        }

        analysisReporter.beforePredicateCheck();
        predicateHandler.checkPredicates();
        analysisReporter.afterPredicateCheck();

        analysisReporter.afterAnalysis();
    }

    public final Collection<CrySLRule> getRuleset() {
        return ruleset;
    }

    public final CallGraph getCallGraph() {
        return callGraph;
    }

    public final DataFlowScope getDataFlowScope() {
        return dataFlowScope;
    }

    public final void addAnalysisListener(IAnalysisListener analysisListener) {
        analysisReporter.addAnalysisListener(analysisListener);
    }

    public final void addErrorListener(IErrorListener errorListener) {
        analysisReporter.addErrorListener(errorListener);
    }

    public final void addResultsListener(IResultsListener resultsListener) {
        analysisReporter.addResultsListener(resultsListener);
    }

    public final AnalysisReporter getAnalysisReporter() {
        return analysisReporter;
    }

    public final Table<WrappedClass, Method, Set<AbstractError>> getCollectedErrors() {
        return errorCollector.getErrorCollection();
    }

    public final Collection<IAnalysisSeed> getDiscoveredSeeds() {
        return discoveredSeeds.keySet();
    }

    public final Collection<AnalysisSeedWithSpecification> getAnalysisSeedsWithSpec() {
        Collection<AnalysisSeedWithSpecification> seeds = new HashSet<>();

        for (IAnalysisSeed seed : discoveredSeeds.keySet()) {
            if (seed instanceof AnalysisSeedWithSpecification) {
                seeds.add((AnalysisSeedWithSpecification) seed);
            }
        }
        return seeds;
    }

    public final AnalysisStatistics getStatistics() {
        return analysisPrinter.getStatistics();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *               Methods that may or must be overridden by subclasses                *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public abstract String getRulesetPath();

    protected abstract CallGraph constructCallGraph();

    protected DataFlowScope createDataFlowScope() {
        return new CryptoAnalysisDataFlowScope(ruleset, Collections.emptySet());
    }

    public Debugger<TransitionFunction> debugger(Query query) {
        return new Debugger<>();
    }

    public SparseCFGCache.SparsificationStrategy getSparsificationStrategy() {
        return SparseCFGCache.SparsificationStrategy.NONE;
    }

    public int getTimeout() {
        return 10000;
    }
}
