package test.constraints;

import static org.junit.Assert.assertEquals;

public class ResultPrinter {

	public static void evaluateResults(String methodName, int consSize, int relConsSize, Integer brokenConsSize, Integer expected) {
		System.out.println("=========" + methodName + "==============");
		System.out.println("Number of constraints:" + consSize);
		System.out.println("Number of relevant constraints:" + relConsSize);
		System.out.println("Number of violated constraints:" + brokenConsSize);
		System.out.println("Number of expected constraints:" + expected);
		assertEquals(brokenConsSize, expected);
	}
	
}
