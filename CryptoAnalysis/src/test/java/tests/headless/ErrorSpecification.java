package tests.headless;

import java.util.List;

import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
import tests.headless.FindingsType.TruePositives;

public class ErrorSpecification {
	private String methodSignature;
	private List<TruePositives> truePositives;
	private List<FalsePositives> falsePositives;
	private List<FalseNegatives> falseNegatives;
	private Class<?> errorType;
	
	public ErrorSpecification(String methodSignature, Class<?> errorType, List<TruePositives> truePositives, List<FalsePositives> falsePositives,
			List<FalseNegatives> falseNegatives) {
		this.methodSignature = methodSignature;
		this.errorType = errorType;
		this.truePositives = truePositives;
		this.falsePositives = falsePositives;
		this.falseNegatives = falseNegatives;
	}
	
	public Class<?> getErrorType() {
		return errorType;
	}

	public void setErrorType(Class<?> errorType) {
		this.errorType = errorType;
	}

	public String getMethodSignature() {
		return methodSignature;
	}
	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}
	public List<TruePositives> getTruePositives() {
		return truePositives;
	}
	public void setTruePositives(List<TruePositives> truePositives) {
		this.truePositives = truePositives;
	}
	public List<FalsePositives> getFalsePositives() {
		return falsePositives;
	}
	public void setFalsePositives(List<FalsePositives> falsePositives) {
		this.falsePositives = falsePositives;
	}
	public List<FalseNegatives> getFalseNegatives() {
		return falseNegatives;
	}
	public void setFalseNegatives(List<FalseNegatives> falseNegatives) {
		this.falseNegatives = falseNegatives;
	}
	
	public int getTotalNumberOfFindings() {
		int totalFindings = 0;
		totalFindings += truePositives == null ? 0 : truePositives.size();
		totalFindings += falsePositives == null ? 0 : falsePositives.size();
		if (totalFindings == 0)
			throw new IllegalArgumentException("Specify atleast one findings type.");
		return totalFindings;
	}
}
