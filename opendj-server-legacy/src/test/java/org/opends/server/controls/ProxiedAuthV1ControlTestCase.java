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
 * Copyright 2008-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.controls;



import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.opends.server.TestCaseUtils;
import org.opends.server.protocols.ldap.LDAPControl;
import org.forgerock.opendj.io.ASN1Writer;
import org.forgerock.opendj.io.ASN1;
import org.opends.server.types.*;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.ByteStringBuilder;
import org.forgerock.opendj.ldap.DN;

import static org.testng.Assert.*;

import static org.opends.server.util.ServerConstants.*;



/**
 * This class contains a number of test cases for the proxied authorization v1
 * control.
 */
public class ProxiedAuthV1ControlTestCase
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
   * Tests the first constructor, which creates an instance of the control using
   * a raw, unprocessed DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testConstructor1()
         throws Exception
  {
    // Try a DN of "null", which is not valid and will fail on the attempt to
    // create the control
    ProxiedAuthV1Control proxyControl;
    try
    {
      proxyControl = new ProxiedAuthV1Control((ByteString) null);
      throw new AssertionError("Expected a failure when creating a proxied " +
                               "auth V1 control with a null octet string.");
    } catch (Throwable t) {}


    // Try an empty DN, which is acceptable.
    proxyControl = new ProxiedAuthV1Control(ByteString.valueOfUtf8(""));
    assertEquals(proxyControl.getOID(), OID_PROXIED_AUTH_V1);
    assertTrue(proxyControl.isCritical());
    assertTrue(proxyControl.getAuthorizationDN().isRootDN());


    // Try a valid DN, which is acceptable.
    proxyControl =
         new ProxiedAuthV1Control(ByteString.valueOfUtf8("uid=test,o=test"));
    assertEquals(proxyControl.getOID(), OID_PROXIED_AUTH_V1);
    assertTrue(proxyControl.isCritical());
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationDN(), DN.valueOf("uid=test,o=test"));


    // Try an invalid DN, which will be initally accepted but will fail when
    // attempting to get the authorization DN.
    proxyControl = new ProxiedAuthV1Control(ByteString.valueOfUtf8("invalid"));
    assertEquals(proxyControl.getOID(), OID_PROXIED_AUTH_V1);
    assertTrue(proxyControl.isCritical());
    try
    {
      proxyControl.getAuthorizationDN();
      throw new AssertionError("Expected a failure when creating a proxied " +
                               "auth V1 control with an invalid DN string.");
    } catch (Exception e) {}
  }



  /**
   * Tests the second constructor, which creates an instance of the control
   * using a processed DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testConstructor2()
         throws Exception
  {
    // Try a DN of "null", which is not valid and will fail on the attempt to
    // create the control
    ProxiedAuthV1Control proxyControl;
    try
    {
      proxyControl = new ProxiedAuthV1Control((DN) null);
      throw new AssertionError("Expected a failure when creating a proxied " +
                               "auth V1 control with a null octet string.");
    } catch (Throwable t) {}


    // Try an empty DN, which is acceptable.
    proxyControl = new ProxiedAuthV1Control(DN.rootDN());
    assertEquals(proxyControl.getOID(), OID_PROXIED_AUTH_V1);
    assertTrue(proxyControl.isCritical());
    assertTrue(proxyControl.getAuthorizationDN().isRootDN());


    // Try a valid DN, which is acceptable.
    proxyControl =
         new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));
    assertEquals(proxyControl.getOID(), OID_PROXIED_AUTH_V1);
    assertTrue(proxyControl.isCritical());
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationDN(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code decodeControl} method when the provided control has a
   * criticality of "false".
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlNotCritical()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeOctetString("uid=test,o=test");
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, false, bsb.toByteString());

    ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method when the provided control does not
   * have a value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlNoValue()
         throws Exception
  {
    LDAPControl c = new LDAPControl(OID_PROXIED_AUTH_V1, true);

    ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is not a
   * sequence.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlValueNotSequence()
         throws Exception
  {
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true,
            ByteString.valueOfUtf8("uid=test,o=test"));

    ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is a sequence
   * with zero elements.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlValueEmptySequence()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true, bsb.toByteString());

    ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is a sequence
   * with multiple elements.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlValueMultiElementSequence()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeOctetString("uid=element1,o=test");
    writer.writeOctetString("uid=element2,o=test");
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true, bsb.toByteString());

    assertEquals(ByteString.valueOfUtf8("uid=element1,o=test"),
        ProxiedAuthV1Control.DECODER.decode(c.isCritical(),
            c.getValue()).getRawAuthorizationDN());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is a valid
   * octet string that contains an invalid DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testDecodeControlValueInvalidDN()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeOctetString("invaliddn");
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true, bsb.toByteString());

    ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is a valid
   * octet string that contains an valid empty DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlValueEmptyDN()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeOctetString("");
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true, bsb.toByteString());

    ProxiedAuthV1Control proxyControl = ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
    assertTrue(proxyControl.getAuthorizationDN().isRootDN());
  }



  /**
   * Tests the {@code decodeControl} method when the control value is a valid
   * octet string that contains an valid non-empty DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testDecodeControlValueNonEmptyDN()
         throws Exception
  {
    ByteStringBuilder bsb = new ByteStringBuilder();
    ASN1Writer writer = ASN1.getWriter(bsb);
    writer.writeStartSequence();
    writer.writeOctetString("uid=test,o=test");
    writer.writeEndSequence();
    LDAPControl c =
        new LDAPControl(OID_PROXIED_AUTH_V1, true, bsb.toByteString());

    ProxiedAuthV1Control proxyControl = ProxiedAuthV1Control.DECODER.decode(c.isCritical(), c.getValue());
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationDN(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code getRawAuthorizationDN} and {@code setRawAuthorizationDN}
   * methods.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetAndSetRawAuthorizationDN()
         throws Exception
  {
    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(ByteString.valueOfUtf8(""));
    assertEquals(proxyControl.getRawAuthorizationDN(), ByteString.valueOfUtf8(""));

    proxyControl =
         new ProxiedAuthV1Control(ByteString.valueOfUtf8("uid=test,o=test"));
    assertEquals(proxyControl.getRawAuthorizationDN(),
        ByteString.valueOfUtf8("uid=test,o=test"));
  }



  /**
   * Tests the {@code getAuthorizationDN} and {@code setRawAuthorizationDN}
   * methods.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetAndSetAuthorizationDN()
         throws Exception
  {
    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(DN.rootDN());
    assertEquals(proxyControl.getRawAuthorizationDN(), ByteString.valueOfUtf8(""));
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationDN(), DN.rootDN());

    proxyControl =
         new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));
    assertEquals(proxyControl.getRawAuthorizationDN(),
                 ByteString.valueOfUtf8("uid=test,o=test"));
    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationDN(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method for the null DN.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationDNNullDN()
         throws Exception
  {
    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(DN.rootDN());

    assertNull(proxyControl.getAuthorizationEntry());
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method for a normal user
   * that exists in the directory data and doesn't have any restrictions on its
   * use.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test
  public void testGetValidatedAuthorizationExistingNormalUser()
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

    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));

    TestCaseUtils.assertObjectEquals(proxyControl.getAuthorizationEntry().getName(), DN.valueOf("uid=test,o=test"));
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method for a user that
   * doesn't exist in the directory data.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationNonExistingNormalUser()
         throws Exception
  {
    TestCaseUtils.initializeTestBackend(true);

    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));

    proxyControl.getAuthorizationEntry();
  }



  /**
   * Tests the {@code getValidatedAuthorizationDN} method for a disabled user.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { DirectoryException.class })
  public void testGetValidatedAuthorizationDisabledUser()
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

    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));

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
    ProxiedAuthV1Control proxyControl =
         new ProxiedAuthV1Control(ByteString.valueOfUtf8("uid=test,o=test"));
    proxyControl.toString();

    proxyControl = new ProxiedAuthV1Control(DN.valueOf("uid=test,o=test"));
    proxyControl.toString();
  }
}

