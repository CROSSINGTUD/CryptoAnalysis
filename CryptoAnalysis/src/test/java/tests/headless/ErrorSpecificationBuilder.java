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
	
	public ErrorSpecificationBuilder(String methodSignature) {
		this.methodSignature = methodSignature;
		this.truePositives = new ArrayList<TruePositives>();
		this.falseNegatives = new ArrayList<FalseNegatives>();
		this.falsePositives = new ArrayList<FalsePositives>();
	}
	
	public ErrorSpecificationBuilder withTPs(TruePositives truePositives) {
		this.truePositives.add(truePositives);
		return this;
	}
	
	public ErrorSpecificationBuilder withFPs(FalsePositives falsePositives) {
		this.falsePositives.add(falsePositives);
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
