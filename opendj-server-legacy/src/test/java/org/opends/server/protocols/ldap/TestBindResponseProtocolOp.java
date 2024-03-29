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
 * Copyright 2006-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.protocols.ldap;

import java.util.ArrayList;

import java.util.List;
import org.opends.server.TestCaseUtils;
import org.forgerock.i18n.LocalizableMessage;
import static org.opends.server.protocols.ldap.LDAPConstants.*;
import org.forgerock.opendj.io.ASN1Writer;
import org.forgerock.opendj.io.ASN1;
import org.forgerock.opendj.io.ASN1Reader;
import org.opends.server.types.*;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.ByteStringBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@SuppressWarnings("javadoc")
public class TestBindResponseProtocolOp  extends LdapTestCase {

    private static LocalizableMessage message = LocalizableMessage.raw("This is a message");
    ResultCode        okCode          = ResultCode.SUCCESS;
    ResultCode  busyCode = ResultCode.BUSY;
    ResultCode invalidSyntaxCode = ResultCode.INVALID_ATTRIBUTE_SYNTAX;
    private static String dn = "cn=dn, dc=example,dc=com";
    private static String saslCreds = "sasl credentials";
    private static String url = "ldap://somewhere.example.com";

    /**
     * Once-only initialization.
     *
     * @throws Exception
     *           If an unexpected error occurred.
     */
    @BeforeClass
    public void setUp() throws Exception {
      // This test suite depends on having the schema available, so we'll
      // start the server.
      TestCaseUtils.startServer();
    }


    @Test
    public void testBindRequestToString() throws Exception
    {
        List<String> referralURLs = new ArrayList<>();
        referralURLs.add(url);
        DN responseDn = DN.valueOf(dn);
        ByteString serverSASLCredentials =
            ByteString.valueOfUtf8(saslCreds);
        BindResponseProtocolOp r =
            new BindResponseProtocolOp(okCode.intValue(),
                    message, responseDn, referralURLs,
                    serverSASLCredentials);
        toString(r);
    }

    @Test (expectedExceptions = LDAPException.class)
    public void testBindResponseTooFew() throws Exception {
      ByteStringBuilder bsb = new ByteStringBuilder();
      ASN1Writer writer = ASN1.getWriter(bsb);
      writer.writeStartSequence(OP_TYPE_BIND_RESPONSE);
      writer.writeOctetString((String)null);
      writer.writeOctetString((String)null);
      writer.writeEndSequence();

      ASN1Reader reader = ASN1.getReader(bsb.toByteString());
      LDAPReader.readProtocolOp(reader);
    }

  /**
   * Test to ensure trailing unrecognized components are ignored.
   */
    @Test
    public void testBindResponseTooMany() throws Exception {
      DN responseDn = DN.valueOf(dn);

      ByteStringBuilder bsb = new ByteStringBuilder();
      ASN1Writer writer = ASN1.getWriter(bsb);
      writer.writeStartSequence(OP_TYPE_BIND_RESPONSE);
      writer.writeInteger(okCode.intValue());
      writer.writeOctetString(responseDn.toString());
      writer.writeOctetString(message.toString());
      writer.writeBoolean(true);
      writer.writeEndSequence();

      ASN1Reader reader = ASN1.getReader(bsb.toByteString());
      ProtocolOp protocolOp = LDAPReader.readProtocolOp(reader);

      assertTrue(protocolOp instanceof BindResponseProtocolOp);
      BindResponseProtocolOp bindResponse = (BindResponseProtocolOp)protocolOp;
      assertEquals(bindResponse.getResultCode(), okCode.intValue());
      TestCaseUtils.assertObjectEquals(bindResponse.getMatchedDN(), responseDn);
      assertEquals(bindResponse.getErrorMessage().toString(), message.toString());
      assertNull(bindResponse.getReferralURLs());
      assertNull(bindResponse.getServerSASLCredentials());
    }

    @Test (expectedExceptions = LDAPException.class)
    public void testBindResponseBadResult() throws Exception {
      ByteStringBuilder bsb = new ByteStringBuilder();
      ASN1Writer writer = ASN1.getWriter(bsb);
      writer.writeStartSequence(OP_TYPE_BIND_RESPONSE);
      writer.writeOctetString("invalid element");
      writer.writeOctetString((String)null);
      writer.writeOctetString((String)null);
      writer.writeEndSequence();

      ASN1Reader reader = ASN1.getReader(bsb.toByteString());
      LDAPReader.readProtocolOp(reader);
    }

