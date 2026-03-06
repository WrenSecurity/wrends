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
package org.opends.server.loggers.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMDCAdapter;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * Wren:DS's SLF4J service provider.
 */
public class OpenDJServiceProvider implements SLF4JServiceProvider {

    /**
     * Declare the version of the SLF4J API this implementation is compiled
     * against. The value of this field is modified with each major release.
     */
    // to avoid constant folding by the compiler, this field must *not* be final
    public static String REQUESTED_API_VERSION = "2.0.99"; // !final

    private final ILoggerFactory loggerFactory = new OpenDJLoggerFactory();

    private final IMarkerFactory markerFactory = new BasicMarkerFactory();

    private final MDCAdapter mdcAdapter = new BasicMDCAdapter();

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize() {
        // already initialized
    }

}
