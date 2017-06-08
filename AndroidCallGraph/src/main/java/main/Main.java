package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import soot.G;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.TestApps.Test;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

public class Main {
	private static List<String> relevantCalls = Lists.newLinkedList();
	private static FileWriter fout;


	private static void readInRelevantCalls() throws FileNotFoundException, IOException {
		String line;
		try (
		    InputStream fis = new FileInputStream("RelevantCalls.txt");
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		) {
		    while ((line = br.readLine()) != null) {
		    	relevantCalls.add(line);
		    }
		}
	}

	
	
	
	public static void main(String...args)  throws  InterruptedException, IOException{
		readInRelevantCalls();
		File file = new File(args[0]);
		Test.main(new String[]{ args[0],args[1],"--notaintanalysis"});
		ReachableMethods reachableMethods = Scene.v().getReachableMethods();
		QueueReader<MethodOrMethodContext> listener = reachableMethods.listener();
		fout = new FileWriter("Report.txt", true);

		try{
			log(0, "Analyzing " + file.getName());
			Set<SootMethod> visited = Sets.newHashSet();
			while(listener.hasNext()){
				MethodOrMethodContext next = listener.next();
				analyzeMethod(next.method(), "Application",file);
				visited.add(next.method());
			}
			log(1,"Call graph reachable methods: " + visited.size());
			for(SootClass c : Scene.v().getClasses()){
				for(SootMethod m : c.getMethods()){
					if(visited.add(m))
						analyzeMethod(m, "Library",file);
				}
			}
			log(1,"APK file reachable methods: " + visited.size());
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			fout.close();
		}
	}
	
	private static void log(int i, String string) throws IOException {
		String s = "";
		for(int j = 0; j < i; j++){
			s += "\t";
		}
		System.out.println(s + string);
		fout.write(s + string +" \n");
	}

	private static void analyzeMethod(SootMethod method, String moveTo, File file) throws IOException {
		if(!method.hasActiveBody())
			return;
		for(Unit u : method.getActiveBody().getUnits()){
			if(u instanceof InvokeStmt){
				for(String relevantCall : relevantCalls)
					if(u.toString().contains(relevantCall)){
						log(2, moveTo+ "\t Class: " + method.getDeclaringClass() + "  " + method.getDeclaringClass().isApplicationClass()+ "\t Method: " + method.getName() + "\t Unit " + u);
						File parentFile = file.getParentFile();
						File dir = new File(parentFile.getAbsolutePath() + File.separator +moveTo);
						if(!dir.exists()){
							System.out.println("Created dir " + dir.getAbsolutePath());
							dir.mkdir();
						}
						File copyToFile = new File(dir.getAbsolutePath() + File.separator +file.getName());
						Files.copy(file, copyToFile);
					}
			}
		}
	}
}
