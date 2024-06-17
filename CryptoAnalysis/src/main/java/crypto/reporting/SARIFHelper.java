package crypto.reporting;

import boomerang.scene.WrappedClass;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SARIFHelper {

	private final Map<String, String> rulesMap = new HashMap<>();
	
	public SARIFHelper() {
		initialize();
	}

	private void initialize() {
		//SARIFConfig
		this.rulesMap.put(SARIFConfig.CALL_TO_ERROR_KEY, SARIFConfig.CALL_TO_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.CONSTRAINT_ERROR_KEY, SARIFConfig.CONSTRAINT_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.FORBIDDEN_METHOD_ERROR_KEY, SARIFConfig.FORBIDDEN_METHOD_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.HARD_CODED_ERROR_KEY, SARIFConfig.HARDCODED_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_KEY, SARIFConfig.IMPRECISE_VALUE_EXTRACTION_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.INCOMPLETE_OPERATION_ERROR_KEY, SARIFConfig.INCOMPLETE_OPERATION_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.INSTANCE_OF_ERROR_KEY, SARIFConfig.INSTANCE_OF_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.NEVER_TYPE_OF_ERROR_KEY, SARIFConfig.NEVER_TYPE_OF_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.NO_CALL_TO_ERROR_KEY, SARIFConfig.NO_CALL_TO_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.PREDICATE_CONTRADICTION_ERROR_KEY, SARIFConfig.PREDICATE_CONTRADICTION_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.REQUIRED_PREDICATE_ERROR_KEY, SARIFConfig.REQUIRED_PREDICATE_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.TYPESTATE_ERROR_KEY, SARIFConfig.TYPE_STATE_ERROR_VALUE);
		this.rulesMap.put(SARIFConfig.UNCAUGHT_EXCEPTION_ERROR_KEY, SARIFConfig.UNCAUGHT_EXCEPTION_ERROR_VALUE);
	}
	
	public JSONObject initialJson() {
		JSONObject json = new JSONObject();
		json.put(SARIFConfig.VERSION, SARIFConfig.VERSION_NUMBER);
		json.put(SARIFConfig.RUNS_KEY, Collections.singletonList(getRuns()));

		return json;
	}
	
	private JSONObject getRuns() {
		JSONObject jsonRuns = new JSONObject();
		jsonRuns.put(SARIFConfig.TOOL_KEY, getToolInfo());

		return jsonRuns;
	}
	
	public JSONObject getToolInfo() {
		JSONObject tool = new JSONObject();
		
		tool.put(SARIFConfig.ANALYSISTOOL_NAME_KEY, SARIFConfig.ANALYSISTOOL_NAME_VALUE);
		tool.put(SARIFConfig.VERSION, getClass().getPackage().getImplementationVersion());
		tool.put(SARIFConfig.SEMANTIC_VERSION_KEY, getClass().getPackage().getImplementationVersion());
		tool.put(SARIFConfig.LANGUAGE_KEY, SARIFConfig.LANGUAGE_VALUE);

		return tool;
	}
	
	public JSONObject getMessage(String text, String richText) {
		JSONObject message = new JSONObject();
		
		message.put(SARIFConfig.TEXT_KEY, text);
		message.put(SARIFConfig.RICH_TEXT_KEY, richText);
		
		return message;
	}
	
	public String getFileName(WrappedClass c) {
		return c.getName().replace(".", "/") + ".java";
	}
	
	public JSONArray getLocations(WrappedClass c, String methodName, int lineNumber, String method, String statement) {
		JSONArray locations = new JSONArray();
		JSONObject location = new JSONObject();

		JSONObject region = new JSONObject();
		region.put(SARIFConfig.START_LINE_KEY, String.valueOf(lineNumber));
		region.put(SARIFConfig.METHOD_KEY, method);
		region.put(SARIFConfig.STATEMENT_KEY, statement);

		JSONObject uri = new JSONObject();
		uri.put(SARIFConfig.URI_KEY, getFileName(c));

		JSONObject physicalLocation = new JSONObject();
		physicalLocation.put(SARIFConfig.FILE_LOCATION_KEY, uri);
		physicalLocation.put(SARIFConfig.REGION_KEY, region);
		
		location.put(SARIFConfig.PHYSICAL_LOCATION_KEY, physicalLocation);
		
		String fullyQualifiedLogicalName = c.getName().replace(".", "::") + "::" + methodName;
		location.put(SARIFConfig.FULLY_QUALIFIED_LOGICAL_NAME_KEY, fullyQualifiedLogicalName);
		
		locations.put(location);

		return new JSONArray(Collections.singletonList(locations));
	}
	
	public String getRuleDescription(String ruleId) {
		return this.rulesMap.get(ruleId);
	}

}
