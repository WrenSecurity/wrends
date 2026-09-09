/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2026 Wren Security
 */
package org.opends.server.extensions;

import static org.testng.Assert.assertTrue;

import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.server.config.meta.PBKDF2HmacSHA256PasswordStorageSchemeCfgDefn;
import org.opends.server.api.PasswordStorageScheme;
import org.opends.server.types.DirectoryException;
import org.testng.annotations.Test;

/**
 * A set of test cases for the PBKDF2-HMAC-SHA256 password storage scheme.
 */
public class PBKDF2HmacSHA256PasswordStorageSchemeTestCase extends PasswordStorageSchemeTestCase {

    /**
     * The published PBKDF2 test vector for the password "password", the salt "salt"
     * and 4096 iterations, encoded the way this scheme stores it: the iteration
     * count, a colon, then the base64 encoding of the derived key followed by the
     * salt.
     */
    private final String PASSWORD = "4096:xeR41ZKIyEGqUw22hFxMjZYok6ABzk4RpJY4c6qYE0pzYWx0";

    public PBKDF2HmacSHA256PasswordStorageSchemeTestCase() {
        super("cn=PBKDF2-HMAC-SHA256,cn=Password Storage Schemes,cn=config");
    }

    /**
     * Verifies that this scheme interoperates with the published PBKDF2
     * HMAC-SHA-256 test vector, which pins both the key derivation and the way the
     * derived key and salt are encoded.
     */
    @Test
    public void testKnownVectorMatches() throws Exception {
        assertTrue(getScheme().passwordMatches(ByteString.valueOfUtf8("password"),
                ByteString.valueOfUtf8(PASSWORD)));
    }

    /**
     * Verifies that the same published test vector is matched through the
     * authentication password syntax, where the salt and the derived key are
     * encoded separately.
     */
    @Test
    public void testKnownVectorMatchesAuthPassword() throws Exception {
        assertTrue(getScheme().authPasswordMatches(ByteString.valueOfUtf8("password"),
                "4096:c2FsdA==", "xeR41ZKIyEGqUw22hFxMjZYok6ABzk4RpJY4c6qYE0o="));
    }

    @Override
    protected PasswordStorageScheme<?> getScheme() throws Exception {
        return InitializationUtils.initializePasswordStorageScheme(new PBKDF2HmacSHA256PasswordStorageScheme(),
                configEntry, PBKDF2HmacSHA256PasswordStorageSchemeCfgDefn.getInstance());
    }

    @Override
    protected String encodeOffline(final byte[] plaintextBytes) throws DirectoryException {
        return PBKDF2HmacSHA256PasswordStorageScheme.encodeOffline(plaintextBytes);
    }
}
