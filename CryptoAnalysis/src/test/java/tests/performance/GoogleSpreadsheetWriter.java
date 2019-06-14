package tests.performance;

import java.io.IOException;
import java.io.StringReader;
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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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

	private static final String APPLICATION_NAME = "CryptoAnalysis-Performance";
	private static final String SPREADSHEET_ID = "1NrfiAUsPYNXYsE05nSimu7JFAO5LXOpSQXtv_8lA8LM"; 
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);

	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String credentials) throws IOException {
		// Load client secrets.
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(credentials));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	private static boolean addSheet(String projectName, String credentials) throws GoogleJsonResponseException {
		Sheets service;
		boolean sheetAdded = true;
		try {
			service = getService(credentials);
			AddSheetRequest addSheet = new AddSheetRequest();
			addSheet.setProperties(new SheetProperties().setTitle(projectName));
			List<Request> requests = new ArrayList<>(); 
			requests.add(new Request().setAddSheet(addSheet));
			BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
			requestBody.setRequests(requests);
			service.spreadsheets().batchUpdate(SPREADSHEET_ID, requestBody).execute();
		} catch (GoogleJsonResponseException e) {
			if (e.getMessage().contains("Invalid requests[0].addSheet"))
				sheetAdded = false;
			else
				e.printStackTrace();
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			return sheetAdded;
		}
		return sheetAdded;
	}
	
	private static void addHeaders(List<Object> headers, String projectName, String projectUrl, String credentials) throws IOException, GeneralSecurityException {
		Sheets service = getService(credentials);
		ValueRange metricNames = new ValueRange().setValues(Arrays.asList(headers));
		ValueRange projectDetails = new ValueRange().setValues(Arrays.asList(Arrays.asList(new String[] {projectName, projectUrl})));
		service.spreadsheets().values().append(SPREADSHEET_ID, projectName, projectDetails).setValueInputOption("USER_ENTERED")
		.execute();
		service.spreadsheets().values().append(SPREADSHEET_ID, projectName, metricNames).setValueInputOption("USER_ENTERED")
		.execute();
	}

	public static void createSheet(String projectName, String projectUrl, List<Object> headers, String credentials) throws IOException, GeneralSecurityException {
		if (addSheet(projectName, credentials)) {
			addHeaders(headers, projectName, projectUrl, credentials);
		}
	}

	public static void write(List<Object> data, String projectName, String credentials) throws IOException, GeneralSecurityException  {
		Sheets service = getService(credentials);
		ArrayList<List<Object>> rows = Lists.newArrayList();
		rows.add(data);
		ValueRange body = new ValueRange().setValues(rows);
		service.spreadsheets().values().append(SPREADSHEET_ID, projectName, body).setValueInputOption("USER_ENTERED")
		.execute();
	}

	private static Sheets getService(String credentials) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Credential cred = getCredentials(HTTP_TRANSPORT, credentials);
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
				.setApplicationName(APPLICATION_NAME).build();
	}
}
