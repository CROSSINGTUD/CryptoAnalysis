package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvoiceFlags {

	private boolean _refundable = false;
	
    public InvoiceFlags() {}

    @JsonIgnore
	public boolean getRefundable() {
		return _refundable;
	}
        
    @JsonProperty("refundable")
	public void setRefundable(boolean refundable) {
		this._refundable = refundable;
	}

}
