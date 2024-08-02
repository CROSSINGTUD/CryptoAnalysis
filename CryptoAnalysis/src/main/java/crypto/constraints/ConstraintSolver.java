package crypto.constraints;

import boomerang.scene.CallGraph;
import boomerang.scene.ControlFlowGraph;
import boomerang.scene.DataFlowScope;
import boomerang.scene.Statement;
import com.google.common.collect.Lists;
import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisReporter;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.RequiredCrySLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractParameterAnalysis;
import crypto.definition.ExtractParameterDefinition;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import crypto.rules.ICrySLPredicateParameter;
import crypto.rules.ISLConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class ConstraintSolver {

	public static final Collection<String> predefinedPreds = Arrays.asList(
			"callTo", "noCallTo", "neverTypeOf", "length", "notHardCoded", "instanceOf");

	private final AnalysisSeedWithSpecification seed;
	private final Collection<Statement> collectedCalls;
	private final Collection<ISLConstraint> relConstraints;
	private final Collection<ISLConstraint> requiredPredicates;
	private final ExtractParameterAnalysis parameterAnalysis;

	public ConstraintSolver(AnalysisSeedWithSpecification seed) {
		this.seed = seed;

		this.collectedCalls = new HashSet<>();
		for (ControlFlowGraph.Edge edge : seed.getAllCallsOnObject().keySet()) {
			collectedCalls.add(edge.getStart());
		}

		relConstraints = new HashSet<>();
		requiredPredicates = new HashSet<>();

		ExtractParameterDefinition definition = new ExtractParameterDefinition() {
			@Override
			public CallGraph getCallGraph() {
				return seed.getScanner().callGraph();
			}

			@Override
			public DataFlowScope getDataFlowScope() {
				return seed.getScanner().getDataFlowScope();
			}

			@Override
			public Collection<Statement> getCollectedCalls() {
				return collectedCalls;
			}

			@Override
			public CrySLRule getRule() {
				return seed.getSpecification();
			}

			@Override
			public AnalysisReporter getAnalysisReporter() {
				return seed.getScanner().getAnalysisReporter();
			}

			@Override
			public int getTimeout() {
				return seed.getScanner().getTimeout();
			}
		};
		parameterAnalysis = new ExtractParameterAnalysis(definition);
	}

	/**
	 * Evaluate the constraints from the CONSTRAINTS section
	 *
	 * @return the errors that violate the constraints
	 */
	public Collection<AbstractError> evaluateConstraints() {
		// Run Boomerang to find all allocation sites
		extractValuesFromCollectedCalls();
		partitionConstraints();

		return evaluateRelConstraints();
	}

	private void extractValuesFromCollectedCalls() {
		parameterAnalysis.run();
		seed.getScanner().getAnalysisReporter().collectedValues(seed, parameterAnalysis.getExtractedValues());
	}

	public CrySLRule getSpecification() {
		return seed.getSpecification();
	}

	public Collection<Statement> getCollectedCalls() {
		return collectedCalls;
	}

	public AnalysisSeedWithSpecification getSeed() {
		return seed;
	}

	public Collection<CallSiteWithExtractedValue> getCollectedValues() {
		return parameterAnalysis.getExtractedValues();
	}

	public Collection<ISLConstraint> getRelConstraints() {
		return relConstraints;
	}

	public Collection<ISLConstraint> getRequiredPredicates() {
		return requiredPredicates;
	}

	private Collection<AbstractError> evaluateRelConstraints() {
		Collection<AbstractError> violatedConstraints = new HashSet<>();

		for (ISLConstraint con : getRelConstraints()) {
			EvaluableConstraint currentConstraint = EvaluableConstraint.getInstance(con, this);
			currentConstraint.evaluate();

			violatedConstraints.addAll(currentConstraint.getErrors());
		}
		return violatedConstraints;
	}

	/**
	 * Partitions the constraints into relevant constraints from the CONSTRAINTS section
	 * and required predicate constraints from the REQUIRES section
	 */
	private void partitionConstraints() {
		for (ISLConstraint cons : seed.getSpecification().getConstraints()) {
			Collection<String> involvedVarNames = new HashSet<>(cons.getInvolvedVarNames());

			for (CallSiteWithExtractedValue callSite : this.getCollectedValues()) {
				CallSiteWithParamIndex callSiteWithParamIndex = callSite.getCallSiteWithParam();
				involvedVarNames.remove(callSiteWithParamIndex.getVarName());
			}

			if (!involvedVarNames.isEmpty()) {
				continue;
			}

			if (cons instanceof CrySLPredicate) {
				CrySLPredicate predicate = (CrySLPredicate) cons;
				if (predefinedPreds.contains(predicate.getPredName())) {
					relConstraints.add(predicate);
					continue;
				}

				Collection<RequiredCrySLPredicate> preds = retrieveValuesForPred(predicate);

				for (RequiredCrySLPredicate pred : preds) {
					CrySLPredicate innerPred = pred.getPred();

					if (innerPred != null) {
						relConstraints.add(innerPred);
						requiredPredicates.add(pred);
					}
				}
			} else if (cons instanceof CrySLConstraint) {
				ISLConstraint left = ((CrySLConstraint) cons).getLeft();

				if (left instanceof CrySLPredicate && !predefinedPreds.contains(((CrySLPredicate) left).getPredName())) {
					requiredPredicates.addAll(collectAlternativePredicates((CrySLConstraint) cons, new ArrayList<>()));
				} else {
					relConstraints.add(cons);
				}
			} else {
				relConstraints.add(cons);
			}
		}
	}

	private Collection<AlternativeReqPredicate> collectAlternativePredicates(CrySLConstraint cons, Collection<AlternativeReqPredicate> alts) {
		CrySLPredicate left = (CrySLPredicate) cons.getLeft();
		
		if (alts.isEmpty()) {
			for (CallSiteWithExtractedValue callSite : this.getCollectedValues()) {
				CallSiteWithParamIndex cwpi = callSite.getCallSiteWithParam();

				for (ICrySLPredicateParameter p : left.getParameters()) {
					if (p.getName().equals("transformation")) {
						continue;
					}

					if (cwpi.getVarName().equals(p.getName())) {
						alts.add(new AlternativeReqPredicate(left, cwpi.stmt(), cwpi.getIndex()));
					}
				}
			}

			// Extract predicates with 'this' as parameter
			if (left.getParameters().stream().anyMatch(param -> param.getName().equals("this"))) {
				AlternativeReqPredicate altPred = new AlternativeReqPredicate(left, seed.getOrigin(), -1);

				if (!alts.contains(altPred)) {
					alts.add(altPred);
				}
			}
		} else {
			for (AlternativeReqPredicate alt : alts) {
				alt.addAlternative(left);
			}
		}

		if (cons.getRight() instanceof CrySLPredicate) {
			for (AlternativeReqPredicate alt : alts) {
				alt.addAlternative((CrySLPredicate) cons.getRight());
			}
		} else {
			return collectAlternativePredicates((CrySLConstraint) cons.getRight(), alts);
		}

		return alts;
	}

	private Collection<RequiredCrySLPredicate> retrieveValuesForPred(CrySLPredicate pred) {
		Collection<RequiredCrySLPredicate> result = Lists.newArrayList();
		
		for (CallSiteWithExtractedValue callSite : this.getCollectedValues()) {
			CallSiteWithParamIndex cwpi = callSite.getCallSiteWithParam();

			for (ICrySLPredicateParameter p : pred.getParameters()) {
				// TODO: FIX Cipher rule
				if (p.getName().equals("transformation")) {
					continue;
				}
				
				// Predicates with _ can have any type
				if (cwpi.getVarName().equals("_")) {
					continue;
				}
				
				if (cwpi.getVarName().equals(p.getName())) {
					result.add(new RequiredCrySLPredicate(pred, cwpi.stmt(), cwpi.getIndex()));
				}
			}
		}

		// Extract predicates with 'this' as parameter
		if (pred.getParameters().stream().anyMatch(param -> param.getName().equals("this"))) {
			RequiredCrySLPredicate reqPred = new RequiredCrySLPredicate(pred, seed.getOrigin(), -1);

			if (!result.contains(reqPred)) {
				result.add(reqPred);
			}
		}
		
		return result;
	}
}
