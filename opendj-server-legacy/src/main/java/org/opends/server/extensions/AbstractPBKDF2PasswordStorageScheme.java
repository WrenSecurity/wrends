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
 * Copyright 2013-2016 ForgeRock AS.
 * Portions Copyright 2026 Wren Security
 */
package org.opends.server.extensions;

import static org.opends.messages.ExtensionMessages.ERR_PWSCHEME_CANNOT_BASE64_DECODE_STORED_PASSWORD;
import static org.opends.messages.ExtensionMessages.ERR_PWSCHEME_CANNOT_ENCODE_PASSWORD;
import static org.opends.messages.ExtensionMessages.ERR_PWSCHEME_INVALID_BASE64_DECODED_STORED_PASSWORD;
import static org.opends.messages.ExtensionMessages.ERR_PWSCHEME_NOT_REVERSIBLE;
import static org.opends.messages.ExtensionMessages.ERR_PWSCHEME_UNSUPPORTED_ALGORITHM;
import static org.opends.server.extensions.ExtensionsConstants.SECURE_PRNG_SHA1;
import static org.opends.server.util.StaticUtils.getExceptionMessage;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.i18n.slf4j.LocalizedLogger;
import org.forgerock.opendj.config.server.ConfigException;
import org.forgerock.opendj.ldap.Base64;
import org.forgerock.opendj.ldap.ByteSequence;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.server.config.server.PasswordStorageSchemeCfg;
import org.opends.server.api.PasswordStorageScheme;
import org.opends.server.core.DirectoryServer;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.InitializationException;

/**
 * Common implementation of the Directory Server password storage schemes based
 * on the PBKDF2 algorithm defined in RFC 2898. This is a one-way digest
 * algorithm so there is no way to retrieve the original clear-text version of
 * the password from the hashed value (although this means that it is not
 * suitable for things that need the clear-text password like DIGEST-MD5).
 * <p>
 * Passwords are stored as the number of iterations, followed by a colon,
 * followed by the base64 encoding of the derived key concatenated with the
 * salt. Concrete subclasses only need to name the underlying pseudo-random
 * function and the length of the digest it produces, together with the
 * configured number of iterations.
 *
 * @param <T> The type of the configuration of the concrete storage scheme.
 */
abstract class AbstractPBKDF2PasswordStorageScheme<T extends PasswordStorageSchemeCfg> extends PasswordStorageScheme<T> {

    private static final LocalizedLogger logger = LocalizedLogger.getLoggerForThisClass();

    /**
     * The number of bytes of random data to use as the salt when generating the
     * hashes. NIST SP 800-132 requires at least 128 bits of randomly generated salt
     * for PBKDF2.
     */
    private static final int NUM_SALT_BYTES = 16;

    /**
     * The secure random number generator to use to generate the salt values.
     */
    private SecureRandom random;

    /**
     * Returns the name of the JCE secret key factory algorithm implementing the
     * pseudo-random function of this scheme.
     *
     * @return The name of the JCE secret key factory algorithm.
     */
    abstract String getSecretKeyFactoryAlgorithm();

    /**
     * Returns the number of bytes produced by the pseudo-random function of this
     * scheme, which is also the length of the derived key that is stored.
     *
     * @return The number of bytes of the derived key.
     */
    abstract int getDigestLengthBytes();

    /**
     * Returns the number of algorithm iterations to make, as currently configured,
     * or the default of this scheme when it has not been configured, which is the
     * case when encoding a password with the server offline.
     *
     * @return The number of algorithm iterations to make.
     */
    abstract int getIterations();

