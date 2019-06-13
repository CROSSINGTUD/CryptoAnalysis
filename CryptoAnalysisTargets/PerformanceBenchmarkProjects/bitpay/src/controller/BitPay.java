package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.core.ECKey;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Antonio Buedo
 * @date 4.26.2019
 * @version 2.1.1904
 *
 * See bitpay.com/api for more information.
 */

public class BitPay {

	private static final BitPayLogger _log = new BitPayLogger(BitPayLogger.DEBUG);

	private static final String BITPAY_API_VERSION = "2.0.0";
    private static final String BITPAY_PLUGIN_INFO = "BitPay_Java_Client_v2.1.1904";
    public static final String BITPAY_URL = "https://bitpay.com/";
    public static final String BITPAY_TEST_URL = "https://test.bitpay.com/";

    public static final String FACADE_MERCHANT = "merchant";
    public static final String FACADE_PAYROLL = "payroll";
    public static final String FACADE_POS = "pos";
    public static final String FACADE_USER = "user";

    public static final String PUBLIC_NO_TOKEN = "";

    private HttpClient _httpClient = null;
    private String _baseUrl = BITPAY_URL;
    private ECKey _ecKey = null;
    private String _identity = "";
    private String _clientName = "";
    private Hashtable<String, String> _tokenCache; // {facade, token}

