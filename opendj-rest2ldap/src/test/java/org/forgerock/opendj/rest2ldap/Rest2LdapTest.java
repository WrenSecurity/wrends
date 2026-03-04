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
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2016 ForgeRock AS.
 * Portions copyright 2026 Wren Security
 */
package org.forgerock.opendj.rest2ldap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.forgerock.json.JsonValue.array;
import static org.forgerock.json.JsonValue.json;
import static org.forgerock.json.resource.PatchOperation.replace;
import static org.forgerock.json.resource.Requests.newCreateRequest;
import static org.forgerock.json.resource.Requests.newPatchRequest;
import static org.forgerock.json.resource.Requests.newQueryRequest;
import static org.forgerock.json.resource.Requests.newReadRequest;
import static org.forgerock.json.resource.Requests.newUpdateRequest;
import static org.forgerock.json.resource.Responses.newResourceResponse;
import static org.forgerock.opendj.ldap.Connections.newInternalConnection;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.collectionOf;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.constant;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.object;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.resource;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.rest2Ldap;
import static org.forgerock.opendj.rest2ldap.Rest2Ldap.simple;
import static org.forgerock.opendj.rest2ldap.WritabilityPolicy.CREATE_ONLY;
import static org.forgerock.opendj.rest2ldap.WritabilityPolicy.READ_ONLY;
import static org.forgerock.opendj.rest2ldap.WritabilityPolicy.READ_ONLY_DISCARD_WRITES;
import static org.forgerock.util.Options.defaultOptions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.forgerock.json.JsonPointer;
import org.forgerock.json.JsonValue;
import org.forgerock.json.resource.BadRequestException;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.NotFoundException;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.QueryResourceHandler;
import org.forgerock.json.resource.QueryResponse;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.Requests;
import org.forgerock.json.resource.ResourceResponse;
import org.forgerock.json.resource.UpdateRequest;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.IntermediateResponseHandler;
import org.forgerock.opendj.ldap.LdapResultHandler;
import org.forgerock.opendj.ldap.MemoryBackend;
import org.forgerock.opendj.ldap.RequestContext;
import org.forgerock.opendj.ldap.SearchResultHandler;
import org.forgerock.opendj.ldap.requests.AddRequest;
import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.requests.CompareRequest;
import org.forgerock.opendj.ldap.requests.ExtendedRequest;
import org.forgerock.opendj.ldap.requests.ModifyDNRequest;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.SearchRequest;
import org.forgerock.opendj.ldap.responses.BindResult;
import org.forgerock.opendj.ldap.responses.CompareResult;
import org.forgerock.opendj.ldap.responses.ExtendedResult;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldif.EntryReader;
import org.forgerock.opendj.ldif.LDIFEntryReader;
import org.forgerock.services.context.Context;
import org.forgerock.services.context.RootContext;
import org.forgerock.testng.ForgeRockTestCase;
import org.forgerock.util.query.QueryFilter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SuppressWarnings({ "javadoc" })
@Test
public final class Rest2LdapTest extends ForgeRockTestCase {
    // TODO: unit test for DN template variables
    // TODO: unit test for nested sub-resources
    // TODO: unit test for singletons

