package de.fraunhofer.iem.android;

import crypto.exceptions.CryptoAnalysisParserException;
import crypto.reporting.Reporter;
import picocli.CommandLine;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(mixinStandardHelpOptions = true)
public class AndroidSettings implements Callable<Integer> {

	@CommandLine.Option(
			names = {"--apkFile"},
			description = {"The absolute path to the .apk file"},
			required = true)
	private String apkFile = null;

	@CommandLine.Option(
			names = {"--platformDirectory"},
			description = "The absolute path to the android SDK platforms",
			required = true)
	private String platformDirectory = null;

	@CommandLine.Option(
			names = {"--rulesDir"},
			description = {"The path to ruleset directory. Can be a simple directory or a ZIP archive"},
			required = true)
	private String rulesetDirectory = null;

	@CommandLine.Option(
			names = {"--reportPath"},
			description = "Path to a directory where the reports are stored")
	private String reportPath = null;

	@CommandLine.Option(
			names = {"--reportFormat"},
			split = ",",
			description = "The format of the report. Possible values are CMD, TXT, SARIF, CSV and CSV_SUMMARY (default: CMD)."
					+ " Multiple formats should be split with a comma (e.g. CMD,TXT,CSV)")
	private String[] reportFormat = null;

	private Collection<Reporter.ReportFormat> reportFormats;

	public AndroidSettings() {
		reportFormats = new HashSet<>(List.of(Reporter.ReportFormat.CMD));
	}

	public void parseSettingsFromCLI(String[] settings) throws CryptoAnalysisParserException {
		CommandLine parser = new CommandLine(this);
		parser.setOptionsCaseInsensitive(true);
		int exitCode = parser.execute(settings);

		if (reportFormat != null) {
			parseReportFormatValues(reportFormat);
		}

		if (exitCode != CommandLine.ExitCode.OK) {
			throw new CryptoAnalysisParserException("Error while parsing the CLI arguments");
		}
	}

	private void parseReportFormatValues(String[] settings) throws CryptoAnalysisParserException {
		reportFormats.clear();

		for (String format : settings) {
			String reportFormatValue = format.toLowerCase();

			switch (reportFormatValue) {
				case "cmd":
					reportFormats.add(Reporter.ReportFormat.CMD);
					break;
				case "txt":
					reportFormats.add(Reporter.ReportFormat.TXT);
					break;
				case "sarif":
					reportFormats.add(Reporter.ReportFormat.SARIF);
					break;
				case "csv":
					reportFormats.add(Reporter.ReportFormat.CSV);
					break;
				case "csv_summary":
					reportFormats.add(Reporter.ReportFormat.CSV_SUMMARY);
					break;
				case "github_annotation":
					reportFormats.add(Reporter.ReportFormat.GITHUB_ANNOTATION);
					break;
				default:
					throw new CryptoAnalysisParserException("Incorrect value " + reportFormatValue + " for --reportFormat option. "
							+ "Available options are: CMD, TXT, SARIF, CSV and CSV_SUMMARY.\n");
			}
		}
	}

	public String getApkFile() {
		return apkFile;
	}

	public void setApkFile(String apkFile) {
		this.apkFile = apkFile;
	}

	public String getPlatformDirectory() {
		return platformDirectory;
	}

	public void setPlatformDirectory(String platformDirectory) {
		this.platformDirectory = platformDirectory;
	}

	public String getRulesetDirectory() {
		return rulesetDirectory;
	}

	public void setRulesetDirectory(String rulesetDirectory) {
		this.rulesetDirectory = rulesetDirectory;
	}

	public Collection<Reporter.ReportFormat> getReportFormats() {
		return reportFormats;
	}

	public void setReportFormats(Collection<Reporter.ReportFormat> reportFormats) {
		this.reportFormats = new HashSet<>(reportFormats);
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	@Override
	public Integer call() throws Exception {
		return 0;
	}
}
