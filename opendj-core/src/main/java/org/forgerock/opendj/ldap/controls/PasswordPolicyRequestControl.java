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
 */
package org.forgerock.opendj.ldap.controls;

import static com.forgerock.opendj.ldap.CoreMessages.ERR_PWPOLICYREQ_CONTROL_BAD_OID;
import static com.forgerock.opendj.ldap.CoreMessages.ERR_PWPOLICYREQ_CONTROL_HAS_VALUE;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.DecodeException;
import org.forgerock.opendj.ldap.DecodeOptions;

import org.forgerock.util.Reject;

/**
 * The password policy request control as defined in
 * draft-behera-ldap-password-policy.
 * <p>
 * This control may be sent with any request in order to convey to the server
 * that this client is aware of, and can process the password policy response
 * control. When a server receives this control, it will return the password
 * policy response control when appropriate and with the proper data.
 *
 * <pre>
 * Connection connection = ...;
 * String DN = ...;
 * char[] password = ...;
 *
 * try {
 *     BindRequest request = Requests.newSimpleBindRequest(DN, password)
 *             .addControl(PasswordPolicyRequestControl.newControl(true));
 *
 *     BindResult result = connection.bind(request);
 *
 *     PasswordPolicyResponseControl control =
 *             result.getControl(PasswordPolicyResponseControl.DECODER,
 *                     new DecodeOptions());
 *     if (!(control == null) &amp;&amp; !(control.getWarningType() == null)) {
 *         // Password policy warning, use control.getWarningType(),
 *         // and control.getWarningValue().
 *     }
 * } catch (LdapException e) {
 *     Result result = e.getResult();
 *     try {
 *         PasswordPolicyResponseControl control =
 *                 result.getControl(PasswordPolicyResponseControl.DECODER,
 *                         new DecodeOptions());
 *         if (!(control == null)) {
 *             // Password policy error, use control.getErrorType().
 *         }
 *     } catch (DecodeException de) {
 *         // Failed to decode the response control.
 *     }
 * } catch (DecodeException e) {
 *     // Failed to decode the response control.
 * }
 * </pre>
 *
 * @see PasswordPolicyResponseControl
 * @see <a href="http://tools.ietf.org/html/draft-behera-ldap-password-policy">
 *      draft-behera-ldap-password-policy - Password Policy for LDAP Directories
 *      </a>
 */
public final class PasswordPolicyRequestControl implements Control {
    /** The OID for the password policy control from draft-behera-ldap-password-policy. */
    public static final String OID = "1.3.6.1.4.1.42.2.27.8.5.1";

    private final boolean isCritical;

    private static final PasswordPolicyRequestControl CRITICAL_INSTANCE =
            new PasswordPolicyRequestControl(true);
    private static final PasswordPolicyRequestControl NONCRITICAL_INSTANCE =
            new PasswordPolicyRequestControl(false);

    /** A decoder which can be used for decoding the password policy request control. */
    public static final ControlDecoder<PasswordPolicyRequestControl> DECODER =
            new ControlDecoder<PasswordPolicyRequestControl>() {

                @Override
                public PasswordPolicyRequestControl decodeControl(final Control control,
                        final DecodeOptions options) throws DecodeException {
                    Reject.ifNull(control);

                    if (control instanceof PasswordPolicyRequestControl) {
                        return (PasswordPolicyRequestControl) control;
                    }

                    if (!control.getOID().equals(OID)) {
                        final LocalizableMessage message =
                                ERR_PWPOLICYREQ_CONTROL_BAD_OID.get(control.getOID(), OID);
                        throw DecodeException.error(message);
                    }

                    if (control.hasValue()) {
                        final LocalizableMessage message = ERR_PWPOLICYREQ_CONTROL_HAS_VALUE.get();
                        throw DecodeException.error(message);
                    }

                    return control.isCritical() ? CRITICAL_INSTANCE : NONCRITICAL_INSTANCE;
                }

                @Override
                public String getOID() {
                    return OID;
                }
            };

    /**
     * Creates a new password policy request control having the provided
     * criticality.
     *
     * @param isCritical
     *            {@code true} if it is unacceptable to perform the operation
     *            without applying the semantics of this control, or
     *            {@code false} if it can be ignored.
     * @return The new control.
     */
    public static PasswordPolicyRequestControl newControl(final boolean isCritical) {
        return isCritical ? CRITICAL_INSTANCE : NONCRITICAL_INSTANCE;
    }

    private PasswordPolicyRequestControl(final boolean isCritical) {
        this.isCritical = isCritical;
    }

    @Override
    public String getOID() {
        return OID;
    }

    @Override
    public ByteString getValue() {
        return null;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public boolean isCritical() {
        return isCritical;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PasswordPolicyRequestControl(oid=");
        builder.append(getOID());
        builder.append(", criticality=");
        builder.append(isCritical());
        builder.append(")");
        return builder.toString();
    }

}
