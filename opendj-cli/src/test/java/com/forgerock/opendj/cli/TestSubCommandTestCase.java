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
 *      Copyright 2008 Sun Microsystems, Inc.
 *      Portions Copyright 2014 ForgeRock AS
 */
package com.forgerock.opendj.cli;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.forgerock.i18n.LocalizableMessage;


/**
 * Unit tests for the SubCommand class.
 */
public final class TestSubCommandTestCase extends CliTestCase {

    /**
     * Tests that allowsTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testAllowsTrailingArgumentsFalse1() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command1", LocalizableMessage.raw("XXX"));
        Assert.assertFalse(sc.allowsTrailingArguments());
    }

    /**
     * Tests that allowsTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testAllowsTrailingArgumentsFalse2() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", false, 0, 0, null, LocalizableMessage.raw("XXX"));
        Assert.assertFalse(sc.allowsTrailingArguments());
    }

    /**
     * Tests that allowsTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testAllowsTrailingArgumentsTrue() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                LocalizableMessage.raw("XXX"));
        Assert.assertTrue(sc.allowsTrailingArguments());
    }

    /**
     * Tests that getMaxTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMaxTrailingArguments1() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command1", LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMaxTrailingArguments(), 0);
    }

    /**
     * Tests that getMaxTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMaxTrailingArguments2() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", false, 0, 0, null, LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMaxTrailingArguments(), 0);
    }

    /**
     * Tests that getMaxTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMaxTrailingArguments3() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMaxTrailingArguments(), 4);
    }

    /**
     * Tests that getMinTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMinTrailingArguments1() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command1", LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMinTrailingArguments(), 0);
    }

    /**
     * Tests that getMinTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMinTrailingArguments2() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", false, 0, 0, null, LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMinTrailingArguments(), 0);
    }

    /**
     * Tests that getMinTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetMinTrailingArguments3() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getMinTrailingArguments(), 2);
    }

    /**
     * Tests that getTrailingArgumentsDisplayName works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArgumentsDisplayName1() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command1", LocalizableMessage.raw("XXX"));
        Assert.assertNull(sc.getTrailingArgumentsDisplayName());
    }

    /**
     * Tests that getTrailingArgumentsDisplayName works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArgumentsDisplayName2() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", false, 0, 0, null, LocalizableMessage.raw("XXX"));
        Assert.assertNull(sc.getTrailingArgumentsDisplayName());
    }

    /**
     * Tests that getTrailingArgumentsDisplayName works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArgumentsDisplayName3() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                LocalizableMessage.raw("XXX"));
        Assert.assertEquals(sc.getTrailingArgumentsDisplayName(), "args1 arg2 [arg3 arg4]");
    }

    /**
     * Tests that getTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArguments1() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command1", LocalizableMessage.raw("XXX"));
        parser.parseArguments(new String[] { "sub-command1" });
        Assert.assertTrue(sc.getTrailingArguments().isEmpty());
    }

    /**
     * Tests that getTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArguments2() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", false, 0, 0, null, LocalizableMessage.raw("XXX"));
        parser.parseArguments(new String[] { "sub-command2" });
        Assert.assertTrue(sc.getTrailingArguments().isEmpty());
    }

    /**
     * Tests that getTrailingArguments works correctly.
     *
     * @throws Exception
     *             If an unexpected problem occurred.
     */
    @Test
    public void testGetTrailingArguments3() throws Exception {
        SubCommandArgumentParser parser = new SubCommandArgumentParser(getClass().getName(),
                LocalizableMessage.raw("test description"), true);
        SubCommand sc = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                LocalizableMessage.raw("XXX"));
        parser.parseArguments(new String[] { "sub-command2", "arg1", "arg2", "arg3" });
        Assert.assertEquals(sc.getTrailingArguments(), Arrays.asList(new String[] { "arg1", "arg2", "arg3" }));
    }

}
