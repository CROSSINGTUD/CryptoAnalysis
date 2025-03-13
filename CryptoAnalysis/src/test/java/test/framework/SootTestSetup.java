package test.framework;

import boomerang.scope.soot.BoomerangPretransformer;
import boomerang.scope.soot.SootFrameworkScope;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import soot.ArrayType;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;

public class SootTestSetup implements TestSetup {

    private SootMethod testMethod = null;

    @Override
    public void initialize(String className, String testName) {
        G.reset();
        Options.v().set_whole_program(true);

        // https://soot-build.cs.uni-paderborn.de/public/origin/develop/soot/soot-develop/options/soot_options.htm#phase_5_2
        // Options.v().setPhaseOption("cg.cha", "on");
        // Options.v().setPhaseOption("cg.cha", "verbose:true");

        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().set_output_format(Options.output_format_none);

        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_keep_line_number(true);

        Options.v().setPhaseOption("jb.sils", "enabled:false");

        Options.v()
                .set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + getSootClassPath());

        // Options.v().set_main_class(this.getTargetClass());
        SootClass sootTestCaseClass = Scene.v().forceResolve(className, SootClass.BODIES);

        for (SootMethod m : sootTestCaseClass.getMethods()) {
            if (m.getName().equals(testName)) testMethod = m;
        }
        if (testMethod == null)
            throw new RuntimeException(
                    "The method with name " + testName + " was not found in the Soot Scene.");
        String targetClassName = getTargetClass(className, testMethod);
        testMethod.getDeclaringClass().setApplicationClass();
        Scene.v().addBasicClass(targetClassName, SootClass.BODIES);
        Scene.v().loadNecessaryClasses();
        SootClass c = Scene.v().forceResolve(targetClassName, SootClass.BODIES);
        c.setApplicationClass();

        SootMethod methodByName = c.getMethodByName("main");
        List<SootMethod> ePoints = new LinkedList<>();
        for (SootMethod m : sootTestCaseClass.getMethods()) {
            if (m.isStaticInitializer()) ePoints.add(m);
        }
        for (SootClass inner : Scene.v().getClasses()) {
            if (inner.getName().contains(sootTestCaseClass.getName())) {
                inner.setApplicationClass();
                for (SootMethod m : inner.getMethods()) {
                    if (m.isStaticInitializer()) ePoints.add(m);
                }
            }
        }
        ePoints.add(methodByName);
        Scene.v().setEntryPoints(ePoints);
    }

    private String getTargetClass(String className, SootMethod sootTestMethod) {
        SootClass sootClass = new SootClass("dummyClass");
        Type paramType = ArrayType.v(RefType.v("java.lang.String"), 1);
        SootMethod mainMethod =
                new SootMethod(
                        "main",
                        Collections.singletonList(paramType),
                        VoidType.v(),
                        Modifier.PUBLIC | Modifier.STATIC);
        sootClass.addMethod(mainMethod);
        JimpleBody body = Jimple.v().newBody(mainMethod);
        mainMethod.setActiveBody(body);
        RefType testCaseType = RefType.v(className);
        Local loc = Jimple.v().newLocal("l0", paramType);
        body.getLocals().add(loc);
        body.getUnits()
                .add(Jimple.v().newIdentityStmt(loc, Jimple.v().newParameterRef(paramType, 0)));
        Local allocatedTestObj = Jimple.v().newLocal("dummyObj", testCaseType);
        body.getLocals().add(allocatedTestObj);
        body.getUnits()
                .add(
                        Jimple.v()
                                .newAssignStmt(
                                        allocatedTestObj, Jimple.v().newNewExpr(testCaseType)));
        body.getUnits()
                .add(
                        Jimple.v()
                                .newInvokeStmt(
                                        Jimple.v()
                                                .newVirtualInvokeExpr(
                                                        allocatedTestObj,
                                                        sootTestMethod.makeRef())));
        body.getUnits().add(Jimple.v().newReturnVoidStmt());

        Scene.v().addClass(sootClass);
        body.validate();
        return sootClass.toString();
    }

    @Override
    public Method getTestMethod() {
        return JimpleMethod.of(testMethod);
    }

    @Override
    public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
        PackManager.v().getPack("cg").apply();

        BoomerangPretransformer.v().reset();
        BoomerangPretransformer.v().apply();

        return new SootFrameworkScope(
                Scene.v(),
                Scene.v().getCallGraph(),
                Collections.singleton(testMethod),
                dataFlowScope);
    }

    protected String getSootClassPath() {
        String userDir = System.getProperty("user.dir");
        String javaHome = System.getProperty("java.home");
        if (javaHome == null || javaHome.isEmpty())
            throw new RuntimeException("Could not get property java.home!");

        return userDir + "/target/test-classes";
    }
}
