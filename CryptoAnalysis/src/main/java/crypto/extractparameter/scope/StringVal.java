package crypto.extractparameter.scope;

import boomerang.scene.ControlFlowGraph;
import boomerang.scene.Method;
import boomerang.scene.Pair;
import boomerang.scene.Type;
import boomerang.scene.Val;

public class StringVal extends Val {

    private final String value;

    public StringVal(String value, Method method) {
        this(value, method, null);
    }

    private StringVal(String value, Method method, ControlFlowGraph.Edge edge) {
        super(method, edge);

        this.value = value;
    }

    @Override
    public Type getType() {
        return new StringType();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isNewExpr() {
        return false;
    }

    @Override
    public Type getNewExprType() {
        throw new RuntimeException("String constant is not a new expression");
    }

    @Override
    public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
        return new StringVal(value, m, stmt);
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public boolean isArrayAllocationVal() {
        return false;
    }

    @Override
    public Val getArrayAllocationSize() {
        throw new RuntimeException("String constant is not an array allocation expression");
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isStringConstant() {
        return true;
    }

    @Override
    public String getStringValue() {
        return value;
    }

    @Override
    public boolean isStringBufferOrBuilder() {
        return false;
    }

    @Override
    public boolean isThrowableAllocationType() {
        return false;
    }

    @Override
    public boolean isCast() {
        return false;
    }

    @Override
    public Val getCastOp() {
        throw new RuntimeException("String constant is not a cast expression");
    }

    @Override
    public boolean isArrayRef() {
        return false;
    }

    @Override
    public boolean isInstanceOfExpr() {
        return false;
    }

    @Override
    public Val getInstanceOfOp() {
        throw new RuntimeException("String constant is not an instanceOf expression");
    }

    @Override
    public boolean isLengthExpr() {
        return false;
    }

    @Override
    public Val getLengthOp() {
        throw new RuntimeException("String constant is not a length expression");
    }

    @Override
    public boolean isIntConstant() {
        return false;
    }

    @Override
    public boolean isClassConstant() {
        return false;
    }

    @Override
    public Type getClassConstantType() {
        throw new RuntimeException("String constant is not a class constant");
    }

    @Override
    public Val withNewMethod(Method callee) {
        return new StringVal(value, callee);
    }

    @Override
    public boolean isLongConstant() {
        return false;
    }

    @Override
    public int getIntValue() {
        throw new RuntimeException("String constant is not an integer value");
    }

    @Override
    public long getLongValue() {
        throw new RuntimeException("String constant is not a long value");
    }

    @Override
    public Pair<Val, Integer> getArrayBase() {
        throw new RuntimeException("String constant has no array base");
    }

    @Override
    public String getVariableName() {
        return "String constant: " + value;
    }
}