    private enum UseCase {
        CLIENT_ID_PRIMARY_VIEW {
            @Override
            RequestHandler handler() {
                return rest2Ldap(defaultOptions(),
                                 top(),
                                 userUidResource(),
                                 resource("test")
                                         .subResources(collectionOf("userUid").useClientDnNaming("uid")
                                                                              .dnTemplate("dc=test")
                                                                              .urlTemplate("users")))
                        .newRequestHandlerFor("test");
            }

            @Override
            CreateRequest createRequest() {
                return newCreateRequest("users", "bjensen", userJson("bjensen", null, "bjensen", "created"));
            }

            @Override
            ResourceResponse createdResource() {
                return newResourceResponse("bjensen", "1", userJson("bjensen", "1", "bjensen", "created"));
            }
        },
        CLIENT_ID_SECONDARY_VIEW {
            @Override
            RequestHandler handler() {
                return rest2Ldap(defaultOptions(),
                                 top(),
                                 userUidResource(),
                                 resource("test")
                                         .subResources(collectionOf("userUid").useClientNaming("uid", "mail")
                                                                              .dnTemplate("dc=test")
                                                                              .urlTemplate("users")))
                        .newRequestHandlerFor("test");
            }

            @Override
            CreateRequest createRequest() {
                return newCreateRequest("users", "bjensen@test.com", userJson("bjensen", null, "bjensen", "created"));
            }

            @Override
            ResourceResponse createdResource() {
                return newResourceResponse("bjensen@test.com", "1", userJson("bjensen", "1", "bjensen", "created"));
            }
        },
        SERVER_ID_PRIMARY_VIEW {
            @Override
            RequestHandler handler() {
                return rest2Ldap(defaultOptions(),
                                 top(),
                                 userEntryUuidResource(),
                                 resource("test")
                                         .subResources(collectionOf("userEntryUuid").useServerEntryUuidNaming("uid")
                                                                                    .dnTemplate("dc=test")
                                                                                    .urlTemplate("users")))
                        .newRequestHandlerFor("test");
            }

            @Override
            CreateRequest createRequest() {
                return newCreateRequest("users", null, userJson(null, null, "bjensen", "created"));
            }

            @Override
            ResourceResponse createdResource() {
                return newResourceResponse(ENTRY_UUID, "1", userJson(ENTRY_UUID, "1", "bjensen", "created"));
            }
        },
        SERVER_ID_SECONDARY_VIEW {
            @Override
            RequestHandler handler() {
                return rest2Ldap(defaultOptions(),
                                 top(),
                                 userEntryUuidResource(),
                                 resource("test")
                                         .subResources(collectionOf("userEntryUuid").useClientNaming("uid", "mail")
                                                                                    .dnTemplate("dc=test")
                                                                                    .urlTemplate("users")))
                        .newRequestHandlerFor("test");
            }

            @Override
            CreateRequest createRequest() {
                return newCreateRequest("users", "bjensen@test.com", userJson(null, null, "bjensen", "created"));
            }

            @Override
            ResourceResponse createdResource() {
                return newResourceResponse("bjensen@test.com", "1", userJson(ENTRY_UUID, "1", "bjensen", "created"));
            }
        };

        abstract ResourceResponse createdResource();

        Context ctx() throws Exception {
            final EntryReader ldif = new LDIFEntryReader("dn: dc=test",
                                                         "objectClass: top",
                                                         "objectClass: domain",
                                                         "dc: test");
            final Connection connection = newInternalConnection(updateMeta(new MemoryBackend(ldif)));
            return new AuthenticatedConnectionContext(new RootContext(), connection);
        }

        abstract RequestHandler handler();

        abstract CreateRequest createRequest();
    }

    private static final String ENTRY_UUID = UUID.randomUUID().toString();
    private static final String USER_SCHEMA_URI = "fr:opendj:user:1.0";

    // Field values may be null.
    private static JsonValue userJson(final String id, final String rev, final String uid, final String description) {
        return json(o(f("_id", id),
                      f("_rev", rev),
                      f("schema", USER_SCHEMA_URI),
                      f("uid", uid),
                      f("email", uid + "@test.com"),
                      f("name", o(f("displayName", uid + " displayName"), f("surname", uid + " surname"))),
                      f("description", array(description))));
    }

    private static Map.Entry<String, Object> f(final String k, final Object v) {
        return v != null ? JsonValue.field(k, v) : null;
    }

    @SafeVarargs
    private static Object o(Map.Entry<String, Object>... fields) {
        return JsonValue.object(fields);
    }

    private static Resource top() {
        return resource("top").isAbstract(true)
                              .objectClass("top")
                              .property("_rev", simple("etag").isRequired(true).writability(READ_ONLY));
    }

    private static Resource userEntryUuidResource() {
        return userResource("userEntryUuid", simple("entryUUID").writability(READ_ONLY));
    }

    private static Resource userUidResource() {
        return userResource("userUid", simple("uid").isRequired(true).writability(CREATE_ONLY));
    }

    private static Resource userResource(final String resourceId, final PropertyMapper id) {
        return resource(resourceId).superType("top")
                                   .objectClasses("person", "organizationalPerson", "inetOrgPerson")
                                   .property("schema", constant(USER_SCHEMA_URI))
                                   .property("_id", id)
                                   .property("uid", simple("uid").isRequired(true).writability(CREATE_ONLY))
                                   .property("email", simple("mail"))
                                   .property("name", object().property("displayName", simple("cn").isRequired(true))
                                                             .property("surname", simple("sn").isRequired(true)))
                                   .property("description", simple("description").isMultiValued(true));
    }

    @Test(dataProvider = "useCases")
    public void canCreateResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();

