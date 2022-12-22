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
 * Portions copyright 2011-2016 ForgeRock AS.
 * Portions Copyright 2022 Wren Security.
 */
package org.forgerock.opendj.ldap;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.forgerock.i18n.LocalizedIllegalArgumentException;
import org.forgerock.opendj.ldap.schema.CoreSchema;
import org.forgerock.opendj.ldap.schema.Schema;
import org.forgerock.opendj.ldap.schema.UnknownSchemaElementException;
import org.forgerock.util.Pair;
import org.forgerock.util.Reject;

import com.forgerock.opendj.util.SubstringReader;

import static com.forgerock.opendj.ldap.CoreMessages.*;

/**
 * A distinguished name (DN) as defined in RFC 4512 section 2.3 is the
 * concatenation of its relative distinguished name (RDN) and its immediate
 * superior's DN. A DN unambiguously refers to an entry in the Directory.
 * <p>
 * The following are examples of string representations of DNs:
 *
 * <pre>
 * UID=nobody@example.com,DC=example,DC=com CN=John
 * Smith,OU=Sales,O=ACME Limited,L=Moab,ST=Utah,C=US
 * </pre>
 *
 * @see <a href="http://tools.ietf.org/html/rfc4512#section-2.3">RFC 4512 -
 *      Lightweight Directory Access Protocol (LDAP): Directory Information
 *      Models </a>
 */
public final class DN implements Iterable<RDN>, Comparable<DN> {
    static final byte NORMALIZED_RDN_SEPARATOR = 0x00;
    static final byte NORMALIZED_AVA_SEPARATOR = 0x01;
    static final byte NORMALIZED_ESC_BYTE = 0x02;

    static final char RDN_CHAR_SEPARATOR = ',';
    static final char AVA_CHAR_SEPARATOR = '+';

    private static final DN ROOT_DN = new DN(CoreSchema.getInstance(), null, null);

    /**
     * This is the size of the per-thread per-schema DN cache. We should
     * be conservative here in case there are many threads. We will only
     * cache parent DNs, so there's no need for it to be big.
     */
    private static final int DN_CACHE_SIZE = 32;

