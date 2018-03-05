package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import crypto.rules.CryptSLRule;

import java.util.List;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;

public class CommandLineReporter extends ErrorMarkerListener {

	private File outputFile;
	private List<CryptSLRule> rules;

	public CommandLineReporter(String string, List<CryptSLRule> rules) {
		this.outputFile = (string != null ? new File(string) : null);
		this.rules = rules;
	}

	@Override
	public void afterAnalysis() {
		String s = "";

		s += "Ruleset: \n";
		for (CryptSLRule r : this.rules) {
			s += String.format("\t%s\n", r.getClassName());
		}
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			s += String.format("Findings in Java Class: %s\n", c.getName());
			for (Entry<SootMethod, Set<ErrorMarker>> e : this.errorMarkers.row(c).entrySet()) {
				s += String.format("\t in Method: %s\n", e.getKey().getSubSignature());
				for (ErrorMarker marker : e.getValue()) {
					s += String.format("\t\t%s\n", marker);
				}
			}
			s += "\n";
		}
		if (outputFile != null) {
			try {
				FileWriter writer = new FileWriter(outputFile);
				writer.write(s);
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException("Could not write to file " + outputFile);
			}
		} else {
			System.out.println(s);
		}
	}
}
