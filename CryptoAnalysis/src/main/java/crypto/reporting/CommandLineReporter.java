package crypto.reporting;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import crypto.analysis.IAnalysisSeed;
import crypto.rules.CrySLRule;

public class CommandLineReporter extends ErrorMarkerListener {

	private File outputFolder;
	private List<CrySLRule> rules;
	private Collection<IAnalysisSeed> objects = new HashSet<>();

	/**
	 * The analysis report
	 */
	private String analysisReport;


	/**
	 * Creates {@link CommandLineReporter} a constructor with reportDir and rules as parameter
	 *
	 * @param reportDir a {@link String} path giving the location of the report directory
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
	 */
	public CommandLineReporter(String reportDir, List<CrySLRule> rules) {
		this.outputFolder = (reportDir != null ? new File(reportDir) : null);
		this.rules = rules;
	}

	/**
	 * Creates {@link CommandLineReporter} a constructor with reportDir and rules as parameter
	 *
	 * @param rules {@link CrySLRule} the rules with which the project is analyzed
	 */
	public CommandLineReporter(List<CrySLRule> rules) {
		this.rules = rules;
	}

	@Override
	public void discoveredSeed(IAnalysisSeed object) {
		this.objects.add(object);
	}
	@Override
	public void afterAnalysis() {
		this.analysisReport = ReporterHelper.generateReport(this.rules, this.objects, this.secureObjects, this.errorMarkers, this.errorMarkerCount);
		System.out.println(analysisReport);

		/*
		if (outputFolder != null) {
			try {
				FileWriter writer = new FileWriter(outputFolder +"/CogniCrypt-Report.txt");
				writer.write(s);
				writer.close();
				for (SootClass c : this.errorMarkers.rowKeySet()) {
					FileOutputStream streamOut = new FileOutputStream(new File(outputFolder +"/"+c.toString()+".jimple"));
					PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
					Printer.v().printTo(c, writerOut);
					writerOut.flush();
					streamOut.close();
					writerOut.close();
				}
			} catch (IOException e) {
				throw new RuntimeException("Could not write to file " + outputFolder);
			}
		}
		*/
	}
}
