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
 * Copyright 2008 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.controls;



import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.opends.server.TestCaseUtils;
import org.opends.server.protocols.ldap.LDAPControl;
import org.forgerock.opendj.io.ASN1;
import org.forgerock.opendj.io.ASN1Writer;
import org.opends.server.types.*;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.ByteStringBuilder;
import org.forgerock.opendj.ldap.DN;

import static org.testng.Assert.*;

import static org.opends.server.util.ServerConstants.*;



/**
 * This class contains a number of test cases for the proxied authorization v2
 * control.
 */
public class ProxiedAuthV2ControlTestCase
    extends ControlsTestCase
{
  /**
   * Make sure that the server is running.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @BeforeClass
  public void startServer()
         throws Exception
  {
    TestCaseUtils.startServer();
  }



  /**
   * Tests the constructor with a {@code null} authorization ID.
   */
  @Test(expectedExceptions = { NullPointerException.class })
  public void testConstructorNullAuthzID()
  {
    new ProxiedAuthV2Control(null);
  }



  /**
   * Tests the constructor with an empty authorization ID.
   */
  @Test
  public void testConstructorEmptyAuthzID()
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8(""));
    assertEquals(proxyControl.getAuthorizationID(), ByteString.valueOfUtf8(""));
  }



  /**
   * Tests the constructor with a non-empty authorization ID using the "dn:"
   * form.
   */
  @Test
  public void testConstructorNonEmptyAuthzIDDN()
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:uid=test,o=test"));
    assertEquals(proxyControl.getAuthorizationID(),
                 ByteString.valueOfUtf8("dn:uid=test,o=test"));
  }



  /**
   * Tests the constructor with a non-empty authorization ID using the "u:"
   * form.
   */
  @Test
  public void testConstructorNonEmptyAuthzIDUsername()
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:test"));
    assertEquals(proxyControl.getAuthorizationID(),
                 ByteString.valueOfUtf8("u:test"));
  }



  /**
   * Tests the {@code decodeControl} method with a control that is not marked
   * critical.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlNotCritical()
         throws Exception
  {
    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, false,
                            ByteString.valueOfUtf8("u:test"));
    ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method with a control that does not have a
   * value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlNoValue()
         throws Exception
  {
    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true);
    ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method with a control encoded in the
   * standard from with the "dn:"-style value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlDNValue()
         throws Exception
  {
    ByteString authzID = ByteString.valueOfUtf8("dn:uid=test,o=test");

    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true, authzID);
    ProxiedAuthV2Control proxyControl = ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
    assertEquals(proxyControl.getAuthorizationID(), authzID);
  }



  /**
   * Tests the {@code decodeControl} method with a control encoded in the
   * standard from with the "u:"-style value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlUsernameValue()
         throws Exception
  {
    ByteString authzID = ByteString.valueOfUtf8("u:test");

    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true, authzID);
    ProxiedAuthV2Control proxyControl = ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
    assertEquals(proxyControl.getAuthorizationID(), authzID);
  }



  /**
   * Tests the {@code decodeControl} method with an invalid value (which doesn't
   * start with either "dn:" or "u:").
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlInvalidValue()
         throws Exception
  {
    ByteString authzID = ByteString.valueOfUtf8("invalid");

    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true, authzID);
    ProxiedAuthV2Control proxyControl = ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
    assertEquals(proxyControl.getAuthorizationID(), authzID);
  }



  /**
   * Tests the {@code decodeControl} method with a control encoded in the legacy
   * form (in which the value is wrapped by an extra octet string) with the
   * "dn:"-style value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlLegacyDNValue()
         throws Exception
  {
    ByteString innerValue = ByteString.valueOfUtf8("dn:uid=test,o=test");
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeOctetString(innerValue);
    ByteString outerValue = bsb.toByteString();

    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true, outerValue);
    ProxiedAuthV2Control proxyControl = ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
    assertEquals(proxyControl.getAuthorizationID(), innerValue);
  }



  /**
   * Tests the {@code decodeControl} method with a control encoded in the legacy
   * form (in which the value is wrapped by an extra octet string) with the
   * "u:"-style value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlLegacyUsernameValue()
         throws Exception
  {
    ByteString innerValue = ByteString.valueOfUtf8("u:test");
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeOctetString(innerValue);
    ByteString outerValue = bsb.toByteString();

    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V2, true, outerValue);
    ProxiedAuthV2Control proxyControl = ProxiedAuthV2Control.DECODER.decode(c.isCritical(), c.getValue());
    assertEquals(proxyControl.getAuthorizationID(), innerValue);
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an empty
   * authorization ID string.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNEmptyAuthzID()
         throws Exception
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8(""));
    assertNull(proxyControl.getAuthorizationEntry());
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID of "dn:".
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNEmptyAuthzIDDN()
         throws Exception
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:"));
    assertNull(proxyControl.getAuthorizationEntry());
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "dn:" form that points to a valid user.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNExistingUserDN()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);
    TestCaseUtils.addEntry(
      "dn: uid=test,o=test",
      "objectClass: top",
      "objectClass: person",
      "objectClass: organizationalPerson",
      "objectClass: inetOrgPerson",
      "uid: test",
      "givenName: Test",
      "sn: User",
      "cn: Test User");

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:uid=test,o=test"));
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationEntry().getName(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "dn:" form that points to a user that doesn't exist.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDNNonExistingUserDN()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:uid=test,o=test"));
    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "dn:" form that points to a valid user but whose account is
   * disabled.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDNExistingDisabledUserDN()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);
    TestCaseUtils.addEntry(
      "dn: uid=test,o=test",
      "objectClass: top",
      "objectClass: person",
      "objectClass: organizationalPerson",
      "objectClass: inetOrgPerson",
      "uid: test",
      "givenName: Test",
      "sn: User",
      "cn: Test User",
      "ds-pwp-account-disabled: true");

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:uid=test,o=test"));
    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID of "u:".
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNEmptyAuthzIDUsername()
         throws Exception
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:"));
    assertNull(proxyControl.getAuthorizationEntry());
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "u:" form that points to a valid user.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNExistingUserUsername()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);
    TestCaseUtils.addEntry(
      "dn: uid=test,o=test",
      "objectClass: top",
      "objectClass: person",
      "objectClass: organizationalPerson",
      "objectClass: inetOrgPerson",
      "uid: test",
      "givenName: Test",
      "sn: User",
      "cn: Test User");

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:test"));
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationEntry().getName(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "u:" form that points to a user that doesn't exist.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDNNonExistingUserUsername()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:test"));
    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an authorization
   * ID in the "u:" form that points to a valid user but whose account is
   * disabled.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDNExistingDisabledUserUsername()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);
    TestCaseUtils.addEntry(
      "dn: uid=test,o=test",
      "objectClass: top",
      "objectClass: person",
      "objectClass: organizationalPerson",
      "objectClass: inetOrgPerson",
      "uid: test",
      "givenName: Test",
      "sn: User",
      "cn: Test User",
      "ds-pwp-account-disabled: true");

    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:test"));
    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method with an invalid
   * authorization ID.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDNInvalidAuthzID()
         throws Exception
  {
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("invalid"));
    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code toString} methods.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testToString()
         throws Exception
  {
    // The default toString() calls the version that takes a string builder
    // argument, so we only need to use the default version to cover both cases.
    ProxiedAuthV2Control proxyControl =
         new ProxiedAuthV2Control(ByteString.valueOfUtf8("dn:uid=test,o=test"));
    proxyControl.toString();

    proxyControl = new ProxiedAuthV2Control(ByteString.valueOfUtf8("u:test"));
    proxyControl.toString();
  }
}

