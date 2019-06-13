package controller;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.core.Sha256Hash;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;

public class KeyUtils {

    final private static char[] hexArray = "0123456789abcdef".toCharArray();
    final private static String PRIV_KEY_FILENAME = "bitpay_private.key";
    private static URI privateKey;

    public KeyUtils() {
    }

    public static boolean privateKeyExists() {
        return new File(PRIV_KEY_FILENAME).exists();
    }

    public static ECKey createEcKey() {
        //Default constructor uses SecureRandom numbers.
        return new ECKey();
    }

    public static ECKey createEcKeyFromHexString(String privateKey) {
        //if you are going to choose this option, please ensure this string is as random as
        //possible, consider http://world.std.com/~reinhold/diceware.html
        SecureRandom randomSeed = new SecureRandom(privateKey.getBytes());
        return new ECKey(randomSeed);
    }

    /**
     * Convenience method.
     */
    public static ECKey createEcKeyFromHexStringFile(String privKeyFile) throws IOException {
        return createEcKeyFromHexString(getKeyStringFromFile(privKeyFile));
    }

    public static ECKey loadEcKey() throws IOException {
        FileInputStream fileInputStream;
        File file;

        if (KeyUtils.privateKey == null) {
            file = new File(PRIV_KEY_FILENAME);
        } else {
            file = new File(KeyUtils.privateKey);
        }

        byte[] bytes = new byte[(int) file.length()];

        fileInputStream = new FileInputStream(file);
        int numBytesRead = fileInputStream.read(bytes);

        fileInputStream.close();

        if (numBytesRead == -1) {
            throw new IOException("read nothing from the file.");
        }
        return ECKey.fromASN1(bytes);
    }

    public static ECKey loadEcKey(URI privateKey) throws IOException, URISyntaxException {
        KeyUtils.privateKey = privateKey;
        File file = new File(privateKey);
        if (!file.exists()) {
            ECKey key = createEcKey();
            saveEcKey(key, KeyUtils.privateKey);
            return key;
        }
        return loadEcKey();
    }

    public static String getKeyStringFromFile(String filename) throws IOException {
        BufferedReader br;

        br = new BufferedReader(new FileReader(filename));

        String line = br.readLine();

        br.close();

        return line;
    }

    public static void saveEcKey(ECKey ecKey) throws IOException {
        byte[] bytes = ecKey.toASN1();
        File file;

        if (KeyUtils.privateKey == null) {
            file = new File(PRIV_KEY_FILENAME);
        } else {
            file = new File(KeyUtils.privateKey);
        }

        FileOutputStream output = new FileOutputStream(file);

        output.write(bytes);
        output.close();
    }

    public static void saveEcKey(ECKey ecKey, URI privateKey) throws IOException, URISyntaxException {
        File file = new File(privateKey);
        //we shan't overwrite an existing file

        if (file.exists()) {
            return;
        }
        KeyUtils.privateKey = privateKey;
        saveEcKey(ecKey);
    }


    public static String deriveSIN(ECKey ecKey) throws IllegalArgumentException {
        // Get sha256 hash and then the RIPEMD-160 hash of the public key (this call gets the result in one step).
        byte[] pubKeyHash = ecKey.getPubKeyHash();

        // Convert binary pubKeyHash, SINtype and version to Hex
        String version = "0F";
        String SINtype = "02";
        String pubKeyHashHex = bytesToHex(pubKeyHash);

        // Concatenate all three elements
        String preSIN = version + SINtype + pubKeyHashHex;

        // Convert the hex string back to binary and double sha256 hash it leaving in binary both times
        byte[] preSINbyte = hexToBytes(preSIN);
        byte[] hash2Bytes = Sha256Hash.hashTwice(preSINbyte);

        // Convert back to hex and take first four bytes
        String hashString = bytesToHex(hash2Bytes);
        String first4Bytes = hashString.substring(0, 8);

        // Append first four bytes to fully appended SIN string
        String unencoded = preSIN + first4Bytes;
        byte[] unencodedBytes = new BigInteger(unencoded, 16).toByteArray();
        return Base58.encode(unencodedBytes);
    }

    public static String sign(ECKey key, String input) throws UnsupportedEncodingException {
        byte[] data = input.getBytes("UTF8");

        Sha256Hash hash = Sha256Hash.of(data);
        ECDSASignature sig = key.sign(hash, null);

        byte[] bytes = sig.encodeToDER();

        return bytesToHex(bytes);
    }

    private static int getHexVal(char hex) {
        int val = (int) hex;
        return val - (val < 58 ? 48 : (val < 97 ? 55 : 87));
    }

    public static byte[] hexToBytes(String hex) throws IllegalArgumentException {
        char[] hexArray = hex.toCharArray();

        if (hex.length() % 2 == 1) {
            throw new IllegalArgumentException("Error: The binary key cannot have an odd number of digits");
        }

        byte[] arr = new byte[hex.length() >> 1];

        for (int i = 0; i < hex.length() >> 1; ++i) {
            arr[i] = (byte) ((getHexVal(hexArray[i << 1]) << 4) + (getHexVal(hexArray[(i << 1) + 1])));
        }

        return arr;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;

            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}
