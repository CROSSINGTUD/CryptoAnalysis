package tests.performance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSpreadsheetWriter {

	private static final String APPLICATION_NAME = "CryptoAnalysis-PerformanceWriter";
	private static final String SPREADSHEET_ID = "1mSqVxzV5rlaXjhZN9PFXvpVxzr_RkvVpi83sVbuz8Gc";
	private static final String SHEET_ID = "metrics";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static boolean onlyOnce;
    
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSpreadsheetWriter.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    
	public static void createSheet(List<Object> headers) throws IOException, GeneralSecurityException {
		if(onlyOnce)
			return;
		onlyOnce = true;	
		Sheets service = getService();
		List<Request> requests = new ArrayList<>(); 
		AddSheetRequest addSheet = new AddSheetRequest();
		addSheet.setProperties(new SheetProperties().setTitle(SHEET_ID));
		requests.add(new Request().setAddSheet(addSheet));
		BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
		requestBody.setRequests(requests);
		service.spreadsheets().batchUpdate(SPREADSHEET_ID, requestBody).execute();
		
		ArrayList<List<Object>> rows = Lists.newArrayList();
		rows.add(headers);
		ValueRange body = new ValueRange().setValues(Arrays.asList(headers));
		service.spreadsheets().values().append(SPREADSHEET_ID, SHEET_ID, body).setValueInputOption("USER_ENTERED")
				.execute();
	}
	
	public static void write(List<Object> data) throws IOException, GeneralSecurityException  {
		Sheets service = getService();
		ArrayList<List<Object>> rows = Lists.newArrayList();
		rows.add(data);
		ValueRange body = new ValueRange().setValues(rows);
		service.spreadsheets().values().append(SPREADSHEET_ID, SHEET_ID, body).setValueInputOption("USER_ENTERED")
				.execute();
	}
	
	private static Sheets getService() throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY,  getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
	}
}
