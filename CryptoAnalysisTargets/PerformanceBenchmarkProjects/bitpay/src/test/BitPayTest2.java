package test;

import controller.BitPay;
import controller.BitPayException;
import controller.BitPayLogger;
import model.Invoice;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.junit.Assert.assertNotNull;

public class BitPayTest2 {

	private static final BitPayLogger _log = new BitPayLogger(BitPayLogger.DEBUG);

    private BitPay bitpay;
    private static String clientName = "BitPay Java Library Tester2";

    @Before
    public void setUp() throws BitPayException {
        //ensure the second argument (api url) is the same as the one used in setUpOneTime()
        bitpay = new BitPay(clientName, BitPay.BITPAY_TEST_URL);
    }

	@BeforeClass
	public static void setUpOneTime() throws UnknownHostException, BitPayException
	{
		// If this test has never been run before then this test must be run twice in order to pass.
		// The first time this test runs it will create an identity and emit a client pairing code.
		// The pairing code must then be authorized in a BitPay account.  Running the test a second
		// time should result in the authorized client (this test) running to completion.
		clientName += " on " + java.net.InetAddress.getLocalHost();
        BitPay bitpay = new BitPay(clientName, BitPay.BITPAY_TEST_URL); //this tests the old way of creating keys/clients
        
        if (!bitpay.clientIsAuthorized(BitPay.FACADE_POS))
        {
            // Get POS facade authorization code.
            // Obtain a pairingCode from the BitPay server.  The pairingCode must be emitted from
        	// this device and input into and approved by the desired merchant account.  To
        	// generate invoices a POS facade is required.
            String pairingCode = bitpay.requestClientAuthorization(BitPay.FACADE_POS);
            
            // Signal the device operator that this client needs to be paired with a merchant account.
            _log.info("Client is requesting POS facade access. Go to " + BitPay.BITPAY_TEST_URL + " and pair this client with your merchant account using the pairing code: " + pairingCode);
            throw new BitPayException("Error: client is not authorized.");
        }
	}

    @Test
    public void testShouldGetInvoiceId()
    {
        Invoice invoice = new Invoice(1.00, "USD");
        try {
            invoice = bitpay.createInvoice(invoice);
        } catch (BitPayException e) {
            e.printStackTrace();
        }
        assertNotNull(invoice.getId());
    }
}
