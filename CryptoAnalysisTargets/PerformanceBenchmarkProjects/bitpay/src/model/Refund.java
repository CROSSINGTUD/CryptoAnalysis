package model;

import java.util.Date;

import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Refund {

	private String _guid = "";
	private String _token = "";
	// _token is used for both the invoice resource token (during the request) and the refund resource token (for the response).

	private Double _amount = 0.0;
	private String _bitcoinAddress = "";
	private String _refundEmail = "";
	private String _currency = "";
	
	private String _id;
	private Date _requestDate;
	private String _status;
	private RefundParams _params = new RefundParams();

	
/*
		{
		  "id":"CWsqDZX3miQH57r1kjXyKw",
		  "requestDate":"2015-10-23T15:54:44.937Z",
		  "status":"pending",
		  "params":{
		 	  "guid":"61254802",
		 	  "token":"AKJQRv4ugw5invg2Ha4D4oJgNQ47qme6Y6YrHekXuQtSzGqXKyZJJN2BbeCGAHKP7V",
		 	  "currency":"USD",
		 	  "amount":1,
		 	  "bitcoinAddress":"381rUw3naC9HujBPMyVfPoVsnVCeTQz1m8",
		 	  "invoiceId":"9Hz86CCoAJWdTHGsB6Bra9"
		 	},
		 	"token":"6a33WsLwD68kGjoGk4NiL2WX8b1ZfGJdB5LBNSJ8z5nJWr6QgtMaGMqJiwUL9B8xzL"
		}		
*/	
	
	
    public Refund() {}

    // API fields
    //
	
    @JsonProperty("guid")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getGuid() {
		return _guid;
	}
	
    @JsonProperty("guid")
	public void setGuid(String guid) {
		this._guid = guid;
	}

    @JsonProperty("token")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public String getToken() {
		return _token;
	}
	
    @JsonProperty("token")
	public void setToken(String token) {
		this._token = token;
	}

    // Request fields
    //

    @JsonProperty("amount")
	public Double getAmount() {
		return _amount;
	}
    
    @JsonProperty("amount")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public void setAmount(Double amount) {
		this._amount = amount;
	}

    @JsonProperty("bitcoinAddress")
	public String getBitcoinAddress() {
		return _bitcoinAddress;
	}
    
    @JsonProperty("bitcoinAddress")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public void setBitcoinAddress(String bitcoinAddress) {
		this._bitcoinAddress = bitcoinAddress;
	}

	@JsonProperty("refundEmail")
	public String getRefundEmail() {
		return _refundEmail;
	}
    
    @JsonProperty("refundEmail")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public void setRefundEmail(String refundEmail) {
		this._refundEmail = refundEmail;
	}


    @JsonProperty("currency")
	public String getCurrency() {
		return _currency;
	}
    
    @JsonProperty("currency")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public void setCurrency(String currency) {
		this._currency = currency;
	}

    // Response fields
    //

    @JsonIgnore
	public String getId() {
		return _id;
	}
	
    @JsonProperty("id")
	public void setId(String id) {
		this._id = id;
	}

    @JsonIgnore
	public Date getRequestDate() {
		return _requestDate;
	}
	
    @JsonProperty("requestDate")
	public void setRequestDate(Date requestDate) {
		this._requestDate = requestDate;
	}

    @JsonIgnore
	public String getStatus() {
		return _status;
	}
	
    @JsonProperty("status")
	public void setStatus(String status) {
		this._status = status;
	}

    @JsonIgnore
  	public RefundParams getParams() {
  		return _params;
  	}
  	
    @JsonProperty("params")
  	public void setPaymentUrls(RefundParams refundParams) {
  		this._params = refundParams;
  	}
    
}
