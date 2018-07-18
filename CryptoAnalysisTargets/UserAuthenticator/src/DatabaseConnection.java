
public class DatabaseConnection {
	
	private static String password;
	
	public static void storePassword(String user, String pass) {
		password = pass;
	}
	
	public static String retrievePassword(String user) {
		return password;
	}
}
