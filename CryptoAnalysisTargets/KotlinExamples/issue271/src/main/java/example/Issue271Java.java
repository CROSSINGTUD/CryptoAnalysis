package example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Issue271Java {

    public static void main(String [] args) throws NoSuchAlgorithmException {
        testFail("abc123ABC");
        testOk("abc123ABC");
    }

    public static void testFail(String input) throws NoSuchAlgorithmException {
        String someManipulation = input.substring(0, 2);
        MessageDigest.getInstance("SHA-256").digest(someManipulation.getBytes());
    }

    public static void testOk(String input) throws NoSuchAlgorithmException {
        MessageDigest.getInstance("SHA-256").digest(input.getBytes());
    }
}
