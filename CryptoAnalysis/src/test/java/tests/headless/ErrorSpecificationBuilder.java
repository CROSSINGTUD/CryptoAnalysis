package tests.headless;

import java.util.ArrayList;
import java.util.List;

import tests.headless.FindingsType.FalseNegatives;
import tests.headless.FindingsType.FalsePositives;
import tests.headless.FindingsType.TruePositives;

public class ErrorSpecificationBuilder {
	private String methodSignature;
	private List<TruePositives> truePositives;
	private List<FalsePositives> falsePositives;
	private List<FalseNegatives> falseNegatives;
	private Class<?> errorType;
	
	private ErrorSpecificationBuilder(String methodSignature) {
		this.methodSignature = methodSignature;
		this.truePositives = new ArrayList<TruePositives>();
		this.falseNegatives = new ArrayList<FalseNegatives>();
		this.falsePositives = new ArrayList<FalsePositives>();
	}
	
	public ErrorSpecificationBuilder withTPs(Class<?> errorType, int numberOfFindings) {
		this.truePositives.add(new TruePositives(errorType, numberOfFindings));
		return this;
	}
	
	public ErrorSpecificationBuilder withFPs(Class<?> errorType, int numberOfFindings, String explanation) {
		this.falsePositives.add(new FalsePositives(errorType, numberOfFindings, explanation));
		return this;
	}
	
	public ErrorSpecificationBuilder withFNs(FalseNegatives falseNegatives) {
		this.falseNegatives.add(falseNegatives);
		return this;
	}
	
	public ErrorSpecification build() {
		return new ErrorSpecification(methodSignature, errorType, truePositives, falsePositives, falseNegatives);
	}
}
