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
import java.util.ArrayList;
import java.util.List;

public class Downloader {
	public static final File DOWNLOAD_DIRECTORY = new File("AndroidZooApps");
	private static final String ANDROID_ZOO_CSV = "AndroidZooAPKs.csv";
	private static final String ANDROID_ZOO_API_KEY = "AndroidZooAPIKey.txt";
	private static final String ANDROID_ZOO_URL = "https://androzoo.uni.lu/api/download?apikey=${APIKEY}&sha256=${SHA256}";
	private static int downloadedApps = 0;
	private static int redundant = 250;
	private final static List<String> downloadedAppsList = new ArrayList<String>(); 

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
				if (downloadedApps > Integer.parseInt(args[0]))
					break;
//				System.out.println(downloadedApps);
			}
		} catch (IOException e) {
			System.err.println("Error: " + e);
		}
		System.err.println(downloadedApps);
	}

	private static void handleLine(String ln) {
//		System.out.println(ln.replace(",", "\t"));
		AndroidZooEntry entry = new AndroidZooEntry(ln);
		if (isApkOfInterest(entry)) {
			createDownloadDir();
			download(entry);
			downloadedAppsList.add(entry.pkg_name);
			downloadedApps++;
		} else if (redundant > 0 && downloadedAppsList.contains(entry.pkg_name)) {
			redundant--;
			System.out.println("Entry " + entry.pkg_name + " already downloaded.");
		}
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
			File targetFile = new File(DOWNLOAD_DIRECTORY +File.separator + entry.pkg_name+"-"+entry.sha256 +".apk");
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

	private static boolean isApkOfInterest(AndroidZooEntry entry) {
		System.out.println(entry.pkg_name);
		return !downloadedAppsList.contains(entry.pkg_name) && (entry.dex_date.startsWith("2017")) && entry.markets.equals("play.google.com");
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

		private final String sha256;
		private final String sha1;
		private final String markets;
		private final String pkg_name;
		private final String apk_size;
		private final String dex_date;
		private final String md5;

		public AndroidZooEntry(String ln) {
			String[] split = ln.split(",");
			sha256 = split[0];
			sha1 = split[1];
			md5 = split[2];
			dex_date = split[3];
			apk_size = split[4];
			pkg_name = split[5].replace("\"", "");
			markets = split[10];
		}

	}
}
