package crypto.analysis;

import java.io.File;
import java.util.Set;

import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import boomerang.accessgraph.AccessGraph;
import boomerang.cfg.IExtendedICFG;
import crypto.rules.StateNode;
import ideal.debug.IDEVizDebugger;
import soot.Unit;
import typestate.TypestateDomainValue;

public class CryptoVizDebugger extends IDEVizDebugger<TypestateDomainValue<StateNode>>{

	public CryptoVizDebugger(File file, IExtendedICFG icfg) {
		super(file, icfg);
	}


	public void addEnsuredPredicates(
			Table<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> existingPredicates) {
		for(Cell<Unit, AccessGraph, Set<EnsuredCryptSLPredicate>> c: existingPredicates.cellSet()){
			if(!c.getValue().isEmpty())
				this.addInformationAtStmt(c.getRowKey(), new SeedWithEnsuredPreds(c.getColumnKey(), c.getValue()));
		}
	}

	private class SeedWithEnsuredPreds{

		private AccessGraph analysisSeedWithSpecification;
		private Set<EnsuredCryptSLPredicate> value;

		public SeedWithEnsuredPreds(AccessGraph analysisSeedWithSpecification, Set<EnsuredCryptSLPredicate> value) {
			this.analysisSeedWithSpecification = analysisSeedWithSpecification;
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((analysisSeedWithSpecification == null) ? 0 : analysisSeedWithSpecification.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			SeedWithEnsuredPreds other = (SeedWithEnsuredPreds) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (analysisSeedWithSpecification == null) {
				if (other.analysisSeedWithSpecification != null)
					return false;
			} else if (!analysisSeedWithSpecification.equals(other.analysisSeedWithSpecification))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		private CryptoVizDebugger getOuterType() {
			return CryptoVizDebugger.this;
		}
		
		@Override
		public String toString() {
			return analysisSeedWithSpecification.getBase() +" " + value;
		}
		
	}
}
