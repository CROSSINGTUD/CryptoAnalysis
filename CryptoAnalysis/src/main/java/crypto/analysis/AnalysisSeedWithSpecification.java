package crypto.analysis;

import ideal.FactAtStatement;

public class AnalysisSeedWithSpecification {
	public final FactAtStatement factAtStmt;
	public final ClassSpecification spec;
	public AnalysisSeedWithSpecification(FactAtStatement factAtStmt, ClassSpecification spec){
		this.factAtStmt = factAtStmt;
		this.spec = spec;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((spec == null) ? 0 : spec.hashCode());
		result = prime * result + ((factAtStmt == null) ? 0 : factAtStmt.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisSeedWithSpecification other = (AnalysisSeedWithSpecification) obj;
		if (spec == null) {
			if (other.spec != null)
				return false;
		} else if (!spec.equals(other.spec))
			return false;
		if (factAtStmt == null) {
			if (other.factAtStmt != null)
				return false;
		} else if (!factAtStmt.equals(other.factAtStmt))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AnalysisSeedWithSpecification [factAtStmt=" + factAtStmt + ", spec=" + spec + "]";
	}
	
	
}
