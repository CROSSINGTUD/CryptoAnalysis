package crypto.typestate;

import boomerang.scene.DeclaredMethod;
import crypto.rules.CrySLMethod;
import crypto.utils.MatcherUtils;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.State;

import java.util.Collection;
import java.util.Optional;

public class LabeledMatcherTransition extends MatcherTransition {

    private final Collection<CrySLMethod> methods;

    public LabeledMatcherTransition(State from, Collection<CrySLMethod> methods, Parameter param, State to, Type type) {
        super(from, "", param, to, type);

        this.methods = methods;
    }

    @Override
    public boolean matches(DeclaredMethod declaredMethod) {
        return getMatching(declaredMethod).isPresent();
    }

    // Return value corresponds to crysl method on
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
}
