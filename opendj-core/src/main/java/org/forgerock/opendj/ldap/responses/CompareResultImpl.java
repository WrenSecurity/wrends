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
 *      Copyright 2010 Sun Microsystems, Inc.
 *      Portions copyright 2012 ForgeRock AS.
 */

package org.forgerock.opendj.ldap.responses;

import org.forgerock.opendj.ldap.ResultCode;

/**
 * Compare result implementation.
 */
final class CompareResultImpl extends AbstractResultImpl<CompareResult> implements CompareResult {

    CompareResultImpl(final CompareResult compareResult) {
        super(compareResult);
    }

    CompareResultImpl(final ResultCode resultCode) {
        super(resultCode);
    }

    @Override
    public boolean matched() {
        final ResultCode code = getResultCode();
        return code.equals(ResultCode.COMPARE_TRUE);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CompareResult(resultCode=");
        builder.append(getResultCode());
        builder.append(", matchedDN=");
        builder.append(getMatchedDN());
        builder.append(", diagnosticMessage=");
        builder.append(getDiagnosticMessage());
        builder.append(", referrals=");
        builder.append(getReferralURIs());
        builder.append(", controls=");
        builder.append(getControls());
        builder.append(")");
        return builder.toString();
    }

    @Override
    CompareResult getThis() {
        return this;
    }

}
