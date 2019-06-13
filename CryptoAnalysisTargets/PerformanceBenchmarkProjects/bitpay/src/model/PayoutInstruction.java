package model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import utils.PayoutInstructionBtcSummaryDeserializer;

public class PayoutInstruction {

	public static final String STATUS_PAID = "paid";
	public static final String STATUS_UNPAID = "unpaid";

	private Double _amount;
	private String _address;
	private String _label = "";

	private String _id;
	private String _status;
	private PayoutInstructionBtcSummary _btc;
	private List <PayoutInstructionTransaction> _transactions;
	
    /**
     * Constructor, create an empty PayoutInstruction object.
     */
    public PayoutInstruction() {}

    /**
     * Constructor, create a PayoutInstruction object.
     * @param amount BTC amount.
     * @param address Bitcoin address.
     * @param label Label.
     */
    public PayoutInstruction(Double amount, String address, String label)
    {
        this._amount = amount;
        this._address = address;
        this._label = label;
    }

    @JsonProperty("amount")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public Double getAmount() {
		return _amount;
	}
	
    @JsonProperty("amount")
	public void setAmount(Double amount) {
		this._amount = amount;
	}

    @JsonProperty("address")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getAddress() {
		return _address;
	}
	
    @JsonProperty("address")
	public void setAddress(String address) {
		this._address = address;
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

    // Response fields
    //

    @JsonIgnore
	public String getId() {
		return _id;
	}
	
    @JsonProperty("id")
	public void setId(String _id) {
		this._id = _id;
	}

    @JsonIgnore
	public String getStatus() {
		return _status;
	}
	
    @JsonProperty("status")
	public void setStatus(String _status) {
		this._status = _status;
	}

    @JsonIgnore
	public PayoutInstructionBtcSummary getBtc() {
		return _btc;
	}
	
    @JsonProperty("btc")
    @JsonDeserialize(using=PayoutInstructionBtcSummaryDeserializer.class)
    public void setBtc(PayoutInstructionBtcSummary _btc) {
		this._btc = _btc;
	}

    @JsonIgnore
	public List <PayoutInstructionTransaction> getTransactions() {
		return _transactions;
	}
	
    @JsonProperty("transactions")
	public void setTransactions(List <PayoutInstructionTransaction> _transactions) {
		this._transactions = _transactions;
	}

}
