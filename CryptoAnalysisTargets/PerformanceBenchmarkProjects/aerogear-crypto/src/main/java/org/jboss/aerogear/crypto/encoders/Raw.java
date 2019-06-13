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

/**
 * Converts raw Strings
 */
public class Raw implements Encoder {

    /**
     * Decode the provided string
     * @param data to be decoded
     * @return sequence of bytes
     */
    @Override
    public byte[] decode(final String data) {
        return data != null ? data.getBytes(CHARSET) : null;
    }

    /**
     * Encode the provided sequence of bytes
     * @param data to be encoded
     * @return string with the specified array of bytes decoded
     */
    @Override
    public String encode(byte[] data) {
        return data != null ? new String(data, CHARSET) : null;
    }
}
