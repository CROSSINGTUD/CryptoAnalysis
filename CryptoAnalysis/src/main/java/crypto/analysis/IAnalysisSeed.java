/********************************************************************************
 * Copyright (c) 2017 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package crypto.analysis;

import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import crypto.analysis.errors.AbstractError;
import crypto.predicates.AbstractPredicate;
import crypto.predicates.EnsuredPredicate;
import crypto.predicates.ExpectedPredicate;
import crypto.predicates.IPredicateCheckListener;
import crypto.predicates.IPredicateStateChangeListener;
import crypto.predicates.UnEnsuredPredicate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import typestate.TransitionFunction;
import typestate.finiteautomata.ITransition;
import typestate.finiteautomata.State;

public abstract class IAnalysisSeed implements IPredicateCheckListener {

    protected final CryptoScanner scanner;
    protected final Collection<AbstractError> errorCollection;
    protected final ForwardBoomerangResults<TransitionFunction> analysisResults;

    protected final Table<Statement, Val, TransitionFunction> statementValWeightTable;
    protected final Multimap<Statement, ExpectedPredicate> expectedPredicates;
    protected final Collection<IPredicateStateChangeListener> predicateStateChangeListeners;

    protected final Multimap<Statement, EnsuredPredicate> ensuredPredicates;
    protected final Multimap<Statement, UnEnsuredPredicate> unEnsuredPredicates;

    private final Statement origin;
    private final Val fact;

    public IAnalysisSeed(
            CryptoScanner scanner,
            Statement origin,
            Val fact,
            ForwardBoomerangResults<TransitionFunction> results) {
        this.scanner = scanner;
        this.origin = origin;
        this.fact = fact;
        this.analysisResults = results;

        this.statementValWeightTable = results.asStatementValWeightTable();

        this.errorCollection = new HashSet<>();
        this.expectedPredicates = HashMultimap.create();
        this.predicateStateChangeListeners = new HashSet<>();
        this.ensuredPredicates = HashMultimap.create();
        this.unEnsuredPredicates = HashMultimap.create();
    }

    public abstract void execute();

    public Collection<Val> getAliasesAtStatement(Statement statement) {
        return statementValWeightTable.row(statement).keySet();
    }

    public Collection<State> getStatesAtStatement(Statement statement) {
        Collection<State> states = new HashSet<>();

        Collection<TransitionFunction> transitions =
                statementValWeightTable.row(statement).values();
        for (TransitionFunction transition : transitions) {
            Collection<State> targetStates = getTargetStates(transition);

            states.addAll(targetStates);
        }

        return states;
    }

    private Collection<State> getTargetStates(TransitionFunction transitionFunction) {
        Collection<State> states = new HashSet<>();

        for (ITransition transition : transitionFunction.values()) {
            states.add(transition.to());
        }

        return states;
    }

    public void addPredicateStateChangeListener(IPredicateStateChangeListener listener) {
        predicateStateChangeListeners.add(listener);
    }

    public void resetPredicateStateChangeListener() {
        predicateStateChangeListeners.clear();
    }

    public Method getMethod() {
        return origin.getMethod();
    }

    public Statement getOrigin() {
        return origin;
    }

    public Val getFact() {
        return fact;
    }

    public Type getType() {
        return fact.getType();
    }

    public boolean isSecure() {
        return errorCollection.isEmpty();
    }

    public ForwardBoomerangResults<TransitionFunction> getAnalysisResults() {
        return analysisResults;
    }

    public void addError(AbstractError e) {
        this.errorCollection.add(e);
    }

    public Collection<AbstractError> getErrors() {
        return new HashSet<>(errorCollection);
    }

    public CryptoScanner getScanner() {
        return scanner;
    }

    public void registerExpectedPredicate(ExpectedPredicate expectedPredicate) {
        expectedPredicates.put(expectedPredicate.statement(), expectedPredicate);
    }

    public void onGeneratedPredicate(AbstractPredicate predicate) {
        if (predicate instanceof EnsuredPredicate ensPred) {
            if (ensuredPredicates.put(ensPred.getStatement(), ensPred)) {
                for (IPredicateStateChangeListener listener : predicateStateChangeListeners) {
                    listener.onPredicateStateChange(this);
                }

                Optional<UnEnsuredPredicate> changedPred =
                        isPredicateStateChangeUnEnsuredToEnsured(ensPred);
                changedPred.ifPresent(p -> unEnsuredPredicates.remove(predicate.getStatement(), p));
            }
        } else if (predicate instanceof UnEnsuredPredicate unEnsPred) {
            /* UnEnsured predicates may correspond to the same predicate, but their set of violations may be
             * different from previous propagations. To avoid having both versions of the same predicate, we
             * override the existing one explicitly.
             */
            Optional<UnEnsuredPredicate> existingPredOpt = isPredicateUnEnsured(unEnsPred);
            if (existingPredOpt.isPresent()) {
                UnEnsuredPredicate existingPred = existingPredOpt.get();

                unEnsuredPredicates.remove(existingPred.getStatement(), existingPred);
                unEnsuredPredicates.put(unEnsPred.getStatement(), unEnsPred);
            }

            if (unEnsuredPredicates.put(unEnsPred.getStatement(), unEnsPred)) {
                for (IPredicateStateChangeListener listener : predicateStateChangeListeners) {
                    listener.onPredicateStateChange(this);
                }

                Optional<EnsuredPredicate> changedPred =
                        isPredicateStateChangeEnsuredToUnEnsured(unEnsPred);
                changedPred.ifPresent(p -> ensuredPredicates.remove(predicate.getStatement(), p));
            }
        }
    }

    private Optional<UnEnsuredPredicate> isPredicateStateChangeUnEnsuredToEnsured(
            EnsuredPredicate predicate) {
        Statement statement = predicate.getStatement();

        for (UnEnsuredPredicate unEnsPred : unEnsuredPredicates.get(statement)) {
            if (unEnsPred.equalsSimple(predicate)) {
                return Optional.of(unEnsPred);
            }
        }

        return Optional.empty();
    }

    private Optional<EnsuredPredicate> isPredicateStateChangeEnsuredToUnEnsured(
            UnEnsuredPredicate predicate) {
        Statement statement = predicate.getStatement();

        for (EnsuredPredicate unEnsPred : ensuredPredicates.get(statement)) {
            if (unEnsPred.equalsSimple(predicate)) {
                return Optional.of(unEnsPred);
            }
        }

        return Optional.empty();
    }

    private Optional<UnEnsuredPredicate> isPredicateUnEnsured(UnEnsuredPredicate predicate) {
        Statement statement = predicate.getStatement();

        for (UnEnsuredPredicate unEnsPred : unEnsuredPredicates.get(statement)) {
            if (unEnsPred.equalsSimple(predicate)) {
                return Optional.of(unEnsPred);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IAnalysisSeed other
                && Objects.equals(origin, other.origin)
                && Objects.equals(fact, other.fact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, fact);
    }

    @Override
    public String toString() {
        return fact.getVariableName() + " at " + origin;
    }
}
