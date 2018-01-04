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

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.base.Joiner;
import com.jcabi.aether.Classpath;

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

	@Parameter(property = "check.rulesDirectory")
	private String rulesDirectory;

	@Parameter(property = "check.callGraph", defaultValue = "CHA")
	private String callGraph;

	@Parameter(property = "check.reportsDirectory", defaultValue = "cognicrypt-reports")
	private String reportsFolderParameter;
	private File reportsFolder;

	private Model model;
	private Build build;
	private File targetDir;
	private String classPath;
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
		Classpath jars;
		try {
			jars = new Classpath(this.project, new File(this.session.getLocalRepository().getBasedir()), "compile");
			classPath = Joiner.on(":").join(jars);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!targetDir.exists())
			return;
		if (!new File(targetDir.getAbsolutePath() + File.separator + "classes").exists())
			return;

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
				return targetDir.getAbsolutePath() + "/classes";
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
