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
package org.jboss.aerogear.crypto.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PKCS12Test {

    private static final String PASSWORD = "12345678";
    private byte[] valid;
    private byte[] invalid;

    @Before
    public void setUp() throws Exception {
        this.valid = toByteArray(getClass().getResourceAsStream("/cert/valid.p12"));
        this.invalid = toByteArray(getClass().getResourceAsStream("/cert/invalid.p12"));
    }

    @Test
    public void testValidCertificate() throws Exception {
        PKCS12.validate(valid, PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testInvalidCertificate() throws Exception {
        PKCS12.validate(invalid, PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testNullCertificate() throws Exception {
        PKCS12.validate(null, PASSWORD);
    }

    @Test(expected = Exception.class)
    public void testCertificateWithNullPassphrase() throws Exception {
        PKCS12.validate(invalid, null);
    }

    //Utility method to convert InputStream to bytes
    private byte[] toByteArray(InputStream file) throws IOException {
        int n;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        while (-1 != (n = file.read(buffer))) {
            bos.write(buffer, 0, n);
        }

        return bos.toByteArray();
    }
}