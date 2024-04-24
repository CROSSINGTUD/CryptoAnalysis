package test;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.InvokeExpr;
import boomerang.scene.Method;
import boomerang.scene.Statement;
import boomerang.scene.Val;
import boomerang.scene.jimple.JimpleMethod;
import boomerang.scene.jimple.SootCallGraph;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.CrySLResultsReporter;
import crypto.analysis.CrySLRulesetSelector;
import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.analysis.CryptoScanner;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorVisitor;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ForbiddenPredicateError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.analysis.errors.UncaughtExceptionError;
import crypto.exceptions.CryptoAnalysisException;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.preanalysis.TransformerSetup;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import soot.SceneTransformer;
import soot.options.Options;
import sync.pds.solver.nodes.Node;
import test.assertions.Assertions;
import test.assertions.CallToForbiddenMethodAssertion;
import test.assertions.ConstraintErrorCountAssertion;
import test.assertions.DependentErrorAssertion;
import test.assertions.ExtractedValueAssertion;
import test.assertions.HasEnsuredPredicateAssertion;
import test.assertions.InAcceptingStateAssertion;
import test.assertions.IncompleteOperationErrorCountAssertion;
import test.assertions.MissingTypestateChange;
import test.assertions.NoMissingTypestateChange;
import test.assertions.NotHasEnsuredPredicateAssertion;
import test.assertions.NotInAcceptingStateAssertion;
import test.assertions.PredicateContradiction;
import test.assertions.PredicateErrorCountAssertion;
import test.assertions.StateResult;
import test.assertions.TypestateErrorCountAssertion;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework {

	private CallGraph callGraph;
	private List<CrySLRule> rules = getRules();
	private CryptoScanner scanner;
	
	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {

		// Required since Soot 4.3.0
		Options.v().setPhaseOption("jb.sils", "enabled:false");

		return new SceneTransformer() {

			protected void internalTransform(String phaseName, Map<String, String> options) {
				TransformerSetup.v().setupPreTransformer(rules);

				callGraph = new SootCallGraph();
				final Set<Assertion> expectedResults = extractBenchmarkMethods(JimpleMethod.of(sootTestMethod));
				scanner = new CryptoScanner(excludedPackages()) {

					@Override
					public CrySLResultsReporter getAnalysisListener() {
						CrySLAnalysisListener cryslListener = new CrySLAnalysisListener() {

							@Override
							public void onSeedFinished(IAnalysisSeed seed, ForwardBoomerangResults<TransitionFunction> res) {
								Multimap<Statement, StateResult> expectedTypestateResults = HashMultimap.create();

								for (Assertion a : expectedResults) {
									if (a instanceof StateResult) {
										StateResult stateResult = (StateResult) a;
										expectedTypestateResults.put(stateResult.getStmt(), stateResult);
									}
								}

								for (Map.Entry<Statement, StateResult> entry : expectedTypestateResults.entries()) {
									for (Cell<ControlFlowGraph.Edge, Val, TransitionFunction> cell : res.asStatementValWeightTable().cellSet()) {
										Statement expectedStatement = entry.getKey();
										Val expectedVal = entry.getValue().getVal();

										Statement analysisResultStatement = cell.getRowKey().getStart();
										Val analysisResultVal = cell.getColumnKey();

										if (!analysisResultStatement.equals(expectedStatement) || !analysisResultVal.equals(expectedVal)) {
											continue;
										}

										for (ITransition transition : cell.getValue().values()) {
											if (transition.from() == null || transition.to() == null) {
												continue;
											}

											if (transition.from().isInitialState()) {
												entry.getValue().computedResults(transition.to());
											}
										}
									}
								}
							}

							@Override
							public void collectedValues(AnalysisSeedWithSpecification seed,
									Multimap<CallSiteWithParamIndex, ExtractedValue> collectedValues) {
								for (Assertion a : expectedResults) {
									if (a instanceof ExtractedValueAssertion) {
										ExtractedValueAssertion assertion = (ExtractedValueAssertion) a;
										assertion.computedValues(collectedValues);
									}
								}
							}
							
							@Override
							public void reportError(AbstractError error) {
								for (Assertion a : expectedResults) {
									if (a instanceof DependentErrorAssertion) {
										DependentErrorAssertion depErrorAssertion = (DependentErrorAssertion) a;
										depErrorAssertion.addError(error);
									}
								}

								error.accept(new ErrorVisitor() {
									
									@Override
									public void visit(RequiredPredicateError predicateError) {
										for(Assertion a: expectedResults){
											if(a instanceof PredicateErrorCountAssertion){
												PredicateErrorCountAssertion errorCountAssertion = (PredicateErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}
									
									@Override
									public void visit(TypestateError typestateError) {
										for(Assertion a: expectedResults){
											if(a instanceof TypestateErrorCountAssertion){
												TypestateErrorCountAssertion errorCountAssertion = (TypestateErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}
									
									@Override
									public void visit(IncompleteOperationError incompleteOperationError) {
										boolean hasTypestateChangeError = false;
										boolean expectsTypestateChangeError = false;
										for (Assertion a: expectedResults) {
											if (a instanceof MissingTypestateChange) {
												MissingTypestateChange missingTypestateChange = (MissingTypestateChange) a;
												if (missingTypestateChange.getStmt().equals(incompleteOperationError.getErrorStatement())) {
													missingTypestateChange.trigger();
													hasTypestateChangeError = true;
												}
												expectsTypestateChangeError = true;
											}
											if (a instanceof NoMissingTypestateChange) {
												throw new RuntimeException("Reports a typestate error that should not be reported");
											}

											if (a instanceof IncompleteOperationErrorCountAssertion) {
												IncompleteOperationErrorCountAssertion errorCountAssertion = (IncompleteOperationErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
										if(hasTypestateChangeError != expectsTypestateChangeError){
											throw new RuntimeException("Reports a typestate error that should not be reported");
										}
									}
									
									@Override
									public void visit(ForbiddenMethodError abstractError) {
										for(Assertion e : expectedResults){
											if(e instanceof CallToForbiddenMethodAssertion){
												CallToForbiddenMethodAssertion expectedResults = (CallToForbiddenMethodAssertion) e;
												expectedResults.reported(abstractError.getErrorStatement());
											}
										}
									}
									
									@Override
									public void visit(ConstraintError constraintError) {
										for(Assertion a: expectedResults){
											if(a instanceof ConstraintErrorCountAssertion){
												ConstraintErrorCountAssertion errorCountAssertion = (ConstraintErrorCountAssertion) a;
												errorCountAssertion.increaseCount();
											}
										}
									}

									@Override
									public void visit(ImpreciseValueExtractionError predicateError) {

									}

									@Override
									public void visit(NeverTypeOfError predicateError) {
										
									}

									@Override
									public void visit(PredicateContradictionError predicateContradictionError) {
										for (Assertion e : expectedResults) {
											if (e instanceof PredicateContradiction) {
												PredicateContradiction p = (PredicateContradiction) e;
												p.trigger();
											}
										}
									}

									@Override
									public void visit(UncaughtExceptionError uncaughtExceptionError) {
										
									}
									@Override
									public void visit(HardCodedError predicateError) {
										
									}

									@Override
									public void visit(ForbiddenPredicateError forbiddenPredicateError) {
										
									}
								});
							}

							@Override
							public void discoveredSeed(IAnalysisSeed curr) {
								
							}

							@Override
							public void ensuredPredicates(Table<Statement, Val, Set<EnsuredCrySLPredicate>> existingPredicates,
									Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> expectedPredicates,
									Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> missingPredicates) {
								for(Cell<Statement, Val, Set<EnsuredCrySLPredicate>> c : existingPredicates.cellSet()){
									for(Assertion e : expectedResults){
										if(e instanceof HasEnsuredPredicateAssertion){
											HasEnsuredPredicateAssertion assertion = (HasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey())){
												for(EnsuredCrySLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
										if(e instanceof NotHasEnsuredPredicateAssertion){
											NotHasEnsuredPredicateAssertion assertion = (NotHasEnsuredPredicateAssertion) e;
											if(assertion.getStmt().equals(c.getRowKey())){
												for(EnsuredCrySLPredicate pred : c.getValue()){
													assertion.reported(c.getColumnKey(),pred);
												}	
											}
										}
									}
								}
							}

							@Override
							public void seedStarted(IAnalysisSeed analysisSeedWithSpecification) {
								
							}

							@Override
							public void boomerangQueryStarted(Query seed, BackwardQuery q) {
								
							}

							@Override
							public void boomerangQueryFinished(Query seed, BackwardQuery q) {
								
							}


							@Override
							public void checkedConstraints(AnalysisSeedWithSpecification analysisSeedWithSpecification,
									Collection<ISLConstraint> relConstraints) {
								
							}

							@Override
							public void beforeAnalysis() {
								
							}

							@Override
							public void afterAnalysis() {
								
							}

							@Override
							public void beforeConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void afterConstraintCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void beforePredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void afterPredicateCheck(AnalysisSeedWithSpecification analysisSeedWithSpecification) {
								
							}

							@Override
							public void onSeedTimeout(Node<ControlFlowGraph.Edge, Val> seed) {
								
							}

							@Override
							public void onSecureObjectFound(IAnalysisSeed analysisObject) {
								
							}

							@Override
							public void addProgress(int processedSeeds, int workListSize) {
								
							}
							

						};
						CrySLResultsReporter reporters = new CrySLResultsReporter();
						reporters.addReportListener(cryslListener);
						return reporters;
					}
				};
				scanner.scan(rules);
				
				List<Assertion> unsound = Lists.newLinkedList();
				List<Assertion> imprecise = Lists.newLinkedList();
				for (Assertion r : expectedResults) {
					if (!r.isSatisfied()) {
						unsound.add(r);
					}
				}
				for (Assertion r : expectedResults) {
					if (r.isImprecise()) {
						imprecise.add(r);
					}
				}
				if (!unsound.isEmpty())
					throw new RuntimeException("Unsound results: \n" + Joiner.on("\n").join(unsound));
				if (!imprecise.isEmpty()) {
					throw new ImprecisionException("Imprecise results: " + Joiner.on("\n").join(imprecise));
				}
			}


		};
	}

	private List<CrySLRule> getRules() {
		if (rules == null) {
			try {
				if (getRulesetPath() == null) {
					rules = CrySLRulesetSelector.makeFromRuleset(TestConstants.RULES_BASE_DIR, getRuleSet());
				} else {
					rules = CrySLRulesetSelector.makeFromRulesetPath(TestConstants.RULES_TEST_DIR + getRulesetPath());
				}
			} catch (CryptoAnalysisException e) {
				throw new RuntimeException(e);
			}
		}
		return rules;
	}

	@Override
	public List<String> getIncludeList() {
		return new ArrayList<>();
	}

	@Override
	public List<String> excludedPackages() {
		List<String> excludedPackages = super.excludedPackages();
		excludedPackages.add("java.lang.String");

		for (CrySLRule r : rules) {
			excludedPackages.add(r.getClassName());
		}
		return excludedPackages;
	}
	
	protected abstract Ruleset getRuleSet();

	protected String getRulesetPath() {
		return null;
	}


	private Set<Assertion> extractBenchmarkMethods(Method testMethod) {
		Set<Assertion> results = new HashSet<>();
		extractBenchmarkMethods(testMethod, results, new HashSet<>());
		return results;
	}

	private void extractBenchmarkMethods(Method method, Set<Assertion> queries, Set<Method> visited) {
		if (visited.contains(method)) {
			return;
		}
		visited.add(method);

		for (CallGraph.Edge callSite : callGraph.edgesInto(method)) {
			Method callee = callSite.tgt();
			extractBenchmarkMethods(callee, queries, visited);
		}

		for (Statement statement : method.getStatements()) {
			if (!statement.containsInvokeExpr()) {
				continue;
			}

			InvokeExpr invokeExpr = statement.getInvokeExpr();

			if (!invokeExpr.getMethod().getDeclaringClass().toString().equals(Assertions.class.getName())) {
				continue;
			}

			String invocationName = invokeExpr.getMethod().getName();

			if (invocationName.startsWith("extValue")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isIntConstant()) {
					continue;
				}

				for (Statement pred : getPredecessorsNotBenchmark(statement)) {
					queries.add(new ExtractedValueAssertion(pred, param.getIntValue()));
				}
			}

			if (invocationName.startsWith("callToForbiddenMethod")) {
				for (Statement pred : getPredecessorsNotBenchmark(statement)) {
					queries.add(new CallToForbiddenMethodAssertion(pred));
				}
			}
			if (invocationName.startsWith("mustBeInAcceptingState")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isLocal()) {
					continue;
				}
				queries.add(new InAcceptingStateAssertion(statement, param));
			}
			
			//if (invocationName.startsWith("violatedConstraint")) {
			//	queries.add(new ConstraintViolationAssertion(statement));
			//}

			if (invocationName.startsWith("hasEnsuredPredicate")){
				Val param = invokeExpr.getArg(0);
				if (!param.isLocal()) {
					continue;
				}

				if (invokeExpr.getArgs().size() == 2) {
					// predicate name is passed as parameter
					Val predNameParam = invokeExpr.getArg(1);
					if (!(predNameParam.isStringConstant())) {
						continue;
					}
					String predName = param.getStringValue();
					queries.add(new HasEnsuredPredicateAssertion(statement, param, predName));
				} else {
					queries.add(new HasEnsuredPredicateAssertion(statement, param));
				}
			}
			
			if (invocationName.startsWith("notHasEnsuredPredicate")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isLocal()) {
					continue;
				}

				if (invokeExpr.getArgs().size()== 2) {
					// predicate name is passed as parameter
					Val predNameParam = invokeExpr.getArg(1);
					if (!(predNameParam.isStringConstant())) {
						continue;
					}
					String predName = predNameParam.getStringValue();
					queries.add(new NotHasEnsuredPredicateAssertion(statement, param, predName));
				} else {
					queries.add(new NotHasEnsuredPredicateAssertion(statement, param));
				}
			}
			
			if (invocationName.startsWith("mustNotBeInAcceptingState")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isLocal()) {
					continue;
				}
				queries.add(new NotInAcceptingStateAssertion(statement, param));
			}

			if (invocationName.startsWith("predicateContradiction")) {
				queries.add(new PredicateContradiction());
			}

			if (invocationName.startsWith("missingTypestateChange")) {
				for (Statement pred : getPredecessorsNotBenchmark(statement)) {
					queries.add(new MissingTypestateChange(pred));
				}
			}

			if (invocationName.startsWith("noMissingTypestateChange")) {
				for (Statement pred : getPredecessorsNotBenchmark(statement)) {
					queries.add(new NoMissingTypestateChange(pred));
				}
			}
			
			if (invocationName.startsWith("predicateErrors")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isIntConstant()) {
					continue;
				}
				queries.add(new PredicateErrorCountAssertion(param.getIntValue()));
			}

			if (invocationName.startsWith("constraintErrors")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isIntConstant()) {
					continue;
				}
				queries.add(new ConstraintErrorCountAssertion(param.getIntValue()));
			}

			if (invocationName.startsWith("typestateErrors")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isIntConstant()) {
					continue;
				}
				queries.add(new TypestateErrorCountAssertion(param.getIntValue()));
			}

			if (invocationName.startsWith("incompleteOperationErrors")) {
				Val param = invokeExpr.getArg(0);
				if (!param.isIntConstant()) {
					continue;
				}
				queries.add(new IncompleteOperationErrorCountAssertion(param.getIntValue()));
			}

			if (invocationName.startsWith("dependentError")) {
				// extract parameters
				List<Val> params = invokeExpr.getArgs();
				if (!params.stream().allMatch(Val::isIntConstant)) {
					continue;
				}
				int thisErrorID = params.remove(0).getIntValue();
				int[] precedingErrorIDs = params.stream().mapToInt(Val::getIntValue).toArray();
				for (Statement pred : getPredecessorsNotBenchmark(statement)) {
					queries.add(new DependentErrorAssertion(pred, thisErrorID, precedingErrorIDs));
				}
			}

			// connect DependentErrorAssertions
			Set<Assertion> depErrors = queries.stream().filter(q -> q instanceof DependentErrorAssertion).collect(Collectors.toSet());
			depErrors.forEach(ass -> ((DependentErrorAssertion)ass).registerListeners(depErrors));
		}
	}

	private Set<Statement> getPredecessorsNotBenchmark(Statement stmt) {
		Set<Statement> res = Sets.newHashSet();
		Set<Statement> visited = Sets.newHashSet();
		LinkedList<Statement> workList = Lists.newLinkedList();
		workList.add(stmt);

		while (!workList.isEmpty()) {
			Statement curr = workList.poll();

			if (!visited.add(curr)) {
				continue;
			}

			if (curr.containsInvokeExpr()) {
				String invokedClassName = curr.getInvokeExpr().getMethod().getDeclaringClass().getName();
				String assertionClassName = Assertions.class.getName();

				if (!invokedClassName.equals(assertionClassName)) {
					res.add(curr);
					continue;
				}
			}

			Collection<Statement> preds = stmt.getMethod().getControlFlowGraph().getPredsOf(curr);
			workList.addAll(preds);
		}
		return res;
	}
}
