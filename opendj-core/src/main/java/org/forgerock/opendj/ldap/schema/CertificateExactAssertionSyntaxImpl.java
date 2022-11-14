/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2009 Sun Microsystems, Inc.
 *      Portions Copyright 2014 Manuel Gaupp
 */
package org.forgerock.opendj.ldap.schema;

import static org.forgerock.opendj.ldap.schema.SchemaConstants.SYNTAX_CERTIFICATE_EXACT_ASSERTION_NAME;

import org.forgerock.i18n.LocalizableMessageBuilder;
import org.forgerock.opendj.ldap.ByteSequence;

/**
 * This class defines the Certificate Exact Assertion attribute syntax, which
 * contains components for matching X.509 certificates.
 */
final class CertificateExactAssertionSyntaxImpl extends AbstractSyntaxImpl {

    /** {@inheritDoc} */
    public String getName() {
        return SYNTAX_CERTIFICATE_EXACT_ASSERTION_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBEREncodingRequired() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isHumanReadable() {
        return true;
    }

    /** {@inheritDoc} */
    public boolean valueIsAcceptable(final Schema schema, final ByteSequence value,
            final LocalizableMessageBuilder invalidReason) {
        // This method will never be called because this syntax is only used
        // within assertions.
        return true;
    }
}
