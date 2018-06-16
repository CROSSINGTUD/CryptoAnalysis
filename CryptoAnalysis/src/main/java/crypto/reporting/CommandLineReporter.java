package crypto.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ErrorWithObjectAllocation;
import crypto.rules.CryptSLRule;
import soot.Printer;
import soot.SootClass;
import soot.SootMethod;
import soot.util.EscapedWriter;

public class CommandLineReporter extends ErrorMarkerListener {

	private File outputFolder;
	private List<CryptSLRule> rules;
	private Collection<IAnalysisSeed> objects = new HashSet<>();

	public CommandLineReporter(String string, List<CryptSLRule> rules) {
		this.outputFolder = (string != null ? new File(string) : null);
		this.rules = rules;
	}

	@Override
	public void discoveredSeed(IAnalysisSeed object) {
		this.objects.add(object);
	}
	@Override
	public void afterAnalysis() {
		String s = "";

		s += "Ruleset: \n";
		for (CryptSLRule r : this.rules) {
			s += String.format("\t%s\n", r.getClassName());
		}

		s += "\n";

		s += "Analyzed Objects: \n";
		for (IAnalysisSeed r : this.objects) {
			s += String.format("\tObject:\n");
			s += String.format("\t\tVariable: %s\n", r.var().value());
			s += String.format("\t\tType: %s\n", r.getType());
			s += String.format("\t\tStatement: %s\n", r.stmt().getUnit().get());
			s += String.format("\t\tMethod: %s\n", r.getMethod());
			s += String.format("\t\tSHA-256: %s\n", r.getObjectId());
		}
		
		
		s += "\n";
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			s += String.format("Findings in Java Class: %s\n", c.getName());
			for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				s += String.format("\n\t in Method: %s\n", e.getKey().getSubSignature());
				for (AbstractError marker : e.getValue()) {
					s += String.format("\t\t%s violating CrySL rule for %s", marker.getClass().getSimpleName() ,marker.getRule().getClassName());
					if(marker instanceof ErrorWithObjectAllocation) {
						 s += String.format(" (on Object #%s)\n", ((ErrorWithObjectAllocation) marker).getObjectLocation().getObjectId());
					} else {
						 s += "\n";
					}
					s += String.format("\t\t\t%s\n", marker.toErrorMarkerString());
					s += String.format("\t\t\tat statement: %s\n\n", marker.getErrorLocation().getUnit().get());
				}
			}
			s += "\n";
		}
		s += "======================= CogniCrypt Summary ==========================\n";
		s += String.format("\tNumber of CrySL rules: %s\n", rules.size());
		s += String.format("\tNumber of Objects Analyzed: %s\n", this.objects.size());
		if(this.errorMarkers.rowKeySet().isEmpty()){
			s += "No violation of any of the rules found.";
		} else{
			s += "\n\tCogniCrypt found the following violations. For details see description above.\n";
			for(Entry<Class, Integer> e : errorMarkerCount.entrySet()){
				s += String.format("\t%s: %s\n", e.getKey().getSimpleName(),e.getValue());
			}
		}
		s += "=====================================================================";

		System.out.println(s);
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
	}
}
