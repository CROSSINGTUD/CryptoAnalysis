package crypto.reporting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;
import crypto.analysis.errors.ErrorWithObjectAllocation;
import crypto.rules.CrySLRule;
import soot.SootClass;
import soot.SootMethod;

/**
 * This listener generates analysis report in JSON format
 *
 * @author Seena Mathew
 *
 */

public class JSONReporter extends ErrorMarkerListener{

	private File outputFolder;
	private List<CrySLRule> rules;
	private Collection<IAnalysisSeed> objects = new HashSet<>();
	
	public JSONReporter(String reportDir,  List<CrySLRule> rules) {
		this.outputFolder = (reportDir != null ? new File(reportDir) : null);
		this.rules = rules;
	}
	
	@Override
	public void discoveredSeed(IAnalysisSeed object) {
		this.objects.add(object);
	}
	
	@Override
	public void afterAnalysis() {
		Map<String, Object> json = new LinkedHashMap<>();
		List<Object> ruleSet = new ArrayList<Object>();
		for (CrySLRule r : this.rules) {
			ruleSet.add(r.getClassName());
		}
		json.put(JSONConfig.RULESET, ruleSet);
		
		Map<String, Object> analysis = new LinkedHashMap<>();
		List<Object> analysedObject = new ArrayList<Object>();
		Map<String, Object> object;
		for (IAnalysisSeed r : this.objects) {
		 object = new LinkedHashMap<>();
		 object.put(JSONConfig.VARIABLE, r.var().value().toString());
		 object.put(JSONConfig.TYPE, r.getType().toString());
		 object.put(JSONConfig.STATEMENT, r.stmt().getUnit().get().toString());
		 object.put(JSONConfig.METHOD, r.getMethod().toString());
		 object.put(JSONConfig.SHA_256, r.getObjectId());
		 object.put(JSONConfig.SECURE, secureObjects.contains(r));
		 analysedObject.add(object);
		}
		analysis.put(JSONConfig.ANALYZED_OBJECTS, analysedObject);
		List<Object> findingsInClass = new ArrayList<Object>();
		Map<String, Object> findings;
		for (SootClass c : this.errorMarkers.rowKeySet()) {
			findings = new LinkedHashMap<>();
			findings.put(JSONConfig.CLASS_NAME, c.getName());
			List<Object> methods = new ArrayList<Object>();
			Map<String, Object> method;
			for (Entry<SootMethod, Set<AbstractError>> e : this.errorMarkers.row(c).entrySet()) {
				method = new LinkedHashMap<>();
				method.put(JSONConfig.METHOD_NAME, e.getKey().getSubSignature());
				List<Object> details = new ArrayList<Object>();
				Map<String, Object> detail;
				for (AbstractError marker : e.getValue()) {
					detail = new LinkedHashMap<>();
					String violation = String.format("%s violating CrySL rule for %s", marker.getClass().getSimpleName() ,marker.getRule().getClassName());
					if(marker instanceof ErrorWithObjectAllocation) {
						violation += String.format(" (on Object #%s)\n", ((ErrorWithObjectAllocation) marker).getObjectLocation().getObjectId());
					}
					detail.put(JSONConfig.VIOLATION, violation);
					detail.put(JSONConfig.DESCRIPTION, marker.toErrorMarkerString());
					detail.put(JSONConfig.LOCATION, "At statement " + marker.getErrorLocation().getUnit().get().toString());
					details.add(detail);
				}
				method.put(JSONConfig.DETAILS, details);
				methods.add(method);
			}
			findings.put(JSONConfig.METHODS,methods);
			findingsInClass.add(findings);
		}
		analysis.put(JSONConfig.FINDINGS_IN_JAVA_CLASS, findingsInClass);
		json.put(JSONConfig.ANALYSIS, analysis);
		
		Map<String, Object> summary = new LinkedHashMap<>();
		summary.put(JSONConfig.TOTAL_CRYSL_RULES, rules.size());
		summary.put(JSONConfig.OBJECTS_ANALYSED, this.objects.size());
		Map<String, Object> violations = new LinkedHashMap<>();
		if(!this.errorMarkers.rowKeySet().isEmpty()){
			for(Entry<Class, Integer> e : errorMarkerCount.entrySet()){
				violations.put(e.getKey().getSimpleName(),e.getValue());
			}
		}
		summary.put(JSONConfig.VIOLATIONS, violations);
		json.put(JSONConfig.SUMMARY, summary);
	
		if (outputFolder != null) {
		try {
			 ObjectMapper mapper = new ObjectMapper();
			 ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			 writer.writeValue(Paths.get(outputFolder + File.separator+"CogniCrypt-JSON-Report.json").toFile(), json);
	     } 
		catch (IOException e) {
	    	 e.printStackTrace();
	     }
		}
	}
}
	

