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
 * Copyright 2010 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 * Portions Copyright 2022 Wren Security.
 */
package org.forgerock.opendj.ldap.controls;

import static com.forgerock.opendj.ldap.CoreMessages.ERR_PWEXPIRED_CONTROL_BAD_OID;
import static com.forgerock.opendj.ldap.CoreMessages.ERR_PWEXPIRED_CONTROL_INVALID_VALUE;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.DecodeException;
import org.forgerock.opendj.ldap.DecodeOptions;

import org.forgerock.util.Reject;

/**
 * The Netscape password expired response control as defined in
 * draft-vchu-ldap-pwd-policy. This control indicates to a client that their
 * password has expired and must be changed. This control always has a value
 * which is the string {@code "0"}.
 *
 * <pre>
 * Connection connection = ...;
 * String DN = ...;
 * char[] password = ...;
 *
 * try {
 *     connection.bind(DN, password);
 * } catch (LdapException e) {
 *     Result result = e.getResult();
 *     try {
 *         PasswordExpiredResponseControl control =
 *                 result.getControl(PasswordExpiredResponseControl.DECODER,
 *                         new DecodeOptions());
 *         if (!(control == null) &amp;&amp; control.hasValue()) {
 *             // Password expired
 *         }
 *     } catch (DecodeException de) {
 *         // Failed to decode the response control.
 *     }
 * }
 * </pre>
 *
 * @see <a
 *      href="http://tools.ietf.org/html/draft-vchu-ldap-pwd-policy">draft-vchu-ldap-pwd-policy
 *      - Password Policy for LDAP Directories </a>
 */
public final class PasswordExpiredResponseControl implements Control {
    /** The OID for the Netscape password expired response control. */
    public static final String OID = "2.16.840.1.113730.3.4.4";

    private final boolean isCritical;

    private static final PasswordExpiredResponseControl CRITICAL_INSTANCE =
            new PasswordExpiredResponseControl(true);
    private static final PasswordExpiredResponseControl NONCRITICAL_INSTANCE =
            new PasswordExpiredResponseControl(false);

    /** A decoder which can be used for decoding the password expired response control. */
    public static final ControlDecoder<PasswordExpiredResponseControl> DECODER =
            new ControlDecoder<PasswordExpiredResponseControl>() {

                @Override
                public PasswordExpiredResponseControl decodeControl(final Control control,
                        final DecodeOptions options) throws DecodeException {
                    Reject.ifNull(control);

                    if (control instanceof PasswordExpiredResponseControl) {
                        return (PasswordExpiredResponseControl) control;
                    }

                    if (!control.getOID().equals(OID)) {
                        final LocalizableMessage message =
                                ERR_PWEXPIRED_CONTROL_BAD_OID.get(control.getOID(), OID);
                        throw DecodeException.error(message);
                    }

                    if (control.hasValue()) {
                        try {
                            Integer.parseInt(control.getValue().toString());
                        } catch (final Exception e) {
                            final LocalizableMessage message =
                                    ERR_PWEXPIRED_CONTROL_INVALID_VALUE.get();
                            throw DecodeException.error(message);
                        }
                    }

                    return control.isCritical() ? CRITICAL_INSTANCE : NONCRITICAL_INSTANCE;
                }

                @Override
                public String getOID() {
                    return OID;
                }
            };

    private static final ByteString CONTROL_VALUE = ByteString.valueOfUtf8("0");

    /**
     * Creates a new Netscape password expired response control.
     *
     * @return The new control.
     */
    public static PasswordExpiredResponseControl newControl() {
        return NONCRITICAL_INSTANCE;
    }

    private PasswordExpiredResponseControl(final boolean isCritical) {
        this.isCritical = isCritical;
    }

    @Override
    public String getOID() {
        return OID;
    }

    @Override
    public ByteString getValue() {
        return CONTROL_VALUE;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public boolean isCritical() {
        return isCritical;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PasswordExpiredResponseControl(oid=");
        builder.append(getOID());
        builder.append(", criticality=");
        builder.append(isCritical());
        builder.append(")");
        return builder.toString();
    }
}
