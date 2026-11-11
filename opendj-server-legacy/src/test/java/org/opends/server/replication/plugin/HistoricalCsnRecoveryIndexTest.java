/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.1.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.1.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2026 Wren Security
 */
package org.opends.server.replication.plugin;

import static org.forgerock.opendj.config.ConfigurationMock.mockCfg;
import static org.forgerock.opendj.ldap.SearchScope.WHOLE_SUBTREE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opends.server.TestCaseUtils.getServerContext;
import static org.opends.server.protocols.internal.InternalClientConnection.getRootConnection;
import static org.opends.server.protocols.internal.Requests.newSearchRequest;
import static org.opends.server.util.CollectionUtils.newTreeSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.server.config.meta.BackendIndexCfgDefn.IndexType;
import org.forgerock.opendj.server.config.meta.ReplicationDomainCfgDefn.AssuredType;
import org.forgerock.opendj.server.config.server.BackendIndexCfg;
import org.forgerock.opendj.server.config.server.JEBackendCfg;
import org.opends.server.TestCaseUtils;
import org.opends.server.api.MonitorProvider;
import org.opends.server.backends.jeb.JEBackend;
import org.opends.server.core.AddOperation;
import org.opends.server.core.DirectoryServer;
import org.opends.server.protocols.internal.InternalSearchOperation;
import org.opends.server.replication.ReplicationTestCase;
import org.opends.server.replication.common.CSN;
import org.opends.server.replication.protocol.AddMsg;
import org.opends.server.replication.protocol.ReplicationMsg;
import org.opends.server.replication.server.ReplServerFakeConfiguration;
import org.opends.server.replication.server.ReplicationServer;
import org.opends.server.replication.service.ReplicationBroker;
import org.opends.server.types.Attribute;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Checks the recovery search against the real disk-backed history index.
 */
public class HistoricalCsnRecoveryIndexTest extends ReplicationTestCase {

    private static final int SERVER_ID = 123;
    private static final String BACKEND_ID = "history-disk-test";
    // 201 x 500 = 100,500 values per half-range, exceeding the index cursor
    // limit of 100,000 (IndexFilter.CURSOR_ENTRY_LIMIT)
    private static final int ENTRY_COUNT = 201;
    private static final int VALUES_PER_ENTRY = 500;
    private static final DN BASE_DN = DN.valueOf("dc=history-test");

    private Path directory;
    private JEBackend backend;
    private ReplicationServer replicationServer;
    private LDAPReplicationDomain domain;
    private int port;
    private boolean opened;
    private boolean registered;

    @BeforeMethod
    void prepare() throws Exception {
        directory = Files.createTempDirectory("wrends-history-index-");
        backend = createBackend(directory);
        backend.openBackend();
        opened = true;
        getServerContext().getBackendConfigManager().registerLocalBackend(backend);
        registered = true;
        port = TestCaseUtils.findFreePort();
        SortedSet<String> servers = new TreeSet<>();
        servers.add("localhost:" + port);
        replicationServer = new ReplicationServer(new ReplServerFakeConfiguration(
                port, "HistoricalCsnRecoveryIndexTest", 0, 1, 0, 100, servers, 1, 1000, 5000));
        clearChangelogDB(replicationServer);
    }

    @AfterMethod(alwaysRun = true)
    void cleanup() throws Exception {
        if (domain != null) {
            MultimasterReplication.deleteDomain(BASE_DN);
        }
        if (replicationServer != null) {
            remove(replicationServer);
        }
        if (registered) {
            getServerContext().getBackendConfigManager().deregisterLocalBackend(backend);
        }
        if (opened) {
            backend.closeBackend();
        }
        if (directory != null) {
            TestCaseUtils.deleteDirectory(directory.toFile());
        }
    }

    @Test
    void recoverySearchRemainsIndexedWithLargeMigratedHistory() throws Exception {
        seedMigratedHistory(backend);
        startReplicationDomain();

        long start = System.currentTimeMillis();
        TestCaseUtils.addEntry("dn: cn=change," + BASE_DN, "objectClass: top", "objectClass: person",
                "cn: change", "sn: change");
        CSN lower = new CSN(start - 1000, 0, SERVER_ID);
        CSN upper = new CSN(start + 10000, -1, SERVER_ID);

        assertOldFilterExceedsCursorLimit(lower, upper);
        assertIndexedRecoveryAndPublishedChanges(domain, lower);
    }

    private void startReplicationDomain() throws Exception {
        DomainFakeCfg domainCfg = new DomainFakeCfg(BASE_DN, SERVER_ID, newTreeSet("localhost:" + port),
                AssuredType.NOT_ASSURED, 2, 1, 0, null);
        domain = MultimasterReplication.createNewDomain(domainCfg);
        domain.start();
    }

