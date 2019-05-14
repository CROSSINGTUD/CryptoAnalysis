package tests.headless;

import java.util.ArrayList;
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
	
	public static class Builder {
		private String methodSignature;
		private List<TruePositives> truePositives;
		private List<FalsePositives> falsePositives;
		private List<FalseNegatives> falseNegatives;
		private Class<?> errorType;
		
		public Builder(String methodSignature) {
			this.methodSignature = methodSignature;
			this.truePositives = new ArrayList<TruePositives>();
			this.falseNegatives = new ArrayList<FalseNegatives>();
			this.falsePositives = new ArrayList<FalsePositives>();
		}
		
		public Builder withTPs(Class<?> errorType, int numberOfFindings) {
			this.truePositives.add(new TruePositives(errorType, numberOfFindings));
			return this;
		}
		
		public Builder withFPs(Class<?> errorType, int numberOfFindings, String explanation) {
			this.falsePositives.add(new FalsePositives(errorType, numberOfFindings, explanation));
			return this;
		}
		
		public Builder withFNs(FalseNegatives falseNegatives) {
			this.falseNegatives.add(falseNegatives);
			return this;
		}
		
		public ErrorSpecification build() {
			return new ErrorSpecification(methodSignature, errorType, truePositives, falsePositives, falseNegatives);
		}
	}
}
