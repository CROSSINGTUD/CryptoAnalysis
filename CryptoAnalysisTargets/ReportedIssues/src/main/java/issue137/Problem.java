package issue137;

import javax.crypto.Cipher;

class Program {
	public static void main(String[] args) throws Exception {
		String x = getFoo();
		
		if (x != null) {
			Cipher cipher = Cipher.getInstance(x);
			cipher.toString();
		}
	}

	public static String getFoo() {
		String f = "foo";
		return f.equals("bar") ? "A" :  "B";
	}
}
