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
 * Copyright 2014-2016 ForgeRock AS.
 */
package org.forgerock.opendj.ldap.schema;

import static org.fest.assertions.Assertions.assertThat;
import static org.forgerock.opendj.ldap.spi.LdapPromises.newSuccessfulLdapPromise;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.LdapPromise;
import org.forgerock.opendj.ldap.requests.SearchRequest;
import org.forgerock.opendj.ldap.responses.Responses;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.testng.annotations.Test;

/** Tests the Schema class. */
@SuppressWarnings("javadoc")
public class SchemaTestCase extends AbstractSchemaTestCase {
    @Test(description = "Unit test for OPENDJ-1477")
    public void asNonStrictSchemaAlwaysReturnsSameInstance() {
        final Schema schema = Schema.getCoreSchema();
        final Schema nonStrictSchema1 = schema.asNonStrictSchema();
        final Schema nonStrictSchema2 =
                schema.asNonStrictSchema().asStrictSchema().asNonStrictSchema();
        assertThat(nonStrictSchema1).isSameAs(nonStrictSchema2);
    }

    @Test(description = "Unit test for OPENDJ-1477")
    public void asStrictSchemaAlwaysReturnsSameInstance() {
        final Schema schema = Schema.getCoreSchema();
        final Schema strictSchema1 = schema.asStrictSchema();
        final Schema strictSchema2 = schema.asStrictSchema().asNonStrictSchema().asStrictSchema();
        assertThat(strictSchema1).isSameAs(strictSchema2);
    }

    /**
     * Asynchronously retrieving a simple schema.
     *
     * @throws Exception
     */
    @Test
    public final void testReadSchemaAsyncMethodsMockConnection() throws Exception {
        Connection connection = mock(Connection.class);

        // @formatter:off
        final String[] entry = {
            "# Search result entry: uid=bjensen,ou=People,dc=example,dc=com",
            "dn: uid=bjensen,ou=People,dc=example,dc=com",
            "subschemaSubentry: cn=schema",
            "entryDN: uid=bjensen,ou=people,dc=example,dc=com",
            "entryUUID: fc252fd9-b982-3ed6-b42a-c76d2546312c"
            // N.B : also works with previous example but needs the subschemaSubentry line.
        };

        // Send a search entry result promise :
        LdapPromise<SearchResultEntry> result = newSuccessfulLdapPromise(Responses.newSearchResultEntry(entry));
        when(connection.searchSingleEntryAsync((SearchRequest) any())).thenReturn(result);
        DN testDN = DN.valueOf("uid=bjensen,ou=People,dc=example,dc=com");
        // @formatter:on
        Schema[] schemas = new Schema[] {
                Schema.readSchemaAsync(connection, testDN).getOrThrow(),
                Schema.readSchemaForEntryAsync(connection, testDN).getOrThrow()
        };

        // We retrieve the schemas :
        for (Schema sc : schemas) {
            assertThat(sc.getSyntaxes()).isNotNull();
            assertThat(sc.getAttributeTypes()).isNotNull();
            assertThat(sc.getObjectClasses()).isNotNull();
            assertThat(sc.getMatchingRuleUses()).isNotNull();
            assertThat(sc.getMatchingRuleUses()).isEmpty();
            assertThat(sc.getMatchingRules()).isNotNull();
            assertThat(sc.getDITContentRules()).isNotNull();
            assertThat(sc.getDITContentRules()).isEmpty();
            assertThat(sc.getDITStuctureRules()).isNotNull();
            assertThat(sc.getDITStuctureRules()).isEmpty();
            assertThat(sc.getNameForms()).isNotNull();
            assertThat(sc.getNameForms()).isEmpty();
        }
        connection.close();
    }

    @Test
    public void getAttributeTypeWithDifferentNamesReturnSame() throws Exception {
        Schema schema = CoreSchema.getInstance();
        AttributeType cnAttrType = schema.getAttributeType("cn");
        assertThat(cnAttrType).isSameAs(schema.getAttributeType("commonname"));
        assertThat(cnAttrType).isSameAs(schema.getAttributeType("commonName"));
        assertThat(cnAttrType).isSameAs(schema.getAttributeType("CN"));
    }

    @Test
    public void getAttributeTypeWithDifferentPlaceholderNames() throws Exception {
        Schema schema = CoreSchema.getInstance().asNonStrictSchema();
        AttributeType placeHolderAttrType = schema.getAttributeType("placeholder");
        assertThat(placeHolderAttrType).isEqualTo(schema.getAttributeType("PLACEHOLDER"));
        assertThat(placeHolderAttrType).isNotEqualTo(schema.getAttributeType("another_placeholder"));
    }
}
