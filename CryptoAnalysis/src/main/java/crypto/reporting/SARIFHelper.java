package crypto.reporting;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import soot.SootClass;

@SuppressWarnings("unchecked")
public class SARIFHelper {
	
	private final Map<String, String> rulesMap = new HashMap<>();
	private final SourceCodeLocater sourceLocater;
	
	public SARIFHelper() {
		initialize();
		this.sourceLocater = null;
	}
	
	public SARIFHelper(SourceCodeLocater sourceLocater) {
		this.sourceLocater = sourceLocater;
	}

	private void initialize() {
		//SARIFConfig
		this.rulesMap.put(SARIFConfig.CONSTRAINT_ERROR_KEY, SARIFConfig.CONSTRAINT_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.FORBIDDEN_METHOD_ERROR_KEY, SARIFConfig.FORBIDDEN_METHOD_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_KEY, SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.INCOMPLETE_OPERATION_ERROR_KEY, SARIFConfig.INCOMPLETE_OPERATION_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.NEVER_TYPE_OF_ERROR_KEY, SARIFConfig.NEVER_TYPE_OF_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.REQUIRED_PREDICATE_ERROR_KEY, SARIFConfig.REQUIRED_PREDICATE_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.TYPE_STATE_ERROR_KEY, SARIFConfig.TYPE_STATE_ERROR_VALUE);
	}
	
	public JSONObject initialJson() {
		JSONObject json = new JSONObject();
		json.put(SARIFConfig.VERSION, SARIFConfig.VERSION_NUMBER);
		json.put(SARIFConfig.RUNS_KEY, new JSONArray().add(getRuns()));
		return json;
	}
	
	private JSONObject getRuns() {
		JSONObject jsonRuns = new JSONObject();
		jsonRuns.put(SARIFConfig.TOOL_KEY, getToolInfo());
		return jsonRuns;
	}
	
	public JSONObject getToolInfo() {
		JSONObject tool = new JSONObject();
		tool.put(SARIFConfig.TOOL_NAME_KEY, SARIFConfig.TOOL_NAME_VALUE);
		tool.put(SARIFConfig.TOOL_FULL_NAME_KEY, SARIFConfig.TOOL_FULL_NAME_VALUE);
		tool.put(SARIFConfig.VERSION, SARIFConfig.VERSION_NUMBER);
		tool.put(SARIFConfig.SEMANTIC_VERSION_KEY,SARIFConfig.SEMANTIC_VERSION_VALUE);
		tool.put(SARIFConfig.LANGUAGE_KEY, SARIFConfig.LANGUAGE_VALUE);
		return tool;
	}
	
	public JSONObject getMessage(String text, String richText) {
		JSONObject message = new JSONObject();
		message.put(SARIFConfig.TEXT_KEY, text);
		message.put(SARIFConfig.RICH_TEXT_KEY, richText);
		return message;
	}
	
	public String getFileName(SootClass c) {
		return sourceLocater == null ? c.getName().replace(".", "/") + ".java" : sourceLocater.getAbsolutePath(c);
	}
	
	public JSONArray getLocations(SootClass c, String methodName, int lineNumber) {
		JSONArray locations = new JSONArray();
		JSONObject location = new JSONObject();
		
		JSONObject startLine = new JSONObject();
		startLine.put(SARIFConfig.START_LINE_KEY, lineNumber);
		JSONObject uri = new JSONObject();
		uri.put(SARIFConfig.URI_KEY, getFileName(c));
		JSONObject physicalLocation = new JSONObject();
		physicalLocation.put(SARIFConfig.FILE_LOCATION_KEY, uri);
		physicalLocation.put(SARIFConfig.REGION_KEY, startLine);
		
		location.put(SARIFConfig.PHYSICAL_LOCATION_KEY, physicalLocation);
		String fullyQualifiedLogicalName = c.getName().replace(".", "::") + "::" + methodName;
		location.put(SARIFConfig.FULLY_QUALIFIED_LOGICAL_NAME_KEY, fullyQualifiedLogicalName);
		
		locations.add(location);
		return locations;
	}
	
	public String getRuleDescription(String ruleId) {
		return this.rulesMap.get(ruleId);
	}
}
