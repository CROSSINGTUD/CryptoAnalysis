import java.net.URI;

import jdk.incubator.http.*;

// HTTP/2 client API of Java 9 which replaces HttpURLConnection API
public class Http2Feature {

	public static void main(String[] args) throws Exception{
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest req =
		   HttpRequest.newBuilder(URI.create("http://www.google.com"))
		              .header("User-Agent", "Java")
		              .GET()
		              .build();


		HttpResponse resp = client.send(req, HttpResponse.BodyHandler.asString());
	}
}
