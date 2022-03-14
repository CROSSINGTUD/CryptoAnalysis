package tests.customerules;

import java.io.File;
import java.net.URI;

import org.junit.Before;

import crypto.analysis.CrySLRulesetSelector.Ruleset;
import crypto.cryslhandler.CrySLModelReaderClassPath;
import test.UsagePatternTestingFramework;
import tests.headless.MavenProject;

public class UsagePatternTestingFramworkForCustomRules extends UsagePatternTestingFramework {

	//private static boolean buildJarWithCostumClasses = false;
	private URI jarWithCostumClassesUri;
	
	@Override
	@Before
    public void beforeTestCaseExecution() {
		String mavenProjectPath = new File("../CustomClassesForSpecification/CustomClasses").getAbsolutePath();
		jarWithCostumClassesUri = new File(mavenProjectPath + File.separator + "target" + File.separator + "CustomClasses-0.0.1-SNAPSHOT.jar" ).toURI();
		CrySLModelReaderClassPath.addToClassPath(jarWithCostumClassesUri);
        super.beforeTestCaseExecution();
    }
	
	@Override
	protected Ruleset getRuleSet() {
		return Ruleset.CostumClasses;
	}

}
