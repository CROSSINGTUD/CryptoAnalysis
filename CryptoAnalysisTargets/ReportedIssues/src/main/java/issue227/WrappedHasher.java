package issue227;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WrappedHasher extends AbstractWrappedHasher implements Hasher {

    private final MessageDigest md;

    public WrappedHasher(String algorithmName) {
        try {
            md = MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(byte b) {
        md.update(b);
    }

    public void update(byte[] b) {
        md.update(b);
    }

    @Override
    public void putLong(long l) {
        ByteBuffer buffer = ByteBuffer.allocate((int) l);
        buffer.putLong(l);

        byte[] array = buffer.array();
        update(array);
    }

    @Override
    public byte[] hash() {
        return md.digest();
    }
}
