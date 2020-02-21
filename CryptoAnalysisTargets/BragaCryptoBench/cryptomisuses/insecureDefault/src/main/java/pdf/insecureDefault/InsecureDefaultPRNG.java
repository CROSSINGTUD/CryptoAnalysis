package pdf.insecureDefault;

import java.security.SecureRandom;

public final class InsecureDefaultPRNG {

	public static void main(String[] args) {
		try {
			SecureRandom r1 = new SecureRandom();
			SecureRandom r2 = new SecureRandom();

		} catch (Exception e) {
		}
	}

}
