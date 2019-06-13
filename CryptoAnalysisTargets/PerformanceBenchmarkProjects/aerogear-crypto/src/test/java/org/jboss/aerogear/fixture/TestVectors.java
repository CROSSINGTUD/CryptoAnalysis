/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.fixture;

public class TestVectors {

    public static final String PASSWORD = "My Bonnie lies over the ocean, my Bonnie lies over the sea";
    public static final String INVALID_PASSWORD = "invalid";

    //TEST VECTORS
    public static final String BOB_SECRET_KEY = "f3a9375ec8746cc6e78e4b02d7e748988e10c74525e5c0a41d63fe5d21f84224";
    public static final String BOB_PRIVATE_KEY = "3041020100301306072a8648ce3d020106082a8648ce3d030107042730250201010420787ed8d21a33ec9f61aa4e3554262d32afb360e6559c2b7bdca27f4d2ffb43f8";
    public static final String BOB_PUBLIC_KEY = "3059301306072a8648ce3d020106082a8648ce3d03010703420004cfdda272dbef98b20db02a26122ac3fa5a25535afa9843dbb9a13559edd0c81c030c029fa095024150853b1d83091c40eb37c83e3456d51308a87c65fee8311c";

    public static final String ALICE_PRIVATE_KEY = "3041020100301306072a8648ce3d020106082a8648ce3d030107042730250201010420c3868e3acaab084e0a5984849f11c36f48a465c8fe6c7ed1e2c80537d488abd0";
    public static final String ALICE_PUBLIC_KEY = "3059301306072a8648ce3d020106082a8648ce3d03010703420004d5719ba60b706b2aff2eed91d4a7090940d72f88fd55f409358a780667f97c63cbb838cc6909fc7da370097eeff9c74564188fa6cbf360f5d407f5aa796a4198";

    public static final String CRYPTOBOX_IV = "69696ee955b62b73cd62bda875fc73d68219e0036b7a0b37";
    public static final String CRYPTOBOX_MESSAGE = "be075fc53c81f2d5cf141316ebeb0c7b5228c52a4c62cbd44b66849b64244ffc" +
            "e5ecbaaf33bd751a1ac728d45e6c61296cdc3c01233561f41db66cce314adb31" +
            "0e3be8250c46f06dceea3a7fa1348057e2f6556ad6b1318a024a838f21af1fde" +
            "048977eb48f59ffd4924ca1c60902e52f0a089bc76897040e082f93776384864" +
            "5e0705";
    public static final String CRYPTOBOX_CIPHERTEXT = "b05177ed87371685bcb4ba37ec51ec66712950fb18778e3c915be42a9" +
            "73f127f15f8b053c57c223ef7c2f7bea59bc5b63f32ac0d2daaecb75cb489489a46cbcdb749c09cdc0f6adfbf1cb625053f7" +
            "b20800f9a7c1ef6a4379df67a9873ec696e0ca484861a70de22254fe3790f02b39746fb1f6a47d95294fdfc3e55ff3a38a855" +
            "c1572e518ad0a036b7c75f7e894858562992";

    public static final String BOX_NONCE = "69696ee955b62b73cd62bda875fc73d68219e0036b7a0b37";
    public static final String BOX_MESSAGE = "be075fc53c81f2d5cf141316ebeb0c7b5228c52a4c62cbd44b66849b64244ffc" +
            "e5ecbaaf33bd751a1ac728d45e6c61296cdc3c01233561f41db66cce314adb31" +
            "0e3be8250c46f06dceea3a7fa1348057e2f6556ad6b1318a024a838f21af1fde" +
            "048977eb48f59ffd4924ca1c60902e52f0a089bc76897040e082f93776384864" +
            "5e0705";
    public static final String BOX_CIPHERTEXT = "fa11d3d36080df6f68d1201bf98d761886dc17003129b1a2ffa8fcb96292ed53f178b07dbd6b451a746f3ae1a47c194f0003b331ef9d0be1b81b49392870a26e4f8e3307e299d9ca23adc0f704559de9bd93e0e95d044829f2b0b2a2b830be0f82de4ea774341cc882c8a63b7914285323e19af6b862281597b14847b16dc840de8d8d2f7b526b5c4a0516d11c4bd5e4415b20";
    public static final String BOX_STRING_MESSAGE = "Bacon ipsum dolor sit amet pastrami pork chop short loin corned beef hamburger ground round sirloin pig kielbasa brisket.";

    //Keep in mind that the SALT is hard coded only for unit tests purposes
    public static final String HMAC_STRING_SALT = "Sweet home Alabama";
    public static final String HMAC_STRING_MESSAGE = "Where the skies are so blue";
    public static final String HMAC_STRING_DIGEST_SHA1 = "H7c79yzIWorc9C7HZSlPmCetHg8=";
    public static final String HMAC_STRING_DIGEST_SHA256 = "MJiEUwAbeTb2VzLdCAYkbQiVMi0DoW/ZC56MAIer6aw=";
    public static final String HMAC_STRING_DIGEST_SHA512 = "/x5fedlzTfCXgK/q5k9d1BkEl0roF5g1wy7+qYS5zW51pS33WGMXeivZGMi4E3p9rQwXLwtzXban78SRHP7rvA==";

    //Test vectors for digital signatures
    public static final String SIGN_MESSAGE = "916c7d1d268fc0e77c1bef238432573c39be577bbea0998936add2b50a653171" +
            "ce18a542b0b7f96c1691a3be6031522894a8634183eda38798a0c5d5d79fbd01" +
            "dd04a8646d71873b77b221998a81922d8105f892316369d5224c9983372d2313" +
            "c6b1f4556ea26ba49d46e8b561e0fc76633ac9766e68e21fba7edca93c4c7460" +
            "376d7f3ac22ff372c18f613f2ae2e856af40";


    //SHA256 test vectors
    public static final String SHA256_MESSAGE = "My Bonnie lies over the ocean, my Bonnie lies over the sea";
    public static final String SHA256_DIGEST = "d281d10296b7bde20df3f3f4a6d1bdb513f4aa4ccb0048c7b2f7f5786b4bcb77";
    public static final String SHA256_DIGEST_EMPTY_STRING = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    //SHA512 test vectors
    public static final String SHA512_MESSAGE = "My Bonnie lies over the ocean, Oh bring back my Bonnie to me";
    public static final String SHA512_DIGEST = "2823e0373001b5f3aa6db57d07bc588324917fc221dd27975613942d7f2e19bf4" +
            "44654c8b9f4f9cb908ef15f2304522e60e9ced3fdec66e34bc2afb52be6ad1c";
    public static final String SHA512_DIGEST_EMPTY_STRING = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921" +
            "d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
}
