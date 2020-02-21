package ivm.constantIV;

import javax.crypto.spec.IvParameterSpec;

public class SimpleIVConstant {

    public static void main(String[] args) {
        byte[] iv = {1, 2, 0, 2, 1, 0, 8, 8, 1, 6, 1, 2, 1, 9, 9, 1};
        IvParameterSpec constant = new IvParameterSpec(iv);
    }
}
