package model;

import java.util.List;
import com.fasterxml.jackson.annotation.*;

public class Token {
	
	private String _guid;
	private String _id = "";
	private String _pairingCode = "";
	private long _pairingExpiration;
	private String _facade = "";
	private String _label = "";
	private int _count = 0;
	private List<Policy> _policies;
	private String _resource;
	private String _value;
	private long _dateCreated;
	
    public Token() {}

    // API fields
    //

    @JsonProperty("guid")
	public String getGuid() {
		return _guid;
	}
    
    @JsonProperty("guid")
	public void setGuid(String guid) {
		this._guid = guid;
	}
    
    // Required fields
    //

    @JsonProperty("id")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getId() {
		return _id;
	}
    
    @JsonProperty("id")
	public void setId(String id) {
		this._id = id;
	}

    // Optional fields
    //

    @JsonProperty("pairingCode")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getPairingCode() {
		return _pairingCode;
	}
    
    @JsonProperty("pairingCode")
	public void setPairingCode(String pairingCode) {
		this._pairingCode = pairingCode;
	}

    @JsonProperty("facade")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String getFacade() {
		return _facade;
	}

    @JsonProperty("facade")
    public void setFacade(String facade) {
		this._facade = facade;
	}
	
    @JsonProperty("label")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public String getLabel() {
		return _label;
	}

    @JsonProperty("label")
    public void setLabel(String label) {
		this._label = label;
	}
	
    @JsonProperty("count")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int getCount() {
		return _count;
	}

    @JsonProperty("count")
    public void setCount(int count) {
		this._count = count;
	}

    // Response fields
    //

    @JsonIgnore
	public long getPairingExpiration() {
		return _pairingExpiration;
	}
    
    @JsonProperty("pairingExpiration")
	public void setPairingExpiration(long pairingExpiration) {
		this._pairingExpiration = pairingExpiration;
	}

    @JsonIgnore
	public List<Policy> getPolicies() {
		return _policies;
	}
        
    @JsonProperty("policies")
	public void setPolicies(List<Policy> policies) {
		this._policies = policies;
	}

    @JsonIgnore
    public String getResource() {
		return _resource;
	}
    
    @JsonProperty("resource")
	public void setResource(String resource) {
		this._resource = resource;
	}
	
    @JsonIgnore
	public String getValue() {
		return _value;
	}
    
    @JsonProperty("token")
	public void setValue(String value) {
		this._value = value;
	}

    @JsonIgnore
    public long getDateCreated() {
		return _dateCreated;
	}
    
    @JsonProperty("dateCreated")
	public void setDateCreated(long dateCreated) {
		this._dateCreated = dateCreated;
	}

}
