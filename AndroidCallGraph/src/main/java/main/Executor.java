package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;

import android.zoo.Downloader;

public class Executor {
	public static void main(String... args) {
		String javaHome = System.getProperty("java.home");
		File[] listFiles = Downloader.DOWNLOAD_DIRECTORY.listFiles();
		for (File file : listFiles) {
			System.out.println(file);
			if (file.getName().endsWith(".apk") || file.getName().endsWith(".APK")) {
				String[] command = new String[] { javaHome + File.separator + "bin" + File.separator + "java","-Xmx8g","-Xss16m", "-cp",
					args[0], PerAPKAnalyzer.class.getName(), file.getAbsolutePath(), args[1]};
				System.out.println("Running command: " + Arrays.toString(command));
				try {
					ProcessBuilder pb = new ProcessBuilder(command);
					File reportsDir = new File("target/reports/");
					if(!reportsDir.exists())
						reportsDir.mkdirs();
					pb.redirectOutput(new File("target/reports/"+file.getName() + "-out.txt"));
					pb.redirectError(new File("target/reports/" +  file.getName() + "-err.txt"));
					Process proc = pb.start();

					boolean finished = proc.waitFor(Integer.parseInt(args[2]), TimeUnit.MINUTES);
					if (!finished) {
						proc.destroy();
						proc.waitFor(); // wait for the process to terminate
					
						String folder = file.getParent();
						String analyzedFolder = folder+ File.separator +  "timedout";
						File dir = new File(analyzedFolder);
						if(!dir.exists()){
							dir.mkdir();
						}
						File moveTo = new File(dir.getAbsolutePath()+File.separator+file.getName());
						Files.move(file, moveTo);
					}
				} catch (IOException ex) {
					System.err.println("Could not execute timeout command: " + ex.getMessage());
					ex.printStackTrace();
				} catch (InterruptedException ex) {
					System.err.println("Process was interrupted: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

}