        // When
        ResourceResponse actual = handler.handleCreate(ctx, useCase.createRequest()).getOrThrowUninterruptibly();

        // Then
        assertThatExpectedResourceWasReturned(actual, useCase.createdResource());
    }

    @Test(dataProvider = "useCases", dependsOnMethods = "canCreateResources")
    public void canReadResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();
        CreateRequest createRequest = useCase.createRequest();
        ResourceResponse resource = handler.handleCreate(ctx, createRequest).getOrThrowUninterruptibly();

        // When
        ReadRequest readRequest = newReadRequest(createRequest.getResourcePath(), resource.getId());
        ResourceResponse actual = handler.handleRead(ctx, readRequest).getOrThrowUninterruptibly();

        // Then
        assertThatExpectedResourceWasReturned(actual, resource);
    }

    @Test(dataProvider = "useCases", dependsOnMethods = "canCreateResources")
    public void canUpdateResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();
        CreateRequest createRequest = useCase.createRequest();
        ResourceResponse resource = handler.handleCreate(ctx, createRequest).getOrThrowUninterruptibly();

        // When
        JsonValue newContent = resource.getContent().copy();
        newContent.put("description", array("updated"));
        UpdateRequest updateRequest = newUpdateRequest(createRequest.getResourcePath(), resource.getId(), newContent);

        ResourceResponse actual = handler.handleUpdate(ctx, updateRequest).getOrThrowUninterruptibly();

        // Then
        newContent.put("_rev", "2");
        ResourceResponse expected = newResourceResponse(resource.getId(), "2", newContent);

        assertThatExpectedResourceWasReturned(actual, expected);

        ReadRequest readRequest = newReadRequest(createRequest.getResourcePath(), resource.getId());
        ResourceResponse actual2 = handler.handleRead(ctx, readRequest).getOrThrowUninterruptibly();
        assertThatExpectedResourceWasReturned(actual2, expected);
    }

    @Test(dataProvider = "useCases", dependsOnMethods = "canCreateResources")
    public void canDeleteResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();
        CreateRequest createRequest = useCase.createRequest();
        ResourceResponse resource = handler.handleCreate(ctx, createRequest).getOrThrowUninterruptibly();

        // When
        DeleteRequest deleteRequest = Requests.newDeleteRequest(createRequest.getResourcePath(), resource.getId());
        ResourceResponse actual = handler.handleDelete(ctx, deleteRequest).getOrThrowUninterruptibly();

        // Then
        assertThatExpectedResourceWasReturned(actual, resource);

        ReadRequest readRequest = newReadRequest(createRequest.getResourcePath(), resource.getId());
        try {
            handler.handleRead(ctx, readRequest).getOrThrowUninterruptibly();
            fail("Deleted resource can still be read");
        } catch (NotFoundException e) {
            // Expected.
        }
    }

    @Test(dataProvider = "useCases", dependsOnMethods = "canCreateResources")
    public void canPatchResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();
        CreateRequest createRequest = useCase.createRequest();
        ResourceResponse resource = handler.handleCreate(ctx, createRequest).getOrThrowUninterruptibly();

        // When
        PatchRequest patchRequest = newPatchRequest(createRequest.getResourcePath(),
                                                    resource.getId(),
                                                    replace("description", array("patched")));
        ResourceResponse actual = handler.handlePatch(ctx, patchRequest).getOrThrowUninterruptibly();

        // Then
        JsonValue newContent = resource.getContent().copy();
        newContent.put("description", array("patched"));
        newContent.put("_rev", "2");
        ResourceResponse expected = newResourceResponse(resource.getId(), "2", newContent);

        assertThatExpectedResourceWasReturned(actual, expected);

        ReadRequest readRequest = newReadRequest(createRequest.getResourcePath(), resource.getId());
        ResourceResponse actual2 = handler.handleRead(ctx, readRequest).getOrThrowUninterruptibly();
        assertThatExpectedResourceWasReturned(actual2, expected);
    }

    @Test(dataProvider = "useCases", dependsOnMethods = "canCreateResources")
    public void canQueryResources(UseCase useCase) throws Exception {
        // Given
        RequestHandler handler = useCase.handler();
        Context ctx = useCase.ctx();
        CreateRequest createRequest = useCase.createRequest();
        ResourceResponse resource = handler.handleCreate(ctx, createRequest).getOrThrowUninterruptibly();

        // When
        QueryRequest queryRequest = newQueryRequest(createRequest.getResourcePath());
        queryRequest.setQueryFilter(QueryFilter.<JsonPointer>alwaysTrue());

        final AtomicReference<ResourceResponse> actualResource = new AtomicReference<>();
        QueryResponse actualResponse = handler.handleQuery(ctx, queryRequest, new QueryResourceHandler() {
            @Override
            public boolean handleResource(final ResourceResponse resource) {
                if (!actualResource.compareAndSet(null, resource)) {
                    fail("Too many resources returned during query");
                }
                return true;
            }
        }).getOrThrowUninterruptibly();

        // Then
        assertThat(actualResponse).isNotNull();
        assertThatExpectedResourceWasReturned(actualResource.get(), resource);
    }

    @Test
    public void cannotCreateResourceWithReadOnlyAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen"),
                f("readOnly", "value")));

        // When / Then
        try {
            handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                   .getOrThrowUninterruptibly();
            fail("Expected BadRequestException when setting a read-only attribute during create");
        } catch (BadRequestException e) {
            // Expected
        }
    }

    @Test
    public void canCreateResourceIgnoringReadOnlyDiscardWritesAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen"),
                f("readOnlyDiscard", "value")));

        // When - the readOnlyDiscard value should be silently discarded
        ResourceResponse actual = handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                .getOrThrowUninterruptibly();

        // Then
        assertThat(actual.getContent().get("readOnlyDiscard").getObject()).isNull();
    }

    @Test
    public void cannotUpdateResourceWithChangedReadOnlyAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen")));
        ResourceResponse resource = handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                .getOrThrowUninterruptibly();

        JsonValue newContent = resource.getContent().copy();
        newContent.put("readOnly", "value");

        // When / Then
        try {
            handler.handleUpdate(ctx, newUpdateRequest("users", resource.getId(), newContent))
                    .getOrThrowUninterruptibly();
            fail("Expected BadRequestException when changing a read-only attribute during update");
        } catch (BadRequestException e) {
            // Expected
        }
    }

    @Test
    public void canUpdateResourceIgnoringReadOnlyDiscardWritesAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen")));
        ResourceResponse resource = handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                .getOrThrowUninterruptibly();

        JsonValue newContent = resource.getContent().copy();
        newContent.put("readOnlyDiscard", "value");

        // When - the readOnlyDiscard value should be silently discarded
        ResourceResponse actual = handler.handleUpdate(ctx, newUpdateRequest("users", resource.getId(), newContent))
                .getOrThrowUninterruptibly();

        // Then
        assertThat(actual.getContent().get("readOnlyDiscard").getObject()).isNull();
    }

    @Test
    public void cannotPatchReadOnlyAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen")));
        ResourceResponse resource = handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                .getOrThrowUninterruptibly();

        // When / Then
        try {
            handler.handlePatch(ctx, newPatchRequest("users", resource.getId(), replace("readOnly", "value")))
                   .getOrThrowUninterruptibly();
            fail("Expected BadRequestException when patching a read-only attribute");
        } catch (BadRequestException e) {
            // Expected
        }
    }

    @Test
    public void cannotPatchReadOnlyDiscardWritesAttribute() throws Exception {
        // Given
        RequestHandler handler = readOnlyTestHandler();
        Context ctx = readOnlyTestCtx();
        JsonValue content = json(o(
                f("_id", "bjensen"),
                f("uid", "bjensen"),
                f("cn", "bjensen"),
                f("sn", "bjensen")));
        ResourceResponse resource = handler.handleCreate(ctx, newCreateRequest("users", "bjensen", content))
                .getOrThrowUninterruptibly();

        // When / Then - patch always rejects read-only attributes, even when discardWrites is set
        try {
            handler.handlePatch(ctx, newPatchRequest("users", resource.getId(), replace("readOnlyDiscard", "value")))
                    .getOrThrowUninterruptibly();
            fail("Expected BadRequestException when patching a read-only (discard writes) attribute");
        } catch (BadRequestException e) {
            // Expected
        }
    }

    @DataProvider
    Object[][] useCases() throws Exception {
        UseCase[] values = UseCase.values();
        Object[][] data = new Object[values.length][];
        for (int i = 0; i < values.length; i++) {
            data[i] = new Object[] { values[i] };
        }
        return data;
    }

    private void assertThatExpectedResourceWasReturned(final ResourceResponse actual, final ResourceResponse expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getRevision()).isEqualTo(expected.getRevision());
        assertThat(actual.getContent().asMap()).isEqualTo(expected.getContent().asMap());
    }

    private static RequestHandler readOnlyTestHandler() {
        return rest2Ldap(defaultOptions(), top(), resource("readOnlyTest").superType("top")
                        .objectClasses("person", "organizationalPerson", "inetOrgPerson")
                        .property("_id", simple("uid").isRequired(true).writability(CREATE_ONLY))
                        .property("uid", simple("uid").isRequired(true).writability(CREATE_ONLY))
                        .property("cn", simple("cn").isRequired(true)).property("sn", simple("sn").isRequired(true))
                        .property("readOnly", simple("telephoneNumber").writability(READ_ONLY))
                        .property("readOnlyDiscard", simple("mobile").writability(READ_ONLY_DISCARD_WRITES)),
                resource("test").subResources(
                        collectionOf("readOnlyTest").useClientDnNaming("uid").dnTemplate("dc=test")
                                .urlTemplate("users"))).newRequestHandlerFor("test");
    }

    private static Context readOnlyTestCtx() throws Exception {
        EntryReader ldif = new LDIFEntryReader("dn: dc=test", "objectClass: top", "objectClass: domain", "dc: test");
        Connection connection = newInternalConnection(updateMeta(new MemoryBackend(ldif)));
        return new AuthenticatedConnectionContext(new RootContext(), connection);
    }

    private static org.forgerock.opendj.ldap.RequestHandler<RequestContext> updateMeta(final MemoryBackend delegate) {
        return new org.forgerock.opendj.ldap.RequestHandler<RequestContext>() {
            public void handleAdd(final RequestContext requestContext, final AddRequest request,
                                  final IntermediateResponseHandler intermediateResponseHandler,
                                  final LdapResultHandler<Result> resultHandler) {
                request.addAttribute("entryUuid", ENTRY_UUID);
                request.addAttribute("etag", 1);
                delegate.handleAdd(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            public void handleBind(final RequestContext requestContext, final int version, final BindRequest request,
                                   final IntermediateResponseHandler intermediateResponseHandler,
                                   final LdapResultHandler<BindResult> resultHandler) {
                delegate.handleBind(requestContext, version, request, intermediateResponseHandler, resultHandler);
            }

            public void handleCompare(final RequestContext requestContext, final CompareRequest request,
                                      final IntermediateResponseHandler intermediateResponseHandler,
                                      final LdapResultHandler<CompareResult> resultHandler) {
                delegate.handleCompare(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            public void handleDelete(final RequestContext requestContext,
                                     final org.forgerock.opendj.ldap.requests.DeleteRequest request,
                                     final IntermediateResponseHandler intermediateResponseHandler,
                                     final LdapResultHandler<Result> resultHandler) {
                delegate.handleDelete(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            public <R extends ExtendedResult> void handleExtendedRequest(final RequestContext requestContext,
                                                                         final ExtendedRequest<R> request,
                                                                         final IntermediateResponseHandler
                                                                                 intermediateResponseHandler,
                                                                         final LdapResultHandler<R> resultHandler) {
                delegate.handleExtendedRequest(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            public void handleModify(final RequestContext requestContext, final ModifyRequest request,
                                     final IntermediateResponseHandler intermediateResponseHandler,
                                     final LdapResultHandler<Result> resultHandler) {
                incrementEtag(request.getName());
                delegate.handleModify(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            private void incrementEtag(final DN name) {
                final Entry entry = delegate.get(name);
                if (entry != null) {
                    final int etag = entry.parseAttribute("etag").asInteger(1);
                    entry.replaceAttribute("etag", etag + 1);
                }
            }

            public void handleModifyDN(final RequestContext requestContext, final ModifyDNRequest request,
                                       final IntermediateResponseHandler intermediateResponseHandler,
                                       final LdapResultHandler<Result> resultHandler) {
                incrementEtag(request.getName());
                delegate.handleModifyDN(requestContext, request, intermediateResponseHandler, resultHandler);
            }

            public void handleSearch(final RequestContext requestContext, final SearchRequest request,
                                     final IntermediateResponseHandler intermediateResponseHandler,
                                     final SearchResultHandler entryHandler,
                                     final LdapResultHandler<Result> resultHandler) {
                delegate.handleSearch(requestContext,
                                      request,
                                      intermediateResponseHandler,
                                      entryHandler,
                                      resultHandler);
            }
        };
    }
}
