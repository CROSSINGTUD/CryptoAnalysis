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
package org.jboss.aerogear.crypto.encoders;

import java.nio.charset.Charset;

/**
 * Provides a level of abstraction for encoding schemes
 */
public interface Encoder {

    public static final Charset CHARSET = Charset.forName("US-ASCII");

    public static final Hex HEX = new Hex();
    public static final Raw RAW = new Raw();
    public static final Base64 BASE64 = new Base64();
    public static final UrlBase64 URL_BASE64 = new UrlBase64();

    /**
     * Decode a provided string to bytes
     * @param data to be decoded
     * @return byte array with decoded data
     */
    public byte[] decode(String data);

    /**
     * Encode the provided data to string
     * @param data to be encoded
     * @return string with encoded content
     */
    public String encode(final byte[] data);
}
