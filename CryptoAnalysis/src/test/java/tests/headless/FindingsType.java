package tests.headless;

public class FindingsType {
	private int numberOfFindings;

	private FindingsType(int numberOfFindings) {
		this.numberOfFindings = numberOfFindings;
	}

	public int getNumberOfFindings() {
		return numberOfFindings;
	}

	public static final class TruePositives extends FindingsType {

		public TruePositives(int numberOfFindings) {
			super(numberOfFindings);
		}

		@Override
		public String toString() {
			return "TruePositives(" + getNumberOfFindings() + ")";
		}
	}

	public static class FalsePositives extends FindingsType {

		private String explanation;

		public FalsePositives(int numberOfFindings, String explanation) {
			super(numberOfFindings);
			this.explanation = explanation;
		}

		public String getExplanation() {
			return explanation;
		};

		@Override
		public String toString() {
			return "FalsePositives(" + getNumberOfFindings() + ")";
		}
	}

	public static class FalseNegatives extends FindingsType {

		private String explanation;

		public FalseNegatives(int numberOfFindings, String explanation) {
			super(numberOfFindings);
			this.explanation = explanation;
		}

		public String getExplanation() {
			return explanation;
		};

		@Override
		public String toString() {
			return "FalsePositives(" + getNumberOfFindings() + ")";
		}
	}

	public static class NoFalseNegatives extends FalseNegatives {

		public NoFalseNegatives() {
			super(0, "No false negatives, no explanation required!");
		}

		@Override
		public String toString() {
			return "No False Negatives";
		}
	}
	
	public static class NoFalsePositives extends FalsePositives {

		public NoFalsePositives() {
			super(0, "No false positives, no explanation required!");
		}

		@Override
		public String toString() {
			return "No False Positives";
		}
	}
}
