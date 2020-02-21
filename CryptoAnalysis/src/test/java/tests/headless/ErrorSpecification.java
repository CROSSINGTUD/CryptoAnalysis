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
	
	public ErrorSpecification(String methodSignature) {
		this.methodSignature = methodSignature;
		this.truePositives = new ArrayList<TruePositives>();
		this.falseNegatives = new ArrayList<FalseNegatives>();
		this.falsePositives = new ArrayList<FalsePositives>();
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
		totalFindings += falseNegatives == null ? 0 : falseNegatives.size();
		if (totalFindings == 0)
			throw new IllegalArgumentException("Specify atleast one findings type.");
		return totalFindings;
	}
	
	public static class Builder {
		private ErrorSpecification spec;
		
		public Builder(String methodSignature) {
			this.spec = new ErrorSpecification(methodSignature);
		}
		
		public Builder withTPs(Class<?> errorType, int numberOfFindings) {
			this.spec.truePositives.add(new TruePositives(errorType, numberOfFindings));
			return this;
		}
		
		public Builder withFPs(Class<?> errorType, int numberOfFindings, String explanation) {
			this.spec.falsePositives.add(new FalsePositives(errorType, numberOfFindings, explanation));
			return this;
		}
		
		public Builder withFNs(Class<?> errorType, int numberOfFindings, String explanation) {
			this.spec.falseNegatives.add(new FalseNegatives(errorType, numberOfFindings, explanation));
			return this;
		}
		
		public ErrorSpecification build() {
			return spec;
		}
	}
}
