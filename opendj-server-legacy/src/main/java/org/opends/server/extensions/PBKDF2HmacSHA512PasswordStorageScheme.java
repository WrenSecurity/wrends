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

import static org.opends.server.extensions.ExtensionsConstants.AUTH_PASSWORD_SCHEME_NAME_PBKDF2_HMAC_SHA512;
import static org.opends.server.extensions.ExtensionsConstants.SECRET_KEY_FACTORY_ALGORITHM_PBKDF2_SHA512;
import static org.opends.server.extensions.ExtensionsConstants.STORAGE_SCHEME_NAME_PBKDF2_HMAC_SHA512;

import java.util.List;
import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.config.server.ConfigChangeResult;
import org.forgerock.opendj.config.server.ConfigException;
import org.forgerock.opendj.config.server.ConfigurationChangeListener;
import org.forgerock.opendj.server.config.server.PBKDF2HmacSHA512PasswordStorageSchemeCfg;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.InitializationException;

/**
 * This class defines a Directory Server password storage scheme based on the
 * PBKDF2 algorithm defined in RFC 2898, using HMAC-SHA-512 as its pseudo-random
 * function. This is a one-way digest algorithm so there is no way to retrieve
 * the original clear-text version of the password from the hashed value
 * (although this means that it is not suitable for things that need the
 * clear-text password like DIGEST-MD5). This implementation uses a configurable
 * number of iterations.
 */
public class PBKDF2HmacSHA512PasswordStorageScheme
        extends AbstractPBKDF2PasswordStorageScheme<PBKDF2HmacSHA512PasswordStorageSchemeCfg>
        implements ConfigurationChangeListener<PBKDF2HmacSHA512PasswordStorageSchemeCfg> {

    private static final int SHA512_LENGTH = 64;

    /** The number of iterations used when this scheme has not been configured. */
    private static final int DEFAULT_ITERATIONS = 220000;

    private volatile PBKDF2HmacSHA512PasswordStorageSchemeCfg config;

    /**
     * Creates a new instance of this password storage scheme. Note that no
     * initialization should be performed here, as all initialization should be done
     * in the <code>initializePasswordStorageScheme</code> method.
     */
    public PBKDF2HmacSHA512PasswordStorageScheme() {
    }

    @Override
    public void initializePasswordStorageScheme(PBKDF2HmacSHA512PasswordStorageSchemeCfg configuration)
            throws ConfigException, InitializationException {
        initializeScheme();

        this.config = configuration;
        config.addPBKDF2HmacSHA512ChangeListener(this);
    }

    @Override
    public boolean isConfigurationChangeAcceptable(PBKDF2HmacSHA512PasswordStorageSchemeCfg configuration,
            List<LocalizableMessage> unacceptableReasons) {
        return true;
    }

    @Override
    public ConfigChangeResult applyConfigurationChange(PBKDF2HmacSHA512PasswordStorageSchemeCfg configuration) {
        this.config = configuration;
        return new ConfigChangeResult();
    }

    @Override
    String getSecretKeyFactoryAlgorithm() {
        return SECRET_KEY_FACTORY_ALGORITHM_PBKDF2_SHA512;
    }

    @Override
    int getDigestLengthBytes() {
        return SHA512_LENGTH;
    }

    @Override
    int getIterations() {
        return config != null ? config.getPBKDF2Iterations() : DEFAULT_ITERATIONS;
    }

    @Override
    public String getStorageSchemeName() {
        return STORAGE_SCHEME_NAME_PBKDF2_HMAC_SHA512;
    }

    @Override
    public String getAuthPasswordSchemeName() {
        return AUTH_PASSWORD_SCHEME_NAME_PBKDF2_HMAC_SHA512;
    }

    /**
     * See {@link AbstractPBKDF2PasswordStorageScheme#encodePasswordOffline(byte[])}.
     *
     * @param passwordBytes The bytes that make up the clear-text password.
     * @return The encoded password string, including the scheme name in curly braces.
     */
    public static String encodeOffline(byte[] passwordBytes) throws DirectoryException {
        return new PBKDF2HmacSHA512PasswordStorageScheme().encodePasswordOffline(passwordBytes);
    }
}