  /**
   * Test to ensure trailing unrecognized components are ignored
   * without generating an error.
   */
    @Test
    public void testBindResponseBadReferral() throws Exception {
      DN responseDn = DN.valueOf(dn);
      ByteString serverSASLCredentials =
          ByteString.valueOfUtf8(saslCreds);

      ByteStringBuilder bsb = new ByteStringBuilder();
      ASN1Writer writer = ASN1.getWriter(bsb);
      writer.writeStartSequence(OP_TYPE_BIND_RESPONSE);
      writer.writeInteger(okCode.intValue());
      writer.writeOctetString(responseDn.toString());
      writer.writeOctetString(message.toString());
      writer.writeInteger(Long.MAX_VALUE);
      writer.writeOctetString(TYPE_SERVER_SASL_CREDENTIALS,
          serverSASLCredentials);
      writer.writeEndSequence();

      ASN1Reader reader = ASN1.getReader(bsb.toByteString());
      ProtocolOp protocolOp = LDAPReader.readProtocolOp(reader);

      assertTrue(protocolOp instanceof BindResponseProtocolOp);
      BindResponseProtocolOp bindResponse = (BindResponseProtocolOp)protocolOp;
      assertEquals(bindResponse.getResultCode(), okCode.intValue());
      TestCaseUtils.assertObjectEquals(bindResponse.getMatchedDN(), responseDn);
      assertEquals(bindResponse.getErrorMessage().toString(), message.toString());
      assertNull(bindResponse.getReferralURLs());
      assertNull(bindResponse.getServerSASLCredentials());
    }

    @Test
    public void testBindResponseEncodeDecode() throws Exception {
        List<String> referralURLs=new ArrayList<>();
        referralURLs.add(url);
        DN responseDn = DN.valueOf(dn);
        ByteString serverSASLCredentials =
            ByteString.valueOfUtf8(saslCreds);

        BindResponseProtocolOp saslOkResp =
            new BindResponseProtocolOp(okCode.intValue(),
                    message, responseDn, referralURLs,
                    serverSASLCredentials);
        BindResponseProtocolOp busyResp =
            new BindResponseProtocolOp(busyCode.intValue());
        BindResponseProtocolOp invalidSyntaxResp =
            new BindResponseProtocolOp(invalidSyntaxCode.intValue(), message);

        ByteStringBuilder saslOkBuilder = new ByteStringBuilder();
        ASN1Writer saslOkWriter = ASN1.getWriter(saslOkBuilder);
        ByteStringBuilder busyBuilder = new ByteStringBuilder();
        ASN1Writer busyWriter = ASN1.getWriter(busyBuilder);
        ByteStringBuilder invalidSyntaxBuilder = new ByteStringBuilder();
        ASN1Writer invalidSyntaxWriter = ASN1.getWriter(invalidSyntaxBuilder);

        saslOkResp.write(saslOkWriter);
        busyResp.write(busyWriter);
        invalidSyntaxResp.write(invalidSyntaxWriter);

        ASN1Reader saslOkReader = ASN1.getReader(saslOkBuilder.toByteString());
        ASN1Reader busyReader = ASN1.getReader(busyBuilder.toByteString());
        ASN1Reader invalidSyntaxReader = ASN1.getReader(invalidSyntaxBuilder.toByteString());
        ProtocolOp saslOkDec= LDAPReader.readProtocolOp(saslOkReader);
        ProtocolOp busyDec = LDAPReader.readProtocolOp(busyReader);
        ProtocolOp invalidSyntaxDec = LDAPReader.readProtocolOp(invalidSyntaxReader);

        assertTrue(saslOkDec instanceof BindResponseProtocolOp);
        assertTrue(busyDec instanceof BindResponseProtocolOp);
        assertTrue(invalidSyntaxDec instanceof BindResponseProtocolOp);

        BindResponseProtocolOp saslOkOp =
             (BindResponseProtocolOp)saslOkDec;
        BindResponseProtocolOp busyOp =
             (BindResponseProtocolOp)busyDec;
        BindResponseProtocolOp invalidSyntaxOp =
             (BindResponseProtocolOp)invalidSyntaxDec;

        assertSame(saslOkOp.getProtocolOpName(), saslOkResp.getProtocolOpName());
        assertSame(busyOp.getProtocolOpName(), busyResp.getProtocolOpName());
        assertSame(invalidSyntaxOp.getProtocolOpName(), invalidSyntaxResp.getProtocolOpName());

        assertEquals(saslOkOp.getType(), saslOkResp.getType());
        assertEquals(busyOp.getType(), busyResp.getType());
        assertEquals(invalidSyntaxOp.getType(), invalidSyntaxResp.getType());

        assertEquals(saslOkOp.getResultCode(), saslOkResp.getResultCode());
        assertEquals(busyOp.getResultCode(), busyResp.getResultCode());
        assertEquals(invalidSyntaxOp.getResultCode(), invalidSyntaxResp.getResultCode());

        assertEquals(saslOkOp.getErrorMessage(), saslOkResp.getErrorMessage());
        assertEquals(invalidSyntaxOp.getErrorMessage(), invalidSyntaxResp.getErrorMessage());

        String str1=saslOkOp.getServerSASLCredentials().toString();
        String str2=saslOkResp.getServerSASLCredentials().toString();
        assertEquals(str1, str2);
        List<String> list1 = saslOkOp.getReferralURLs();
        List<String> list2 = saslOkResp.getReferralURLs();
        assertTrue(list1.equals(list2));
        DN dn1=saslOkOp.getMatchedDN();
        DN dn2=saslOkResp.getMatchedDN();
        TestCaseUtils.assertObjectEquals(dn1, dn2);
    }
}
