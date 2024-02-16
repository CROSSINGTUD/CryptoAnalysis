package test.assertions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import crypto.analysis.errors.AbstractError;
import soot.jimple.Stmt;
import test.Assertion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DependentErrorAssertion implements Assertion {

    private final Stmt errorLocation;
    private final List<AbstractError> extractedErrors = Lists.newArrayList();
    private final int thisAssertionID;
    private final int[] precedingAssertionIDs;
    private final Map<Integer,List<AbstractError>> idToErrors = Maps.newHashMap();
    private final List<DependentErrorAssertion> listener = Lists.newArrayList();

    public DependentErrorAssertion(Stmt stmt, int thisAssertionID, int... precedingAssertionIDs) {
        this.errorLocation = stmt;
        this.thisAssertionID = thisAssertionID;
        this.precedingAssertionIDs = precedingAssertionIDs;
    }

    @Override
    public boolean isSatisfied() {
        if (extractedErrors.isEmpty()) {
            return false;
        }

        nextExtractedError:
        for (AbstractError e: this.extractedErrors) {
            for (int id: this.precedingAssertionIDs) {
                if (!this.idToErrors.containsKey(id) || this.idToErrors.get(id).stream().noneMatch(preceding -> e.getRootErrors().contains(preceding))){
                    continue nextExtractedError;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isImprecise() {
        return false;
    }

    public void registerListeners(Collection<Assertion> assertions) {
        assertions.forEach(a -> listener.add((DependentErrorAssertion) a));
    }

    public void addErrorOfOtherLocations(AbstractError error, int errorNr) {
        List<AbstractError> errorsWithMatchingId = this.idToErrors.getOrDefault(errorNr, Lists.newArrayList());
        errorsWithMatchingId.add(error);
        this.idToErrors.put(errorNr, errorsWithMatchingId);
    }

    public void addError(AbstractError error) {
        if (!error.getErrorLocation().getUnit().isPresent()) {
            return;
        }
        if (error.getErrorLocation().getUnit().get() == errorLocation) {
            extractedErrors.add(error);
            listener.forEach(a -> a.addErrorOfOtherLocations(error, thisAssertionID));
        }
    }

    @Override
    public String toString() {
        return extractedErrors.isEmpty()
                ? "Expected an error @ " + errorLocation + " but found none."
                : extractedErrors + " @ " + errorLocation + " is not a subsequent error.";
    }

}
