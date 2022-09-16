package crypto.cryslhandler;

import java.util.AbstractMap.SimpleEntry;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import crypto.rules.CrySLMethod;
import de.darmstadt.tu.crossing.crySL.Aggregate;
import de.darmstadt.tu.crossing.crySL.AnyParameterType;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.LabeledMethodCall;
import de.darmstadt.tu.crossing.crySL.Method;
import de.darmstadt.tu.crossing.crySL.Object;

public class CrySLReaderUtils {

	protected static List<CrySLMethod> resolveEventToCryslMethods(final Event event) {
		return resolveEventToCryslMethodsStream(event).collect(Collectors.toList());
	}

	protected static List<CrySLMethod> resolveEventsToCryslMethods(final Collection<Event> events) {
		return resolveEventsToCryslMethodsStream(events).collect(Collectors.toList());
	}

	protected static Stream<CrySLMethod> resolveEventsToCryslMethodsStream(final Collection<Event> events) {
		return events.parallelStream()
			.flatMap(CrySLReaderUtils::resolveEventToCryslMethodsStream);
	}

	protected static Stream<CrySLMethod> resolveEventToCryslMethodsStream(final Event event) {
		if (event instanceof Aggregate)
			return resolveEventToCryslMethodsStream((Aggregate) event);
		if (event instanceof Aggregate)
			return resolveEventToCryslMethodsStream((LabeledMethodCall) event);
		return Stream.empty();
	}

	protected static Stream<CrySLMethod> resolveEventToCryslMethodsStream(final Aggregate aggregate) {
		return aggregate.getEvents().parallelStream()
			.filter(event -> (event instanceof LabeledMethodCall))
			.flatMap(CrySLReaderUtils::resolveEventToCryslMethodsStream);
	}

	protected static Stream<CrySLMethod> resolveEventToCryslMethodsStream(final LabeledMethodCall event) {
		return Stream.of(toCrySLMethod((event.getMethod())));
	}
	
	protected static CrySLMethod toCrySLMethod(final Method method) {
		String name = method.getMethod().getSimpleName();
		List<Entry<String,String>> parameters = method.getParameters().stream()
			.map(parameter -> parameter instanceof AnyParameterType
					? new SimpleEntry<>(CrySLMethod.NO_NAME, CrySLMethod.ANY_TYPE)
					: resolveObject((parameter.getValue())) )
			.collect(Collectors.toList());
		return new CrySLMethod(name, parameters, resolveObject(method.getReturn()));
	}

	protected static Entry<String,String> resolveObject(final Object o) {
		if(o == null)
			return new SimpleEntry<>(CrySLMethod.NO_NAME, CrySLMethod.VOID);
		return new SimpleEntry<>(o.getName(), o.getType().getQualifiedName());
	}

	public static File getResourceFromWithin(final String inputPath) {
		return new File(inputPath);
	}
}

