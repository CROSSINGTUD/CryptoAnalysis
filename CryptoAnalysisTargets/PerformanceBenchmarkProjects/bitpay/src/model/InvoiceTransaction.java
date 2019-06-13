package model;

import java.util.Date;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown=true)
public class InvoiceTransaction {

	private Double _amount;
	private int _confirmations;
	private Date _time;
	private Date _receivedTime;
	private String _txid;
	
    public InvoiceTransaction() {}
    
    @JsonIgnore
	public Double getAmount() {
		return _amount;
	}
    
    @JsonProperty("amount")
	public void setAmount(Double amount) {
		this._amount = amount;
	}	

    @JsonIgnore
	public int getConfirmations() {
		return _confirmations;
	}
    
    @JsonProperty("confirmations")
	public void setConfirmations(int confirmations) {
		this._confirmations = confirmations;
	}

    @JsonIgnore
	public Date getTime() {
		return _time;
	}
    
    @JsonProperty("time")
	public void setTime(Date time) {
		this._time = time;
	}

    @JsonIgnore
	public Date getReceivedTime() {
		return _receivedTime;
	}
    
    @JsonProperty("receivedTime")
	public void setReceivedTime(Date receivedTime) {
		this._receivedTime = receivedTime;
	}

    @JsonIgnore
	public String getTransactionId() {
		return _txid;
	}
    
    @JsonProperty("txid")
	public void setTransactionId(String txid) {
		this._txid = txid;
	}

}
