package crypto.reporting;

public class SARIFConfig {
	public static final String VERSION = "version";
	public static final String SARIF_VERSION = "sarifVersion";
	public static final String SARIF_VERSION_NUMBER = "2.0.0";
	public static final String VERSION_NUMBER = "1.0.0";
	public static final String RUNS_KEY = "runs";
	
	public static final String TOOL_KEY = "tool";
	public static final String TOOL_NAME_KEY = "name";
	public static final String TOOL_NAME_VALUE = "CogniCrypt";
	public static final String TOOL_FULL_NAME_KEY = "fullName";
	public static final String TOOL_FULL_NAME_VALUE = "CogniCrypt (en-US)";
	public static final String SEMANTIC_VERSION_KEY = "semanticVersion";
	public static final String SEMANTIC_VERSION_VALUE = "1.0.0";
	public static final String LANGUAGE_KEY = "language";
	public static final String LANGUAGE_VALUE = "en-US";
	
	public static final String ANALYSISTOOL_NAME_KEY = "name";
	public static final String ANALYSISTOOL_NAME_VALUE = "CryptoAnalysis";
	
	public static final String FILES_KEY = "files";
	public static final String MIME_TYPE_KEY = "mimeType";
	public static final String MIME_TYPE_VALUE = "text/java";
	
	public static final String RESULTS_KEY = "results";
	public static final String RULE_ID_KEY = "ruleId";
	public static final String MESSAGE_KEY = "message";
	public static final String TEXT_KEY = "text";
	public static final String RICH_TEXT_KEY = "richText";
	public static final String LOCATIONS_KEY = "locations";
	public static final String PHYSICAL_LOCATION_KEY = "physicalLocation";
	public static final String FILE_LOCATION_KEY = "fileLocation";
	public static final String URI_KEY = "uri";
	public static final String REGION_KEY = "region";
	public static final String START_LINE_KEY = "startLine";
	public static final String FULLY_QUALIFIED_LOGICAL_NAME_KEY = "fullyQualifiedLogicalName";
//	public static final String 
	
	public static final String RESOURCES_KEY = "resources";
	public static final String RULES_KEY = "rules";
	public static final String RULES_ID_KEY = "id";
	public static final String FULL_DESCRIPTION_KEY = "fullDescription";
	
	//rules and their descriptions
	public static final String CONSTRAINT_ERROR_KEY = "ConstraintError";
	public static final String CONSTRAINT_ERROR_VALUE = "A constraint of a CrySL rule is violated, e.g., a key is generated with the wrong key size.";
	public static final String NEVER_TYPE_OF_ERROR_KEY = "NeverTypeOfError";
	public static final String NEVER_TYPE_OF_ERROR_VALUE = "Reported when a value was found to be of a certain reference type: For example, a character array containing a password should never be converted from a String";
	public static final String HARDCODED_ERROR_KEY = "HardCodedError";
	public static final String HARDCODED_ERROR_VALUE = "A hardcoded value was found. Load the value dynamically from a data storage.";
	public static final String FORBIDDEN_METHOD_ERROR_KEY = "ForbiddenMethodError";
	public static final String FORBIDDEN_METHOD_ERROR_VALUE = "A method that is forbidden (CrySL block FORBIDDEN) to be called under some circumstances was found.";
	public static final String IMPRECISE_VALUE_EXTRACTION_ERROR_KEY = "ImpreciseValueExtractionError";
	public static final String IMPRECISE_VALUE_EXTRACTION_ERROR_VALUE = "The static analysis was not able to extract all information required within the CrySL CONSTRAINT block. For example the key size could be supplied as a value listed in a configuration file. The static analysis does not model the file's content and may not constraint on the value.";
	public static final String TYPE_STATE_ERROR_KEY = "TypestateError";
	public static final String TYPE_STATE_ERROR_VALUE = "The ORDER block of CrySL is violated, i.e., the expected method sequence call to be made is incorrect. For example, a Signature object expects a call to initSign(key) prior to update(data).";
	public static final String REQUIRED_PREDICATE_ERROR_KEY = "RequiredPredicateError";
	public static final String REQUIRED_PREDICATE_ERROR_VALUE = "An object A expects an object B to have been used correctly (CrySL blocks REQUIRES and ENSURES). For example a Cipher object requires a SecretKey object to be correctly and securely generated.";
	public static final String INCOMPLETE_OPERATION_ERROR_KEY = "IncompleteOperationError";
	public static final String INCOMPLETE_OPERATION_ERROR_VALUE = "The usage of an object may be incomplete: For example a Cipherobject may be initialized but never used for en- or decryption, this may render the code dead. This error heavily depends on the computed call graph (CHA by default)";
	public static final String INSTANCE_OF_ERROR_KEY = "InstanceOfError";
	public static final String INSTANCE_OF_ERROR_VALUE = "Reported when a value was found to not be of a certain instance.";
	
}
