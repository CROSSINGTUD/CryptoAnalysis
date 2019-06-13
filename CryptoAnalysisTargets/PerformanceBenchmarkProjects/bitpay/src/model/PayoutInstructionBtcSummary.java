package model;

public class PayoutInstructionBtcSummary {

	private Double _paid;
	private Double _unpaid;
	
    public PayoutInstructionBtcSummary(Double paid, Double unpaid) {
    	this._paid = paid;
    	this._unpaid = unpaid;
    }
    
	public Double getPaid() {
		return _paid;
	}
    
	public Double getUnpaid() {
		return _unpaid;
	}

}
