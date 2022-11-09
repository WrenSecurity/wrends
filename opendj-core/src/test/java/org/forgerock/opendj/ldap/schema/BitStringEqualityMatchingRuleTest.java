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
 */
package org.forgerock.opendj.ldap.schema;

import static org.forgerock.opendj.ldap.schema.SchemaConstants.EMR_BIT_STRING_OID;

import org.forgerock.opendj.ldap.ConditionResult;
import org.testng.annotations.DataProvider;

/**
 * Test the BitStringEqualityMatchingRule.
 */
public class BitStringEqualityMatchingRuleTest extends MatchingRuleTest {
    /** {@inheritDoc} */
    @Override
    @DataProvider(name = "matchingRuleInvalidAttributeValues")
    public Object[][] createMatchingRuleInvalidAttributeValues() {
        return new Object[][] {
            { "\'a\'B" },
            { "0" },
            { "010101" },
            { "\'10101" },
            { "\'1010\'A" }, };
    }

    /** {@inheritDoc} */
    @Override
    @DataProvider(name = "matchingrules")
    public Object[][] createMatchingRuleTest() {
        return new Object[][] {
            { "\'0\'B", "\'0\'B", ConditionResult.TRUE },
            { "\'1\'B", "\'1\'B", ConditionResult.TRUE },
            { "\'0\'B", "\'1\'B", ConditionResult.FALSE }, };
    }

    /** {@inheritDoc} */
    @Override
    protected MatchingRule getRule() {
        return Schema.getCoreSchema().getMatchingRule(EMR_BIT_STRING_OID);
    }
}
