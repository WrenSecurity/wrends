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
 * Copyright 2006-2008 Sun Microsystems, Inc.
 * Portions Copyright 2006 Brighton Consulting, Inc.
 * Portions Copyright 2013-2016 ForgeRock AS.
 */
package org.opends.server.tools.makeldif;

import static org.assertj.core.api.Assertions.*;
import static org.opends.messages.ToolMessages.*;
import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.forgerock.i18n.LocalizableMessage;
import org.opends.server.TestCaseUtils;
import org.opends.server.core.DirectoryServer;
import org.opends.server.tasks.LdifFileWriter;
import org.opends.server.tools.ToolsTestCase;
import org.opends.server.types.Attribute;
import org.opends.server.types.Attributes;
import org.opends.server.types.Entry;
import org.opends.server.types.InitializationException;
import org.opends.server.types.LDIFImportConfig;
import org.opends.server.util.LDIFException;
import org.opends.server.util.LDIFReader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** A set of test cases for the MakeLDIF tool. */
@SuppressWarnings("javadoc")
public class MakeLDIFTestCase extends ToolsTestCase
{
  private String resourcePath;

  @BeforeClass
  public void setUp() throws Exception
  {
    // The server must be running for these tests, so that
    // it can provide "getServerRoot()".
    TestCaseUtils.startServer();

    resourcePath = DirectoryServer.getInstanceRoot() + File.separator +
         "config" + File.separator + "MakeLDIF";
  }

  /**
   * Test to show that reporting an error about an
   * uninitialized variable when generating templates reports the
   * correct line.
   */
  @Test
  public void testParseTemplate() throws Exception
  {
    String[] lines =
    {
      /* 0 */ "template: template",
      /* 1 */ "a: {missingVar}",
      /* 2 */ "a: b",
      /* 3 */ "a: c",
      /* 4 */ "",
      /* 5 */ "template: template2",
    };

    // Test must show "missingVar" missing on line 1.
    // Previous behaviour showed "missingVar" on line 5.

    TemplateFile templateFile = new TemplateFile(resourcePath, new Random());
    List<LocalizableMessage> warns = new ArrayList<>();

    try
    {
      templateFile.parse(lines, warns);
    }
    catch (InitializationException e)
    {
      String msg = e.getMessage();
      LocalizableMessage msg_locale = ERR_MAKELDIF_TAG_UNDEFINED_ATTRIBUTE.get("missingVar",1);
      assertEquals(msg, msg_locale.toString(), msg);
    }
  }

  @DataProvider (name="validTemplates")
  public Object[][] createTestTemplates() {
    return new Object[][] {
        { "CurlyBracket",
          new String[] {
            "template: templateWithEscape",
            "rdnAttr: uid",
            "uid: testEntry",
            "cn: I\\{Foo\\}F"} },
        { "AngleBracket",
          new String[] {
            "template: templateWithEscape",
            "rdnAttr: uid",
            "uid: testEntry",
            "sn: \\<Bar\\>"} },
        { "SquareBracket",
            new String[] {
                "template: templateWithEscape",
                "rdnAttr: uid",
                "uid: testEntry",
                "description: \\[TEST\\]"} },
        { "BackSlash",
            new String[] {
                "template: templateWithEscape",
                "rdnAttr: uid",
                "uid: testEntry",
                "description: Foo \\\\ Bar"} },
        { "EscapedAlpha",
            new String[] {
                "template: templateWithEscape",
                "rdnAttr: uid",
                "uid: testEntry",
                "description: Foo \\\\Bar"} },
        { "Normal Variable",
            new String[] {
                "template: templateNormal",
                "rdnAttr: uid",
                "uid: testEntry",
                "sn: {uid}"} },
        { "Constant",
            new String[] {
                "define foo=Test123",
                "",
                "template: templateConstant",
                "rdnAttr: uid",
                "uid: testEntry",
                "sn: {uid}",
                "cn: [foo]"} },
    };
  }

  /** Test for parsing escaped character in templates. */
  @Test(dataProvider = "validTemplates")
  public void testParsingEscapeCharInTemplate(String testName, String[] lines)
      throws Exception
  {
    TemplateFile templateFile = new TemplateFile(resourcePath, new Random());
    List<LocalizableMessage> warns = new ArrayList<>();
    templateFile.parse(lines, warns);
    assertTrue(warns.isEmpty(),"Warnings in parsing test template " + testName );
  }

