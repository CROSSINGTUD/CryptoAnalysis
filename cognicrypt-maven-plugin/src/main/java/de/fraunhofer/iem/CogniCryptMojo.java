package de.fraunhofer.iem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import crypto.SourceCryptoScanner;
import crypto.SourceCryptoScanner.CG;

@Mojo(name = "check")
public class CogniCryptMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 */

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;
	@Parameter(defaultValue = "${session}", readonly = true)
	private MavenSession session;

	@Parameter(property = "check.rulesDirectory", required = true)
	private String rulesDirectory;

	@Parameter(property = "check.callGraph", defaultValue = "CHA")
	private String callGraph;

	@Parameter(property = "check.reportsDirectory", defaultValue = "cognicrypt-reports")
	private String reportsFolderParameter;
	private File reportsFolder;

	private Model model;
	private Build build;
	private File targetDir;
	private String classPath ="";
	private String artifactIdentifier;
	private static final boolean REDIRECT_LOG = false;

	public void execute() throws MojoExecutionException {
		this.model = project.getModel();
		this.build = model.getBuild();
		this.targetDir = new File(build.getDirectory());
		this.artifactIdentifier = model.getGroupId() + ":" + model.getArtifactId() + ":" + model.getVersion();

		createReportFolder();
		PrintStream ps_console = null;
		if(REDIRECT_LOG){
			ps_console = System.out;
			redirectOutput();
		}

		if (rulesDirectory == null){
			getLog().error("Set the path to the CrySL rules! Use the option check.rulesDirectory");
			return;
		}
		if (!targetDir.exists()){
			getLog().warn("Expected the directory " +targetDir+ " to exist!");
			return;
		}
		final File classFolder = new File(targetDir.getAbsolutePath() + File.separator + "classes");
		if (!classFolder.exists()){
			getLog().error("Expected a class folder  directory " +classFolder+ " to exist! Run mvn compile first!");
			return;
		}

		final CG callGraphAlogrithm;
		if (callGraph.equalsIgnoreCase("cha")) {
			callGraphAlogrithm = CG.CHA;
		} else if (callGraph.equalsIgnoreCase("spark")) {
			callGraphAlogrithm = CG.SPARK;
		} else if (callGraph.equalsIgnoreCase("spark-library")) {
			callGraphAlogrithm = CG.SPARK_LIBRARY;
		} else if (callGraph.equalsIgnoreCase("library")) {
			callGraphAlogrithm = CG.SPARK_LIBRARY;
		} else {
			callGraphAlogrithm = CG.CHA;
		}
		SourceCryptoScanner sourceCryptoScanner = new SourceCryptoScanner() {

			@Override
			protected String sootClassPath() {
				if (classPath == null) {
					System.out.println("Potentially missing some dependencies");
					return applicationClassPath();
				}
				return classPath;
			}

			@Override
			protected String applicationClassPath() {
				return classFolder.getAbsolutePath();
			}

			@Override
			protected String softwareIdentifier() {
				return artifactIdentifier;
			}

			@Override
			protected CG callGraphAlogrithm() {
				return callGraphAlogrithm;
			}

			@Override
			protected String getRulesDirectory() {
				return rulesDirectory;
			}
		};
		sourceCryptoScanner.exec();
		if (sourceCryptoScanner.hasSeeds()) {
			String outputFile = reportsFolder.getAbsolutePath() + File.separator + artifactIdentifier + ".txt";
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
				writer.write(sourceCryptoScanner.getReporter().toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(ps_console != null)
			System.setOut(ps_console);
	}

	private void redirectOutput() {
		String targetDir = reportsFolder.getAbsolutePath() + File.separator + "log";
		File logDir = new File(targetDir);
		if (!logDir.exists()) {
			logDir.mkdirs();
		}

		File file = new File(logDir.getAbsolutePath() + File.separator + artifactIdentifier + ".txt");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void createReportFolder() {
		reportsFolder = new File(reportsFolderParameter);
		if (!reportsFolder.isAbsolute()) {
			String absolutePath = targetDir.getAbsolutePath();
			String targetDir = absolutePath + File.separator + reportsFolderParameter;
			reportsFolder = new File(targetDir);
		}
		if (!reportsFolder.exists()) {
			reportsFolder.mkdirs();
		}
	}
}
