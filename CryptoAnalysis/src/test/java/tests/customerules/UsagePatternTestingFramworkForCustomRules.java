package tests.customerules;

import java.io.File;
import java.net.URI;

import org.junit.Before;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.cryslhandler.CrySLModelReaderClassPath;
import main.prefined.A;
import test.UsagePatternTestingFramework;
import test.assertions.Assertions;
import tests.headless.MavenProject;

public class UsagePatternTestingFramworkForCustomRules extends UsagePatternTestingFramework {

	//private static boolean buildJarWithCostumClasses = false;
	private URI jarWithCostumClassesUri;
	
	@Override
	@Before
    public void beforeTestCaseExecution() {
		try {
			String mavenProjectPath = new File("../CustomClassesForSpecification/CustomClasses").getAbsolutePath();
			jarWithCostumClassesUri = new File(mavenProjectPath + File.separator + "target" + File.separator + "CustomClasses-0.0.1-SNAPSHOT.jar" ).toURI();
			CrySLModelReaderClassPath.addToClassPath(jarWithCostumClassesUri);
		}
		catch(IllegalStateException e){
			
		}
        super.beforeTestCaseExecution();
    }
	
	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.CostumClasses;
	}

}
