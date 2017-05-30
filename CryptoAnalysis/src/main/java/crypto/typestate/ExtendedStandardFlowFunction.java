package crypto.typestate;

import java.util.Set;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.StateNode;
import heros.FlowFunction;
import ideal.PerSeedAnalysisContext;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Local;
import soot.Unit;
import soot.jimple.AssignStmt;
import typestate.TypestateDomainValue;

public class ExtendedStandardFlowFunction extends StandardFlowFunctions<TypestateDomainValue<StateNode>> {

	public ExtendedStandardFlowFunction(PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context) {
		super(context);
	}
	
	@Override
	public FlowFunction<AccessGraph> getCallToReturnFlowFunction(AccessGraph sourceFact, Unit callStmt, Unit returnSite,
			boolean hasCallees) {
		return new WrappedFlowFunction(callStmt, super.getCallToReturnFlowFunction(sourceFact, callStmt, returnSite, hasCallees));
	}
	
	private class WrappedFlowFunction implements FlowFunction<AccessGraph>{

		private FlowFunction<AccessGraph> res;
		private Unit callStmt;

		public WrappedFlowFunction(Unit callStmt, FlowFunction<AccessGraph> res) {
			this.callStmt = callStmt;
			this.res = res;
		}

		@Override
		public Set<AccessGraph> computeTargets(AccessGraph arg) {
			Set<AccessGraph> targets = res.computeTargets(arg);
			if(callStmt.toString().contains("generateKey") && callStmt instanceof AssignStmt){
				AssignStmt as = (AssignStmt) callStmt;
				targets.add(new AccessGraph((Local) as.getLeftOp(), as.getLeftOp().getType()));
				System.out.println("FLOWs FTO " + targets);
			}
			return targets;
		}
		
	}

}
