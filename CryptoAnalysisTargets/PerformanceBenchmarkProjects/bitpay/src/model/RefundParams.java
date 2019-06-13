package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefundParams {

	private String _guid = "";
	private String _token = "";
	private String _currency = "";
	private Double _amount = 0.0;
	private String _bitcoinAddress = "";
	private String _refundEmail = "";
	private String _invoiceId = "";
	
    public RefundParams() {}

    @JsonIgnore
	public String getGuid() {
		return _guid;
	}
        
    @JsonProperty("guid")
	public void setGuid(String guid) {
		this._guid = guid;
	}

    @JsonIgnore
	public String getToken() {
		return _token;
	}
        
    @JsonProperty("token")
	public void setToken(String token) {
		this._token = token;
	}

    @JsonIgnore
	public String getCurrency() {
		return _currency;
	}
        
    @JsonProperty("currency")
	public void setCurrency(String currency) {
		this._currency = currency;
	}

    @JsonIgnore
	public Double getAmount() {
		return _amount;
	}
        
    @JsonProperty("amount")
	public void setAmount(Double amount) {
		this._amount = amount;
	}

    @JsonIgnore
	public String getBitcoinAddress() {
		return _bitcoinAddress;
	}
        
    @JsonProperty("bitcoinAddress")
	public void setBitcoinAddress(String bitcoinAddress) {
		this._bitcoinAddress = bitcoinAddress;
	}

	@JsonIgnore
	public String getRefundEmail() {
		return _refundEmail;
	}
        
    @JsonProperty("refundEmail")
	public void setRefundEmail(String refundEmail) {
		this._refundEmail = refundEmail;
	}

    @JsonIgnore
	public String getInvoiceId() {
		return _invoiceId;
	}
        
    @JsonProperty("invoiceId")
	public void setInvoiceId(String invoiceId) {
		this._invoiceId = invoiceId;
	}

}
