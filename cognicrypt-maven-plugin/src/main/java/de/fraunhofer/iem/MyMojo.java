package de.fraunhofer.iem;

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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;


@Mojo( name = "check" )
public class MyMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession session;
	private Model model;
	private Build build;
	private File targetDir;

    public void execute()
        throws MojoExecutionException
    {

        getLog().info( "Hello, world." + project.getArtifacts().size() );

        getLog().info( "No" );
        Collection<File> jars = new Classpath(
                this.project,
                new File(this.session.getLocalRepository().getBasedir()),
                "compile"
        );
        
        this.model = project.getModel();
		this.build = model.getBuild();
		this.targetDir = new File(build.getDirectory());

        getLog().info( "Do" );
        getLog().info( "BEFORE" +Joiner.on(":").join(jars));
        getLog().info( "BEFORE" +targetDir.getAbsolutePath());
        SourceCryptoScanner.runAnalysisAllReachable(targetDir.getAbsolutePath()+"/classes", Joiner.on(":").join(jars),  "/Users/johannesspath/Arbeit/Fraunhofer/CryptoAnalysis/CryptoAnalysis/src/test/resources");
//        if(analysisListener.hasErrors()){
//            getLog().info( "HASERROR" );
//
//            throw new MojoExecutionException("CogniCrypt found a problem!");
//        }
        getLog().info( "AFTER" );

    }
}
