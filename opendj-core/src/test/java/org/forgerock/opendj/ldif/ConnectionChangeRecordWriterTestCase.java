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
 * Copyright 2009-2010 Sun Microsystems, Inc.
 * Portions Copyright 2012-2016 ForgeRock AS.
 */

package org.forgerock.opendj.ldif;

import static org.forgerock.opendj.ldap.LdapException.newLdapException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.forgerock.i18n.LocalizedIllegalArgumentException;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldap.requests.AddRequest;
import org.forgerock.opendj.ldap.requests.DeleteRequest;
import org.forgerock.opendj.ldap.requests.ModifyDNRequest;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * This class tests the ConnectionChangeRecordWriter functionality.
 */
@SuppressWarnings("javadoc")
public class ConnectionChangeRecordWriterTestCase extends AbstractLDIFTestCase {

    /**
     * Provide a standard LDIF Change Record, valid, for tests below.
     *
     * @return a string containing a standard LDIF Change Record.
     */
    public final String[] getStandardLDIFChangeRecord() {
        // @formatter:off
        return new String[] {
            "version: 1",
            "dn: uid=scarter,ou=People,dc=example,dc=com",
            "changetype: add",
            "sn: Carter",
            "cn: Samnatha Carter",
            "givenName: Sam",
            "objectClass: inetOrgPerson",
            "telephoneNumber: 555 555-5555",
            "mail: scarter@mail.org",
            "entryDN: uid=scarter,ou=people,dc=example,dc=org",
            "entryUUID: ad55a34a-763f-358f-93f9-da86f9ecd9e4",
            "modifyTimestamp: 20120903142126Z",
            "modifiersName: cn=Internal Client,cn=Root DNs,cn=config"
        };
        // @formatter:on
    }

