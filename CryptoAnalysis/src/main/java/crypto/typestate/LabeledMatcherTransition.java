package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import crypto.rules.CrySLMethod;
import crypto.utils.MatcherUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

public class LabeledMatcherTransition extends MatcherTransition {

    private final Collection<CrySLMethod> methods;

    public LabeledMatcherTransition(State from, Collection<CrySLMethod> methods, State to) {
        super(from, "", Parameter.This, to, Type.OnCallOrOnCallToReturn);

        this.methods = methods;
    }

    @Override
    public boolean matches(DeclaredMethod declaredMethod) {
        return getMatching(declaredMethod).isPresent();
    }

    /**
     * Return the {@link CrySLMethod}'s that match the given method. As the method is taken from a
     * statement, we need to apply the matching logic defined here, to get the {@link CrySLMethod}s
     * that were resolved to the matching {@link DeclaredMethod}s.
     *
     * @param declaredMethod the given method
     * @return The {@link CrySLMethod}'s matching the given declared method.
     */
    public Optional<CrySLMethod> getMatching(DeclaredMethod declaredMethod) {
        for (CrySLMethod method : methods) {
            if (MatcherUtils.matchCryslMethodAndDeclaredMethod(method, declaredMethod)) {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    public Collection<CrySLMethod> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        LabeledMatcherTransition matcherTransition = (LabeledMatcherTransition) other;
        return this.methods.equals(matcherTransition.getMethods());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {super.hashCode(), from(), to(), methods});
    }
}