    /**
     * Constructor for use if the keys and SIN are managed by this library.
     *
     * @param clientName The label for this client.
     * @param envUrl     The target server URL.
     * @throws BitPayException
     */
    public BitPay(String clientName, String envUrl) throws BitPayException {
        if (clientName.equals(BITPAY_PLUGIN_INFO)) {
            try {
                clientName += " on " + java.net.InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                clientName += " on unknown host";
            }
        }

        // Eliminate special characters from the client
        // name (used as a token label).  Trim to 60 chars.
        _clientName = clientName.replaceAll("[^a-zA-Z0-9_ ]", "_");

        if (_clientName.length() > 60) {
            _clientName = _clientName.substring(0, 60);
        }

        _baseUrl = envUrl;
        _httpClient = HttpClientBuilder.create().build();

        try {
            this.initKeys();
        } catch (IOException e) {
            throw new BitPayException("Error: failed to intialize public/private key pair\n" + e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new BitPayException("Error: " + e.getMessage());
        }

        this.deriveIdentity();
        this.tryGetAccessTokens();
    }

    /**
     * Constructor for use if the keys and SIN are managed by this library.  Use BitPay production server.
     *
     * @param clientName The label for this client.
     * @throws BitPayException
     */
    public BitPay(String clientName) throws BitPayException {
        this(clientName, BITPAY_URL);
    }

    /**
     * Constructor for use if the keys and SIN are managed by this library.  Use BitPay production server.  Default client name.
     *
     * @throws BitPayException
     */
    public BitPay() throws BitPayException {
        this(BITPAY_PLUGIN_INFO, BITPAY_URL);
    }

    /**
     * Constructor for use if the keys derived external to this library.  Use BitPay production server.  Default client name.
     *
     * @param privateKey A URI object representing the compressed, DER-encoded ASN.1 private key
     * @throws BitPayException, IOException
     */
    public BitPay(URI privateKey) throws BitPayException, URISyntaxException, IOException {
        this(KeyUtils.loadEcKey(privateKey), BITPAY_PLUGIN_INFO, BITPAY_URL);
    }

    /**
     * Constructor for use if the keys derived external to this library.  Use BitPay production server.  Default client name.
     *
     * @param privateKey A URI object representing the compressed, DER-encoded ASN.1 private key
     * @param clientName The label for this client.
     * @throws BitPayException, IOException
     */
    public BitPay(URI privateKey, String clientName) throws BitPayException, IOException, URISyntaxException {
        this(KeyUtils.loadEcKey(privateKey), clientName, BITPAY_URL);
    }

    /**
     * Constructor for use if the keys derived external to this library.  Use BitPay production server.  Default client name.
     *
     * @param privateKey A URI object representing the compressed, DER-encoded ASN.1 private key
     * @param clientName The label for this client.
     * @param envUrl     The target server URL.
     * @throws BitPayException, IOException
     */
    public BitPay(URI privateKey, String clientName, String envUrl) throws BitPayException, IOException, URISyntaxException {
        this(KeyUtils.loadEcKey(privateKey), clientName, envUrl);
    }

    /**
     * Constructor for use if the keys and SIN were derived external to this library.
     *
     * @param ecKey      An elliptical curve key.
     * @param clientName The label for this client.
     * @param envUrl     The target server URL.
     * @throws BitPayException
     */
    public BitPay(ECKey ecKey, String clientName, String envUrl) throws BitPayException {
    	_ecKey = ecKey;

        this.deriveIdentity();

        if (clientName.equals(BITPAY_PLUGIN_INFO)) {
            try {
                clientName += " on " + java.net.InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                clientName += " on unknown host";
            }
        }

        // Eliminate special characters from the client
        // name (used as a token label).  Trim to 60 chars.
        _clientName = clientName.replaceAll("[^a-zA-Z0-9_ ]", "_");

        if (_clientName.length() > 60) {
            _clientName = _clientName.substring(0, 60);
        }

        _baseUrl = envUrl;
        _httpClient = HttpClientBuilder.create().build();

        this.tryGetAccessTokens();
    }

    /**
     * Constructor for use if the keys and SIN were derived external to this library.  Use BitPay production server.
     *
     * @param ecKey      An elliptical curve key.
     * @param clientName The label for this client.
     * @throws BitPayException
     */
    public BitPay(ECKey ecKey, String clientName) throws BitPayException {
        this(ecKey, clientName, BITPAY_URL);
    }

    /**
     * Constructor for use if the keys and SIN were derived external to this library.  Use BitPay production server.  Default client name.
     *
     * @param ecKey An elliptical curve key.
     * @throws BitPayException
     */
    public BitPay(ECKey ecKey) throws BitPayException {
        this(ecKey, BITPAY_PLUGIN_INFO, BITPAY_URL);
    }

    /**
     * Get the generated client identity.
     *
     * @return Client identify as a string
     */
    public String getIdentity() {
        return _identity;
    }

    /**
     * Authorize this client for use with the BitPay server.
     *
     * @param pairingCode A pairing code generated at https://bitpay.com/dashboard/merchant/api-tokens.
     * @throws BitPayException
     */
    public void authorizeClient(String pairingCode) throws BitPayException {
        Token token = new Token();
        token.setId(_identity);
        token.setGuid(this.getGuid());
        token.setPairingCode(pairingCode);
        token.setLabel(_clientName);

        ObjectMapper mapper = new ObjectMapper();

        String json;

        try {
            json = mapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to serialize Token object : " + e.getMessage());
        }

        HttpResponse response = this.post("tokens", json);

        List<Token> tokens;

        try {
            tokens = Arrays.asList(mapper.readValue(this.responseToJsonString(response), Token[].class));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Tokens) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Tokens) : " + e.getMessage());
        }

