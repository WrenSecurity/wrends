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
 * Copyright 2009 Sun Microsystems, Inc.
 * Portions copyright 2014-2015 ForgeRock AS.
 */
package org.forgerock.opendj.ldap.schema;

import org.forgerock.opendj.ldap.ByteSequence;
import org.forgerock.opendj.ldap.ByteString;

import static com.forgerock.opendj.util.StringPrepProfile.*;
import static org.forgerock.opendj.ldap.schema.SchemaConstants.*;

/**
 * This class implements the presentationAddressMatch matching rule defined in
 * X.520 and referenced in RFC 2252. However, since this matching rule and the
 * associated syntax have been deprecated, this matching rule behaves exactly
 * like the caseIgnoreMatch rule.
 */
final class PresentationAddressEqualityMatchingRuleImpl extends AbstractEqualityMatchingRuleImpl {

    PresentationAddressEqualityMatchingRuleImpl() {
        super(EMR_PRESENTATION_ADDRESS_NAME);
    }

    @Override
    public ByteString normalizeAttributeValue(final Schema schema, final ByteSequence value) {
        return SchemaUtils.normalizeStringAttributeValue(value, TRIM, CASE_FOLD);
    }
}