  @DataProvider (name="templatesToTestLDIFOutput")
  public Object[][] createTemplatesToTestLDIFOutput() {
    return new Object[][]{
        {
            "Curly",
            new String[]{
                "branch: dc=test",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "cn: I\\{ Foo \\}F"},
            "cn", // Attribute to test
            "I{ Foo }F", // Expected value
        },
        {
            "Angle",
            new String[]{
                "branch: dc=test",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "sn: \\< Bar \\>"},
            "sn", // Attribute to test
            "< Bar >", // Expected value
        },
        {
            "Square",
            new String[]{
                "branch: dc=test",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "description: \\[TEST\\]"},
            "description", // Attribute to test
            "[TEST]", // Expected value
        },
        {
            "BackSlash",
            new String[]{
                "branch: dc=test",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "displayName: Foo \\\\ Bar"},
            "displayname", // Attribute to test
            "Foo \\ Bar", // Expected value
        },
        {
            "MultipleSquare",
            new String[]{
                "define top=dc=com",
                "define container=ou=group",
                "",
                "branch: dc=test,[top]",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "manager: cn=Bar,[container],dc=test,[top]",
                ""},
            "manager", // Attribute to test
            "cn=Bar,ou=group,dc=test,dc=com", // Expected value
        },
        {
            "MixedSquare",
            new String[]{
                "define top=dc=com",
                "define container=ou=group",
                "",
                "branch: dc=test,[top]",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "description: test [container] \\[[top]\\]",
                "",},
            "description", // Attribute to test
            "test ou=group [dc=com]", // Expected value
        },
        {
            "NoConstantBecauseEscaped",
            new String[]{
                "define top=dc=com",
                "define container=ou=group",
                "",
                "branch: dc=test,[top]",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "description: test \\[top]",
                "",},
            "description", // Attribute to test
            "test [top]", // Expected value
        },
       {
            "NoConstantBecauseStrangeChar",
            new String[]{
                "define top=dc=com",
                "define container=ou=group",
                "",
                "branch: dc=test,[top]",
                "subordinateTemplate: templateWithEscape:1",
                "",
                "template: templateWithEscape",
                "rdnAttr: uid",
                "objectclass: inetOrgPerson",
                "uid: testEntry",
                "description: test [group \\[top]",
                "",},
            "description", // Attribute to test
            "test [group [top]", // Expected value
        },
        /* If adding a test, please copy and reuse template code down below
        {
            "",
            new String[]{
                "template: templateWithEscape",
                "rdnAttr: uid",
                "uid: testEntry",
                "cn: I\\{Foo\\}F"},
            "", // Attribute to test
            "", // Expected value
        }
        */
    };
  }

  /** Test for escaped characters in templates, check LDIF output. */
  @Test(dataProvider = "templatesToTestLDIFOutput", dependsOnMethods = "testParsingEscapeCharInTemplate")
  public void testLDIFOutputFromTemplate(String testName, String[] lines,
                                         String attrName, String expectedValue) throws Exception
  {
    File tmpFile = File.createTempFile(testName, "out.ldif");
    tmpFile.deleteOnExit();
    String outLdifFilePath = tmpFile.getAbsolutePath();

    LdifFileWriter.makeLdif(outLdifFilePath, resourcePath, lines);

    Entry e = readEntry(outLdifFilePath);
    assertNotNull(e);
    Iterable<Attribute> attrs = e.getAllAttributes(attrName);
    assertThat(attrs).isNotEmpty();
    Attribute a = attrs.iterator().next();
    Attribute expectedRes = Attributes.create(attrName, expectedValue);
    TestCaseUtils.assertObjectEquals(a, expectedRes);
  }

  private Entry readEntry(String outLdifFilePath) throws IOException, LDIFException
  {
    LDIFImportConfig ldifConfig = new LDIFImportConfig(outLdifFilePath);
    ldifConfig.setValidateSchema(false);
    try (LDIFReader reader = new LDIFReader(ldifConfig))
    {
      assertNotNull(reader.readEntry());
      return reader.readEntry();
    }
  }

  /**
   * Test for escaped characters in templates, check LDIF output when
   * the templates combines escaped characters and variables.
   */
  @Test(dependsOnMethods = "testParsingEscapeCharInTemplate")
  public void testOutputCombineEscapeCharInTemplate() throws Exception
  {
    String[] lines =
        {
            "branch: dc=test",
            "subordinateTemplate: templateWithEscape:1",
            "",
            "template: templateWithEscape",
            "rdnAttr: uid",
            "objectclass: inetOrgPerson",
            "uid: testEntry",
            "sn: Bar",
            // The value below combines variable, randoms and escaped chars.
            // The resulting value is "Foo <?>{1}Bar" where ? is a letter from [A-Z].
            "cn: Foo \\<<random:chars:ABCDEFGHIJKLMNOPQRSTUVWXYZ:1>\\>\\{1\\}{sn}",
            "",
        };

    File tmpFile = File.createTempFile("combineEscapeChar", "out.ldif");
    tmpFile.deleteOnExit();
    String outLdifFilePath = tmpFile.getAbsolutePath();

    LdifFileWriter.makeLdif(outLdifFilePath, resourcePath, lines);

    Entry e = readEntry(outLdifFilePath);
    assertNotNull(e);
    Iterable<Attribute> attrs = e.getAllAttributes("cn");
    assertThat(attrs).isNotEmpty();
    Attribute a = attrs.iterator().next();
    assertTrue(a.iterator().next().toString().matches("Foo <[A-Z]>\\{1\\}Bar"),
        "cn value doesn't match the expected value");
  }
}