    private static final ThreadLocal<Map<String, DN>> CACHE = new ThreadLocal<Map<String, DN>>() {
        @SuppressWarnings("serial")
        @Override
        protected Map<String, DN> initialValue() {
            return new LinkedHashMap<String, DN>(DN_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Entry<String, DN> eldest) {
                    return size() > DN_CACHE_SIZE;
                }
            };
        }
    };

    /**
     * Returns the LDAP string representation of the provided DN attribute value
     * in a form suitable for substitution directly into a DN string. This
     * method may be useful in cases where a DN is to be constructed from a DN
     * template using {@code String#format(String, Object...)}. The following
     * example illustrates two approaches to constructing a DN:
     *
     * <pre>
     * // This may contain user input.
     * String attributeValue = ...;
     *
     * // Using the equality filter constructor:
     * DN dn = DN.valueOf("ou=people,dc=example,dc=com").child("uid", attributeValue);
     *
     * // Using a String template:
     * String dnTemplate = "uid=%s,ou=people,dc=example,dc=com";
     * String dnString = String.format(dnTemplate,
     *                                 DN.escapeAttributeValue(attributeValue));
     * DN dn = DN.valueOf(dnString);
     * </pre>
     *
     * <b>Note:</b> attribute values do not and should not be escaped before
     * passing them to constructors like {@link #child(String, Object)}.
     * Escaping is only required when creating DN strings.
     *
     * @param attributeValue
     *            The attribute value.
     * @return The LDAP string representation of the provided filter assertion
     *         value in a form suitable for substitution directly into a filter
     *         string.
     */
    public static String escapeAttributeValue(final Object attributeValue) {
        Reject.ifNull(attributeValue);
        final String s = String.valueOf(attributeValue);
        final StringBuilder builder = new StringBuilder(s.length());
        AVA.escapeAttributeValue(s, builder);
        return builder.toString();
    }

    /**
     * Creates a new DN using the provided DN template and unescaped attribute
     * values using the default schema. This method first escapes each of the
     * attribute values and then substitutes them into the template using
     * {@link String#format(String, Object...)}. Finally, the formatted string
     * is parsed as an LDAP DN using {@link #valueOf(String)}.
     * <p>
     * This method may be useful in cases where the structure of a DN is not
     * known at compile time, for example, it may be obtained from a
     * configuration file. Example usage:
     *
     * <pre>
     * String template = &quot;uid=%s,ou=people,dc=example,dc=com&quot;;
     * DN dn = DN.format(template, &quot;bjensen&quot;);
     * </pre>
     *
     * @param template
     *            The DN template.
     * @param attributeValues
     *            The attribute values to be substituted into the template.
     * @return The formatted template parsed as a {@code DN}.
     * @throws LocalizedIllegalArgumentException
     *             If the formatted template is not a valid LDAP string
     *             representation of a DN.
     * @see #escapeAttributeValue(Object)
     */
    public static DN format(final String template, final Object... attributeValues) {
        return format(template, Schema.getDefaultSchema(), attributeValues);
    }

    /**
     * Creates a new DN using the provided DN template and unescaped attribute
     * values using the provided schema. This method first escapes each of the
     * attribute values and then substitutes them into the template using
     * {@link String#format(String, Object...)}. Finally, the formatted string
     * is parsed as an LDAP DN using {@link #valueOf(String)}.
     * <p>
     * This method may be useful in cases where the structure of a DN is not
     * known at compile time, for example, it may be obtained from a
     * configuration file. Example usage:
     *
     * <pre>
     * String template = &quot;uid=%s,ou=people,dc=example,dc=com&quot;;
     * DN dn = DN.format(template, &quot;bjensen&quot;);
     * </pre>
     *
     * @param template
     *            The DN template.
     * @param schema
     *            The schema to use when parsing the DN.
     * @param attributeValues
     *            The attribute values to be substituted into the template.
     * @return The formatted template parsed as a {@code DN}.
     * @throws LocalizedIllegalArgumentException
     *             If the formatted template is not a valid LDAP string
     *             representation of a DN.
     * @see #escapeAttributeValue(Object)
     */
    public static DN format(final String template, final Schema schema,
            final Object... attributeValues) {
        final String[] attributeValueStrings = new String[attributeValues.length];
        for (int i = 0; i < attributeValues.length; i++) {
            attributeValueStrings[i] = escapeAttributeValue(attributeValues[i]);
        }
        final String dnString = String.format(template, (Object[]) attributeValueStrings);
        return valueOf(dnString, schema);
    }

    /**
     * Returns the Root DN. The Root DN does not contain and RDN components and
     * is superior to all other DNs.
     *
     * @return The Root DN.
     */
    public static DN rootDN() {
        return ROOT_DN;
    }

    /**
     * Parses the provided LDAP string representation of a DN using the default schema.
     *
     * @param dn
     *            The LDAP string representation of a DN.
     * @return The parsed DN.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     * @see #format(String, Object...)
     */
    public static DN valueOf(final String dn) {
        return valueOf(dn, Schema.getDefaultSchema());
    }

    /**
     * Parses the provided LDAP string representation of a DN using the provided schema.
     *
     * @param dn
     *            The LDAP string representation of a DN.
     * @param schema
     *            The schema to use when parsing the DN.
     * @return The parsed DN.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a DN.
     * @throws NullPointerException
     *             If {@code dn} or {@code schema} was {@code null}.
     * @see #format(String, Schema, Object...)
     */
    public static DN valueOf(final String dn, final Schema schema) {
        Reject.ifNull(dn, schema);
        if (dn.length() == 0) {
            return ROOT_DN;
        }

        // First check if DN is already cached.
        final Map<String, DN> cache = CACHE.get();
        final DN cachedDN = cache.get(dn);
        if (cachedDN != null && cachedDN.schema == schema) {
            return cachedDN;
        }

        // Not in cache so decode.
        return decode(new SubstringReader(dn), schema, cache);
    }

    /**
     * Parses the provided LDAP string representation of a DN using the default schema.
     *
     * @param dn
     *            The LDAP byte string representation of a DN.
     * @return The parsed DN.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP byte string representation of a DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public static DN valueOf(ByteString dn) {
        return DN.valueOf(dn.toString());
    }

    /** Decodes a DN using the provided reader and schema. */
    private static DN decode(final SubstringReader reader, final Schema schema, final Map<String, DN> cache) {
        reader.skipWhitespaces();
        if (reader.remaining() == 0) {
            return ROOT_DN;
        }

        final RDN rdn;
        try {
            rdn = RDN.decode(reader, schema);
        } catch (final UnknownSchemaElementException e) {
            throw new LocalizedIllegalArgumentException(
                    ERR_DN_TYPE_NOT_FOUND.get(reader.getString(), e.getMessageObject()));
        }

        LinkedList<Pair<Integer, RDN>> parentRDNs = null;
        DN parent = null;
        while (reader.remaining() > 0 && reader.read() == ',') {
            reader.skipWhitespaces();
            if (reader.remaining() == 0) {
                throw new LocalizedIllegalArgumentException(ERR_ATTR_SYNTAX_DN_ATTR_NO_NAME.get(reader.getString()));
            }
            reader.mark();
            final String parentString = reader.read(reader.remaining());
            parent = cache.get(parentString);
            if (parent != null) {
                break;
            }
            reader.reset();
            if (parentRDNs == null) {
                parentRDNs = new LinkedList<>();
            }
            parentRDNs.add(Pair.of(reader.pos(), RDN.decode(reader, schema)));
        }
        if (parent == null) {
            parent = ROOT_DN;
        }

        if (parentRDNs != null) {
            Iterator<Pair<Integer, RDN>> iter = parentRDNs.descendingIterator();
            int parentsLeft = parentRDNs.size();
            while (iter.hasNext()) {
                Pair<Integer, RDN> parentRDN = iter.next();
                parent = new DN(schema, parent, parentRDN.getSecond());
                if (parentsLeft-- < DN_CACHE_SIZE) {
                    cache.put(reader.getString().substring(parentRDN.getFirst()), parent);
                }
            }
        }
        return new DN(schema, parent, rdn);
    }

    private final RDN rdn;
    private DN parent;
    private final int size;
    private int hashCode = -1;

    /**
     * The normalized byte string representation of this DN, which is not
     * a valid DN and is not reversible to a valid DN.
     */
    private ByteString normalizedDN;

    /**
     * The RFC 4514 string representation of this DN. A value of {@code null}
     * indicates that the value needs to be computed lazily.
     */
    private String stringValue;

    /** The schema used to create this DN. */
    private final Schema schema;

    /** Private constructor. */
    private DN(final Schema schema, final DN parent, final RDN rdn) {
        this(schema, parent, rdn, parent != null ? parent.size + 1 : 0);
    }

    /** Private constructor. */
    private DN(final Schema schema, final DN parent, final RDN rdn, final int size) {
        this.schema = schema;
        this.parent = parent;
        this.rdn = rdn;
        this.size = size;
        this.stringValue = rdn == null ? "" : null;
    }

    /**
     * Returns a DN which is subordinate to this DN and having the additional
     * RDN components contained in the provided DN.
     *
     * @param dn
     *            The DN containing the RDN components to be added to this DN.
     * @return The subordinate DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public DN child(final DN dn) {
        Reject.ifNull(dn);

        if (dn.isRootDN()) {
            return this;
        } else if (isRootDN()) {
            return dn;
        } else {
            final RDN[] rdns = new RDN[dn.size()];
            int i = rdns.length;
            for (DN next = dn; next.rdn != null; next = next.parent) {
                rdns[--i] = next.rdn;
            }
            DN newDN = this;
            for (i = 0; i < rdns.length; i++) {
                newDN = new DN(this.schema, newDN, rdns[i]);
            }
            return newDN;
        }
    }

    /**
     * Returns a DN which is an immediate child of this DN and having the
     * specified RDN.
     * <p>
     * <b>Note:</b> the child DN whose RDN is {@link RDN#maxValue()} compares
     * greater than all other possible child DNs, and may be used to construct
     * range queries against DN keyed sorted collections such as
     * {@code SortedSet} and {@code SortedMap}.
     *
     * @param rdn
     *            The RDN for the child DN.
     * @return The child DN.
     * @throws NullPointerException
     *             If {@code rdn} was {@code null}.
     * @see RDN#maxValue()
     */
    public DN child(final RDN rdn) {
        Reject.ifNull(rdn);
        return new DN(this.schema, this, rdn);
    }

    /**
     * Returns a DN which is subordinate to this DN and having the additional
     * RDN components contained in the provided DN decoded using the default
     * schema.
     *
     * @param dn
     *            The DN containing the RDN components to be added to this DN.
     * @return The subordinate DN.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public DN child(final String dn) {
        Reject.ifNull(dn);
        return child(valueOf(dn));
    }

    /**
     * Returns a DN which is an immediate child of this DN and with an RDN
     * having the provided attribute type and value decoded using the default
     * schema.
     * <p>
     * If {@code attributeValue} is not an instance of {@code ByteString} then
     * it will be converted using the {@link ByteString#valueOfObject(Object)} method.
     *
     * @param attributeType
     *            The attribute type.
     * @param attributeValue
     *            The attribute value.
     * @return The child DN.
     * @throws UnknownSchemaElementException
     *             If {@code attributeType} was not found in the default schema.
     * @throws NullPointerException
     *             If {@code attributeType} or {@code attributeValue} was
     *             {@code null}.
     */
    public DN child(final String attributeType, final Object attributeValue) {
        return child(new RDN(attributeType, attributeValue));
    }

    @Override
    public int compareTo(final DN dn) {
        return toNormalizedByteString().compareTo(dn.toNormalizedByteString());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DN) {
            DN otherDN = (DN) obj;
            return toNormalizedByteString().equals(otherDN.toNormalizedByteString());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == -1) {
            hashCode = toNormalizedByteString().hashCode();
        }
        return hashCode;
    }

    /**
     * Returns {@code true} if this DN is an immediate child of the provided DN.
     *
     * @param dn
     *            The potential parent DN.
     * @return {@code true} if this DN is the immediate child of the provided
     *         DN, otherwise {@code false}.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isChildOf(final DN dn) {
        // If this is the Root DN then parent will be null but this is ok.
        return dn.equals(parent);
    }

    /**
     * Returns {@code true} if this DN is an immediate child of the provided DN
     * decoded using the default schema.
     *
     * @param dn
     *            The potential parent DN.
     * @return {@code true} if this DN is the immediate child of the provided
     *         DN, otherwise {@code false}.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isChildOf(final String dn) {
        // If this is the Root DN then parent will be null but this is ok.
        return isChildOf(valueOf(dn));
    }

    /**
     * Returns {@code true} if this DN matches the provided base DN and search
     * scope.
     *
     * @param dn
     *            The base DN.
     * @param scope
     *            The search scope.
     * @return {@code true} if this DN matches the provided base DN and search
     *         scope, otherwise {@code false}.
     * @throws NullPointerException
     *             If {@code dn} or {@code scope} was {@code null}.
     */
    public boolean isInScopeOf(DN dn, SearchScope scope) {
        switch (scope.asEnum()) {
        case BASE_OBJECT:
            // The base DN must equal this DN.
            return equals(dn);
        case SINGLE_LEVEL:
            // The parent DN must equal the base DN.
            return isChildOf(dn);
        case SUBORDINATES:
            // This DN must be a descendant of the provided base DN, but
            // not equal to it.
            return isSubordinateOrEqualTo(dn) && !equals(dn);
        case WHOLE_SUBTREE:
            // This DN must be a descendant of the provided base DN.
            return isSubordinateOrEqualTo(dn);
        default:
            // This is a scope that we don't recognize.
            return false;
        }
    }

    /**
     * Returns {@code true} if this DN matches the provided base DN and search
     * scope.
     *
     * @param dn
     *            The base DN.
     * @param scope
     *            The search scope.
     * @return {@code true} if this DN matches the provided base DN and search
     *         scope, otherwise {@code false}.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} or {@code scope} was {@code null}.
     */
    public boolean isInScopeOf(String dn, SearchScope scope) {
        return isInScopeOf(valueOf(dn), scope);
    }

    /**
     * Returns {@code true} if this DN is the immediate parent of the provided
     * DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is the immediate parent of the provided
     *         DN, otherwise {@code false}.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isParentOf(final DN dn) {
        // If dn is the Root DN then parent will be null but this is ok.
        return equals(dn.parent);
    }

    /**
     * Returns {@code true} if this DN is the immediate parent of the provided
     * DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is the immediate parent of the provided
     *         DN, otherwise {@code false}.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isParentOf(final String dn) {
        // If dn is the Root DN then parent will be null but this is ok.
        return isParentOf(valueOf(dn));
    }

    /**
     * Returns {@code true} if this DN is the Root DN.
     *
     * @return {@code true} if this DN is the Root DN, otherwise {@code false}.
     */
    public boolean isRootDN() {
        return size == 0;
    }

    /**
     * Returns {@code true} if this DN is subordinate to or equal to the
     * provided DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is subordinate to or equal to the
     *         provided DN, otherwise {@code false}.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isSubordinateOrEqualTo(final DN dn) {
        if (size < dn.size) {
            return false;
        } else if (size == dn.size) {
            return equals(dn);
        } else {
            // dn is a potential superior of this.
            return parent(size - dn.size).equals(dn);
        }
    }

    /**
     * Returns {@code true} if this DN is subordinate to or equal to the
     * provided DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is subordinate to or equal to the
     *         provided DN, otherwise {@code false}.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isSubordinateOrEqualTo(final String dn) {
        return isSubordinateOrEqualTo(valueOf(dn));
    }

    /**
     * Returns {@code true} if this DN is superior to or equal to the provided
     * DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is superior to or equal to the provided
     *         DN, otherwise {@code false}.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isSuperiorOrEqualTo(final DN dn) {
        if (size > dn.size) {
            return false;
        } else if (size == dn.size) {
            return equals(dn);
        } else {
            // dn is a potential subordinate of this.
            return dn.parent(dn.size - size).equals(this);
        }
    }

    /**
     * Returns {@code true} if this DN is superior to or equal to the provided
     * DN.
     *
     * @param dn
     *            The potential child DN.
     * @return {@code true} if this DN is superior to or equal to the provided
     *         DN, otherwise {@code false}.
     * @throws LocalizedIllegalArgumentException
     *             If {@code dn} is not a valid LDAP string representation of a
     *             DN.
     * @throws NullPointerException
     *             If {@code dn} was {@code null}.
     */
    public boolean isSuperiorOrEqualTo(final String dn) {
        return isSuperiorOrEqualTo(valueOf(dn));
    }

    /**
     * Returns an iterator of the RDNs contained in this DN. The RDNs will be
     * returned in the order starting with this DN's RDN, followed by the RDN of
     * the parent DN, and so on.
     * <p>
     * Attempts to remove RDNs using an iterator's {@code remove()} method are
     * not permitted and will result in an {@code UnsupportedOperationException}
     * being thrown.
     *
     * @return An iterator of the RDNs contained in this DN.
     */
    @Override
    public Iterator<RDN> iterator() {
        return new Iterator<RDN>() {
            private DN dn = DN.this;

            @Override
            public boolean hasNext() {
                return dn.rdn != null;
            }

            @Override
            public RDN next() {
                if (dn.rdn == null) {
                    throw new NoSuchElementException();
                }

                final RDN rdn = dn.rdn;
                dn = dn.parent;
                return rdn;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns the DN whose content is the specified number of RDNs from this
     * DN. The following equivalences hold:
     *
     * <pre>
     * dn.localName(0).isRootDN();
     * dn.localName(1).equals(rootDN.child(dn.rdn()));
     * dn.localName(dn.size()).equals(dn);
     * </pre>
     *
     * Said otherwise, a new DN is built using {@code index} RDNs,
     * retained in the same order, starting from the left.
     *
     * @param index
     *            The number of RDNs to be included in the local name.
     * @return The DN whose content is the specified number of RDNs from this
     *         DN.
     * @throws IllegalArgumentException
     *             If {@code index} is less than zero.
     * @see #parent(int) for the reverse operation (starting from the right)
     */
    public DN localName(final int index) {
        Reject.ifFalse(index >= 0, "index less than zero");

        if (index == 0) {
            return ROOT_DN;
        } else if (index >= size) {
            return this;
        } else {
            final DN localName = new DN(this.schema, null, rdn, index);
            DN nextLocalName = localName;
            DN lastDN = parent;
            for (int i = index - 1; i > 0; i--) {
                nextLocalName.parent = new DN(this.schema, null, lastDN.rdn, i);
                nextLocalName = nextLocalName.parent;
                lastDN = lastDN.parent;
            }
            nextLocalName.parent = ROOT_DN;
            return localName;
        }
    }

    /**
     * Returns the DN which is the immediate parent of this DN, or {@code null}
     * if this DN is the Root DN.
     * <p>
     * This method is equivalent to:
     *
     * <pre>
     * parent(1);
     * </pre>
     *
     * @return The DN which is the immediate parent of this DN, or {@code null}
     *         if this DN is the Root DN.
     */
    public DN parent() {
        return parent;
    }

    /**
     * Returns the DN which is equal to this DN with the specified number of
     * RDNs removed. Note that if {@code index} is zero then this DN will be
     * returned (identity).
     *
     * Said otherwise, a new DN is built using {@code index} RDNs,
     * retained in the same order, starting from the right.
     *
     * @param index
     *            The number of RDNs to be removed.
     * @return The DN which is equal to this DN with the specified number of
     *         RDNs removed, or {@code null} if the parent of the Root DN is
     *         reached.
     * @throws IllegalArgumentException
     *             If {@code index} is less than zero.
     * @see #localName(int) for the reverse operation (starting from the left)
     */
    public DN parent(final int index) {
        // We allow size + 1 so that we can return null as the parent of the Root DN.
        Reject.ifTrue(index < 0, "index less than zero");

        if (index > size) {
            return null;
        }
        DN parentDN = this;
        for (int i = 0; parentDN != null && i < index; i++) {
            parentDN = parentDN.parent;
        }
        return parentDN;
    }

    /**
     * Returns the RDN of this DN, or {@code null} if this DN is the Root DN.
     * <p>
     * This is the equivalent of calling {@code dn.rdn(0)}.
     *
     * @return The RDN of this DN, or {@code null} if this DN is the Root DN.
     */
    public RDN rdn() {
        return rdn;
    }

    /**
     * Returns the RDN at the specified index for this DN,
     * or {@code null} if no such RDN exists.
     * <p>
     * Here is an example usage:
     * <pre>
     * DN.valueOf("ou=people,dc=example,dc=com").rdn(0) =&gt; "ou=people"
     * DN.valueOf("ou=people,dc=example,dc=com").rdn(1) =&gt; "dc=example"
     * DN.valueOf("ou=people,dc=example,dc=com").rdn(2) =&gt; "dc=com"
     * DN.valueOf("ou=people,dc=example,dc=com").rdn(3) =&gt; null
     * </pre>
     *
     * @param index
     *            The index of the requested RDN.
     * @return The RDN at the specified index, or {@code null} if no such RDN exists.
     * @throws IllegalArgumentException
     *             If {@code index} is less than zero.
     */
    public RDN rdn(int index) {
        DN parentDN = parent(index);
        return parentDN != null ? parentDN.rdn : null;
    }

    /**
     * Returns a copy of this DN whose parent DN, {@code fromDN}, has been
     * renamed to the new parent DN, {@code toDN}. If this DN is not subordinate
     * or equal to {@code fromDN} then this DN is returned (i.e. the DN is not
     * renamed).
     *
     * @param fromDN
     *            The old parent DN.
     * @param toDN
     *            The new parent DN.
     * @return The renamed DN, or this DN if no renaming was performed.
     * @throws NullPointerException
     *             If {@code fromDN} or {@code toDN} was {@code null}.
     */
    public DN rename(final DN fromDN, final DN toDN) {
        Reject.ifNull(fromDN, toDN);

        if (!isSubordinateOrEqualTo(fromDN)) {
            return this;
        } else if (equals(fromDN)) {
            return toDN;
        } else {
            return toDN.child(localName(size - fromDN.size));
        }
    }

    /**
     * Returns the number of RDN components in this DN.
     *
     * @return The number of RDN components in this DN.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the RFC 4514 string representation of this DN.
     *
     * @return The RFC 4514 string representation of this DN.
     * @see <a href="http://tools.ietf.org/html/rfc4514">RFC 4514 - Lightweight
     *      Directory Access Protocol (LDAP): String Representation of
     *      Distinguished Names </a>
     */
    @Override
    public String toString() {
        // We don't care about potential race conditions here.
        if (stringValue == null) {
            final StringBuilder builder = new StringBuilder();
            builder.append(rdn);
            for (DN dn = parent; dn.rdn != null; dn = dn.parent) {
                builder.append(RDN_CHAR_SEPARATOR);
                if (dn.stringValue != null) {
                    builder.append(dn.stringValue);
                    break;
                }
                builder.append(dn.rdn);
            }
            stringValue = builder.toString();
        }
        return stringValue;
    }

    /**
     * Retrieves a normalized byte string representation of this DN.
     * <p>
     * This representation is suitable for equality and comparisons, and
     * for providing a natural hierarchical ordering.
     * However, it is not a valid DN and can't be reverted to a valid DN.
     * You should consider using a {@code CompactDn} as an alternative.
     *
     * @return The normalized string representation of this DN.
     */
    public ByteString toNormalizedByteString() {
        if (normalizedDN == null) {
            if (rdn == null) {
                normalizedDN = ByteString.empty();
            } else {
                final ByteStringBuilder builder = new ByteStringBuilder(size * 8);
                if (parent.normalizedDN != null) {
                    builder.appendBytes(parent.normalizedDN);
                } else {
                    for (int i = size() - 1; i > 0; i--) {
                        parent(i).rdn().toNormalizedByteString(builder);
                    }
                }
                rdn.toNormalizedByteString(builder);
                normalizedDN = builder.toByteString();
            }
        }
        return normalizedDN;
    }

    /**
     * Retrieves a normalized string representation of this DN.
     * <p>
     * This representation is safe to use in an URL or in a file name.
     * However, it is not a valid DN and can't be reverted to a valid DN.
     *
     * @return The normalized string representation of this DN.
     */
    public String toNormalizedUrlSafeString() {
        if (rdn() == null) {
            return "";
        }

        // This code differs from toNormalizedByteString(),
        // because we do not care about ordering comparisons.
        // (so we do not append the RDN_SEPARATOR first)
        final StringBuilder builder = new StringBuilder();
        int i = size() - 1;
        parent(i).rdn().toNormalizedUrlSafeString(builder);
        for (i--; i >= 0; i--) {
            final RDN rdn = parent(i).rdn();
            // Only add a separator if the RDN is not RDN.maxValue() or RDN.minValue().
            if (rdn.size() != 0) {
                builder.append(RDN_CHAR_SEPARATOR);
            }
            rdn.toNormalizedUrlSafeString(builder);
        }
        return builder.toString();
    }

    /**
     * Returns a UUID whose content is based on the normalized content of this DN.
     * Two equivalent DNs subject to the same schema will always yield the same UUID.
     *
     * @return the UUID representing this DN
     */
    public UUID toUUID() {
        ByteString normDN = toNormalizedByteString();
        if (!normDN.isEmpty()) {
            // remove leading RDN separator
            normDN = normDN.subSequence(1, normDN.length());
        }
        return UUID.nameUUIDFromBytes(normDN.toByteArray());
    }

}
