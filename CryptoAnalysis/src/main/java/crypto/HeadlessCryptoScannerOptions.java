package crypto;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HeadlessCryptoScannerOptions extends Options {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HeadlessCryptoScannerOptions() {
		Option cg = Option.builder().longOpt("cg").hasArg()
				.desc("Select the call graph for the analysis. (CHA, SPARK-LIBRARY, SPARK)").build();
		addOption(cg);

		Option rulesDir = Option.builder().longOpt("rulesDir").hasArg().required()
				.desc("Specify the directory for the CrySL rules").build();
		addOption(rulesDir);

		Option sootCp = Option.builder().longOpt("sootCp").hasArg()
				.desc("The class path of the whole project, including dependencies.").build();
		addOption(sootCp);

		Option applicationCp = Option.builder().longOpt("applicationCp").hasArg().required()
				.desc("The class path of the application, excluding dependencies. Objects within theses classes are analyzed.")
				.build();
		addOption(applicationCp);

		Option identifier = Option.builder().longOpt("softwareIdentifier").hasArg().desc("An identifier used to label output files.")
				.build();
		addOption(identifier);
		Option reportFile = Option.builder().longOpt("reportDir").hasArg().desc("A folder for the CogniCrypt report and .jimple files.")
				.build();
		addOption(reportFile);
		Option csvReportFile = Option.builder().longOpt("csvReportFile").hasArg().desc("Generates a summary of the finding as a CSV file.")
				.build();
		addOption(csvReportFile);
		Option preanalysisOpt = Option.builder().longOpt("preanalysis").hasArg(false).desc("Enables an intra-procedural pre-analysis.").build();
		addOption(preanalysisOpt);
		Option visualization = Option.builder().longOpt("visualization").hasArg(false).desc("Enables the visualization. This option requires that --reportFolder is also set. A folder /viz/ is created containing Json files that can be visualized by the visualization of WPDS.").build();
		addOption(visualization);
	}

}
