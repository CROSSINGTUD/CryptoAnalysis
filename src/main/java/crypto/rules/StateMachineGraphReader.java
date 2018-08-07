package crypto.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class StateMachineGraphReader {
	public static StateMachineGraph readFromFile(File file) {
		StateMachineGraph smg = null;
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			smg = (StateMachineGraph) in.readObject();
			System.err.println(smg);
			in.close();
			fileIn.close();		
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return smg;
	}
	
}