        for (Token t : tokens) {
            _tokenCache.put(t.getFacade(), t.getValue());
        }
    }

    /**
     * Request a pairing code from the BitPay server.
     *
     * @param facade Defines the level of API access being requested
     * @return A pairing code for claim at https://bitpay.com/dashboard/merchant/api-tokens.
     * @throws BitPayException
     */
    public String requestClientAuthorization(String facade) throws BitPayException {
        Token token = new Token();
        token.setId(_identity);
        token.setGuid(this.getGuid());
        token.setFacade(facade);
        token.setCount(1);
        token.setLabel(_clientName);

        ObjectMapper mapper = new ObjectMapper();

        String json;

        try {
            json = mapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to serialize Token object : " + e.getMessage());
        }

        HttpResponse response = this.post("tokens", json);

        List<Token> tokens;

        try {
            tokens = Arrays.asList(mapper.readValue(this.responseToJsonString(response), Token[].class));

            // Expecting a single token resource.
            if (tokens.size() != 1) {
                throw new BitPayException("Error - failed to get token resource; expected 1 token, got " + tokens.size());
            }

        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Tokens) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Tokens) : " + e.getMessage());
        }

        _tokenCache.put(tokens.get(0).getFacade(), tokens.get(0).getValue());

        return tokens.get(0).getPairingCode();
    }

    /**
     * Test whether this client is authorized for a specified level of API access.
     *
     * @param facade Defines the level of API access being requested.
     * @return True if this client is authorized, false otherwise.
     */
    public boolean clientIsAuthorized(String facade) {
        return _tokenCache.containsKey(facade);
    }

    /**
     * Retrieve a token associated with a known resource. The token is used to access other related resources.
     *
     * @param id The identifier for the desired resource.
     * @return The token associated with resource.
     */
    public String getAccessToken(String id) {
        return _tokenCache.get(id);
    }

    /**
     * Create a BitPay invoice.
     *
     * @param invoice An Invoice object with request parameters defined.
     * @param token   The resource access token for the request.
     * @return A BitPay generated Invoice object.
     * @throws BitPayException
     */
    public Invoice createInvoice(Invoice invoice, String token) throws BitPayException {
        invoice.setToken(token);
        invoice.setGuid(this.getGuid());

        ObjectMapper mapper = new ObjectMapper();

        String json;

        try {
            json = mapper.writeValueAsString(invoice);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to serialize Invoice object : " + e.getMessage());
        }

        HttpResponse response = this.postWithSignature("invoices", json);

        try {
            invoice = mapper.readerForUpdating(invoice).readValue(this.responseToJsonString(response));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoice) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoice) : " + e.getMessage());
        }

        this.cacheAccessToken(invoice.getId(), invoice.getToken());
        return invoice;
    }

    /**
     * Create a BitPay invoice using the POS facade.
     *
     * @param invoice An Invoice object with request parameters defined.
     * @return A BitPay generated Invoice object.
     * @throws BitPayException
     */
    public Invoice createInvoice(Invoice invoice) throws BitPayException {
        return this.createInvoice(invoice, this.getAccessToken(FACADE_POS));
    }

    /**
     * Retrieve a BitPay invoice by invoice id using the public facade.
     *
     * @param invoiceId The id of the invoice to retrieve.
     * @return A BitPay Invoice object.
     * @throws BitPayException
     */
    public Invoice getInvoice(String invoiceId) throws BitPayException {
        return this.getInvoice(invoiceId, PUBLIC_NO_TOKEN);
    }

    /**
     * Retrieve a BitPay invoice by invoice id using the specified facade.  The client must have been previously authorized for the specified facade (the public facade requires no authorization).
     *
     * @param invoiceId The id of the invoice to retrieve.
     * @param token     The facade/invoice token (e.g., pos/invoice) for the invoice.
     * @return A BitPay Invoice object.
     * @throws BitPayException
     */
    public Invoice getInvoice(String invoiceId, String token) throws BitPayException {
        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", token));

        boolean requireSignature = !PUBLIC_NO_TOKEN.equals(token);
        HttpResponse response = this.get("invoices/" + invoiceId, params, requireSignature);

        Invoice i;
        try {
            i = new ObjectMapper().readValue(this.responseToJsonString(response), Invoice.class);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoice) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoice) : " + e.getMessage());
        }

        return i;
    }

    /**
     * Retrieve a collection of BitPay invoices.
     *
     * @param dateStart The first date for the query filter.
     * @param dateEnd   The last date for the query filter.
     * @return A list of BitPay Invoice objects.
     * @throws BitPayException
     */
    public List<Invoice> getInvoices(String dateStart, String dateEnd) throws BitPayException {

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", this.getAccessToken(FACADE_MERCHANT)));
        params.add(new BasicNameValuePair("dateStart", dateStart));
        params.add(new BasicNameValuePair("dateEnd", dateEnd));

        HttpResponse response = this.get("invoices", params);

        List<Invoice> invoices;

        try {
            invoices = Arrays.asList(new ObjectMapper().readValue(this.responseToJsonString(response), Invoice[].class));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoices) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Invoices) : " + e.getMessage());
        }

        return invoices;
    }
    /**
     * Checks whether a BitPay invoice has been paid in full.
     * Returns true if the amountPaid >= paymentTotals, returns false otherwise
     *
     * @param Invoice       A Bitpay invoice object
     * @return true if the amountPaid >= paymentTotals, returns false otherwise
     */

    public boolean isFullyPaid(Invoice invoice) {
        long amountPaid = invoice.getAmountPaid();
        String transactionCurrency = invoice.getTransactionCurrency();
        // first check if invoice has a transactionCurrency. If not, this means the invoice has not been paid
        if (transactionCurrency == null){
            return false;
        }
        Hashtable <String, Long> paymentTotals = invoice.getPaymentTotals();
        if (amountPaid < paymentTotals.get(transactionCurrency)){
            return false;
        }
        return true;
    }

    /**
     * Request a full refund for a BitPay invoice.  The invoice full price and currency type are used in the request.
     *
     * @param invoiceId     The id of the BitPay invoice for which a refund request should be made.
     * @param refundEmail   The email of the buyer to which the refund email will be sent
     * @return A BitPay RefundRequest object with the new Refund object.
     * @throws BitPayException
     */
    public RefundHelper requestRefund(String invoiceId, String refundEmail) throws BitPayException {
        Invoice invoice = this.getInvoice(invoiceId, this.getAccessToken(FACADE_MERCHANT));
        return this.requestRefund(invoice, refundEmail, invoice.getPrice(), invoice.getCurrency());
    }

    /**
     * Request a refund for a BitPay invoice.
     *
     * @param invoice        A BitPay invoice object for which a refund request should be made.  Must have been obtained using the merchant facade.
     * @param refundEmail   The email of the buyer to which the refund email will be sent
     * @param amount         The amount of money to refund. If zero then a request for 100% of the invoice value is created.
     * @param currency       The three digit currency code specifying the exchange rate to use when calculating the refund bitcoin amount. If this value is "BTC" then no exchange rate calculation is performed.
     * @return A BitPay RefundRequest object with the new Refund object.
     * @throws BitPayException
     */
    public RefundHelper requestRefund(Invoice invoice, String refundEmail, Double amount, String currency) throws BitPayException {
        
        Refund refund = new Refund();
        refund.setToken(invoice.getToken());
        refund.setGuid(this.getGuid());
        refund.setAmount(amount);
        refund.setRefundEmail(refundEmail);
        refund.setCurrency(currency);

        ObjectMapper mapper = new ObjectMapper();

        String json;

        try {
            json = mapper.writeValueAsString(refund);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to serialize Refund object : " + e.getMessage());
        }

        HttpResponse response = this.postWithSignature("invoices/" + invoice.getId() + "/refunds", json);

        try {
            refund = mapper.readerForUpdating(refund).readValue(this.responseToJsonString(response));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        }

        try {
            this.cacheAccessToken(refund.getId(), refund.getToken());
        } catch (java.lang.NullPointerException e) {
            // When using the email address to refund, BitPay does not instantly return a request id
            // Instead BitPay returns 'success':'true'.
            // BitPay only returns a supportRequestId when the customer has entered his BTC/BCH refund address
        }
        return new RefundHelper(refund, invoice);
    }

    /**
     * Cancel a previously submitted refund request on a BitPay invoice.
     *
     * @param invoiceId The BitPay invoiceId having the associated refund to be canceled.
     * @param refundId  The refund id for the refund to be canceled.
     * @return True if the refund was successfully canceled, false otherwise.
     * @throws BitPayException
     */
    public boolean cancelRefundRequest(String invoiceId, String refundId) throws BitPayException {
        Invoice invoice = this.getInvoice(invoiceId, this.getAccessToken(FACADE_MERCHANT));
        return this.cancelRefundRequest(invoice, refundId);
    }

    /**
     * Cancel a previously submitted refund request on a BitPay invoice.
     *
     * @param invoice  The BitPay invoice having the associated refund to be canceled. Must have been obtained using the merchant facade.
     * @param refundId The refund id for the refund to be canceled.
     * @return True if the refund was successfully canceled, false otherwise.
     * @throws BitPayException
     */
    public boolean cancelRefundRequest(Invoice invoice, String refundId) throws BitPayException {
        Refund refund = this.getRefund(invoice, refundId);
        if (refund == null) {
            throw new BitPayException("Error - refundId is not associated with specified invoice");
        }

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", refund.getToken()));

        HttpResponse response = this.delete("invoices/" + invoice.getId() + "/refunds/" + refundId, params);
        String result = this.responseToJsonString(response);

        return (result.equals("\"Success\""));
    }

    /**
     * Retrieve a previously made refund request on a BitPay invoice.
     *
     * @param invoice  The BitPay invoice having the associated refund.
     * @param refundId The refund id for the refund to be updated with new status.
     * @return A BitPay invoice object with the associated Refund object updated.
     * @throws BitPayException
     */
    public Refund getRefund(Invoice invoice, String refundId) throws BitPayException {
        Refund refund = new Refund();

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", invoice.getToken()));
        HttpResponse response = this.get("invoices/" + invoice.getId() + "/refunds/" + refundId, params);

        ObjectMapper mapper = new ObjectMapper();

        try {
            refund = mapper.readerForUpdating(refund).readValue(this.responseToJsonString(response));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        }

        return refund;
    }

    /**
     * Retrieve all refund requests on a BitPay invoice.
     *
     * @param invoice The BitPay invoice object having the associated refunds.
     * @return A BitPay invoice object with the associated Refund objects updated.
     * @throws BitPayException
     */
    public List<Refund> getAllRefunds(Invoice invoice) throws BitPayException {
        List<Refund> refunds;
        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", invoice.getToken()));

        HttpResponse response = this.get("invoices/" + invoice.getId() + "/refunds", params);

        try {
            refunds = Arrays.asList(new ObjectMapper().readValue(this.responseToJsonString(response), Refund[].class));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Refund) : " + e.getMessage());
        }

        return refunds;
    }

    /**
     * Retrieve the exchange rate table maintained by BitPay.  See https://bitpay.com/bitcoin-exchange-rates.
     *
     * @return A Rates object populated with the BitPay exchange rate table.
     * @throws BitPayException
     */
    public Rates getRates() throws BitPayException {
        HttpResponse response = this.get("rates");

        List<Rate> rates;

        try {
            rates = Arrays.asList(new ObjectMapper().readValue(this.responseToJsonString(response), Rate[].class));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Rates) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Rates) : " + e.getMessage());
        }

        return new Rates(rates, this);
    }

    /**
     * Submit a BitPay Payout batch.
     *
     * @param batch A PayoutBatch object with request parameters defined.
     * @return A BitPay generated PayoutBatch object.
     * @throws BitPayException
     */
    public PayoutBatch submitPayoutBatch(PayoutBatch batch) throws BitPayException {
        String token = this.getAccessToken(FACADE_PAYROLL);
        batch.setToken(token);
        batch.setGuid(this.getGuid());
        
        ObjectMapper mapper = new ObjectMapper();

        String json;

        try {
            json = mapper.writeValueAsString(batch);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to serialize PayoutBatch object : " + e.getMessage());
        }

        HttpResponse response = this.postWithSignature("payouts", json);

        try {
            batch = mapper.readerForUpdating(batch).readValue(this.responseToJsonString(response));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        }

        this.cacheAccessToken(batch.getId(), batch.getToken());
        return batch;
    }

    /**
     * Retrieve a collection of BitPay payout batches.
     *
     * @return A list of BitPay PayoutBatch objects.
     * @throws BitPayException
     */
    public List<PayoutBatch> getPayoutBatches() throws BitPayException {

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", this.getAccessToken(FACADE_PAYROLL)));

        HttpResponse response = this.get("payouts", params);

        List<PayoutBatch> batches;

        try {
        	batches = Arrays.asList(new ObjectMapper().readValue(this.responseToJsonString(response), PayoutBatch[].class));
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        }

        return batches;
    }
    
    /**
     * Retrieve a BitPay payout batch by batch id using.  The client must have been previously authorized for the payroll facade.
     *
     * @param batchId The id of the batch to retrieve.
     * @return A BitPay PayoutBatch object.
     * @throws BitPayException
     */
    public PayoutBatch getPayoutBatch(String batchId) throws BitPayException {
        String token = this.getAccessToken(FACADE_PAYROLL);
        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", token));

        HttpResponse response = this.get("payouts/" + batchId, params, true);

        PayoutBatch b;
        try {
            b = new ObjectMapper().readValue(this.responseToJsonString(response), PayoutBatch.class);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        }

        return b;
    }

    /**
     * Cancel a BitPay Payout batch.
     *
     * @param batchId The id of the batch to cancel.
     * @return A BitPay generated PayoutBatch object.
     * @throws BitPayException
     */
    public PayoutBatch cancelPayoutBatch(String batchId) throws BitPayException {    	
        PayoutBatch b = getPayoutBatch(batchId);
    	final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("token", b.getToken()));

        HttpResponse response = this.delete("payouts/" + batchId, params);

        try {
            b = new ObjectMapper().readValue(this.responseToJsonString(response), PayoutBatch.class);
        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (PayoutBatch) : " + e.getMessage());
        }

        return b;
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initKeys() throws IOException, URISyntaxException {
        if (KeyUtils.privateKeyExists()) {
            _ecKey = KeyUtils.loadEcKey();

            // Alternatively, load your private key from a location you specify.
            // _ecKey = KeyUtils.createEcKeyFromHexStringFile("C:\\Users\\key.priv");

        } else {
            _ecKey = KeyUtils.createEcKey();
            KeyUtils.saveEcKey(_ecKey);
        }
    }

    private void deriveIdentity() throws IllegalArgumentException {
        // Identity in this implementation is defined to be the SIN.
        _identity = KeyUtils.deriveSIN(_ecKey);
    }

    private Hashtable<String, String> responseToTokenCache(HttpResponse response) throws BitPayException {
        // The response is expected to be an array of key/value pairs (facade name = token).
        String json = this.responseToJsonString(response);

        try {
            // Remove the array context so we can map to a hashtable.
            json = json.replaceAll("\\[", "");
            json = json.replaceAll("\\]", "");
            json = json.replaceAll("\\},\\{", ",");
            if (json.length() > 0) {
                _tokenCache = new ObjectMapper().readValue(json, new TypeReference<Hashtable<String, String>>() {
                });
            }

        } catch (JsonProcessingException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Token array) : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to deserialize BitPay server response (Token array) : " + e.getMessage());
        }

        return _tokenCache;
    }

    private void clearAccessTokenCache() {
        _tokenCache = new Hashtable<String, String>();
    }

    private void cacheAccessToken(String id, String token) {
        _tokenCache.put(id, token);
    }

    private boolean tryGetAccessTokens() throws BitPayException {
        // Attempt to get access tokens for this client identity.

        try {
            // Success if at least one access token was returned.
            return this.getAccessTokens() > 0;

        } catch (BitPayException ex) {

            // If the error states that the identity is
            // invalid then this client has not been
            // registered with the BitPay account.
            if (ex.getMessage().contains("Unauthorized sin")) {
                this.clearAccessTokenCache();
                return false;
            } else {
                // Propagate all other errors.
                throw ex;
            }
        }
    }

    private int getAccessTokens() throws BitPayException {
        this.clearAccessTokenCache();

        final List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("id", this.getIdentity()));

        HttpResponse response = this.get("tokens", params);

        _tokenCache = responseToTokenCache(response);

        return _tokenCache.size();
    }


    public HttpResponse get(String uri, List<BasicNameValuePair> parameters) throws BitPayException {
        return get(uri, parameters, true);
    }

    public HttpResponse get(String uri, List<BasicNameValuePair> parameters, boolean signatureRequired) throws BitPayException {
        try {

            String fullURL = _baseUrl + uri;
            HttpGet get = new HttpGet(fullURL);

            if (parameters != null) {

                fullURL += "?" + URLEncodedUtils.format(parameters, "UTF-8");
                get.setURI(new URI(fullURL));
            }
            if (signatureRequired) {
                get.addHeader("x-signature", KeyUtils.sign(_ecKey, fullURL));
                get.addHeader("x-identity", KeyUtils.bytesToHex(_ecKey.getPubKey()));
            }
            get.addHeader("x-bitpay-plugin-info", BITPAY_PLUGIN_INFO);
            get.addHeader("x-accept-version", BITPAY_API_VERSION);


            _log.info(get.toString());
            return _httpClient.execute(get);

        } catch (URISyntaxException e) {
            throw new BitPayException("Error: GET failed\n" + e.getMessage());
        } catch (ClientProtocolException e) {
            throw new BitPayException("Error: GET failed\n" + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error: GET failed\n" + e.getMessage());
        }
    }

    public HttpResponse get(String uri) throws BitPayException {
        return this.get(uri, null, false);
    }

    public HttpResponse post(String uri, String json, boolean signatureRequired) throws BitPayException {
        try {
            HttpPost post = new HttpPost(_baseUrl + uri);

            post.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));

            if (signatureRequired) {
                post.addHeader("x-signature", KeyUtils.sign(_ecKey, _baseUrl + uri + json));
                post.addHeader("x-identity", KeyUtils.bytesToHex(_ecKey.getPubKey()));
            }

            post.addHeader("x-accept-version", BITPAY_API_VERSION);
            post.addHeader("x-bitpay-plugin-info", BITPAY_PLUGIN_INFO);
            post.addHeader("Content-Type", "application/json");

        	_log.info(post.toString());
            return _httpClient.execute(post);

        } catch (UnsupportedEncodingException e) {
            throw new BitPayException("Error: POST failed\n" + e.getMessage());
        } catch (ClientProtocolException e) {
            throw new BitPayException("Error: POST failed\n" + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error: POST failed\n" + e.getMessage());
        }
    }

    public HttpResponse post(String uri, String json) throws BitPayException {
        return this.post(uri, json, false);
    }

    public HttpResponse postWithSignature(String uri, String json) throws BitPayException {
        return this.post(uri, json, true);
    }

    private HttpResponse delete(String uri, List<BasicNameValuePair> parameters) throws BitPayException {
        try {

            String fullURL = _baseUrl + uri;
            HttpDelete delete = new HttpDelete(fullURL);

            if (parameters != null) {

                fullURL += "?" + URLEncodedUtils.format(parameters, "UTF-8");

                delete.setURI(new URI(fullURL));

                delete.addHeader("x-bitpay-plugin-info", BITPAY_PLUGIN_INFO);
                delete.addHeader("x-accept-version", BITPAY_API_VERSION);
                delete.addHeader("x-signature", KeyUtils.sign(_ecKey, fullURL));
                delete.addHeader("x-identity", KeyUtils.bytesToHex(_ecKey.getPubKey()));
            }

        	_log.info(delete.toString());
            return _httpClient.execute(delete);

        } catch (URISyntaxException e) {
            throw new BitPayException("Error: DELETE failed\n" + e.getMessage());
        } catch (ClientProtocolException e) {
            throw new BitPayException("Error: DELETE failed\n" + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error: DELETE failed\n" + e.getMessage());
        }
    }

    public String responseToJsonString(HttpResponse response) throws BitPayException {
        if (response == null) {
            throw new BitPayException("Error: HTTP response is null");
        }

        try {
            // Get the JSON string from the response.
            HttpEntity entity = response.getEntity();

            String jsonString;

            jsonString = EntityUtils.toString(entity, "UTF-8");
            _log.info("RESPONSE: " + jsonString);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(jsonString);
            JsonNode node = rootNode.get("error");

            if (node != null) {
                throw new BitPayException("Error: " + node.asText());
            }

            node = rootNode.get("errors");

            if (node != null) {
                String message = "Multiple errors:";

                if (node.isArray()) {
                    for (final JsonNode errorNode : node) {
                        message += "\n" + errorNode.asText();
                    }

                    throw new BitPayException(message);
                }
            }

            node = rootNode.get("data");

            if (node != null) {
                jsonString = node.toString();
            }

            return jsonString;

        } catch (ParseException e) {
            throw new BitPayException("Error - failed to retrieve HTTP response body : " + e.getMessage());
        } catch (JsonMappingException e) {
            throw new BitPayException("Error - failed to parse json response to map : " + e.getMessage());
        } catch (IOException e) {
            throw new BitPayException("Error - failed to retrieve HTTP response body : " + e.getMessage());
        }
    }

    private String getGuid() {
        int Min = 0;
        int Max = 99999999;

        return Min + (int) (Math.random() * ((Max - Min) + 1)) + "";
    }

}
