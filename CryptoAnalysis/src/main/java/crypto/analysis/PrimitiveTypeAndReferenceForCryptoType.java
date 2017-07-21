package crypto.analysis;

import java.util.Collection;

import com.google.common.base.Optional;

import boomerang.accessgraph.AccessGraph;
import boomerang.allocationsitehandler.PrimitiveTypeAndReferenceType;
import boomerang.pointsofindirection.Alloc;
import boomerang.pointsofindirection.AllocationSiteHandler;
import soot.SootMethod;
import soot.jimple.AssignStmt;

public class PrimitiveTypeAndReferenceForCryptoType extends PrimitiveTypeAndReferenceType{


	@Override
	public Optional<AllocationSiteHandler> callToReturnAssign(AssignStmt callSite, AccessGraph source,
			Collection<SootMethod> callees) {
		if(callSite.containsInvokeExpr()){
			if(callSite.getInvokeExpr().toString().contains("getInstance")){
				return Optional.<AllocationSiteHandler>of(new AllocationSiteHandler() {
					@Override
					public Alloc alloc() {
						return new Alloc(source,callSite, false);
					}
				});
			}
		}
		return super.callToReturnAssign(callSite, source, callees);
	}

}
