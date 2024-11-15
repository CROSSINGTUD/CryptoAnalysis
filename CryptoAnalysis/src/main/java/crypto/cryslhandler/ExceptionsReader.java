package crypto.cryslhandler;

import crypto.rules.CrySLExceptionConstraint;
import crypto.rules.CrySLMethod;
import de.darmstadt.tu.crossing.crySL.EventsBlock;
import de.darmstadt.tu.crossing.crySL.LabeledMethodCall;
import java.util.Collection;
import java.util.stream.Collectors;

/** Helper class to derive {@link CrySLExceptionConstraint}'s from the events. */
public abstract class ExceptionsReader {
    public static Collection<CrySLExceptionConstraint> getExceptionConstraints(
            EventsBlock eventsBlock) {
        return eventsBlock.getEvents().stream()
                .filter(event -> event instanceof LabeledMethodCall)
                .map(event -> (LabeledMethodCall) event)
                .flatMap(
                        meth ->
                                CrySLReaderUtils.resolveExceptionsStream(meth.getException())
                                        .map(
                                                exception -> {
                                                    CrySLMethod method =
                                                            CrySLReaderUtils.toCrySLMethod(
                                                                    meth.getMethod());
                                                    return new CrySLExceptionConstraint(
                                                            method, exception);
                                                }))
                .collect(Collectors.toList());
    }
}
