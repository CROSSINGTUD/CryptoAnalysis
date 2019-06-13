[![Build Status](https://travis-ci.org/aerogear/aerogear-crypto-java.png)](https://travis-ci.org/aerogear/aerogear-crypto-java)
[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jboss.aerogear/aerogear-crypto/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jboss.aerogear/aerogear-crypto)


|                 | Project Info  |
| --------------- | ------------- |
| License:        | Apache License, Version 2.0  |
| Build:          | Maven  |
| Documentation:  | https://aerogear.org/docs/  |
| Issue tracker:  | https://issues.jboss.org/browse/AGSEC  |
| Mailing lists:  | [aerogear-users](http://aerogear-users.1116366.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-users))  |
|                 | [aerogear-dev](http://aerogear-dev.1069024.n5.nabble.com/) ([subscribe](https://lists.jboss.org/mailman/listinfo/aerogear-dev))  |

# AeroGear Crypto Java

A Java API to provide an easy way to use cryptography interfaces for developers built on top of [javax.crypto](http://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html) and [Bouncy Castle](http://www.bouncycastle.org) to support: [AES-GCM authenticated encryption](http://csrc.nist.gov/publications/nistpubs/800-38D/SP-800-38D.pdf), [password based key derivation](http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf) and [elliptic curve cryptography](http://www.nsa.gov/business/programs/elliptic_curve.shtml).

## Requirements

* JDK 6 or [higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven](http://maven.apache.org/guides/getting-started/) 

## Installation

### Android

The Android platform unfortunately ships an incomplete and outdated version of [Bouncy Castle for Android](https://code.google.com/p/android/issues/detail?id=3280) which also makes hard to install an updated version of the library. That said, we had to stick with [Spongy Castle](http://rtyley.github.io/spongycastle/), a version of [Bouncy Castle](http://www.bouncycastle.org) repackaged to make it work on Android.

    <dependency>
        <groupId>org.jboss.aerogear</groupId>
        <artifactId>aerogear-crypto</artifactId>
        <version>0.1.3</version>
        <classifier>android</classifier>
    </dependency>

### Regular Java projects

For regular Java EE and Java SE projects, [Bouncy Castle](http://www.bouncycastle.org) will be supported and there is no need to workaround it.

    <dependency>
        <groupId>org.jboss.aerogear</groupId>
        <artifactId>aerogear-crypto</artifactId>
        <version>0.1.3</version>
    </dependency>
    
    <dependency>
        <groupId>bouncycastle</groupId>
        <artifactId>bcprov-jdk16</artifactId>
        <version>140</version>
    </dependency>

## Getting started

AeroGear Crypto does not reinvent the wheel by writing encryption algorithms or creating protocols, we still have some sanity. The major goal of this project is to provide simple API interfaces for uber complicated parameters, so let's get started.

### Password based key derivation

    Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
    byte[] rawKey = pbkdf2.encrypt("passphrase");

### Symmetric encryption

    //Generate the key
    Pbkdf2 pbkdf2 = AeroGearCrypto.pbkdf2();
    byte[] privateKey = pbkdf2.encrypt("passphrase");
    
    //Initializes the crypto box
    CryptoBox cryptoBox = new CryptoBox(privateKey);
    
    //Encryption
    byte[] IV = new Random().randomBytes();
    byte[] ciphertext = cryptoBox.encrypt(IV, "My bonnie lies over the ocean");

    //Decryption
    CryptoBox pandora = new CryptoBox(privateKey);
    byte[] message = pandora.decrypt(IV, ciphertext);

### Asymmetric encryption

    //Create a new key pair
    KeyPair keyPairBob = new KeyPair();
    KeyPair keyPairAlice = new KeyPair();

    //Initializes the crypto box
    CryptoBox cryptoBox = new CryptoBox(keyPairBob.getPrivateKey(), keyPairAlice.getPublicKey());
    
    byte[] IV = new Random().randomBytes();
    byte[] ciphertext = cryptoBox.encrypt(IV, "My bonnie lies over the ocean");

    //Is possible to use the same crypto box instance, but won't happen in real life
    CryptoBox pandora = new CryptoBox(keyPairAlice.getPrivateKey(), keyPairBob.getPublicKey());
    byte[] message = pandora.decrypt(IV, ciphertext);
    
    
We are big believers that there is too much to improve, for this reason you are more than welcome to file a [JIRA](https://issues.jboss.org/browse/AGSEC) if you find any issue or discuss the improvements on the [mailing list](http://aerogear-dev.1069024.n5.nabble.com). Security is not an island and it is our responsibility like developers to make it better.

## Documentation

For more details about the current release, please consult [our documentation](http://aerogear.org/docs/guides/aerogear-android/).

## Development

If you would like to help develop AeroGear you can join our [developer's mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-dev), join #aerogear on Freenode, or shout at us on Twitter @aerogears.

Also takes some time and skim the [contributor guide](http://aerogear.org/docs/guides/Contributing/)

## Questions?

Join our [user mailing list](https://lists.jboss.org/mailman/listinfo/aerogear-users) for any questions or help! We really hope you enjoy app development with AeroGear!

## Found a bug?

If you found a bug please create a ticket for us on [Jira](https://issues.jboss.org/browse/AGDROID) with some steps to reproduce it.