    private static JEBackend createBackend(Path directory) throws Exception {
        JEBackend backend = new JEBackend();
        JEBackendCfg cfg = mockCfg(JEBackendCfg.class);
        when(cfg.dn()).thenReturn(BASE_DN);
        when(cfg.getBackendId()).thenReturn(BACKEND_ID);
        when(cfg.getDBDirectory()).thenReturn(directory.toString());
        when(cfg.getDBDirectoryPermissions()).thenReturn("755");
        when(cfg.getDBCacheSize()).thenReturn(0L);
        when(cfg.getDBCachePercent()).thenReturn(20);
        when(cfg.getDBNumCleanerThreads()).thenReturn(2);
        when(cfg.getDBNumLockTables()).thenReturn(63);
        when(cfg.isIndexFilterAnalyzerEnabled()).thenReturn(true);
        when(cfg.getIndexFilterAnalyzerMaxFilters()).thenReturn(100);
        when(cfg.getBaseDN()).thenReturn(newTreeSet(BASE_DN));
        when(cfg.listBackendIndexes()).thenReturn(new String[] { "ds-sync-hist" });
        when(cfg.listBackendVLVIndexes()).thenReturn(new String[0]);
        BackendIndexCfg index = mockCfg(BackendIndexCfg.class);
        when(index.getAttribute()).thenReturn(getServerContext().getSchema().getAttributeType("ds-sync-hist"));
        when(index.getIndexType()).thenReturn(newTreeSet(IndexType.ORDERING));
        when(index.getIndexEntryLimit()).thenReturn(4000);
        when(cfg.getBackendIndex("ds-sync-hist")).thenReturn(index);
        backend.setBackendID(cfg.getBackendId());
        backend.configureBackend(cfg, getServerContext());
        return backend;
    }

    private static void seedMigratedHistory(JEBackend backend) throws Exception {
        AddOperation addOperation = mock(AddOperation.class);
        backend.addEntry(TestCaseUtils.makeEntry("dn: " + BASE_DN, "objectClass: top", "objectClass: domain"),
                addOperation);

        long now = System.currentTimeMillis();
        for (int entryNumber = 0; entryNumber < ENTRY_COUNT; entryNumber++) {
            List<String> lines = new ArrayList<>();
            lines.add("dn: cn=history-" + entryNumber + "," + BASE_DN);
            lines.add("objectClass: top");
            lines.add("objectClass: person");
            lines.add("cn: history-" + entryNumber);
            lines.add("sn: history");
            for (int i = 0; i < VALUES_PER_ENTRY; i++) {
                int sequence = entryNumber * VALUES_PER_ENTRY + i + 1;
                lines.add("ds-sync-hist: description:" + new CSN(now - 7200000L + sequence, sequence, SERVER_ID)
                        + ":repl:past-" + sequence);
                lines.add("ds-sync-hist: description:" + new CSN(now + 7200000L + sequence, sequence, SERVER_ID + 1)
                        + ":repl:foreign-" + sequence);
            }
            backend.addEntry(TestCaseUtils.makeEntry(lines.toArray(new String[0])), addOperation);
        }
    }

    private static void assertIndexedRecoveryAndPublishedChanges(
            LDAPReplicationDomain domain, CSN lower) throws Exception {
        Set<String> before = historyFilterStatistics();
        List<ReplicationMsg> recovered = new ArrayList<>();
        assertTrue(domain.buildAndPublishMissingChanges(lower, new TestBroker(recovered)));
        Set<String> recoveryStats = historyFilterStatistics();
        recoveryStats.removeAll(before);
        assertFalse(recoveryStats.isEmpty(), "Recovery must evaluate a history index filter");
        for (String stat : recoveryStats) {
            // The analyzer keeps -1 when the index never returned a defined candidate set for the filter.
            assertFalse(stat.contains("maxmatches:-1"), "Recovery filter was not indexed: " + stat);
        }
        assertEquals(recovered.size(), 1, "The local change must be published during recovery");
        assertTrue(recovered.get(0) instanceof AddMsg);
    }

    private static Set<String> historyFilterStatistics() {
        Set<String> statistics = new HashSet<>();
        String monitorName = (BACKEND_ID + " Storage").toLowerCase(Locale.ROOT);
        MonitorProvider<?> monitor = DirectoryServer.getMonitorProviders().get(monitorName);
        assertNotNull(monitor, "Backend monitor is missing: " + monitorName);
        for (Attribute attribute : monitor.getMonitorData()) {
            if (attribute.getAttributeDescription().toString().equalsIgnoreCase("filter-use")) {
                attribute.forEach(value -> {
                    if (value.toString().contains("ds-sync-hist")) {
                        statistics.add(value.toString());
                    }
                });
            }
        }
        return statistics;
    }

    private static void assertOldFilterExceedsCursorLimit(CSN lower, CSN upper) throws Exception {
        // Both half-ranges from the former recovery filter exceed the cursor limit.
        String oldFilter = "(&(ds-sync-hist>=dummy:" + lower + ")(ds-sync-hist<=dummy:" + upper + "))";
        assertTrue(debugSearch(oldFilter).contains("LIMIT-EXCEEDED"), "Fixture must exercise the old cursor limit");
    }

    private static final class TestBroker extends ReplicationBroker {

        private final List<ReplicationMsg> messages;

        private TestBroker(List<ReplicationMsg> messages) {
            super(new DummyReplicationDomain(0), null, new DomainFakeCfg(null, 0, new TreeSet<>()), null);
            this.messages = messages;
        }

        @Override
        public void publishRecovery(ReplicationMsg message) {
            messages.add(message);
        }
    }

    private static String debugSearch(String filter) throws Exception {
        InternalSearchOperation search = getRootConnection().processSearch(
                newSearchRequest(BASE_DN, WHOLE_SUBTREE, filter).addAttribute("debugsearchindex"));
        assertEquals(search.getResultCode(), ResultCode.SUCCESS);
        assertEquals(search.getSearchEntries().size(), 1);
        return search.getSearchEntries().get(0).getAllAttributes("debugsearchindex").iterator().next().toString();
    }

}
