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
 *      Copyright 2013 ForgeRock AS
 */
package org.forgerock.opendj.ldap;

import static org.fest.assertions.Assertions.assertThat;
import static org.forgerock.opendj.ldap.TestCaseUtils.mockTimeService;

import org.testng.annotations.Test;

import org.forgerock.util.time.TimeService;

@SuppressWarnings("javadoc")
public class TestCaseUtilsTestCase extends SdkTestCase {

    /**
     * Test for {@link #mockTimeSource(long...)}.
     */
    @Test
    public void testMockTimeSource() {
        final TimeService mock1 = mockTimeService(10);
        assertThat(mock1.now()).isEqualTo(10);
        assertThat(mock1.now()).isEqualTo(10);

        final TimeService mock2 = mockTimeService(10, 20, 30);
        assertThat(mock2.now()).isEqualTo(10);
        assertThat(mock2.now()).isEqualTo(20);
        assertThat(mock2.now()).isEqualTo(30);
        assertThat(mock2.now()).isEqualTo(30);
    }
}
