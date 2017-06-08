package main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Executor {
	public static void main(String... args) {
		String classpath = System.getProperty("java.class.path");
		String javaHome = System.getProperty("java.home");
		File[] listFiles = new File("D:\\CROSSING\\android-apps").listFiles();
		for (File file : listFiles) {
			System.out.println(file);
			if (file.getName().endsWith(".apk")) {
				String[] command = new String[] { javaHome + File.separator + "bin" + File.separator + "java", "-cp",
						classpath, Main.class.getName(), file.getAbsolutePath(), "D:\\android-sdk\\platforms" };
				System.out.println("Running command: " + Arrays.toString(command));
				try {
					ProcessBuilder pb = new ProcessBuilder(command);
					pb.redirectOutput(new File("out_" + new File("analysis").getName() + ".txt"));
					pb.redirectError(new File("err_" + new File("analysis").getName() + ".txt"));
					Process proc = pb.start();
					proc.waitFor();
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