    /**
     * Write a Change Record - AddRequest.
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordAddRequest() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord(Requests.newAddRequest(getStandardLDIFChangeRecord()));
            verify(connection, times(1)).add(any(AddRequest.class));
        }
    }

    /**
     * The writeChangeRecord (AddRequest) doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteChangeRecordAddRequestDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord((AddRequest) null);
        }
    }

    /**
     * ConnectionChangeRecordWriter write a change record (in this example the
     * ChangeRecord is an AddRequest).
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordContainingAddRequest() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord(Requests.newChangeRecord(getStandardLDIFChangeRecord()));
            Assert.assertTrue(Requests.newChangeRecord(getStandardLDIFChangeRecord()) instanceof AddRequest);
            verify(connection, times(1)).add(any(AddRequest.class));
        }
    }

    /**
     * ConnectionChangeRecordWriter write a change record (in this example the
     * ChangeRecord is a DeleteRequest).
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordContainingDeleteRequest() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            // @formatter:off
            ChangeRecord cr = Requests.newChangeRecord(
                "dn: dc=example,dc=com",
                "changetype: delete"
            );
            writer.writeChangeRecord(cr);
            // @formatter:on
            Assert.assertTrue(cr instanceof DeleteRequest);
            verify(connection, times(1)).delete(any(DeleteRequest.class));
        }
    }

    /**
     * The writer allow only one LDIF ChangeRecord. Exception expected.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = LocalizedIllegalArgumentException.class)
    public final void testWriteChangeRecordDoesntAllowMultipleLDIF() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            // @formatter:off
            writer.writeChangeRecord(Requests.newChangeRecord(
                "dn: uid=scarter,ou=People,dc=example,dc=com",
                "changetype: modify",
                "replace:sn",
                "sn: scarter",
                "",
                "dn: uid=user.0,ou=People,dc=example,dc=com",
                "changetype: modify",
                "replace:sn",
                "sn: Amarr")
            );
            // @formatter:on
        }
    }

    /**
     * ConnectionChangeRecordWriter writes a ChangeRecord and an IOException
     * occurs on the ChangeAccept call.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = RuntimeException.class)
    public final void testWriteChangeRecordChangeAcceptSendIOException() throws Exception {
        Connection connection = mock(Connection.class);
        ChangeRecord cr = mock(ChangeRecord.class);

        when(cr.accept(any(ChangeRecordVisitor.class), any(ConnectionChangeRecordWriter.class)))
                .thenAnswer(new Answer<IOException>() {
                    @Override
                    public IOException answer(final InvocationOnMock invocation) throws Throwable {
                        // Execute handler and return future.
                        final ChangeRecordVisitor<?, ?> handler =
                                (ChangeRecordVisitor<?, ?>) invocation.getArguments()[0];
                        if (handler != null) {
                            // Data here if needed.
                        }
                        return new IOException("IOException_e_is_not_null");
                    }
                });

        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord(cr);
        }
    }

    /**
     * ConnectionChangeRecordWriter writes a ChangeRecord and an
     * LdapException occurs on the ChangeAccept call.
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = LdapException.class)
    public final void testWriteChangeRecordChangeAcceptSendLdapException() throws Exception {
        Connection connection = mock(Connection.class);
        ChangeRecord cr = mock(ChangeRecord.class);

        when(cr.accept(any(ChangeRecordVisitor.class), any(ConnectionChangeRecordWriter.class)))
                .thenAnswer(new Answer<LdapException>() {
                    @Override
                    public LdapException answer(final InvocationOnMock invocation) throws Throwable {
                        // Execute handler and return future.
                        final ChangeRecordVisitor<?, ?> handler =
                                (ChangeRecordVisitor<?, ?>) invocation.getArguments()[0];
                        if (handler != null) {
                            // Data here if needed.
                        }
                        return newLdapException(ResultCode.UNAVAILABLE_CRITICAL_EXTENSION);
                    }
                });

        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord(cr);
        }
    }

    /**
     * The writeChangeRecord (ChangeRecord) doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteChangeRecordChangeRecordDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord((ChangeRecord) null);
        }
    }

    /**
     * Use the ConnectionChangeRecordWriter to write a DeleteRequest.
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordDeleteRequest() throws Exception {
        Connection connection = mock(Connection.class);

        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            ChangeRecord cr = Requests.newDeleteRequest(DN.valueOf("cn=scarter,dc=example,dc=com"));
            writer.writeChangeRecord(cr);
            verify(connection, times(1)).delete(any(DeleteRequest.class));
        }
    }

    /**
     * The writeChangeRecord (DeleteRequest) doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteChangeRecordDeleteRequestDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord((DeleteRequest) null);
        }
    }

    /**
     * Use the ConnectionChangeRecordWriter to write a ModifyDNRequest.
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordModifyDNRequest() throws Exception {
        Connection connection = mock(Connection.class);

        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            // @formatter:off
            ChangeRecord cr = Requests.newModifyDNRequest(
                "cn=scarter,dc=example,dc=com",
                "cn=Susan Jacobs");
            //@formatter:on
            writer.writeChangeRecord(cr);
            verify(connection, times(1)).modifyDN(any(ModifyDNRequest.class));
        }
    }

    /**
     * The writeChangeRecord (ModifyDNRequest) doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteChangeRecordModifyDNRequestDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord((ModifyDNRequest) null);
        }
    }

    /**
     * Use the ConnectionChangeRecordWriter to write a ModifyRequest.
     *
     * @throws Exception
     */
    @Test
    public final void testWriteChangeRecordModifyRequest() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            // @formatter:off
            ChangeRecord cr = Requests.newModifyRequest(
                    "dn: cn=Fiona Jensen, ou=Marketing, dc=airius, dc=com",
                    "changetype: modify",
                    "delete: telephonenumber",
                    "telephonenumber: +1 408 555 1212"
            );
            writer.writeChangeRecord(cr);
            // @formatter:on
            verify(connection, times(1)).modify(any(ModifyRequest.class));
        }
    }

    /**
     * The writeChangeRecord (ModifyRequest) doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteChangeRecordModifyRequestDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeChangeRecord((ModifyRequest) null);
        }
    }

    /**
     * The writeComment do nothing. ConnectionChangeRecordWriter do not support
     * Comment.
     *
     * @throws Exception
     */
    @Test
    public final void testWriteCommentDoNotSupportComment() throws Exception {
        Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeComment("# A new comment");
            verify(connection, Mockito.never()).add(any(String.class));
            verify(connection, Mockito.never()).delete(any(String.class));
            verify(connection, Mockito.never()).modify(any(String.class));
            verify(connection, Mockito.never()).modifyDN(any(String.class), any(String.class));
        }
    }

    /**
     * The writeComment doesn't allow a null.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testWriteCommentDoesntAllowNull() throws Exception {
        final Connection connection = mock(Connection.class);
        try (ConnectionChangeRecordWriter writer = new ConnectionChangeRecordWriter(connection)) {
            writer.writeComment(null);
        }
    }

    /**
     * The ConnectionChangeRecordWriter doesn't allow a null connection.
     *
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public final void testConnectionChangeRecordWriterDoesntAllowNull() throws Exception {
        new ConnectionChangeRecordWriter(null);
    }

    /**
     * Verify ConnectionChangeRecordWriter close function.
     *
     * @throws Exception
     *             If the test failed unexpectedly.
     */
    @Test
    public final void testConnectionChangeRecordWriterClose() throws Exception {
        Connection connection = mock(Connection.class);
        new ConnectionChangeRecordWriter(connection).close();
        verify(connection, times(1)).close();
    }
}
