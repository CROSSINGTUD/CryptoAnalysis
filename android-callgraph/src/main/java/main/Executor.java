package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

import android.zoo.Downloader;

public class Executor {

	private static int processors = Runtime.getRuntime().availableProcessors();
	private static String platformsDir;
	private static int timeoutTime;
	private static LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();;
	private static Set<File> started = Sets.newHashSet();
	private static ThreadPoolExecutor executor;
	private static String visualization;
	

	public static void main(String... args) throws InterruptedException {
		platformsDir = args[0];
		timeoutTime = Integer.parseInt(args[1]);
		visualization = args[2];
		executor = new ThreadPoolExecutor(processors-1, processors, timeoutTime, TimeUnit.MINUTES,workQueue);
		startProcesses();
		executor.awaitTermination(30, TimeUnit.DAYS);
	}

	private static void startProcesses() {
		File[] listFiles = Downloader.DOWNLOAD_DIRECTORY.listFiles();
		for (final File file : listFiles) {
			if(!started.add(file))
				continue;
			if (file.getName().endsWith(".apk") || file.getName().endsWith(".APK")) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						startProcess(file);
					}
				});
			}
		}
	}

	private static void startProcess(File file) {
		String classpath = System.getProperty("java.class.path");
		String javaHome = System.getProperty("java.home");
		String[] command = new String[] { javaHome + File.separator + "bin" + File.separator + "java", "-Xmx8g", "-Xms1g",
				"-Xss64m", "-cp", classpath, PerAPKAnalyzer.class.getName(), file.getAbsolutePath(), platformsDir, visualization };
		System.out.println("Running command: " + Arrays.toString(command));
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			File reportsDir = new File("target/reports/");
			if (!reportsDir.exists())
				reportsDir.mkdirs();
			pb.redirectOutput(new File("target/reports/" + file.getName() + "-out.txt"));
			pb.redirectError(new File("target/reports/" + file.getName() + "-err.txt"));
			Process proc = pb.start();
			boolean finished = proc.waitFor(timeoutTime, TimeUnit.MINUTES);
			if (!finished) {
				proc.destroy();
				proc.waitFor(); // wait for the process to terminate

				String folder = file.getParent();
				String analyzedFolder = folder + File.separator + "timedout";
				File dir = new File(analyzedFolder);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File moveTo = new File(dir.getAbsolutePath() + File.separator + file.getName());
				Files.move(file, moveTo);
			}
			startProcesses();
		} catch (IOException ex) {
			System.err.println("Could not execute timeout command: " + ex.getMessage());
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			System.err.println("Process was interrupted: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

}
