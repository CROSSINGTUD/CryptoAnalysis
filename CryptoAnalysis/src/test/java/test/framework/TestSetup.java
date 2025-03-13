package test.framework;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;

public interface TestSetup {

    void initialize(String className, String testName);

    Method getTestMethod();

    FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope);
}
