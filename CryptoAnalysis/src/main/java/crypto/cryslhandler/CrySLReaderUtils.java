package crypto.cryslhandler;

import java.util.AbstractMap.SimpleEntry;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import crypto.interfaces.ICrySLPredicateParameter;
import crypto.rules.CrySLMethod;
import crypto.rules.CrySLObject;
import crypto.rules.CrySLArithmeticConstraint.ArithOp;
import crypto.rules.CrySLComparisonConstraint.CompOp;
import crypto.rules.CrySLConstraint.LogOps;
import de.darmstadt.tu.crossing.crySL.Aggregate;
import de.darmstadt.tu.crossing.crySL.AnyParameterType;
import de.darmstadt.tu.crossing.crySL.BooleanLiteral;
import de.darmstadt.tu.crossing.crySL.Constraint;
import de.darmstadt.tu.crossing.crySL.Event;
import de.darmstadt.tu.crossing.crySL.ForbiddenMethod;
import de.darmstadt.tu.crossing.crySL.IntLiteral;
import de.darmstadt.tu.crossing.crySL.LabeledMethodCall;
import de.darmstadt.tu.crossing.crySL.Literal;
import de.darmstadt.tu.crossing.crySL.Method;
import de.darmstadt.tu.crossing.crySL.Object;
import de.darmstadt.tu.crossing.crySL.Operator;
import de.darmstadt.tu.crossing.crySL.StringLiteral;

public class CrySLReaderUtils {

	protected static List<CrySLMethod> resolveEventToCryslMethods(final Event event) {
		return resolveEventToCryslMethodsStream(event).collect(Collectors.toList());
	}

	protected static List<ICrySLPredicateParameter> resolveEventToPredicateParameters(final Event event) {
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
		if (event instanceof LabeledMethodCall)
			return resolveEventToCryslMethodsStream((LabeledMethodCall) event);
		return Stream.empty();
	}

	protected static Stream<CrySLMethod> resolveEventToCryslMethodsStream(final Aggregate aggregate) {
		return aggregate.getEvents().parallelStream()
			.flatMap(CrySLReaderUtils::resolveEventToCryslMethodsStream);
	}

	protected static Stream<CrySLMethod> resolveEventToCryslMethodsStream(final LabeledMethodCall event) {
		return Stream.of(toCrySLMethod((event.getMethod())));
	}
	
	protected static CrySLMethod toCrySLMethod(final ForbiddenMethod method) {
		String name = method.getMethod().getQualifiedName();
		List<Entry<String,String>> parameters = method.getParameters().stream()
			.map(parameter -> new SimpleEntry<>(parameter.getSimpleName(), parameter.getType().getQualifiedName()))
			.collect(Collectors.toList());
		return new CrySLMethod(name, parameters, resolveObject(null));
	}

	protected static CrySLMethod toCrySLMethod(final Method method) {
		String name = method.getMethod().getQualifiedName();
		List<Entry<String,String>> parameters = method.getParameters().stream()
			.map(parameter -> parameter instanceof AnyParameterType
					? new SimpleEntry<>(CrySLMethod.NO_NAME, CrySLMethod.ANY_TYPE)
					: resolveObject((parameter.getValue())) )
			.collect(Collectors.toList());
		return new CrySLMethod(name, parameters, resolveObject(method.getReturn()));
	}

	protected static CrySLObject toCrySLObject(Object object) {
		return new CrySLObject(object.getName(), object.getType().getQualifiedName());
	}

	protected static CrySLObject toCrySLObject(Literal literal) {
		String value = literal.getValue();
		String type =
			  literal instanceof IntLiteral     ? "int"
			: literal instanceof BooleanLiteral ? "boolean"
			: literal instanceof StringLiteral  ? String.class.getName()
			: "void" ;
		return new CrySLObject(value, type);
	}

	protected static Entry<String,String> resolveObject(final Object o) {
		if(o == null)
			return new SimpleEntry<>(CrySLMethod.NO_NAME, CrySLMethod.VOID);
		return new SimpleEntry<>(o.getName(), o.getType().getQualifiedName());
	}

	public static File getResourceFromWithin(final String inputPath) {
		return new File(inputPath);
	}

	public static Optional<ArithOp> arithOpFromOperator(Operator operator) {
		switch(operator) {
			case PLUS:   return Optional.of(ArithOp.p);
			// case TIMES:  return Optional.of(ArithOp.t); /* Only in Syntax yet */
			// case DIVIDE: return Optional.of(ArithOp.g); /* Only in Syntax yet */
			case MINUS:  return Optional.of(ArithOp.n);
			case MODULO: return Optional.of(ArithOp.m);
			default:     return Optional.empty();
		}
	}

	public static Optional<LogOps> logOpFromOperator(Operator operator) {
		switch(operator) {
			case AND:   return Optional.of(LogOps.and);
			case OR:    return Optional.of(LogOps.or);
			case IMPLY: return Optional.of(LogOps.implies);
			// case NOT: return Optional.of(LogOps.not); /* Only in Syntax yet */
			// case EQUAL: return Optional.of(LogOps.eq); /* unused enum item */
			default:    return Optional.empty();
		}
	}

	public static Optional<CompOp> compOpFromOperator(Operator operator) {
		switch(operator) {
			case EQUAL:            return Optional.of(CompOp.eq);
			case UNEQUAL:          return Optional.of(CompOp.neq);
			case LESS:             return Optional.of(CompOp.l);
			case LESS_OR_EQUAL:    return Optional.of(CompOp.le);
			case GREATER:          return Optional.of(CompOp.g);
			case GREATER_OR_EQUAL: return Optional.of(CompOp.ge);
			default:               return Optional.empty();
		}
	}

	public static boolean isArithmeticExpression(Constraint constraint) {
		return arithOpFromOperator(constraint.getOp()).isPresent();
	}

	public static boolean isLogicExpression(Constraint constraint) {
		return logOpFromOperator(constraint.getOp()).isPresent();
	}

	public static boolean isComparisonExpression(Constraint constraint) {
		return compOpFromOperator(constraint.getOp()).isPresent();
	}

}

