package crypto.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import boomerang.BackwardQuery;
import boomerang.Query;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.results.ForwardBoomerangResults;
import crypto.analysis.AnalysisSeedWithSpecification;
import crypto.analysis.CrySLAnalysisListener;
import crypto.analysis.EnsuredCrySLPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ConstraintError;
import crypto.analysis.errors.ErrorVisitor;
import crypto.analysis.errors.ForbiddenMethodError;
import crypto.analysis.errors.HardCodedError;
import crypto.analysis.errors.ImpreciseValueExtractionError;
import crypto.analysis.errors.IncompleteOperationError;
import crypto.analysis.errors.NeverTypeOfError;
import crypto.analysis.errors.PredicateContradictionError;
import crypto.analysis.errors.RequiredPredicateError;
import crypto.analysis.errors.TypestateError;
import crypto.extractparameter.CallSiteWithParamIndex;
import crypto.extractparameter.ExtractedValue;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLPredicate;
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

	protected final Table<SootClass, SootMethod, Set<AbstractError>> errorMarkers = HashBasedTable.create(); 
	protected final Map<Class, Integer> errorMarkerCount = new HashMap<Class, Integer>();
	protected final List<IAnalysisSeed> secureObjects = new ArrayList<IAnalysisSeed>();
	
	private void addMarker(AbstractError error) {
		SootMethod method = error.getErrorLocation().getMethod();
		SootClass sootClass = method.getDeclaringClass();
		
		Set<AbstractError> set = errorMarkers.get(sootClass, method);
		if(set == null){
			set = Sets.newHashSet();
		}
		if(set.add(error)){
			Integer integer = errorMarkerCount.get(error.getClass());
			if(integer == null){
				integer = 0;
			}
			integer++;
			errorMarkerCount.put(error.getClass(), integer);
		}
		errorMarkers.put(sootClass, method, set);
	}
	
	@Override
	public void reportError(AbstractError error) {
		error.accept(new ErrorVisitor(){

			@Override
			public void visit(ConstraintError constraintError) {
				addMarker(constraintError);
			}

			@Override
			public void visit(ForbiddenMethodError forbiddenMethodError) {
				addMarker(forbiddenMethodError);
			}

			@Override
			public void visit(IncompleteOperationError incompleteOperationError) {
				addMarker(incompleteOperationError);
			}

			@Override
			public void visit(TypestateError typestateError) {
				addMarker(typestateError);
			}

			@Override
			public void visit(RequiredPredicateError predicateError) {
				addMarker(predicateError);
			}

			@Override
			public void visit(ImpreciseValueExtractionError extractionError) {
				addMarker(extractionError);
			}

			@Override
			public void visit(NeverTypeOfError neverTypeOfError) {
				addMarker(neverTypeOfError);
			}

			@Override
			public void visit(PredicateContradictionError predicateContradictionError) {
				addMarker(predicateContradictionError);
				
			}

			@Override
			public void visit(HardCodedError hardcodedError) {
				addMarker(hardcodedError);
			}});
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
	}

	@Override
	public void ensuredPredicates(final Table<Statement, Val, Set<EnsuredCrySLPredicate>> arg0, final Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> arg1, final Table<Statement, IAnalysisSeed, Set<CrySLPredicate>> arg2) {
		// Nothing
	}

	@Override
	public void onSeedFinished(final IAnalysisSeed analysisObject, final ForwardBoomerangResults<TransitionFunction> arg1) {
		
	}
	@Override
	public void onSecureObjectFound(final IAnalysisSeed analysisObject) {
		secureObjects.add( analysisObject);
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

	@Override
	public void addProgress(int processedSeeds, int workListsize) {
		// TODO Auto-generated method stub
		
	}
}

