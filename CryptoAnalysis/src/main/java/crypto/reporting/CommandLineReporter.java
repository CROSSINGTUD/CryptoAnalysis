package crypto.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import crypto.analysis.errors.AbstractError;
import crypto.rules.CryptSLRule;
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
		
		s += "\n";
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			s += String.format("Findings in Java Class: %s\n", c.getName());
			for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				s += String.format("\n\t in Method: %s\n", e.getKey().getSubSignature());
				for (AbstractError marker : e.getValue()) {
					s += String.format("\t\t%s violating CrySL rule for %s\n", marker.getClass().getSimpleName() ,marker.getRule().getClassName());
					s += String.format("\t\t\t%s\n", marker.toErrorMarkerString());
					s += String.format("\t\t\tat statement: %s\n\n", marker.getErrorLocation().getUnit().get());
				}
			}
			s += "\n";
		}
		s += "======================= CogniCrypt Summary ==========================\n";
		s += String.format("\tNumber of CrySL rules: %s\n", rules.size());
		s += String.format("\tNumber of Objects Analyzed: %s\n", this.seedCount);
		if(this.errorMarkers.rowKeySet().isEmpty()){
			s += "No violation of any of the rules found.";
		} else{
			s += "\n\tCogniCrypt found the following violations. For details see description above.\n";
			for(Entry<Class, Integer> e : errorMarkerCount.entrySet()){
				s += String.format("\t%s: %s\n", e.getKey().getSimpleName(),e.getValue());
			}
		}
		s += "=====================================================================";
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
