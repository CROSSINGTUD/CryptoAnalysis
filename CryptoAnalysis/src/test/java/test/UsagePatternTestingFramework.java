package test;

import java.util.Map;

import crypto.analysis.ClassSpecification;
import crypto.analysis.CryptoScanner;
import crypto.analysis.ErrorReporter;
import soot.SceneTransformer;
import soot.Unit;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.solver.cfg.InfoflowCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import test.core.selfrunning.AbstractTestingFramework;
import test.core.selfrunning.ImprecisionException;

public abstract class UsagePatternTestingFramework extends AbstractTestingFramework{

	@Override
	protected SceneTransformer createAnalysisTransformer() throws ImprecisionException {
		return new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				final InfoflowCFG icfg = new InfoflowCFG(new JimpleBasedInterproceduralCFG(true));
				
				
				CryptoScanner scanner = new CryptoScanner(null) {
					
					@Override
					public IInfoflowCFG icfg() {
						return icfg;
					}

					@Override
					public ErrorReporter errorReporter() {
						// TODO Auto-generated method stub
						return new ErrorReporter(){

							@Override
							public void report(ClassSpecification spec, Unit stmt, Violation details) {
								System.err.println("Class Specification " + spec +" reporter Error at \n\t" + stmt);
							}
							
						};
					}
				};
				scanner.scan();
			}
		};
	}

}
