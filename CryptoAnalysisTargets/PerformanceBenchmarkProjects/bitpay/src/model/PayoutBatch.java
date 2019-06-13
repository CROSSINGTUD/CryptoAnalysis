package model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import controller.BitPayException;
import utils.DateSerializer;
import utils.DateDeserializer;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PayoutBatch {

	public static final String STATUS_NEW = "new";
	public static final String STATUS_FUNDED = "funded";
	public static final String STATUS_PROCESSING = "processing";
	public static final String STATUS_COMPLETE = "complete";
	public static final String STATUS_FAILED = "failed";
	public static final String STATUS_CANCELLED = "cancelled";

	public static final String METHOD_MANUAL2 = "manual_2";
	public static final String METHOD_VWAP24 = "vwap_24hr";

	private String _guid = "";
	private String _token = "";

	private List<PayoutInstruction> _instructions = Collections.<PayoutInstruction>emptyList();
	private Double _amount = 0.0;
	private String _currency = "";
	private String _reference = "";
	private String _bankTransferId = "";
	private String _pricingMethod = METHOD_VWAP24;
	private String _notificationEmail = "";
	private String _notificationURL	 = "";

	private Long _requestDate;
   	private Long _effectiveDate;

    private String _id;
	private String _account;
	private String _status;
	private Double _btc;
	private Double _percentFee;
	private Double _fee;
	private Double _depositTotal;
	private String _supportPhone;
    
    /**
     * Constructor, create an empty PayoutBatch object.
     */
    public PayoutBatch() {}

    /**
     * Constructor, create an instruction-full request PayoutBatch object.
     * @param effectiveDate Date when request is effective. Note that the time of day will automatically be set to 09:00:00.000 UTC time for the given day. Only requests submitted before 09:00:00.000 UTC are guaranteed to be processed on the same day.
     * @param reference Merchant-provided data.
     * @param bankTransferId Merchant-provided data, to help match funding payments to payout batches.
     * @param instructions Payout instructions.
     */
    public PayoutBatch(String currency, long effectiveDate, String bankTransferId, String reference, List<PayoutInstruction> instructions) {    	
    	this._currency = currency;
        this._effectiveDate = effectiveDate;
        this._reference = reference;
        this._bankTransferId = bankTransferId;
        this._instructions = instructions;
        _computeAndSetAmount();
    }

    // Private methods
    //

    private void _computeAndSetAmount() {
    	Double amount = 0.0;
    	for (PayoutInstruction instruction : this._instructions) {
    	    amount += instruction.getAmount();
    	}
    	this._amount = amount;
    }

    // API fields
    //
	
    @JsonProperty("guid")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getGuid() {
		return _guid;
	}
	
    @JsonProperty("guid")
	public void setGuid(String _guid) {
		this._guid = _guid;
	}

    @JsonProperty("token")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getToken() {
		return _token;
	}
	
    @JsonProperty("token")
	public void setToken(String _token) {
		this._token = _token;
	}

    // Required fields
    //

    @JsonProperty("effectiveDate")
    @JsonSerialize(using=DateSerializer.class)
	public long getEffectiveDate() {
		return _effectiveDate;
	}
	
    @JsonProperty("effectiveDate")
    @JsonDeserialize(using=DateDeserializer.class)
	public void setEffectiveDate(long _effectiveDate) {
		this._effectiveDate = _effectiveDate;
	}

    @JsonProperty("reference")
	public String getReference() {
		return _reference;
	}
	
    @JsonProperty("reference")
	public void setReference(String _reference) {
		this._reference = _reference;
	}

    @JsonProperty("bankTransferId")
	public String getBankTransferId() {
		return _bankTransferId;
	}
	
    @JsonProperty("bankTransferId")
	public void setBankTransferId(String _bankTransferId) {
		this._bankTransferId = _bankTransferId;
	}

    // Optional fields
    //

    @JsonProperty("instructions")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public List<PayoutInstruction> getInstructions() {
		return _instructions;
	}
	
    @JsonProperty("instructions")
	public void setInstructions(List<PayoutInstruction> _instructions) {
		this._instructions = _instructions;
		_computeAndSetAmount();
	}

    @JsonProperty("amount")
	public Double getAmount() {
		return _amount;
	}
	
    @JsonProperty("amount")
	public void setAmount(Double _amount) {
		this._amount = _amount;
	}

    @JsonProperty("currency")
	public String getCurrency() {
		return _currency;
	}
	
    @JsonProperty("currency")
	public void setCurrency(String _currency) throws BitPayException {
        if (_currency.length() != 3)
        {
            throw new BitPayException("Error: currency code must be exactly three characters");
        }
		this._currency = _currency;
	}

    @JsonProperty("pricingMethod")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getPricingMethod() {
		return _pricingMethod;
	}
	
    @JsonProperty("pricingMethod")
	public void setPricingMethod(String _pricingMethod) {
		this._pricingMethod = _pricingMethod;
	}

    @JsonProperty("notificationEmail")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getNotificationEmail() {
		return _notificationEmail;
	}
	
    @JsonProperty("notificationEmail")
	public void setNotificationEmail(String _notificationEmail) {
		this._notificationEmail = _notificationEmail;
	}

    @JsonProperty("notificationURL")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getNotificationURL() {
		return _notificationURL;
	}
	
    @JsonProperty("notificationURL")
	public void setRedirectURL(String _notificationURL) {
		this._notificationURL = _notificationURL;
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
	public String getAccount() {
		return _account;
	}
	
    @JsonProperty("Account")
	public void setAccount(String _account) {
		this._account = _account;
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
	public Double getBtc() {
		return _btc;
	}
	
    @JsonProperty("btc")
	public void setBtc(Double _btc) {
		this._btc = _btc;
	}

    @JsonIgnore
    @JsonSerialize(using=DateSerializer.class)
	public long getRequestDate() {
		return _requestDate;
	}
	
    @JsonProperty("requestDate")
    @JsonDeserialize(using=DateDeserializer.class)
	public void setRequestDate(long _requestDate) {
		this._requestDate = _requestDate;
	}

    @JsonIgnore
	public Double getPercentFee() {
		return _percentFee;
	}
	
    @JsonProperty("percentFee")
	public void setPercentFee(Double _percentFee) {
		this._percentFee = _percentFee;
	}    
    
    @JsonIgnore
	public Double getFee() {
		return _fee;
	}
	
    @JsonProperty("fee")
	public void setFee(Double _fee) {
		this._fee = _fee;
	}    

    @JsonIgnore
	public Double getDepositTotal() {
		return _depositTotal;
	}
	
    @JsonProperty("depositTotal")
	public void setDepositTotal(Double _depositTotal) {
		this._depositTotal = _depositTotal;
    }

    @JsonIgnore
	public String getSupportPhone() {
		return _supportPhone;
	}
	
    @JsonProperty("supportPhone")
	public void setSupportPhone(String _supportPhone) {
		this._supportPhone = _supportPhone;
    }

}
