package crypto.reporting;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.CharMatcher;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCryptSLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorVisitor;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLPredicate;
import soot.SootClass;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

/**
 * This listener is notified of any misuses the analysis finds.
 *
 * @author Stefan Krueger
 * @author Johannes Spaeth
 *
 */
public class ErrorMarkerListener extends CrySLAnalysisListener {

	public static final String NO_RES_FOUND = "No resource to generate error marker for found.";
	public static final String OBJECT_OF_TYPE = "Object of type ";
	public static final String VAR = "Variable ";
	protected final Table<SootClass, SootMethod, Set<ErrorMarker>> errorMarkers = HashBasedTable.create(); 

	private void addMarker(Statement location, String string) {
		SootMethod method = location.getMethod();
		SootClass sootClass = method.getDeclaringClass();
		
		Set<ErrorMarker> set = errorMarkers.get(sootClass, method);
		if(set == null){
			set = Sets.newHashSet();
		}
		set.add(new ErrorMarker(location, string));
		errorMarkers.put(sootClass, method, set);
	}
	
	@Override
	public void reportError(AbstractError error) {
		error.accept(new ErrorVisitor(){

			@Override
			public void visit(ConstraintError constraintError) {
				addMarker(constraintError.getErrorLocation(), constraintError.toErrorMarkerString());
			}

			@Override
			public void visit(ForbiddenMethodError forbiddenMethodError) {
				addMarker(forbiddenMethodError.getErrorLocation(), forbiddenMethodError.toErrorMarkerString());
			}

			@Override
			public void visit(IncompleteOperationError incompleteOperationError) {
				addMarker(incompleteOperationError.getErrorLocation(), incompleteOperationError.toErrorMarkerString());
			}

			@Override
			public void visit(TypestateError typestateError) {
				addMarker(typestateError.getErrorLocation(), typestateError.toErrorMarkerString());
			}

			@Override
			public void visit(RequiredPredicateError predicateError) {
				addMarker(predicateError.getErrorLocation(), predicateError.toErrorMarkerString());
			}

			@Override
			public void visit(ImpreciseValueExtractionError extractionError) {
				addMarker(extractionError.getErrorLocation(), extractionError.toErrorMarkerString());
			}

			@Override
			public void visit(NeverTypeOfError neverTypeOfError) {
				addMarker(neverTypeOfError.getErrorLocation(), neverTypeOfError.toErrorMarkerString());
			}});
	}
	


	@Override
	public void predicateContradiction(final Node<Statement, Val> location, final Entry<CryptSLPredicate, CryptSLPredicate> arg1) {
		addMarker(location.stmt(), "Predicate mismatch");
	}

	@Override
	public void afterAnalysis() {
		// Nothing
	}

	@Override
	public void afterConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// nothing
	}

	@Override
	public void afterPredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforeAnalysis() {
		// Nothing

	}

	@Override
	public void beforeConstraintCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void beforePredicateCheck(final AnalysisSeedWithSpecification arg0) {
		// Nothing
	}

	@Override
	public void boomerangQueryFinished(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void boomerangQueryStarted(final Query arg0, final BackwardQuery arg1) {
		// Nothing
	}

	@Override
	public void checkedConstraints(final AnalysisSeedWithSpecification arg0, final Collection<ISLConstraint> arg1) {
		// Nothing
	}

	@Override
	public void collectedValues(final AnalysisSeedWithSpecification arg0, final Multimap<CallSiteWithParamIndex, ExtractedValue> arg1) {
		// Nothing
	}

	@Override
	public void discoveredSeed(final IAnalysisSeed arg0) {
		// Nothing
	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCryptSLPredicate>> arg0, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg1, final Table<Statement, IAnalysisSeed, Set<CryptSLPredicate>> arg2) {
		// Nothing
	}

	@Override
	public void onSeedFinished(final IAnalysisSeed arg0, final ForwardBoomerangResults<TransitionFunction> arg1) {
		// Nothing
	}

	@Override
	public void onSeedTimeout(final Node<Statement, Val> arg0) {
		//Nothing
	}

	@Override
	public void seedStarted(final IAnalysisSeed arg0) {
		// Nothing
	}
	public static String filterQuotes(final String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}
}

