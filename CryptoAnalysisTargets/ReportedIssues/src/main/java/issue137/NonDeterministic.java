package issue137;

import javax.crypto.Cipher;

public class NonDeterministic {
	public static void main(String[] args) throws Exception {
		Object x = getFoo();

		if (x != null) {
			Cipher cipher = Cipher.getInstance(x.toString());
		}
	}

	public static Object getFoo() {
		String f = "foo";
		return f.equals("bar") ? System.out : System.in;
	}
}
