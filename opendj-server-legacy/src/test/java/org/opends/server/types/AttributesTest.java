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
 * Portions Copyright 2014-2015 ForgeRock AS.
 */
package org.opends.server.types;

import java.util.LinkedList;
import java.util.List;

import org.forgerock.opendj.ldap.ByteString;
import org.opends.server.TestCaseUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * This class defines a set of tests for the
 * org.opends.server.core.Attributes class.
 * <p>
 * Note that the <code>empty</code> and <code>create</code> methods
 * are all tested in the {@link AttributeBuilderTest} suit.
 */
public class AttributesTest extends TypesTestCase
{

  /**
   * Merge attribute data provider.
   *
   * @return The array of merge attributes.
   */
  @DataProvider(name = "mergeAttributes")
  public Object[][] mergeAttributes()
  {
    // Test testCase #, Attribute a1, Attribute a2, Attribute e,
    // Attribute d

    return new Object[][]
    {
        {
            1,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "one", "two", "three")
        },
        {
            2,
            Attributes.empty("cn"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn")
        },
        {
            3,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn")
        },
        {
            4,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "two", "three", "four"),
            Attributes.create("cn", "one", "two", "three", "four"),
            Attributes.create("cn", "two", "three")
        },
        {
            5,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "four", "five", "six"),
            Attributes.create("cn", "one", "two", "three", "four", "five",
                "six"),
            Attributes.empty("cn")
        },
    };
  }



  /**
   * Set up the environment for performing the tests in this suite.
   *
   * @throws Exception
   *           If the environment could not be set up.
   */
  @BeforeClass
  public void setUp() throws Exception
  {
    // This test suite depends on having the schema available, so
    // we'll start the server.
    TestCaseUtils.startServer();
  }



  /**
   * Subtract attribute data provider.
   *
   * @return The array of subtract attributes.
   */
  @DataProvider(name = "subtractAttributes")
  public Object[][] subtractAttributes()
  {
    // Test testCase #, Attribute a1, Attribute a2, Attribute e,
    // Attribute m

    return new Object[][]
    {
        {
            1,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn"),
            Attributes.empty("cn")
        },
        {
            2,
            Attributes.empty("cn"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn"),
            Attributes.create("cn", "one", "two", "three")
        },
        {
            3,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.empty("cn")
        },
        {
            4,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "two", "three", "four"),
            Attributes.create("cn", "one"),
            Attributes.create("cn", "four")
        },
        {
            5,
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "four", "five", "six"),
            Attributes.create("cn", "one", "two", "three"),
            Attributes.create("cn", "four", "five", "six")
        },
    };
  }



  /**
   * Tests {@link Attributes#merge(Attribute, Attribute)}.
   *
   * @param testCase
   *          Test case ID.
   * @param a1
   *          The first attribute to merge.
   * @param a2
   *          The second attribute to merge.
   * @param e
   *          The expected result of the merge.
   * @param d
   *          The expected set of duplicate values.
   * @throws Exception
   *           If an unexpected error occurs.
   */
  @Test(dataProvider = "mergeAttributes")
  public void testMerge(int testCase, Attribute a1, Attribute a2, Attribute e,
      Attribute d) throws Exception
  {
    Attribute actual = Attributes.merge(a1, a2);
    TestCaseUtils.assertObjectEquals(actual, e);
  }



  /**
   * Tests
   * {@link Attributes#merge(Attribute, Attribute, java.util.Collection)}
   * .
   *
   * @param testCase
   *          Test case ID.
   * @param a1
   *          The first attribute to merge.
   * @param a2
   *          The second attribute to merge.
   * @param e
   *          The expected result of the merge.
   * @param d
   *          The expected set of duplicate values.
   * @throws Exception
   *           If an unexpected error occurs.
   */
  @Test(dataProvider = "mergeAttributes")
  public void testMergeWithDuplicates(int testCase, Attribute a1, Attribute a2,
      Attribute e, Attribute d) throws Exception
  {
    List<ByteString> duplicates = new LinkedList<>();
    Attribute actual = Attributes.merge(a1, a2, duplicates);
    TestCaseUtils.assertObjectEquals(actual, e);

    Assert.assertEquals(duplicates.size(), d.size());
    Assert.assertTrue(d.containsAll(duplicates));
  }



  /**
   * Tests {@link Attributes#subtract(Attribute, Attribute)}.
   *
   * @param testCase
   *          Test case ID.
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute to be subtracted.
   * @param e
   *          The expected result of the subtraction.
   * @param m
   *          The expected set of missing values.
   * @throws Exception
   *           If an unexpected error occurs.
   */
  @Test(dataProvider = "subtractAttributes")
  public void testSubtract(int testCase, Attribute a1, Attribute a2,
      Attribute e, Attribute m) throws Exception
  {
    Attribute actual = Attributes.subtract(a1, a2);
    TestCaseUtils.assertObjectEquals(actual, e);
  }



  /**
   * Tests
   * {@link Attributes#subtract(Attribute, Attribute, java.util.Collection)}
   * .
   *
   * @param testCase
   *          Test case ID.
   * @param a1
   *          The first attribute.
   * @param a2
   *          The second attribute to be subtracted.
   * @param e
   *          The expected result of the subtraction.
   * @param m
   *          The expected set of missing values.
   * @throws Exception
   *           If an unexpected error occurs.
   */
  @Test(dataProvider = "subtractAttributes")
  public void testSubtractWithMissingValues(int testCase, Attribute a1,
      Attribute a2, Attribute e, Attribute m) throws Exception
  {
    List<ByteString> missingValues = new LinkedList<>();
    Attribute actual = Attributes.subtract(a1, a2, missingValues);
    TestCaseUtils.assertObjectEquals(actual, e);

    Assert.assertEquals(missingValues.size(), m.size());
    Assert.assertTrue(m.containsAll(missingValues));
  }
}
