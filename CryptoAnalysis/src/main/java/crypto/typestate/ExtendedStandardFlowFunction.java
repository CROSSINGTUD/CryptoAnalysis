package crypto.typestate;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import boomerang.accessgraph.AccessGraph;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLRule;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import heros.FlowFunction;
import ideal.PerSeedAnalysisContext;
import ideal.flowfunctions.StandardFlowFunctions;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import typestate.TypestateDomainValue;

public class ExtendedStandardFlowFunction extends StandardFlowFunctions<TypestateDomainValue<StateNode>> {
	private final Set<FlowAtCallsite> additionalFlows = Sets.newHashSet(); 
	public ExtendedStandardFlowFunction(PerSeedAnalysisContext<TypestateDomainValue<StateNode>> context,
			CryptSLRule rule) {
		super(context);
		extractFlows(rule);
	}

	private void extractFlows(CryptSLRule rule) {
		for (TransitionEdge transEdge : rule.getUsagePattern().getAllTransitions()) {
			for (CryptSLMethod label : transEdge.getLabel()) {
				int index = 0;
				List<Boolean> backward = label.getBackward();
				for (Boolean trackBackward : backward) {
					if (!trackBackward) {
						for(SootMethod callee : CryptSLMethodToSootMethod.v().convert(label)){
							additionalFlows.add(new FlowAtCallsite(callee, index - 1));
						}
					}
					index++;
				}
			}
		}
	}

	@Override
	public FlowFunction<AccessGraph> getCallToReturnFlowFunction(AccessGraph sourceFact, Unit callStmt, Unit returnSite,
			boolean hasCallees) {
		return new WrappedFlowFunction(callStmt,
				super.getCallToReturnFlowFunction(sourceFact, callStmt, returnSite, hasCallees));
	}

	private class WrappedFlowFunction implements FlowFunction<AccessGraph> {

		private FlowFunction<AccessGraph> res;
		private Unit callStmt;

		public WrappedFlowFunction(Unit callStmt, FlowFunction<AccessGraph> res) {
			this.callStmt = callStmt;
			this.res = res;
		}

		@Override
		public Set<AccessGraph> computeTargets(AccessGraph arg) {
			Set<AccessGraph> targets = res.computeTargets(arg);
			if(callStmt instanceof Stmt){
				Stmt callStatement = (Stmt) callStmt;
				if(callStatement.containsInvokeExpr()){
					InvokeExpr invokeExpr = callStatement.getInvokeExpr();
					SootMethod method = invokeExpr.getMethod();
					for(FlowAtCallsite flow : additionalFlows){
						if(flow.method.equals(method)){
							if(flow.index == -1){
								if( callStmt instanceof AssignStmt){
									AssignStmt as = (AssignStmt) callStmt;
									targets.add(new AccessGraph((Local) as.getLeftOp(), as.getLeftOp().getType()));
								}
							} else {
								Value parameter = invokeExpr.getArg(flow.index);
								if(parameter instanceof Local){
									targets.add(new AccessGraph((Local) parameter,parameter.getType()));
								}
							}
						}
					}
				}
			}
			return targets;
		}

	}

	private class FlowAtCallsite{
		private final SootMethod method;
		private final int index;
		private FlowAtCallsite(SootMethod m, int i){
			method = m;
			index = i;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result + ((method == null) ? 0 : method.hashCode());
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
			FlowAtCallsite other = (FlowAtCallsite) obj;
			if (index != other.index)
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			return true;
		}
		
	}
}
