package android.zoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloaderFromFile {
	public static final File DOWNLOAD_DIRECTORY = new File("AndroidZooApps");
	private static final String ANDROID_ZOO_CSV = "Restore.csv";
	private static final String ANDROID_ZOO_API_KEY = "AndroidZooAPIKey.txt";
	private static final String ANDROID_ZOO_URL = "https://androzoo.uni.lu/api/download?apikey=${APIKEY}&sha256=${SHA256}";
	private static int downloadedApps = 0;

	public static void main(String... args) {
		try (BufferedReader br = new BufferedReader(new FileReader(ANDROID_ZOO_CSV))) {
			String ln;
			boolean firstLine = true;
			while ((ln = br.readLine()) != null) {					
				if (firstLine) {
					System.out.println(ln + " \n ====================================================================");
					firstLine = false;
					continue;
				}
				handleLine(ln);
//				System.out.println(downloadedApps);
			}
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}

	private static void handleLine(String ln) {
//		System.out.println(ln.replace(",", "\t"));
		AndroidZooEntry entry = new AndroidZooEntry(ln);
			createDownloadDir();
			download(entry);
			downloadedApps++;
	}

	private static void createDownloadDir() {
		if(!DOWNLOAD_DIRECTORY.exists())
			DOWNLOAD_DIRECTORY.mkdir();
	}

	private static void download(AndroidZooEntry entry) {
		try {
			String concreteUrl = ANDROID_ZOO_URL.replace("${APIKEY}", getAndroidZooAPIKey()).replace("${SHA256}", entry.sha256);
			URL website = new URL(concreteUrl);
			if(useProxy())
				website.openConnection(getProxy());
			System.out.println(concreteUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			File targetFile = new File(DOWNLOAD_DIRECTORY +File.separator + entry.name);
			if(targetFile.exists())
				return;
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean useProxy() {
		String proxyURL = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
//		return false;
		return proxyURL != null || proxyPort != null;
	}

	private static Proxy getProxy() {	
		String proxyURL = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if(proxyURL != null || proxyPort != null){
			SocketAddress addr = new InetSocketAddress(proxyURL, Integer.parseInt(proxyPort));
			System.out.println("Using Proxy: " + proxyURL +":" + proxyPort);
			return new Proxy(Proxy.Type.HTTP,addr);
		}
		return null;
	}


	public static String getAndroidZooAPIKey() {
		try (BufferedReader br = new BufferedReader(new FileReader(ANDROID_ZOO_API_KEY))) {
			String ln;
			while ((ln = br.readLine()) != null) {
				return ln;
			}
		} catch (IOException e) {
		}
		throw new RuntimeException("Could not load Android Zoo API Key from file " + ANDROID_ZOO_API_KEY);
	}

	private static class AndroidZooEntry {

		private final String name;
		private final String sha256;

		public AndroidZooEntry(String ln) {
			String[] split = ln.split(";");
			name = split[0];
			int lastIndexOf = name.lastIndexOf("-");
			sha256 = name.substring(lastIndexOf+1, name.length()-4);
		}

	}
}
