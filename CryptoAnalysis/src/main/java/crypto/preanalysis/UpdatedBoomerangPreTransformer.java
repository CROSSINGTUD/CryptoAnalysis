package crypto.preanalysis;

import com.google.common.collect.Sets;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.AttributeValueException;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.tagkit.Tag;
import soot.util.Chain;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdatedBoomerangPreTransformer extends PreTransformer {

    public static boolean TRANSFORM_CONSTANTS = true;
    public static String UNINITIALIZED_FIELD_TAG_NAME = "UninitializedField";
    public static Tag UNITIALIZED_FIELD_TAG =
            new Tag() {

                @Override
                public String getName() {
                    return UNINITIALIZED_FIELD_TAG_NAME;
                }

                @Override
                public byte[] getValue() throws AttributeValueException {
                    return new byte[0];
                }
            };
    private static UpdatedBoomerangPreTransformer instance;
    private int replaceCounter;

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        if (b.getMethod().isConstructor()) {
            addNulliefiedFields(b.getMethod());
        }
        addNopStmtToMethods(b);
        if (TRANSFORM_CONSTANTS) {
            transformConstantAtFieldWrites(b);
        }
    }

    private void transformConstantAtFieldWrites(Body body) {
        Set<Unit> cwnc = getStmtsWithConstants(body);
        for (Unit u : cwnc) {
            if (u instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt) u;
                if (isFieldRef(assignStmt.getLeftOp())
                        && assignStmt.getRightOp() instanceof Constant
                        && !(assignStmt.getRightOp() instanceof ClassConstant)) {
                    String label = "varReplacer" + replaceCounter++;
                    Local paramVal = new JimpleLocal(label, assignStmt.getRightOp().getType());
                    AssignStmt newUnit = new JAssignStmt(paramVal, assignStmt.getRightOp());
                    body.getLocals().add(paramVal);
                    body.getUnits().insertBefore(newUnit, u);
                    AssignStmt other = new JAssignStmt(assignStmt.getLeftOp(), paramVal);
                    other.addAllTagsOf(u);
                    body.getUnits().insertBefore(other, u);
                    body.getUnits().remove(u);
                }
            }
            if (u instanceof Stmt
                    && ((Stmt) u).containsInvokeExpr()
                    && !u.toString().contains("test.assertions.Assertions:")
                    && !u.toString().contains("intQueryFor")) {
                Stmt stmt = (Stmt) u;

                List<ValueBox> useBoxes = stmt.getInvokeExpr().getUseBoxes();
                List<Map.Entry<Integer, Value>> newArgs = new ArrayList<>();

                for (int i = 0; i < stmt.getInvokeExpr().getArgs().size(); i++) {
                    Value v = stmt.getInvokeExpr().getArg(i);

                    if (v instanceof Constant && !(v instanceof ClassConstant)) {
                        String label = "varReplacer" + replaceCounter++;
                        Local paramVal = new JimpleLocal(label, v.getType());
                        AssignStmt newUnit = new JAssignStmt(paramVal, v);
                        newUnit.addAllTagsOf(u);
                        body.getLocals().add(paramVal);
                        body.getUnits().insertBefore(newUnit, u);

                        for (ValueBox b : useBoxes) {
                            backPropagateSourceLineTags(b, newUnit);
                        }

                        Map.Entry<Integer, Value> entry = new AbstractMap.SimpleEntry<>(i, paramVal);
                        newArgs.add(entry);
                    }
                }

                // Update the parameters
                for (Map.Entry<Integer, Value> entry : newArgs) {
                    int position = entry.getKey();
                    Value newArg = entry.getValue();

                    stmt.getInvokeExpr().setArg(position, newArg);
                }
            }
            if (u instanceof ReturnStmt) {
                ReturnStmt returnStmt = (ReturnStmt) u;
                String label = "varReplacer" + replaceCounter++;
                Local paramVal = new JimpleLocal(label, returnStmt.getOp().getType());
                AssignStmt newUnit = new JAssignStmt(paramVal, returnStmt.getOp());
                newUnit.addAllTagsOf(u);
                body.getLocals().add(paramVal);
                body.getUnits().insertBefore(newUnit, u);
                JReturnStmt other = new JReturnStmt(paramVal);
                body.getUnits().insertBefore(other, u);
                body.getUnits().remove(u);
            }
        }
    }

    /**
     * Propagates back the line number tags from the constant value box to the newly created
     * AssignStmt, to revert the forward propagation done in {@link
     * soot.jimple.toolkits.scalar.CopyPropagator}
     *
     * @param valueBox the constant value box
     * @param assignStmt the corresponding assign statement
     */
    private void backPropagateSourceLineTags(ValueBox valueBox, AssignStmt assignStmt) {
        Tag tag = valueBox.getTag(SourceLnPosTag.NAME);
        if (tag != null) {
            // in case that we copied a line number tag from the original statement, we want to remove
            // that now since the valueBox contains the correct lin number tag for the assign statement as
            // it was before copy propagation
            assignStmt.removeTag(SourceLnPosTag.NAME);
            assignStmt.addTag(tag);
        }

        tag = valueBox.getTag(LineNumberTag.NAME);
        if (tag != null) {
            // same as for the above case
            assignStmt.removeTag(LineNumberTag.NAME);
            assignStmt.addTag(tag);
        }
    }

    /**
     * The first statement of a method must be a nop statement, because the call-flow functions do
     * only map parameters to arguments. If the first statement of a method would be an assign
     * statement, the analysis misses data-flows.
     */
    private void addNopStmtToMethods(Body b) {
        JNopStmt nopStmt = new JNopStmt();
        for (Unit u : b.getUnits()) {
            if (u.getJavaSourceStartLineNumber() > 0) {
                nopStmt.addAllTagsOf(u);
                break;
            }
        }
        b.getUnits().insertBefore(nopStmt, b.getUnits().getFirst());
        Set<IfStmt> ifStmts = Sets.newHashSet();
        for (Unit u : b.getUnits()) {
            if (u instanceof IfStmt) {
                // ((IfStmt) u).getTarget();
                ifStmts.add((IfStmt) u);
            }
        }

        // After all if-stmts we add a nop-stmt to make the analysis
        for (IfStmt ifStmt : ifStmts) {
            nopStmt = new JNopStmt();
            nopStmt.addAllTagsOf(ifStmt);
            b.getUnits().insertAfter(nopStmt, ifStmt);
            Unit target = ifStmt.getTarget();
            nopStmt = new JNopStmt();
            nopStmt.addAllTagsOf(target);
            b.getUnits().insertBefore(nopStmt, target);
            ifStmt.setTarget(nopStmt);
        }
    }

    private Set<Unit> getStmtsWithConstants(Body methodBody) {
        Set<Unit> retMap = Sets.newHashSet();
        for (Unit u : methodBody.getUnits()) {
            if (u instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt) u;
                if (isFieldRef(assignStmt.getLeftOp()) && assignStmt.getRightOp() instanceof Constant) {
                    retMap.add(u);
                }
            }
            if (u instanceof Stmt && ((Stmt) u).containsInvokeExpr()) {
                Stmt stmt = (Stmt) u;
                for (Value v : stmt.getInvokeExpr().getArgs()) {
                    if (v instanceof Constant) {
                        retMap.add(u);
                    }
                }
            }
            if (u instanceof ReturnStmt) {
                ReturnStmt assignStmt = (ReturnStmt) u;
                if (assignStmt.getOp() instanceof Constant) {
                    retMap.add(u);
                }
            }
        }
        return retMap;
    }

    private boolean isFieldRef(Value op) {
        return op instanceof InstanceFieldRef || op instanceof StaticFieldRef || op instanceof ArrayRef;
    }

    private static void addNulliefiedFields(SootMethod cons) {
        Chain<SootField> fields = cons.getDeclaringClass().getFields();
        UnitPatchingChain units = cons.getActiveBody().getUnits();
        Set<SootField> fieldsDefinedInMethod = getFieldsDefinedInMethod(cons, Sets.newHashSet());
        for (SootField f : fields) {
            if (fieldsDefinedInMethod.contains(f)) continue;
            if (f.isStatic()) continue;
            if (f.isFinal()) continue;
            if (f.getType() instanceof RefType) {
                JAssignStmt jAssignStmt =
                        new JAssignStmt(
                                new JInstanceFieldRef(cons.getActiveBody().getThisLocal(), f.makeRef()),
                                NullConstant.v());

                jAssignStmt.addTag(new LineNumberTag(2));
                jAssignStmt.addTag(UNITIALIZED_FIELD_TAG);
                Unit lastIdentityStmt = findLastIdentityStmt(units);
                if (lastIdentityStmt != null) {
                    units.insertAfter(jAssignStmt, lastIdentityStmt);
                } else {
                    units.addFirst(jAssignStmt);
                }
            }
        }
    }

    private static Unit findLastIdentityStmt(UnitPatchingChain units) {
        for (Unit u : units) {
            if (u instanceof IdentityStmt && u instanceof AssignStmt) {
                continue;
            }
            return u;
        }
        return null;
    }

    private static Set<SootField> getFieldsDefinedInMethod(SootMethod cons, Set<SootMethod> visited) {
        Set<SootField> res = Sets.newHashSet();
        if (!visited.add(cons)) return res;
        if (!cons.hasActiveBody()) return res;
        for (Unit u : cons.getActiveBody().getUnits()) {
            if (u instanceof AssignStmt) {
                AssignStmt as = (AssignStmt) u;
                Value left = as.getLeftOp();
                if (left instanceof InstanceFieldRef) {
                    InstanceFieldRef ifr = (InstanceFieldRef) left;
                    res.add(ifr.getField());
                }
            }
            if (u instanceof Stmt) {
                Stmt stmt = (Stmt) u;
                if (stmt.containsInvokeExpr()) {
                    if (stmt.getInvokeExpr().getMethod().isConstructor()) {
                        res.addAll(getFieldsDefinedInMethod(stmt.getInvokeExpr().getMethod(), visited));
                    }
                }
            }
        }
        return res;
    }

    public static UpdatedBoomerangPreTransformer v() {
        if (instance == null) {
            instance = new UpdatedBoomerangPreTransformer();
        }
        return instance;
    }

    public void reset() {
        instance = null;
    }
}
