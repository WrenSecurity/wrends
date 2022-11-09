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
 *      Portions Copyright 2014-2015 ForgeRock AS
 */
package com.forgerock.opendj.cli;

import static com.forgerock.opendj.cli.CliMessages.*;

import java.util.ArrayList;
import java.util.List;

import org.fest.assertions.Assertions;
import org.forgerock.i18n.LocalizableMessage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for the SubCommand class.
 */
@SuppressWarnings("javadoc")
public final class TestSubCommandArgumentParserTestCase extends CliTestCase {

    private SubCommandArgumentParser parser;

    /** First sub-command. */
    private SubCommand sc1;
    /** Second sub-command. */
    private SubCommand sc2;

    /**
     * Create the sub-commands and parser.
     *
     * @throws Exception
     *             If something unexpected happened.
     */
    @BeforeClass
    public void setup() throws Exception {
        parser = new SubCommandArgumentParser(getClass().getName(), LocalizableMessage.raw("test description"), true);

        sc1 = new SubCommand(parser, "sub-command1", INFO_BACKUPDB_DESCRIPTION_BACKEND_ID.get());
        sc2 = new SubCommand(parser, "sub-command2", true, 2, 4, "args1 arg2 [arg3 arg4]",
                INFO_BACKUPDB_DESCRIPTION_BACKUP_ALL.get());
    }

    /**
     * Test the getSubCommand method.
     *
     * @throws Exception
     *             If something unexpected happened.
     */
    @Test
    public void testGetSubCommand() throws Exception {
        Assert.assertSame(parser.getSubCommand("sub-command1"), sc1);
        Assert.assertSame(parser.getSubCommand("sub-command2"), sc2);
        Assert.assertNull(parser.getSubCommand("sub-command3"));
    }

    /**
     * Provide valid command line args.
     *
     * @return Array of valid command line args.
     */
    @DataProvider(name = "validCommandLineArgs")
    public Object[][] createValidCommandLineArgs() {
        return new Object[][] {
            { new String[] {}, null },
            { new String[] { "sub-command1" }, sc1 },
            { new String[] { "sub-command2", "one", "two" }, sc2 },
            { new String[] { "sub-command2", "one", "two", "three" }, sc2 },
            { new String[] { "sub-command2", "one", "two", "three", "four" }, sc2 }, };
    }

    /**
     * Test the parseArguments method with valid args.
     *
     * @param args
     *            The command line args.
     * @param sc
     *            The expected sub-command.
     * @throws Exception
     *             If something unexpected happened.
     */
    @Test(dataProvider = "validCommandLineArgs")
    public void testParseArgumentsWithValidArgs(String[] args, SubCommand sc) throws Exception {
        parser.parseArguments(args);

        // Check the correct sub-command was parsed.
        Assert.assertEquals(parser.getSubCommand(), sc);

        // Check that the trailing arguments were retrieved correctly and
        // in the right order.
        if (args.length > 1) {
            List<String> scargs = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                scargs.add(args[i]);
            }
            Assert.assertEquals(parser.getTrailingArguments(), scargs);
        } else {
            Assert.assertTrue(parser.getTrailingArguments().isEmpty());
        }
    }

    /**
     * Provide invalid command line args.
     *
     * @return Array of invalid command line args.
     */
    @DataProvider(name = "invalidCommandLineArgs")
    public Object[][] createInvalidCommandLineArgs() {
        return new Object[][] {
            { new String[] { "sub-command1", "one" } },
            { new String[] { "sub-command1", "one", "two" } },
            { new String[] { "sub-command2" } },
            { new String[] { "sub-command2", "one" } },
            { new String[] { "sub-command2", "one", "two", "three", "four", "five" } }, };
    }

    /**
     * Test the parseArguments method with invalid args.
     *
     * @param args
     *            The command line args.
     * @throws Exception
     *             If something unexpected happened.
     */
    @Test(dataProvider = "invalidCommandLineArgs", expectedExceptions = ArgumentException.class)
    public void testParseArgumentsWithInvalidArgs(String[] args) throws Exception {
        parser.parseArguments(args);
    }

    @DataProvider
    public Object[][] indentAndWrapProvider() throws Exception {
        final String eol = System.getProperty("line.separator");
        return new Object[][] {
            { "test1",                  5, " ", " test1" + eol },
            { "test1 test2",            5, " ", " test1" + eol + " test2" + eol },
            { "test1 test2test3",       5, " ", " test1" + eol + " test2test3" + eol },
            { "test1 test2test3 test4", 5, " ", " test1" + eol + " test2test3" + eol + " test4" + eol },
        };
    }

    @Test(dataProvider = "indentAndWrapProvider")
    public void testIndentAndWrap(String text, int wrapColumn, String indent, String expected) {
        final StringBuilder buffer = new StringBuilder();
        SubCommandArgumentParser.indentAndWrap(indent, buffer, wrapColumn, LocalizableMessage.raw(text));
        Assertions.assertThat(buffer.toString()).isEqualTo(expected);
    }
}
