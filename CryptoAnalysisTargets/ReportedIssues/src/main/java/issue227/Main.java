package issue227;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        String algorithmName = "SHA-256";
        WrappedHasher wrappedHasher = new WrappedHasher(algorithmName);

        long l = 123L;
        wrappedHasher.putLong(l);

        byte[] hash = wrappedHasher.hash();
        System.out.println("Got " + Arrays.toString(hash));
    }
}
