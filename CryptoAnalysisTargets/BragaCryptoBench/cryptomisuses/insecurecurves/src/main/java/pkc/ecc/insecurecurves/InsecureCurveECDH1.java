package pkc.ecc.insecurecurves;

import org.alexmbraga.utils.U;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;

public final class InsecureCurveECDH1 {

  public static void main(String argv[]) {
    try {
      ECGenParameterSpec ecps = new ECGenParameterSpec("secp112r1");
      U.println("ECDH parameters "+ecps.getName());
      
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC","SunEC");
      kpg.initialize(ecps); 
      KeyPair kp = kpg.generateKeyPair();
      //U.println("Pub  key: " + kp.getPublic());
      //U.println("Priv key: " + U.b2x(kp.getPrivate().getEncoded()));
      //U.println("Size (bytes): " + kp.getPrivate().getEncoded().length);      
    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | 
            NoSuchProviderException e) {
      System.err.println("Error: " + e);
    }
  }
}

/* This is an insecure curve, according to several standards:
      ** [1] http://www.secg.org/sec2-v2.pdf
      ** [2] Martinez, V. Gayoso, and L. Hernandez Encinas. "Implementing ECC
      **     with Java Standard Edition 7." International Journal of Computer 
      **      Science and Artificial Intelligence 3.4 (2013): 134.
      ** [3] NIST suite B
      **
      ** Recommended curves over Fp
      ** – 192 bits: secp192k1 and secp192r1.
      ** – 224 bits: secp224k1 and secp224r1.
      ** – 256 bits: secp256k1 and secp256r1.
      ** – 384 bits: secp384r1.
      ** – 521 bits: secp521r1.
      
      ** Recommended curves over F2m
      ** – 163 bits: sect163k1, sect163r1, and sect163r2.
      ** – 233 bits: sect233k1 and sect233r1.
      ** – 239 bits: sect239k1.
      ** – 283 bits: sect283k1 and sect283r1.
      ** – 409 bits: sect409k1 and sect409r1.
      ** – 571 bits: sect571k1 and sect571r1.

from [2]
ELLIPTIC CURVES OVER Fp IN SunEC
SECG SEC 2 ! ANSI X9.62       ! NIST FIPS 186-2
secp112r1  !                  !
secp112r2  !                  !
secp128r1  !                  !
secp128r2  !                  !
secp160k1  !                  !
secp160r1  !                  !
secp160r2  !                  !
secp192k1  !                  !
secp192r1  ! X9.62 prime192v1 !   NIST P-192
           ! X9.62 prime192v2 !
           ! X9.62 prime192v3 !
secp224k1  !                  !
secp224r1  !                  !  NIST P-224
           ! X9.62 prime239v1 !
           ! X9.62 prime239v2 !
           ! X9.62 prime239v3 !
secp256k1  !                  !
secp256r1  ! X9.62 prime256v1 ! NIST P-256
secp384r1  !                  ! NIST P-384
secp521r1  !                  ! NIST P-521

from[2]
ELLIPTIC CURVES OVER F2m IN SunEC
SECG SEC 2 ! ANSI X9.62       ! NIST FIPS 186-2
sect113r1  !                  !
sect113r2  !                  !
sect131r1  !                  !
sect131r2  !                  !
sect163k1  !                  ! NIST K-163     
sect163r1  !                  !
sect163r2  !                  ! NIST B-163
           ! X9.62 c2tnb191v1 !
           ! X9.62 c2tnb191v2 !
           ! X9.62 c2tnb191v3 !
sect193r1  !                  ! 
sect193r2  !                  ! 
sect233k1  !                  !  NIST K-233
sect233r1  !                  !  NIST B-233
sect239k1  !                  ! 
           ! X9.62 c2tnb239v1 !
           ! X9.62 c2tnb239v2 !
           ! X9.62 c2tnb239v3 !
sect283k1  !                  ! NIST K-283
sect283r1  !                  !  NIST B-283
           ! X9.62 c2tnb359v1 !
sect409k1  !                  !  NIST K-409
sect409r1  !                  !  NIST B-409
           ! X9.62 c2tnb431r1 !
sect571k1  !                  !  NIST K-571
sect571r1  !                  !  NIST B-571

*/