    /**
     * Initializes the state shared by all PBKDF2 based storage schemes. Concrete
     * subclasses must call this from their {@code initializePasswordStorageScheme}
     * implementation before registering their configuration change listener.
     *
     * @throws ConfigException If a configuration problem prevents initialization.
     * @throws InitializationException If the algorithm of this scheme is not supported.
     */
    final void initializeScheme() throws ConfigException, InitializationException {
        try {
            random = SecureRandom.getInstance(SECURE_PRNG_SHA1);
            // Just try to verify if the algorithm is supported.
            SecretKeyFactory.getInstance(getSecretKeyFactoryAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            logger.traceException(e);
            throw new InitializationException(ERR_PWSCHEME_UNSUPPORTED_ALGORITHM.get(getStorageSchemeName(), e), e);
        }
    }

    @Override
    public ByteString encodePassword(ByteSequence plaintext) throws DirectoryException {
        byte[] saltBytes = new byte[NUM_SALT_BYTES];
        int iterations = getIterations();

        byte[] digestBytes = encodeWithRandomSalt(plaintext, saltBytes, iterations, random);
        byte[] hashPlusSalt = concatenateHashPlusSalt(saltBytes, digestBytes);

        return ByteString.valueOfUtf8(iterations + ":" + Base64.encode(hashPlusSalt));
    }

    @Override
    public ByteString encodePasswordWithScheme(ByteSequence plaintext) throws DirectoryException {
        return ByteString.valueOfUtf8('{' + getStorageSchemeName() + '}' + encodePassword(plaintext));
    }

    @Override
    public boolean passwordMatches(ByteSequence plaintextPassword, ByteSequence storedPassword) {
        // Split the iterations from the stored value (separated by a ':')
        // Base64-decode the remaining value and take the trailing bytes as the salt.
        try {
            final String stored = storedPassword.toString();
            final int pos = stored.indexOf(':');
            if (pos == -1) {
                throw new Exception();
            }

            final int digestLength = getDigestLengthBytes();
            final int iterations = Integer.parseInt(stored.substring(0, pos));
            final byte[] decodedBytes = Base64.decode(stored.substring(pos + 1)).toByteArray();

            final int saltLength = decodedBytes.length - digestLength;
            if (saltLength <= 0) {
                logger.error(ERR_PWSCHEME_INVALID_BASE64_DECODED_STORED_PASSWORD, storedPassword);
                return false;
            }

            final byte[] digestBytes = new byte[digestLength];
            final byte[] saltBytes = new byte[saltLength];
            System.arraycopy(decodedBytes, 0, digestBytes, 0, digestLength);
            System.arraycopy(decodedBytes, digestLength, saltBytes, 0, saltLength);
            return encodeAndMatch(plaintextPassword, saltBytes, digestBytes, iterations);
        } catch (Exception e) {
            logger.traceException(e);
            logger.error(ERR_PWSCHEME_CANNOT_BASE64_DECODE_STORED_PASSWORD, storedPassword, e);
            return false;
        }
    }

    @Override
    public boolean supportsAuthPasswordSyntax() {
        return true;
    }

    @Override
    public ByteString encodeAuthPassword(ByteSequence plaintext) throws DirectoryException {
        byte[] saltBytes = new byte[NUM_SALT_BYTES];
        int iterations = getIterations();
        byte[] digestBytes = encodeWithRandomSalt(plaintext, saltBytes, iterations, random);

        // Encode and return the value.
        return ByteString.valueOfUtf8(getAuthPasswordSchemeName() + '$' + iterations + ':' + Base64.encode(saltBytes)
                + '$' + Base64.encode(digestBytes));
    }

    @Override
    public boolean authPasswordMatches(ByteSequence plaintextPassword, String authInfo, String authValue) {
        try {
            int pos = authInfo.indexOf(':');
            if (pos == -1) {
                throw new Exception();
            }
            int iterations = Integer.parseInt(authInfo.substring(0, pos));
            byte[] saltBytes = Base64.decode(authInfo.substring(pos + 1)).toByteArray();
            byte[] digestBytes = Base64.decode(authValue).toByteArray();
            return encodeAndMatch(plaintextPassword, saltBytes, digestBytes, iterations);
        } catch (Exception e) {
            logger.traceException(e);
            return false;
        }
    }

    @Override
    public boolean isReversible() {
        return false;
    }

    @Override
    public ByteString getPlaintextValue(ByteSequence storedPassword) throws DirectoryException {
        LocalizableMessage message = ERR_PWSCHEME_NOT_REVERSIBLE.get(getStorageSchemeName());
        throw new DirectoryException(ResultCode.CONSTRAINT_VIOLATION, message);
    }

    @Override
    public ByteString getAuthPasswordPlaintextValue(String authInfo, String authValue) throws DirectoryException {
        LocalizableMessage message = ERR_PWSCHEME_NOT_REVERSIBLE.get(getAuthPasswordSchemeName());
        throw new DirectoryException(ResultCode.CONSTRAINT_VIOLATION, message);
    }

    @Override
    public boolean isStorageSchemeSecure() {
        return true;
    }

    /**
     * Generates an encoded password string from the given clear-text password using
     * a fixed number of iterations. This is intended for use by the
     * {@code encodeOffline} method of the concrete schemes, which is used when it is
     * necessary to generate a password with the server offline (e.g., when setting
     * the initial root user password), and therefore must not depend on the
     * configuration of this scheme.
     *
     * @param passwordBytes The bytes that make up the clear-text password.
     * @return The encoded password string, including the scheme name in curly braces.
     * @throws DirectoryException If a problem occurs during processing.
     */
    String encodePasswordOffline(byte[] passwordBytes) throws DirectoryException {
        final byte[] saltBytes = new byte[NUM_SALT_BYTES];
        final ByteString password = ByteString.wrap(passwordBytes);
        final int iterations = getIterations();

        final byte[] digestBytes;
        try {
            digestBytes = encodeWithRandomSalt(password, saltBytes, iterations,
                    SecureRandom.getInstance(SECURE_PRNG_SHA1));
        } catch (NoSuchAlgorithmException e) {
            throw cannotEncodePassword(e);
        }
        final byte[] hashPlusSalt = concatenateHashPlusSalt(saltBytes, digestBytes);
        return '{' + getStorageSchemeName() + '}' + iterations + ':' + Base64.encode(hashPlusSalt);
    }

    private boolean encodeAndMatch(ByteSequence plaintext, byte[] saltBytes, byte[] digestBytes, int iterations) {
        try {
            final byte[] userDigestBytes = encodeWithSalt(plaintext, saltBytes, iterations);
            return Arrays.equals(digestBytes, userDigestBytes);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] encodeWithRandomSalt(ByteSequence plaintext, byte[] saltBytes, int iterations, SecureRandom random)
            throws DirectoryException {
        random.nextBytes(saltBytes);
        return encodeWithSalt(plaintext, saltBytes, iterations);
    }

    private byte[] encodeWithSalt(ByteSequence plaintext, byte[] saltBytes, int iterations) throws DirectoryException {
        final char[] plaintextChars = plaintext.toString().toCharArray();
        try {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance(getSecretKeyFactoryAlgorithm());
            final KeySpec spec = new PBEKeySpec(plaintextChars, saltBytes, iterations, getDigestLengthBytes() * 8);
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw cannotEncodePassword(e);
        } finally {
            Arrays.fill(plaintextChars, '0');
        }
    }

    private DirectoryException cannotEncodePassword(Exception e) {
        logger.traceException(e);
        LocalizableMessage message = ERR_PWSCHEME_CANNOT_ENCODE_PASSWORD.get(getClass().getName(), getExceptionMessage(e));
        return new DirectoryException(DirectoryServer.getCoreConfigManager().getServerErrorResultCode(), message, e);
    }

    private static byte[] concatenateHashPlusSalt(byte[] saltBytes, byte[] digestBytes) {
        final byte[] hashPlusSalt = new byte[digestBytes.length + NUM_SALT_BYTES];
        System.arraycopy(digestBytes, 0, hashPlusSalt, 0, digestBytes.length);
        System.arraycopy(saltBytes, 0, hashPlusSalt, digestBytes.length, NUM_SALT_BYTES);
        return hashPlusSalt;
    }

}
