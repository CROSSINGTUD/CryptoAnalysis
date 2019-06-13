package model;

public class RefundHelper {
	
	private Refund _refund;
	private Invoice _invoice;
	
    public RefundHelper(Refund refund, Invoice invoice) 
    {
    	_refund = refund;
    	_invoice = invoice;
    }
    
    /**
     * Retrieve the refund.
     * @return A Refund object.
     */
    public Refund getRefund()
    {
    	return this._refund;
    }

    /**
     * Retrieve the invoice.
     * @return An Invoice object.
     */
    public Invoice getInvoice()
    {
    	return this._invoice;
    }

}
