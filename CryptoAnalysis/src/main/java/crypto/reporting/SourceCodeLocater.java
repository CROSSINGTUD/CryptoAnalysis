package crypto.reporting;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import soot.SootClass;

public class SourceCodeLocater {
	private File baseDir;
	
	public SourceCodeLocater(File baseDir) {
		this.baseDir = baseDir;
	}
	
	public String getAbsolutePath(SootClass className) {
		String shortName = className.getShortName();
		Collection<File> files = FileUtils.listFiles(
				  baseDir, 
				  new RegexFileFilter(shortName+".java"), 
				  DirectoryFileFilter.DIRECTORY
				);
		for(File file : files) {
			if(file.getAbsolutePath().contains(className.toString().replace(".", File.separator))) {
				return file.getAbsolutePath();
			}
		}
		
		return className +" (No source code found)";
	}
}
