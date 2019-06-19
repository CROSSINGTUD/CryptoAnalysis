package tests.performance;

import crypto.analysis.CrySLRulesetSelector.Ruleset;

public class BenchmarkProject {
	String name, projectPath, gitUrl, sootClassPath, commitId;
	boolean isMavenProject;
	Ruleset[] ruleSet;

	public String getName() {
		return name;
	}

	public BenchmarkProject(String name, String projectPath, String gitUrl, String commitId, String sootClassPath, boolean isMavenProject,
			Ruleset[] ruleSet) {
		this.name = name;
		this.projectPath = projectPath;
		this.gitUrl = gitUrl;
		this.commitId = commitId;
		this.sootClassPath = sootClassPath;
		this.isMavenProject = isMavenProject;
		this.ruleSet = ruleSet;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	
	public Ruleset[] getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(Ruleset[] ruleSet) {
		this.ruleSet = ruleSet;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getSootClassPath() {
		return sootClassPath;
	}

	public void setSootClassPath(String sootClassPath) {
		this.sootClassPath = sootClassPath;
	}

	public boolean getIsMavenProject() {
		return isMavenProject;
	}

	public void setIsMavenProject(boolean isMavenProject) {
		this.isMavenProject = isMavenProject;
	}
	
}
