package test.assertions;

import boomerang.scene.Statement;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import crypto.analysis.errors.AbstractError;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import test.Assertion;

public class DependentErrorAssertion implements Assertion {

    private final Statement errorLocation;
    private final List<AbstractError> extractedErrors = Lists.newArrayList();
    private final int thisAssertionID;
    private final int[] precedingAssertionIDs;
    private final Map<Integer, List<AbstractError>> idToErrors = Maps.newHashMap();
    private final List<DependentErrorAssertion> listener = Lists.newArrayList();

    public DependentErrorAssertion(
            Statement stmt, int thisAssertionID, int... precedingAssertionIDs) {
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
        for (AbstractError e : this.extractedErrors) {
            for (int id : this.precedingAssertionIDs) {
                if (!this.idToErrors.containsKey(id)
                        || this.idToErrors.get(id).stream()
                                .noneMatch(preceding -> e.getRootErrors().contains(preceding))) {
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
        List<AbstractError> errorsWithMatchingId =
                this.idToErrors.getOrDefault(errorNr, Lists.newArrayList());
        errorsWithMatchingId.add(error);
        this.idToErrors.put(errorNr, errorsWithMatchingId);
    }

    public void addError(AbstractError error) {
        if (error.getErrorStatement().equals(errorLocation)) {
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
