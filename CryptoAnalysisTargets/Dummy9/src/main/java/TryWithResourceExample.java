import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourceExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	void testARM_Before_Java9() throws IOException{
		BufferedReader reader1 = new BufferedReader(new FileReader("journaldev.txt"));
		
		// Java 9 : Try-With-Resources Improvement
		try (reader1) {
			System.out.println(reader1.readLine());
		}
	}
}
