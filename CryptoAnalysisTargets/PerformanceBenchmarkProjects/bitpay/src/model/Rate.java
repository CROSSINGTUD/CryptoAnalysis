package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rate {
	
	private String _name;
	private String _code;
	private Double _value;
	
    public Rate() {}
    
    @JsonIgnore
	public String getName() {
		return _name;
	}
    
    @JsonProperty("name")
	public void setName(String name) {
		this._name = name;
	}

    @JsonIgnore
	public String getCode() {
		return _code;
	}
    
    @JsonProperty("code")
	public void setCode(String code) {
		this._code = code;
	}

    @JsonIgnore
	public Double getValue() {
		return _value;
	}
    
    @JsonProperty("rate")
	public void setValue(Double value) {
		this._value = value;
	}

}
