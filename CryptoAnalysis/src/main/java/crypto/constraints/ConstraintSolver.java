package crypto.constraints;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Statement;
import boomerang.scene.Type;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import crypto.analysis.AlternativeReqPredicate;
import crypto.analysis.AnalysisReporter;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.RequiredCrySLPredicate;
import crypto.analysis.errors.AbstractError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstraintSolver {

	public final static List<String> predefinedPreds = Arrays.asList("callTo", "noCallTo", "neverTypeOf", "length",
			"notHardCoded", "instanceOf");
	private final Set<ISLConstraint> relConstraints = Sets.newHashSet();
	private final List<ISLConstraint> requiredPredicates = Lists.newArrayList();
	private final Collection<Statement> collectedCalls;
	private final AnalysisReporter analysisReporter;
	private final AnalysisSeedWithSpecification object;

	public ConstraintSolver(AnalysisSeedWithSpecification object, Collection<ControlFlowGraph.Edge> callsOnObject, AnalysisReporter analysisReporter) {
		this.object = object;
		this.analysisReporter = analysisReporter;

		this.collectedCalls = new HashSet<>();
		for (ControlFlowGraph.Edge edge : callsOnObject) {
			collectedCalls.add(edge.getStart());
		}

		partitionConstraints();
	}

	public Multimap<CallSiteWithParamIndex, Type> getPropagatedTypes() {
		return this.object.getParameterAnalysis().getPropagatedTypes();
	}

	public Collection<CallSiteWithParamIndex> getParameterAnalysisQuerySites() {
		return this.object.getParameterAnalysis().getAllQuerySites();
	}

	public CrySLRule getSpecification() {
		return this.object.getSpecification();
	}

	public Collection<Statement> getCollectedCalls() {
		return collectedCalls;
	}

	public AnalysisSeedWithSpecification getObject() {
		return object;
	}

	public Multimap<CallSiteWithParamIndex, ExtractedValue> getParsAndVals() {
		return this.object.getParameterAnalysis().getCollectedValues();
	}

	/**
	 * @return the allConstraints
	 */
	public List<ISLConstraint> getAllConstraints() {
		return getSpecification().getConstraints();
	}

	/**
	 * @return the relConstraints
	 */
	public Set<ISLConstraint> getRelConstraints() {
		return relConstraints;
	}

	public List<ISLConstraint> getRequiredPredicates() {
		return requiredPredicates;
	}

	public int evaluateRelConstraints() {
		int fail = 0;
		for (ISLConstraint con : getRelConstraints()) {
			EvaluableConstraint currentConstraint = EvaluableConstraint.getInstance(con, this);
			currentConstraint.evaluate();
			for (AbstractError error : currentConstraint.getErrors()) {
				analysisReporter.reportError(object, error);
				fail++;
				/*if (e instanceof ImpreciseValueExtractionError) {
					reporter.reportError(getObject(), new ImpreciseValueExtractionError(con, e.getErrorStatement(), e.getRule()));
					fail++;
					//break;
				} else {
					fail++;
					this.object.addError(e);
					getReporter().reportError(getObject(), e);
				}*/
			}
		}
		return fail;
	}

	/**
	 * (Probably) partitions Cosntraints into required Predicates and "normal"
	 * constraints (relConstraints).
	 */
	private void partitionConstraints() {
		for (ISLConstraint cons : getAllConstraints()) {
			Set<String> involvedVarNames = cons.getInvolvedVarNames();

			for (CallSiteWithParamIndex cwpi : this.getParameterAnalysisQuerySites()) {
				involvedVarNames.remove(cwpi.getVarName());
			}

			if (!involvedVarNames.isEmpty()) {
				continue;
			}// || (cons.toString().contains("speccedKey") && involvedVarNames.size() == 1)) {

			if (cons instanceof CrySLPredicate) {
				CrySLPredicate predicate = (CrySLPredicate) cons;
				if (predefinedPreds.contains(predicate.getPredName())) {
					relConstraints.add(predicate);
					continue;
				}

				List<RequiredCrySLPredicate> preds = retrieveValuesForPred(predicate);

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
					requiredPredicates.addAll(collectAlternativePredicates((CrySLConstraint) cons, Lists.newArrayList()));
				} else {
					relConstraints.add(cons);
				}
			} else {
				relConstraints.add(cons);
			}
		}
	}

	private List<AlternativeReqPredicate> collectAlternativePredicates(CrySLConstraint cons, List<AlternativeReqPredicate> alts) {
		CrySLPredicate left = (CrySLPredicate) cons.getLeft();
		
		if (alts.isEmpty()) {
			for (CallSiteWithParamIndex cwpi : this.getParameterAnalysisQuerySites()) {
				for (ICrySLPredicateParameter p : left.getParameters()) {
					if (p.getName().equals("transformation"))
						continue;
					if (cwpi.getVarName().equals(p.getName())) {
						alts.add(new AlternativeReqPredicate(left, cwpi.stmt()));
					}
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

	private List<RequiredCrySLPredicate> retrieveValuesForPred(CrySLPredicate pred) {
		List<RequiredCrySLPredicate> result = Lists.newArrayList();
		
		for (CallSiteWithParamIndex cwpi : this.getParameterAnalysisQuerySites()) {
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
					result.add(new RequiredCrySLPredicate(pred, cwpi.stmt()));
				}
			}
		}

		// Extract predicates with 'this' as parameter
		if (pred.getParameters().stream().anyMatch(param -> param.getName().equals("this"))) {
			result.add(new RequiredCrySLPredicate(pred, object.getOrigin()));
		}
		
		return result;
	}
}
