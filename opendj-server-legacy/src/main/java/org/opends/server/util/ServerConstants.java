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
 * Copyright 2006-2010 Sun Microsystems, Inc.
 * Portions Copyright 2010-2016 ForgeRock AS.
 * Portions Copyright 2023 Wren Security.
 */
package org.opends.server.util;

import org.forgerock.opendj.ldap.ByteString;

/**
 * This class defines a set of constants that may be referenced throughout the
 * Directory Server source.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.UNCOMMITTED,
     mayInstantiate=false,
     mayExtend=false,
     mayInvoke=true)
public final class ServerConstants
{
  /** The end-of-line character for this platform. */
  public static final String EOL = System.getProperty("line.separator");



  /**
   * The value that will be used in the configuration for Boolean elements with
   * a value of "true".
   */
  public static final String CONFIG_VALUE_TRUE = "true";



  /**
   * The value that will be used in the configuration for Boolean elements with
   * a value of "false".
   */
  public static final String CONFIG_VALUE_FALSE = "false";



  /**
   * The date format string that will be used to construct and parse dates
   * represented in a form like UTC time, but using the local time zone.
   */
  public static final String DATE_FORMAT_COMPACT_LOCAL_TIME =
       "yyyyMMddHHmmss";



  /**
   * The date format string that will be used to construct and parse dates
   * represented using generalized time.  It is assumed that the provided date
   * formatter will be set to UTC.
   */
  public static final String DATE_FORMAT_GENERALIZED_TIME =
       "yyyyMMddHHmmss.SSS'Z'";



  /**
   * The date format string that will be used to construct and parse dates
   * represented using generalized time.  It is assumed that the provided date
   * formatter will be set to UTC.
   */
  public static final String DATE_FORMAT_LOCAL_TIME =
       "dd/MMM/yyyy:HH:mm:ss Z";



  /**
   * The date format string that will be used to construct and parse dates
   * represented using generalized time with a four-digit year.  It is assumed
   * that the provided date formatter will be set to UTC.
   */
  public static final String DATE_FORMAT_GMT_TIME =
       "yyyyMMddHHmmss'Z'";



  /**
   * The date format string that will be used to construct and parse dates
   * represented using generalized time with a two-digit year.  It is assumed
   * that the provided date formatter will be set to UTC.
   */
  public static final String DATE_FORMAT_UTC_TIME =
       "yyMMddHHmmss'Z'";



  /**
   * The name of the time zone for universal coordinated time (UTC).
   */
  public static final String TIME_ZONE_UTC = "UTC";



  /**
   * The name of the standard attribute that is used to specify the target DN in
   * an alias entry, formatted in all lowercase.
   */
  public static final String ATTR_ALIAS_DN = "aliasedobjectname";



  /**
   * The name of the standard attribute that is used to hold country names,
   * formatted in all lowercase.
   */
  public static final String ATTR_C = "c";



  /**
   * The name of the standard attribute that is used to hold common names,
   * formatted in all lowercase.
   */
  public static final String ATTR_COMMON_NAME = "cn";



  /**
   * The name of the attribute that is used to specify the number of connections
   * currently established, formatted in camel case.
   */
  public static final String ATTR_CURRENT_CONNS = "currentConnections";



  /**
   * The name of the attribute that is used to specify the number of connections
   * currently established, formatted in all lowercase.
   */
  public static final String ATTR_CURRENT_CONNS_LC = "currentconnections";



  /**
   * The name of the attribute that is used to specify the current time,
   * formatted in camel case.
   */
  public static final String ATTR_CURRENT_TIME = "currentTime";



  /**
   * The name of the attribute that is used to specify the current time,
   * formatted in all lowercase.
   */
  public static final String ATTR_CURRENT_TIME_LC = "currenttime";



  /**
   * The name of the standard attribute that is used to hold domain component
   * names, formatted in all lowercase.
   */
  public static final String ATTR_DC = "dc";



  /**
   * The name of the attribute that is used to specify the maximum number of
   * connections established at any time since startup, formatted in camel case.
   */
  public static final String ATTR_MAX_CONNS = "maxConnections";



  /**
   * The name of the attribute that is used to specify the maximum number of
   * connections established at any time since startup, formatted in all
   * lowercase.
   */
  public static final String ATTR_MAX_CONNS_LC = "maxconnections";



  /**
   * The name of the standard "member" attribute type, formatted in all
   * lowercase characters.
   */
  public static final String ATTR_MEMBER = "member";



  /**
   * The name of the standard "memberURL" attribute type, formatted in camel
   * case.
   */
  public static final String ATTR_MEMBER_URL = "memberURL";



  /**
   * The name of the standard "memberURL" attribute type, formatted in all
   * lowercase characters.
   */
  public static final String ATTR_MEMBER_URL_LC = "memberurl";



  /**
   * The name of the standard "subtreeSpecification" attribute type,
   * formatted in camel case.
   */
  public static final String ATTR_SUBTREE_SPEC = "subtreeSpecification";



  /**
   * The name of the standard "subtreeSpecification" attribute type,
   * formatted in all lowercase characters.
   */
  public static final String ATTR_SUBTREE_SPEC_LC = "subtreespecification";



  /**
   * The name of the standard "collectiveExclusions" attribute type,
   * formatted in camel case.
   */
  public static final String ATTR_COLLECTIVE_EXCLUSIONS =
          "collectiveExclusions";



  /**
   * The name of the standard "collectiveExclusions" attribute type,
   * formatted in all lowercase characters.
   */
  public static final String ATTR_COLLECTIVE_EXCLUSIONS_LC =
          "collectiveexclusions";



  /**
   * The value of the standard "excludeAllCollectiveAttributes" attribute
   * value of the standard "collectiveExclusions" attribute type,
   * formatted in camel case.
   */
  public static final String VALUE_COLLECTIVE_EXCLUSIONS_EXCLUDE_ALL =
          "excludeAllCollectiveAttributes";



  /**
   * The value of the standard "excludeAllCollectiveAttributes" attribute
   * value of the standard "collectiveExclusions" attribute type,
   * formatted in all lowercase characters.
   */
  public static final String VALUE_COLLECTIVE_EXCLUSIONS_EXCLUDE_ALL_LC =
          "excludeallcollectiveattributes";



  /**
   * The OID of the standard "excludeAllCollectiveAttributes" attribute
   * value of the standard "collectiveExclusions" attribute type.
   */
  public static final String OID_COLLECTIVE_EXCLUSIONS_EXCLUDE_ALL =
          "2.5.18.0";



  /**
   * The name of the monitor attribute that is used to hold a backend ID.
   */
  public static final String ATTR_MONITOR_BACKEND_ID = "ds-backend-id";



  /**
   * The name of the monitor attribute that is used to hold the set of base DNs.
   */
  public static final String ATTR_MONITOR_BACKEND_BASE_DN =
       "ds-backend-base-dn";



  /**
   * The name of the monitor attribute that is used to indicate whether a
   * backend is private.
   */
  public static final String ATTR_MONITOR_BACKEND_IS_PRIVATE =
       "ds-backend-is-private";



  /**
   * The name of the monitor attribute that is used to hold the backend entry
   * count.
   */
  public static final String ATTR_MONITOR_BACKEND_ENTRY_COUNT =
       "ds-backend-entry-count";


  /**
   * The name of the monitor attribute that is used to hold the base DN entry
   * count.
   */
  public static final String ATTR_MONITOR_BASE_DN_ENTRY_COUNT =
       "ds-base-dn-entry-count";

  /**
   * The name of the monitor attribute that is used to hold the backend
   * writability mode.
   */
  public static final String ATTR_MONITOR_BACKEND_WRITABILITY_MODE =
       "ds-backend-writability-mode";



  /**
   * The name of the monitor attribute that is used to hold the connection
   * handler connections.
   */
  public static final String ATTR_MONITOR_CONNHANDLER_CONNECTION =
       "ds-connectionhandler-connection";



  /**
   * The name of the monitor attribute that is used to hold the connection
   * handler listeners.
   */
  public static final String ATTR_MONITOR_CONNHANDLER_LISTENER =
       "ds-connectionhandler-listener";

   /**
   * The name of the monitor attribute that is used to hold the connection
   * handler listeners.
   */
  public static final String ATTR_MONITOR_CONFIG_DN =
       "ds-mon-config-dn";


  /**
   * The name of the monitor attribute that is used to hold the connection
   * handler number of established connections.
   */
  public static final String ATTR_MONITOR_CONNHANDLER_NUMCONNECTIONS =
       "ds-connectionhandler-num-connections";



  /**
   * The name of the monitor attribute that is used to hold the connection
   * handler protocol.
   */
  public static final String ATTR_MONITOR_CONNHANDLER_PROTOCOL =
       "ds-connectionhandler-protocol";



  /**
   * The name of the standard attribute that is used to specify the set of
   * public naming contexts (suffixes) for the Directory Server, formatted in
   * camel case.
   */
  public static final String ATTR_NAMING_CONTEXTS = "namingContexts";



  /**
   * The name of the standard attribute that is used to specify the set of
   * public naming contexts (suffixes) for the Directory Server, formatted in
   * all lowercase.
   */
  public static final String ATTR_NAMING_CONTEXTS_LC = "namingcontexts";



  /**
   * The name of the attribute used to hold the DNs that constitute the set of
   * "private" naming contexts registered with the server.
   */
  public static final String ATTR_PRIVATE_NAMING_CONTEXTS =
       "ds-private-naming-contexts";



  /**
   * The name of the standard attribute that is used to hold organization names,
   * formatted in all lowercase.
   */
  public static final String ATTR_O = "o";



  /**
   * The name of the standard attribute that is used to hold organizational unit
   * names, formatted in all lowercase.
   */
  public static final String ATTR_OU = "ou";



  /**
   * The name of the standard attribute that is used to specify the name of the
   * Directory Server product, formatted in camel case.
   */
  public static final String ATTR_PRODUCT_NAME = "productName";



  /**
   * The name of the standard attribute that is used to specify the name of the
   * Directory Server product, formatted in all lowercase.
   */
  public static final String ATTR_PRODUCT_NAME_LC = "productname";



  /**
   * The name of the standard attribute that is used to specify the set of
   * referral URLs in a smart referral entry, formatted in all lowercase.
   */
  public static final String ATTR_REFERRAL_URL = "ref";



  /**
   * The name of the standard attribute that is used to hold surnames, formatted
   * in all lowercase.
   */
  public static final String ATTR_SN = "sn";



  /**
   * The name of the standard attribute that is used to specify the location
   * for the Directory Server schema, formatted in camel case.
   */
  public static final String ATTR_SUBSCHEMA_SUBENTRY = "subschemaSubentry";



  /**
   * The name of the standard attribute that is used to specify the location
   * for the Directory Server schema, formatted in all lowercase.
   */
  public static final String ATTR_SUBSCHEMA_SUBENTRY_LC = "subschemasubentry";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * authentication password schemes supported by the server, formatted in
   * camel case.
   */
  public static final String ATTR_SUPPORTED_AUTH_PW_SCHEMES =
       "supportedAuthPasswordSchemes";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * authentication password schemes supported by the server, formatted in all
   * lowercase.
   */
  public static final String ATTR_SUPPORTED_AUTH_PW_SCHEMES_LC =
       "supportedauthpasswordschemes";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * controls supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_CONTROL = "supportedControl";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * controls supported by the server, formatted in all lowercase.
   */
  public static final String ATTR_SUPPORTED_CONTROL_LC = "supportedcontrol";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * extended operations supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_EXTENSION = "supportedExtension";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * extended operations supported by the server, formatted in all lowercase.
   */
  public static final String ATTR_SUPPORTED_EXTENSION_LC = "supportedextension";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * features supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_FEATURE = "supportedFeatures";



  /**
   * The name of the standard attribute that is used to specify the OIDs of the
   * features supported by the server, formatted in all lowercase.
   */
  public static final String ATTR_SUPPORTED_FEATURE_LC = "supportedfeatures";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * LDAP protocol versions supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_LDAP_VERSION =
       "supportedLDAPVersion";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * LDAP protocol versions supported by the server, formatted in all lowercase.
   */
  public static final String ATTR_SUPPORTED_LDAP_VERSION_LC =
       "supportedldapversion";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * SASL mechanisms supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_SASL_MECHANISMS =
       "supportedSASLMechanisms";



  /**
   * The name of the standard attribute that is used to specify the names of the
   * SASL mechanisms supported by the server, formatted in all lowercase.
   */
  public static final String ATTR_SUPPORTED_SASL_MECHANISMS_LC =
       "supportedsaslmechanisms";



  /**
   * The name of the standard attribute that is used to specify the versions of
   * the TLS protocol supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_TLS_PROTOCOLS =
      "supportedTLSProtocols";



  /**
   * The name of the standard attribute that is used to specify the versions of
   * the TLS protocol supported by the server, formatted in lower case.
   */
  public static final String ATTR_SUPPORTED_TLS_PROTOCOLS_LC =
      "supportedtlsprotocols";



  /**
   * The name of the standard attribute that is used to specify the the TLS
   * ciphers supported by the server, formatted in camel case.
   */
  public static final String ATTR_SUPPORTED_TLS_CIPHERS =
      "supportedTLSCiphers";



  /**
   * The name of the standard attribute that is used to specify the the TLS
   * ciphers supported by the server, formatted in lower case.
   */
  public static final String ATTR_SUPPORTED_TLS_CIPHERS_LC =
      "supportedtlsciphers";



  /**
   * The name of the attribute that is used to specify the time that the
   * Directory Server started, formatted in camel case.
   */
  public static final String ATTR_START_TIME = "startTime";



  /**
   * The name of the attribute that is used to specify the time that the
   * Directory Server started, formatted in all lowercase.
   */
  public static final String ATTR_START_TIME_LC = "starttime";



  /**
   * The name of the attribute that is used to specify the DN of the target
   * group for a virtual static group.
   */
  public static final String ATTR_TARGET_GROUP_DN = "ds-target-group-dn";



  /**
   * The name of the attribute that is used to specify the connection ID of the
   * connection to disconnect.
   */
  public static final String ATTR_TASK_DISCONNECT_CONN_ID =
       "ds-task-disconnect-connection-id";



  /**
   * The name of the attribute that is used to specify the disconnect message.
   */
  public static final String ATTR_TASK_DISCONNECT_MESSAGE =
       "ds-task-disconnect-message";



  /**
   * The name of the attribute that is used to indicate whether to notify the
   * connection it is about to be terminated.
   */
  public static final String ATTR_TASK_DISCONNECT_NOTIFY_CLIENT =
       "ds-task-disconnect-notify-client";



  /**
   * The name of the attribute that is used to specify the total number of
   * connections established since startup, formatted in camel case.
   */
  public static final String ATTR_TOTAL_CONNS = "totalConnections";



  /**
   * The name of the attribute that is used to specify the total number of
   * connections established since startup, formatted in all lowercase.
   */
  public static final String ATTR_TOTAL_CONNS_LC = "totalconnections";



  /**
   * The name of the standard "uniqueMember" attribute type, formatted in
   * camelCase.
   */
  public static final String ATTR_UNIQUE_MEMBER = "uniqueMember";



  /**
   * The name of the standard "uniqueMember" attribute type, formatted in all
   * lowercase characters.
   */
  public static final String ATTR_UNIQUE_MEMBER_LC = "uniquemember";



  /**
   * The name of the attribute that is used to specify the length of time that
   * the server has been online, formatted in camel case.
   */
  public static final String ATTR_UP_TIME = "upTime";



  /**
   * The name of the attribute that is used to specify the length of time that
   * the server has been online, formatted in all lowercase.
   */
  public static final String ATTR_UP_TIME_LC = "uptime";



  /**
   * The name of the standard attribute that is used to specify the password for
   * a user, formatted in all lowercase.
   */
  public static final String ATTR_USER_PASSWORD = "userpassword";



  /**
   * The name of the standard attribute that is used to specify vendor name for
   * the Directory Server, formatted in camel case.
   */
  public static final String ATTR_VENDOR_NAME = "vendorName";



  /**
   * The name of the standard attribute that is used to specify vendor name for
   * the Directory Server, formatted in all lowercase.
   */
  public static final String ATTR_VENDOR_NAME_LC = "vendorname";



  /**
   * The name of the standard attribute that is used to specify vendor version
   * for the Directory Server, formatted in camel case.
   */
  public static final String ATTR_VENDOR_VERSION = "vendorVersion";



  /**
   * The name of the standard attribute that is used to specify vendor version
   * for the Directory Server, formatted in all lowercase.
   */
  public static final String ATTR_VENDOR_VERSION_LC = "vendorversion";



  /**
   * The name of the standard objectclass that is used to indicate that an entry
   * is an alias, formatted in all lowercase.
   */
  public static final String OC_ALIAS = "alias";



  /**
   * The name of the standard objectclass, formatted in all lowercase, that is
   * used to indicate that an entry describes a country.
   */
  public static final String OC_COUNTRY = "country";



  /**
   * The name of the standard objectclass, formatted in all lowercase, that is
   * used to indicate that an entry describes a domain.
   */
  public static final String OC_DOMAIN = "domain";


  /**
   * The name of the standard objectclass that is used to allow any attribute
   * type to be present in an entry, formatted in camel case.
   */
  public static final String OC_EXTENSIBLE_OBJECT = "extensibleObject";


  /**
   * The name of the standard objectclass that is used to allow any attribute
   * type to be present in an entry, formatted in all lowercase characters.
   */
  public static final String OC_EXTENSIBLE_OBJECT_LC = "extensibleobject";



  /**
   * The name of the standard "groupOfEntries" object class, formatted in
   * camelCase.
   */
  public static final String OC_GROUP_OF_ENTRIES = "groupOfEntries";



  /**
   * The name of the standard "groupOfEntries" object class, formatted in all
   * lowercase characters.
   */
  public static final String OC_GROUP_OF_ENTRIES_LC = "groupofentries";



  /**
   * The name of the standard "groupOfNames" object class, formatted in
   * camelCase.
   */
  public static final String OC_GROUP_OF_NAMES = "groupOfNames";



  /**
   * The name of the standard "groupOfNames" object class, formatted in all
   * lowercase characters.
   */
  public static final String OC_GROUP_OF_NAMES_LC = "groupofnames";



  /**
   * The name of the standard "groupOfUniqueNames" object class, formatted in
   * camelCase.
   */
  public static final String OC_GROUP_OF_UNIQUE_NAMES = "groupOfUniqueNames";



  /**
   * The name of the standard "groupOfUniqueNames" object class, formatted in
   * all lowercase characters.
   */
  public static final String OC_GROUP_OF_UNIQUE_NAMES_LC = "groupofuniquenames";



  /**
   * The name of the standard "groupOfURLs" object class, formatted in camel
   * case.
   */
  public static final String OC_GROUP_OF_URLS = "groupOfURLs";



  /**
   * The name of the standard "groupOfURLs" object class, formatted in all
   * lowercase characters.
   */
  public static final String OC_GROUP_OF_URLS_LC = "groupofurls";


  /**
   * The name of the objectclass that will be used as the structural class for
   * monitor entries.
   */
  public static final String OC_CHANGELOG_ENTRY = "changeLogEntry";


  /**
   * The request OID for the cancel extended operation.
   */
  public static final String OID_CANCEL_REQUEST = "1.3.6.1.1.8";



  /**
   * The OID for the extensibleObject objectclass.
   */
  public static final String OID_EXTENSIBLE_OBJECT =
       "1.3.6.1.4.1.1466.101.120.111";



  /**
   * The OID for the extended operation that can be used to get the client
   * connection ID.  It will be both the request and response OID.
   */
  public static final String OID_GET_CONNECTION_ID_EXTOP =
       "1.3.6.1.4.1.26027.1.6.2";



  /**
   * The request OID for the password modify extended operation.
   */
  public static final String OID_PASSWORD_MODIFY_REQUEST =
       "1.3.6.1.4.1.4203.1.11.1";



  /**
   * The OID for the password policy state extended operation (both the request
   * and response types).
   */
  public static final String OID_PASSWORD_POLICY_STATE_EXTOP =
       "1.3.6.1.4.1.26027.1.6.1";



  /**
   * The request OID for the StartTLS extended operation.
   */
  public static final String OID_START_TLS_REQUEST = "1.3.6.1.4.1.1466.20037";



  /**
   * The request OID for the "Who Am I?" extended operation.
   */
  public static final String OID_WHO_AM_I_REQUEST =
       "1.3.6.1.4.1.4203.1.11.3";



  /**
   * The request OID for the get symmetric key extended operation.
   */
  public static final String OID_GET_SYMMETRIC_KEY_EXTENDED_OP =
       "1.3.6.1.4.1.26027.1.6.3";



  /**
   * The name of the standard "ldapSubentry" objectclass (which is a special
   * type of objectclass that makes a kind of "operational" entry), formatted
   * in camel case.
   */
  public static final String OC_LDAP_SUBENTRY = "ldapSubentry";



  /**
   * The name of the standard "ldapSubentry" objectclass (which is a special
   * type of objectclass that makes a kind of "operational" entry), formatted
   * in all lowercase.
   */
  public static final String OC_LDAP_SUBENTRY_LC = "ldapsubentry";



  /**
   * The name of the RFC 3672 "subentry" objectclass (which is a special
   * type of objectclass that makes a kind of "operational" entry),
   * formatted in all lowercase.
   */
  public static final String OC_SUBENTRY = "subentry";



  /**
   * The name of the RFC 3671 "collectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of shared
   * attributes subentry), formatted in camel case.
   */
  public static final String OC_COLLECTIVE_ATTR_SUBENTRY =
          "collectiveAttributeSubentry";



  /**
   * The name of the RFC 3671 "collectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of shared
   * attributes subentry), formatted in all lowercase.
   */
  public static final String OC_COLLECTIVE_ATTR_SUBENTRY_LC =
          "collectiveattributesubentry";



  /**
   * The name of the "inheritedCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of COS
   * template subentry), formatted in camel case.
   */
  public static final String OC_INHERITED_COLLECTIVE_ATTR_SUBENTRY =
          "inheritedCollectiveAttributeSubentry";



  /**
   * The name of the "inheritedCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of COS
   * template subentry), formatted in all lowercase.
   */
  public static final String OC_INHERITED_COLLECTIVE_ATTR_SUBENTRY_LC =
          "inheritedcollectiveattributesubentry";



  /**
   * The name of the "inheritedFromDNCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of indirect
   * COS template subentry), formatted in camel case.
   */
  public static final String OC_INHERITED_FROM_DN_COLLECTIVE_ATTR_SUBENTRY =
          "inheritedFromDNCollectiveAttributeSubentry";



  /**
   * The name of the "inheritedFromDNCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of indirect
   * COS template subentry), formatted in all lowercase.
   */
  public static final String OC_INHERITED_FROM_DN_COLLECTIVE_ATTR_SUBENTRY_LC =
          "inheritedfromdncollectiveattributesubentry";



  /**
   * The name of the "inheritedFromRDNCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of classic
   * COS template subentry), formatted in camel case.
   */
  public static final String OC_INHERITED_FROM_RDN_COLLECTIVE_ATTR_SUBENTRY =
          "inheritedFromRDNCollectiveAttributeSubentry";



  /**
   * The name of the "inheritedFromRDNCollectiveAttributeSubentry" objectclass
   * (which is a special type of objectclass that makes a kind of classic
   * COS template subentry), formatted in all lowercase.
   */
  public static final String OC_INHERITED_FROM_RDN_COLLECTIVE_ATTR_SUBENTRY_LC =
          "inheritedfromrdncollectiveattributesubentry";



  /**
   * The name of the LDAP Password Policy Internet-Draft
   * "pwdPolicy" objectclass, formatted in camel case.
   */
  public static final String OC_PWD_POLICY_SUBENTRY = "pwdPolicy";



  /**
   * The name of the LDAP Password Policy Internet-Draft
   * "pwdPolicy" objectclass, formatted in all lowercase.
   */
  public static final String OC_PWD_POLICY_SUBENTRY_LC = "pwdpolicy";



  /**
   * The name of the custom objectclass that will be included in backend monitor
   * entries.
   */
  public static final String OC_MONITOR_BACKEND = "ds-backend-monitor-entry";



  /**
   * The name of the custom objectclass that will be included in connection
   * handler monitor entries.
   */
  public static final String OC_MONITOR_CONNHANDLER =
       "ds-connectionhandler-monitor-entry";
  /**
   * The name of the custom objectclass that will be included in connection
   * handler statistics monitor entries.
   */
  public static final String OC_MONITOR_CONNHANDLERSTATS =
          "ds-connectionhandler-statistics-monitor-entry";


  /**
   * The name of the objectclass that will be used as the structural class for
   * monitor entries.
   */
  public static final String OC_MONITOR_ENTRY = "ds-monitor-entry";

   /**
   * The name of the objectclass that will be used as the structural class for
   * monitor branches.
   */
  public static final String OC_MONITOR_BRANCH = "ds-mon-branch";


  /**
   * The name of the standard objectclass, formatted in all lowercase, that is
   * used to indicate that an entry describes an organization.
   */
  public static final String OC_ORGANIZATION = "organization";



  /**
   * The name of the standard objectclass that is  used to indicate that an
   * entry describes an organizational unit.
   */
  public static final String OC_ORGANIZATIONAL_UNIT = "organizationalUnit";



  /**
   * The name of the organizationalUnit objectclass formatted in all lowercase
   * characters.
   */
  public static final String OC_ORGANIZATIONAL_UNIT_LC = "organizationalunit";



  /**
   * The name of the person objectclass, formatted in all lowercase characters.
   */
  public static final String OC_PERSON = "person";



  /**
   * The name of the standard objectclass that is used to indicate that an entry
   * is a smart referral, formatted in all lowercase.
   */
  public static final String OC_REFERRAL = "referral";



  /**
   * The name of the structural objectclass that will be used for the Directory
   * Server root DSE entry.
   */
  public static final String OC_ROOT_DSE = "ds-root-dse";



  /**
   * The name of the standard "subschema" objectclass (which is used in entries
   * that publish schema information), formatted in all lowercase.
   */
  public static final String OC_SUBSCHEMA = "subschema";




  /**
   * The name of the standard "top" objectclass, which is the superclass for
   * virtually all other objectclasses, formatted in all lowercase.
   */
  public static final String OC_TOP= "top";



  /**
   * The name of the objectclass that can be used for generic entries for which
   * we don't have any other type of objectclass that is more appropriate.
   */
  public static final String OC_UNTYPED_OBJECT = "untypedObject";



  /**
   * The name of the untypedObject objectclass in all lowercase characters.
   */
  public static final String OC_UNTYPED_OBJECT_LC = "untypedobject";



  /**
   * The name of the ds-virtual-static-group objectclass in all lowercase
   * characters.
   */
  public static final String OC_VIRTUAL_STATIC_GROUP =
       "ds-virtual-static-group";



  /**
   * The English name for the basic disabled log severity used for all
   * log severities.
   */
  public static final String LOG_SEVERITY_DISABLED = "disabled";

  /**
   * The English name for the basic all log severity used for all log
   * severities.
   */
  public static final String LOG_SEVERITY_ALL = "all";

  /**
   * The English name for the basic none log severity used to log
   * no error message beside some specific category.
   */
  public static final String LOG_SEVERITY_NONE = "none";


  /**
   * The English name for the error log category used for access control
   * processing.
   */
  public static final String ERROR_CATEGORY_ACCESS_CONTROL = "access-control";



  /**
   * The English name for the error log category used for backend processing.
   */
  public static final String ERROR_CATEGORY_BACKEND = "backend";



  /**
   * The English name for the error log category used for configuration
   * processing.
   */
  public static final String ERROR_CATEGORY_CONFIG = "config";



  /**
   * The English name for the error log category used for client connection
   * handling.
   */
  public static final String ERROR_CATEGORY_CONNECTION_HANDLING = "connection";



  /**
   * The English name for the error log category used for core server
   * processing.
   */
  public static final String ERROR_CATEGORY_CORE_SERVER = "core";


  /**
   * The English name for the error log category used for extended operation
   * processing.
   */
  public static final String ERROR_CATEGORY_EXTENDED_OPERATION = "extended-op";



  /**
   * The English name for the error log category used for server extension
   * processing.
   */
  public static final String ERROR_CATEGORY_EXTENSIONS = "extensions";



  /**
   * The English name for the error log category used for password policy
   * processing.
   */
  public static final String ERROR_CATEGORY_PASSWORD_POLICY = "pw-policy";



  /**
   * The English name for the error log category used for plugin processing.
   */
  public static final String ERROR_CATEGORY_PLUGIN = "plugin";



  /**
   * The English name for the error log category used for request handling.
   */
  public static final String ERROR_CATEGORY_REQUEST = "request";



  /**
   * The English name for the error log category used for SASL processing.
   */
  public static final String ERROR_CATEGORY_SASL_MECHANISM = "sasl";



  /**
   * The English name for the error log category used for schema processing.
   */
  public static final String ERROR_CATEGORY_SCHEMA = "schema";



  /**
   * The English name for the error log category used for shutdown processing.
   */
  public static final String ERROR_CATEGORY_SHUTDOWN = "shutdown";



  /**
   * The English name for the error log category used for startup processing.
   */
  public static final String ERROR_CATEGORY_STARTUP = "startup";



  /**
   * The English name for the error log category used for synchronization
   * processing.
   */
  public static final String ERROR_CATEGORY_SYNCHRONIZATION = "sync";



  /**
   * The English name for the error log category used for task processing.
   */
  public static final String ERROR_CATEGORY_TASK = "task";



  /**
   * The English name for the error log severity used for fatal error messages.
   */
  public static final String ERROR_SEVERITY_ERROR = "error";

  /**
   * The English name for the error log severity used for informational
   * messages.
   */
  public static final String ERROR_SEVERITY_INFORMATIONAL = "info";


  /**
   * The English name for the error log severity used for warning messages.
   */
  public static final String ERROR_SEVERITY_WARNING = "warning";


  /**
   * The English name for the error log severity used for important
   * informational messages.
   */
  public static final String ERROR_SEVERITY_NOTICE = "notice";


  /**
   * The domain that will be used for JMX MBeans defined within the Directory
   * Server.
   */
  public static final String MBEAN_BASE_DOMAIN = "org.opends.server";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the access control handler is disabled.
   */
  public static final String ALERT_DESCRIPTION_ACCESS_CONTROL_DISABLED =
       "This alert type will be used to notify administrators that the " +
       "access control handler has been disabled.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if the access control handler is disabled.
   */
  public static final String ALERT_TYPE_ACCESS_CONTROL_DISABLED =
       "org.opends.server.AccessControlDisabled";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the access control handler is enabled.
   */
  public static final String ALERT_DESCRIPTION_ACCESS_CONTROL_ENABLED =
       "This alert type will be used to notify administrators that the " +
       "access control handler has been enabled.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if the access control handler is enabled.
   */
  public static final String ALERT_TYPE_ACCESS_CONTROL_ENABLED =
       "org.opends.server.AccessControlEnabled";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an error occurs while attempting to rename the
   * current tasks backing file.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_RENAME_CURRENT_TASK_FILE =
           "This alert type will be used to notify administrators if the " +
           "Directory Server is unable to rename the current tasks backing " +
           "file in the process of trying to write an updated version.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an error occurs while attempting to rename the current tasks
   * backing file.
   */
  public static final String ALERT_TYPE_CANNOT_RENAME_CURRENT_TASK_FILE =
       "org.opends.server.CannotRenameCurrentTaskFile";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an error occurs while attempting to rename the
   * new tasks backing file.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_RENAME_NEW_TASK_FILE =
           "This alert type will be used to notify administrators if the " +
           "Directory Server is unable to rename the new tasks backing " +
           "file into place.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an error occurs while attempting to rename the new tasks
   * backing file.
   */
  public static final String ALERT_TYPE_CANNOT_RENAME_NEW_TASK_FILE =
       "org.opends.server.CannotRenameNewTaskFile";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an error occurs while attempting to schedule an
   * iteration of a recurring task.
   */
  public static final String
       ALERT_DESCRIPTION_CANNOT_SCHEDULE_RECURRING_ITERATION =
           "This alert type will be used to notify administrators if the " +
           "Directory Server is unable to schedule an iteration of a " +
           "recurring task.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an error occurs while attempting to schedule an iteration of a
   * recurring task.
   */
  public static final String ALERT_TYPE_CANNOT_SCHEDULE_RECURRING_ITERATION =
       "org.opends.server.CannotScheduleRecurringIteration";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if a problem occurs while attempting to write the
   * Directory Server configuration to disk.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_WRITE_CONFIGURATION =
      "This alert type will be used to notify administrators if the " +
      "Directory Server is unable to write its updated configuration for " +
      "some reason and therefore the server may not exhibit the new " +
      "configuration if it is restarted.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a problem occurs while attempting to write the Directory
   * Server configuration to disk.
   */
  public static final String ALERT_TYPE_CANNOT_WRITE_CONFIGURATION =
       "org.opends.server.CannotWriteConfig";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated upon entering lockdown mode.
   */
  public static final String ALERT_DESCRIPTION_ENTERING_LOCKDOWN_MODE =
       "This alert type will be used to notify administrators that the " +
       "Directory Server is entering lockdown mode, in which only root " +
       "users will be allowed to perform operations and only over the " +
       "loopback address.";



  /**
   * The alert type that will be used when the Directory Server enters lockdown
   * mode.
   */
  public static final String ALERT_TYPE_ENTERING_LOCKDOWN_MODE =
       "org.opends.server.EnteringLockdownMode";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated upon leaving lockdown mode.
   */
  public static final String ALERT_DESCRIPTION_LEAVING_LOCKDOWN_MODE =
       "This alert type will be used to notify administrators that the " +
       "Directory Server is leaving lockdown mode.";



  /**
   * The alert type that will be used when the Directory Server leaves lockdown
   * mode.
   */
  public static final String ALERT_TYPE_LEAVING_LOCKDOWN_MODE =
       "org.opends.server.LeavingLockdownMode";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the server detects that the configuration has
   * been manually edited with the server online and those edits would have been
   * lost by an online config change.
   */
  public static final String ALERT_DESCRIPTION_MANUAL_CONFIG_EDIT_HANDLED =
      "This alert type will be used to notify administrators if the " +
      "Directory Server detects that its configuration has been manually " +
      "edited with the server online and those changes were overwritten by " +
      "another change made through the server.  The manually-edited " +
      "configuration will be copied to another location.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a problem occurs while attempting to write the Directory
   * Server configuration to disk.
   */
  public static final String ALERT_TYPE_MANUAL_CONFIG_EDIT_HANDLED =
       "org.opends.server.ManualConfigEditHandled";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the server detects that the configuration has
   * been manually edited with the server online, but a problem occurred while
   * trying to preserve the manual changes that may have caused them to be lost.
   */
  public static final String ALERT_DESCRIPTION_MANUAL_CONFIG_EDIT_LOST =
      "This alert type will be used to notify administrators if the " +
      "Directory Server detects that its configuration has been manually " +
      "edited with the server online and those changes were overwritten by " +
      "another change made through the server.  The manually-edited " +
      "configuration could not be preserved due to an unexpected error.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a problem occurs while attempting to write the Directory
   * Server configuration to disk.
   */
  public static final String ALERT_TYPE_MANUAL_CONFIG_EDIT_LOST =
       "org.opends.server.ManualConfigEditLost";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an error occurs while attempting to write the
   * tasks backing file.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_WRITE_TASK_FILE =
           "This alert type will be used to notify administrators if the " +
           "Directory Server is unable to write an updated tasks backing " +
           "file for some reason.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an error occurs while attempting to write the tasks backing
   * file.
   */
  public static final String ALERT_TYPE_CANNOT_WRITE_TASK_FILE =
       "org.opends.server.CannotWriteTaskFile";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if consecutive failures in the LDAP connection
   * handler have caused it to become disabled.
   */
  public static final String
       ALERT_DESCRIPTION_LDAP_CONNECTION_HANDLER_CONSECUTIVE_FAILURES =
            "This alert type will be used to notify administrators of " +
            "consecutive failures that have occurred in the LDAP connection " +
            "handler that have caused it to become disabled.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if consecutive failures in the LDAP connection handler have
   * caused it to become disabled.
   */
  public static final String
       ALERT_TYPE_LDAP_CONNECTION_HANDLER_CONSECUTIVE_FAILURES =
            "org.opends.server.LDAPHandlerDisabledByConsecutiveFailures";

  /**
   * The description for the alert type that will be used for the alert
   * notification generated if consecutive failures in the HTTP connection
   * handler have caused it to become disabled.
   */
  public static final String
      ALERT_DESCRIPTION_HTTP_CONNECTION_HANDLER_CONSECUTIVE_FAILURES =
          "This alert type will be used to notify administrators of " +
          "consecutive failures that have occurred in the HTTP connection " +
          "handler that have caused it to become disabled.";

  /**
   * The alert type string that will be used for the alert notification
   * generated if consecutive failures in the HTTP connection handler have
   * caused it to become disabled.
   */
  public static final String
      ALERT_TYPE_HTTP_CONNECTION_HANDLER_CONSECUTIVE_FAILURES =
          "org.opends.server.HTTPHandlerDisabledByConsecutiveFailures";


  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the LDAP connection handler encountered an
   * unexpected error that has caused it to become disabled.
   */
  public static final String
       ALERT_DESCRIPTION_LDAP_CONNECTION_HANDLER_UNCAUGHT_ERROR =
            "This alert type will be used to notify administrators of " +
            "uncaught errors in the LDAP connection handler that have caused " +
            "it to become disabled.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if the LDAP connection handler encountered an unexpected error
   * that has caused it to become disabled.
   */
  public static final String
       ALERT_TYPE_LDAP_CONNECTION_HANDLER_UNCAUGHT_ERROR =
            "org.opends.server.LDAPHandlerUncaughtError";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the Directory Server has completed its startup
   * process.
   */
  public static final String ALERT_DESCRIPTION_SERVER_STARTED =
      "This alert type will be used to provide notification that the " +
      "Directory Server has completed its startup process.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the Directory Server has completed its startup process.
   */
  public static final String ALERT_TYPE_SERVER_STARTED =
       "org.opends.server.DirectoryServerStarted";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the Directory Server has started the shutdown
   * process.
   */
  public static final String ALERT_DESCRIPTION_SERVER_SHUTDOWN =
      "This alert type will be used to provide notification that the " +
      "Directory Server has begun the process of shutting down.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the Directory Server has started the shutdown process.
   */
  public static final String ALERT_TYPE_SERVER_SHUTDOWN =
       "org.opends.server.DirectoryServerShutdown";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated by a thread that has died because of an uncaught
   * exception.
   */
  public static final String ALERT_DESCRIPTION_UNCAUGHT_EXCEPTION =
       "This alert type will be used if a Directory Server thread has " +
       "encountered an uncaught exception that caused that thread to " +
       "terminate abnormally.  The impact that this problem has on the " +
       "server depends on which thread was impacted and the nature of the " +
       "exception.";



  /**
   * The alert type string that will be used for the alert notification
   * generated by a thread that has died because of an uncaught exception.
   */
  public static final String ALERT_TYPE_UNCAUGHT_EXCEPTION =
       "org.opends.server.UncaughtException";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if a problem occurs while creating copies of the
   * existing schema configuration files and a problem occurs that leaves the
   * schema configuration in a potentially inconsistent state.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_COPY_SCHEMA_FILES =
      "This alert type will be used to notify administrators if a problem " +
      "occurs while attempting to create copies of the existing schema " +
      "configuration files before making a schema update, and the schema " +
      "configuration is left in a potentially inconsistent state.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a problem occurs while creating copies of the existing schema
   * files in a manner that may leave the schema configuration inconsistent.
   */
  public static final String ALERT_TYPE_CANNOT_COPY_SCHEMA_FILES =
       "org.opends.server.CannotCopySchemaFiles";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if a problem occurs while writing new versions of
   * the server schema configuration files and a problem occurs that leaves the
   * schema configuration in a potentially inconsistent state.
   */
  public static final String ALERT_DESCRIPTION_CANNOT_WRITE_NEW_SCHEMA_FILES =
      "This alert type will be used to notify administrators if a problem " +
      "occurs while attempting to write new versions of the server schema " +
      "configuration files, and the schema configuration is left in a " +
      "potentially inconsistent state.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a problem occurs while writing new versions of the server
   * schema files in a manner that may leave the schema configuration
   * inconsistent.
   */
  public static final String ALERT_TYPE_CANNOT_WRITE_NEW_SCHEMA_FILES =
       "org.opends.server.CannotWriteNewSchemaFiles";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if the dseecompat access control subsystem failed
   * to parse one or more ACI rules when the server is first started or a
   * backend is being initialized.
   */
  public static final String ALERT_DESCRIPTION_ACCESS_CONTROL_PARSE_FAILED =
          "This alert type will be used to notify administrators if the  " +
             "dseecompat access control subsystem failed to correctly parse " +
             "one or more ACI rules when the server is first started.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if the dseecompat access control subsystem failed to parse
   * one or more ACI rules when the server is first started or a backend
   * is being initialized.
   */
  public static final String ALERT_TYPE_ACCESS_CONTROL_PARSE_FAILED =
          "org.opends.server.authentiation.dseecompat.ACIParseFailed";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the JE Environment needs to be reopened
   * after a RunRecoveryException is thrown.
   */
  public static final String ALERT_DESCRIPTION_BACKEND_ENVIRONMENT_UNUSABLE =
      "This alert type will be used to provide notification that the " +
      "JE backend throws a RunRecoveryException and Directory Server " +
          "needs to be restarted.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the JE Environment needs to be reopened by restarting
   * the Directory Server.
   */
  public static final String ALERT_TYPE_BACKEND_ENVIRONMENT_UNUSABLE =
       "org.opends.server.BackendRunRecovery";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the free disk space has reached
   * the low threshold.
   */
  public static final String ALERT_DESCRIPTION_DISK_SPACE_LOW =
      "This alert type will be used to provide notification that the " +
      "free disk space has reached the low threshold.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the JE Environment needs to be reopened by restarting
   * the Directory Server.
   */
  public static final String ALERT_TYPE_DISK_SPACE_LOW =
       "org.opends.server.DiskSpaceLow";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the free disk space has reached
   * the full threshold.
   */
  public static final String ALERT_DESCRIPTION_DISK_FULL =
      "This alert type will be used to provide notification that the " +
      "free disk space has reached the full threshold.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the JE Environment needs to be reopened by restarting
   * the Directory Server.
   */
  public static final String ALERT_TYPE_DISK_FULL =
       "org.opends.server.DiskFull";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the LDIF backend cannot write an updated LDIF
   * file.
   */
  public static final String
       ALERT_DESCRIPTION_LDIF_BACKEND_CANNOT_WRITE_UPDATE =
            "This alert type will be used to provide notification that an " +
            "LDIF backend was unable to store an updated copy of the LDIF " +
            "file after processing a write operation.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the LDIF backend cannot write an updated LDIF file.
   */
  public static final String ALERT_TYPE_LDIF_BACKEND_CANNOT_WRITE_UPDATE =
       "org.opends.server.LDIFBackendCannotWriteUpdate";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated when the LDIF connection handler is unable to
   * process the contents of a file as valid LDIF.
   */
  public static final String ALERT_DESCRIPTION_LDIF_CONNHANDLER_PARSE_ERROR =
       "This alert type will be used to provide notification that the " +
       "LDIF connection handler encountered an unrecoverable error while " +
       "attempting to parse an LDIF file.";



  /**
   * The alert type string that will be used for the alert notification
   * generated when the LDIF connection handler is unable to process the
   * contents of a file as valid LDIF.
   */
  public static final String ALERT_TYPE_LDIF_CONNHANDLER_PARSE_ERROR =
       "org.opends.server.LDIFConnectionHandlerParseError";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an I/O error occurs while attempting to
   * read or write LDIF content.
   */
  public static final String ALERT_DESCRIPTION_LDIF_CONNHANDLER_IO_ERROR =
       "This alert type will be used to provide notification that the " +
       "LDIF connection handler encountered an I/O error that prevented it " +
       "from completing its processing.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an I/O error occurs while attempting to  read or write LDIF
   * content.
   */
  public static final String ALERT_TYPE_LDIF_CONNHANDLER_IO_ERROR =
       "org.opends.server.LDIFConnectionHandlerIOError";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if a unique attribute conflict is detected during
   * synchronization processing.
   */
  public static final String ALERT_DESCRIPTION_UNIQUE_ATTR_SYNC_CONFLICT =
       "This alert type will be used to provide notification that a unique " +
       "attribute conflict has been detected during synchronization " +
       "processing.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if a unique attribute conflict is detected during synchronization
   * processing.
   */
  public static final String ALERT_TYPE_UNIQUE_ATTR_SYNC_CONFLICT =
       "org.opends.server.UniqueAttributeSynchronizationConflict";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if an error occurs while attempting to perform
   * unique attribute conflict detection during synchronization processing.
   */
  public static final String ALERT_DESCRIPTION_UNIQUE_ATTR_SYNC_ERROR =
       "This alert type will be used to provide notification that an error " +
       "occurred while attempting to perform unique attribute conflict " +
       "detection during synchronization processing.";



  /**
   * The alert type string that will be used for the alert notification
   * generated if an error occurs while attempting to perform unique attribute
   * conflict detection during synchronization processing.
   */
  public static final String ALERT_TYPE_UNIQUE_ATTR_SYNC_ERROR =
       "org.opends.server.UniqueAttributeSynchronizationError";



  /**
   * The name of the default password storage scheme that will be used for new
   * passwords.
   */
  public static final String DEFAULT_PASSWORD_STORAGE_SCHEME = "SSHA";



  /**
   * The maximum depth to which nested search filters will be processed.  This
   * can prevent stack overflow errors from filters that look like
   * "(&(&(&(&(&(&(&(&(&....".
   */
  public static final int MAX_NESTED_FILTER_DEPTH = 100;



  /**
   * Default maximum size for cached protocol/entry encoding buffers.
   */
  public static final int DEFAULT_MAX_INTERNAL_BUFFER_SIZE = 32 * 1024;



  /**
   * The OID for the attribute type that represents the "objectclass" attribute.
   */
  public static final String OBJECTCLASS_ATTRIBUTE_TYPE_OID = "2.5.4.0";



  /**
   * The name of the attribute type that represents the "objectclass" attribute,
   * formatted in all lowercase characters.
   */
  public static final String OBJECTCLASS_ATTRIBUTE_TYPE_NAME = "objectclass";



  /**
   * The value that will be used for the vendorName attribute in the root DSE.
   */
  public static final String SERVER_VENDOR_NAME = "Wren Security";



  /**
   * The name of the security mechanism that will be used for connections whose
   * communication is protected using the confidentiality features of
   * DIGEST-MD5.
   */
  public static final String SECURITY_MECHANISM_DIGEST_MD5_CONFIDENTIALITY =
       "DIGEST-MD5 Confidentiality";



  /**
   * The name of the security mechanism that will be used for connections whose
   * communication is protected using the confidentiality features of Kerberos.
   */
  public static final String SECURITY_MECHANISM_KERBEROS_CONFIDENTIALITY =
       "Kerberos Confidentiality";


  /**
   * The name of the default protocol used.
   */
  public static final String SASL_DEFAULT_PROTOCOL = "ldap";



  /**
   * The name of the security mechanism that will be used for connections
   * established using SSL.
   */
  public static final String SECURITY_MECHANISM_SSL = "SSL";



  /**
   * The name of the security mechanism that will be used for connections that
   * have established a secure session through StartTLS.
   */
  public static final String SECURITY_MECHANISM_START_TLS = "StartTLS";



  /**
   * The name of the SASL mechanism that does not provide any authentication but
   * rather uses anonymous access.
   */
  public static final String SASL_MECHANISM_ANONYMOUS = "ANONYMOUS";



  /**
   * The name of the SASL mechanism based on external authentication.
   */
  public static final String SASL_MECHANISM_EXTERNAL = "EXTERNAL";



  /**
   * The name of the SASL mechanism based on CRAM-MD5 authentication.
   */
  public static final String SASL_MECHANISM_CRAM_MD5 = "CRAM-MD5";



  /**
   * The name of the SASL mechanism based on DIGEST-MD5 authentication.
   */
  public static final String SASL_MECHANISM_DIGEST_MD5 = "DIGEST-MD5";



  /**
   * The name of the SASL mechanism based on GSS-API authentication.
   */
  public static final String SASL_MECHANISM_GSSAPI = "GSSAPI";



  /**
   * The name of the SASL mechanism based on PLAIN authentication.
   */
  public static final String SASL_MECHANISM_PLAIN = "PLAIN";



  /**
   * The name of the security mechanism that will be used for connections whose
   * communication only SASL authenticated.
   */
  public static final String SASL_MECHANISM_AUTHENTICATION_ONLY =
       "none";



  /**
   * The name of the security mechanism that will be used for connections whose
   * communication is protected using the confidentiality features SASL.
   */
  public static final String SASL_MECHANISM_CONFIDENTIALITY =
       "confidentiality";



  /**
   * The name of the security mechanism that will be used for connections whose
   * communication is verified using SASL integrity.
   */
  public static final String SASL_MECHANISM_INTEGRITY =
       "integrity";



  /**
   * The OID for the account usable request and response controls.
   */
  public static final String OID_ACCOUNT_USABLE_CONTROL =
       "1.3.6.1.4.1.42.2.27.9.5.8";


  /**
   *  The OID for the entry change request control.
   *  FIXME:ECL ask for OID_ECL_REQUEST_CONTROL
   */
  public static final String OID_ECL_COOKIE_EXCHANGE_CONTROL =
    "1.3.6.1.4.1.26027.1.5.4";


  /**
   * The IANA-assigned OID for the feature allowing a user to request that all
   * operational attributes be returned.
   */
  public static final String OID_ALL_OPERATIONAL_ATTRS_FEATURE =
       "1.3.6.1.4.1.4203.1.5.1";



  /**
   * The OID for the authorization identity request control.
   */
  public static final String OID_AUTHZID_REQUEST = "2.16.840.1.113730.3.4.16";



  /**
   * The OID for the authorization identity response control.
   */
  public static final String OID_AUTHZID_RESPONSE = "2.16.840.1.113730.3.4.15";



  /**
   * The OID for the entry change notification control.
   */
  public static final String OID_ENTRY_CHANGE_NOTIFICATION =
       "2.16.840.1.113730.3.4.7";



  /**
   * The OID for the control that will be included in modifications used to
   * alter group membership.
   */
  public static final String OID_INTERNAL_GROUP_MEMBERSHIP_UPDATE =
       "1.3.6.1.4.1.26027.1.5.1";



  /**
   * The OID to include in the supportedFeatures list of the Directory Server
   * to indicate that it supports requesting attributes by objectclass.
   */
  public static final String OID_LDAP_ADLIST_FEATURE = "1.3.6.1.4.1.4203.1.5.2";



  /**
   * The IANA-assigned OID for the LDAP assertion control.
   */
  public static final String OID_LDAP_ASSERTION = "1.3.6.1.1.12";



  /**
   * The OID for the LDAP no-op control that was originally assigned in the
   * initial draft (draft-zeilenga-ldap-noop-00) from the OpenLDAP private
   * range.  Note that this reference has been removed in later drafts, but
   * given that at this time no official OID is assigned, we will use it for
   * now, and will continue to support it in the future (along with the real
   * OID).
   */
  public static final String OID_LDAP_NOOP_OPENLDAP_ASSIGNED =
       "1.3.6.1.4.1.4203.1.10.2";



  /**
   * The IANA-assigned OID for the LDAP readentry control used for retrieving an
   * entry in the state it had immediately before an update was applied.
   */
  public static final String OID_LDAP_READENTRY_PREREAD =
       "1.3.6.1.1.13.1";



  /**
   * The IANA-assigned OID for the LDAP readentry control used for retrieving an
   * entry in the state it had immediately after an update was applied.
   */
  public static final String OID_LDAP_READENTRY_POSTREAD =
       "1.3.6.1.1.13.2";



  /**
   * The OID for the LDAP subentries control as defined in RFC 3672, which is
   * used to indicate that matching subentries should be returned.
   */
  public static final String OID_LDAP_SUBENTRIES = "1.3.6.1.4.1.4203.1.10.1";



  /**
   * The OID for the LDAP subentries control as defined in the legacy
   * draft-ietf-ldup-subentry internet draft, which is used to indicate that
   * matching subentries should be returned.
   */
  public static final String OID_LDUP_SUBENTRIES = "1.3.6.1.4.1.7628.5.101.1";



  /**
   * The OID for the matched values control used to specify which particular
   * attribute values should be returned in a search result entry.
   */
  public static final String OID_MATCHED_VALUES = "1.2.826.0.1.3344810.2.3";



  /**
   * The IANA-assigned OID for the feature allowing the use of the increment
   * modification type.
   */
  public static final String OID_MODIFY_INCREMENT_FEATURE = "1.3.6.1.1.14";



  /**
   * The OID for the Netscape password expired control.
   */
  public static final String OID_NS_PASSWORD_EXPIRED =
       "2.16.840.1.113730.3.4.4";



  /**
   * The OID for the Netscape password expiring control.
   */
  public static final String OID_NS_PASSWORD_EXPIRING =
       "2.16.840.1.113730.3.4.5";



  /**
   * The OID for the password policy control from
   * draft-behera-ldap-password-policy.
   */
  public static final String OID_PASSWORD_POLICY_CONTROL =
       "1.3.6.1.4.1.42.2.27.8.5.1";



  /**
   * The OID for the persistent search control.
   */
  public static final String OID_PERSISTENT_SEARCH = "2.16.840.1.113730.3.4.3";



  /**
   * The OID for the proxied authorization v1 control.
   */
  public static final String OID_PROXIED_AUTH_V1 = "2.16.840.1.113730.3.4.12";



  /**
   * The OID for the proxied authorization v2 control.
   */
  public static final String OID_PROXIED_AUTH_V2 = "2.16.840.1.113730.3.4.18";


   /**
   * The OID for the get effective rights control.
   */
  public static final String OID_GET_EFFECTIVE_RIGHTS =
                                                    "1.3.6.1.4.1.42.2.27.9.5.2";


  /**
   * The OID for the real attributes only control.
   */
  public static final String OID_REAL_ATTRS_ONLY = "2.16.840.1.113730.3.4.17";



  /**
   * The OID for the subtree delete control.
   */
  public static final String OID_SUBTREE_DELETE_CONTROL =
       "1.2.840.113556.1.4.805";

  /**
   * The OID for the transactionId control.
   */
  public static final String OID_TRANSACTION_ID_CONTROL = "1.3.6.1.4.1.36733.2.1.5.1";

  /**
   * The OID for the paged results control defined in RFC 2696.
   */
  public static final String OID_PAGED_RESULTS_CONTROL =
       "1.2.840.113556.1.4.319";



  /**
   * The OID for the ManageDsaIT control defined in RFC 3296.
   */
  public static final String OID_MANAGE_DSAIT_CONTROL =
       "2.16.840.1.113730.3.4.2";


  /**
   * The OID for the Permissive Modify control, defined and used by MSAD.
   */
  public static final String OID_PERMISSIVE_MODIFY_CONTROL =
      "1.2.840.113556.1.4.1413";


  /**
   * The OID for the server-side sort request control.
   */
  public static final String OID_SERVER_SIDE_SORT_REQUEST_CONTROL =
       "1.2.840.113556.1.4.473";



  /**
   * The OID for the server-side sort response control.
   */
  public static final String OID_SERVER_SIDE_SORT_RESPONSE_CONTROL =
       "1.2.840.113556.1.4.474";



  /**
   * The IANA-assigned OID for the feature allowing the use of LDAP true and
   * false filters.
   */
  public static final String OID_TRUE_FALSE_FILTERS_FEATURE =
       "1.3.6.1.4.1.4203.1.5.3";



  /**
   * The OID for the virtual attributes only control.
   */
  public static final String OID_VIRTUAL_ATTRS_ONLY =
       "2.16.840.1.113730.3.4.19";



  /**
   * The OID for the virtual list view request control.
   */
  public static final String OID_VLV_REQUEST_CONTROL =
       "2.16.840.1.113730.3.4.9";



  /**
   * The OID for the virtual list view request control.
   */
  public static final String OID_VLV_RESPONSE_CONTROL =
       "2.16.840.1.113730.3.4.10";

  /**
   * The OID for the CSN control.
   */
  public static final String OID_CSN_CONTROL =
       "1.3.6.1.4.1.42.2.27.9.5.9";

  /**
   * The block length in bytes used when generating an HMAC-MD5 digest.
   */
  public static final int HMAC_MD5_BLOCK_LENGTH = 64;



  /**
   * The number of bytes in a raw MD5 digest.
   */
  public static final int MD5_DIGEST_LENGTH = 16;



  /**
   * The inner pad byte, which will be XORed with the shared secret for the
   * first CRAM-MD5 digest.
   */
  public static final byte CRAMMD5_IPAD_BYTE = 0x36;



  /**
   * The outer pad byte, which will be XORed with the shared secret for the
   * second CRAM-MD5 digest.
   */
  public static final byte CRAMMD5_OPAD_BYTE = 0x5C;



  /**
   * The name of the JAAS login module for Kerberos V.
   */
  public static final String JAAS_MODULE_KRB5 =
       "com.sun.security.auth.module.Krb5LoginModule";



  /**
   * The name of the JAAS property that specifies the path to the login
   * configuration file.
   */
  public static final String JAAS_PROPERTY_CONFIG_FILE =
       "java.security.auth.login.config";



  /**
   * The name of the JAAS property that indicates whether to allow JAAS
   * credentials to come from somewhere other than a GSS mechanism.
   */
  public static final String JAAS_PROPERTY_SUBJECT_CREDS_ONLY =
       "javax.security.auth.useSubjectCredsOnly";



  /**
   * The name of the Kerberos V property that specifies the address of the KDC.
   */
  public static final String KRBV_PROPERTY_KDC = "java.security.krb5.kdc";



  /**
   * The name of the Kerberos V property that specifies the realm to use.
   */
  public static final String KRBV_PROPERTY_REALM = "java.security.krb5.realm";



  /**
   * The name of the file (without path information) that should be used to hold
   * information about the backups contained in that directory.
   */
  public static final String BACKUP_DIRECTORY_DESCRIPTOR_FILE = "backup.info";



  /**
   * The name of the backup property that holds the base name of the archive
   * file containing the contents of the backup.
   */
  public static final String BACKUP_PROPERTY_ARCHIVE_FILENAME = "archive_file";



  /**
   * The name of the backup property that holds the name of the digest algorithm
   * used to generate the hash of a backup.
   */
  public static final String BACKUP_PROPERTY_DIGEST_ALGORITHM =
       "digest_algorithm";



  /**
   * The name of the backup property that holds the identifer of the key entry
   * that contains the MAC algorithm and shared secret key used to generate
   * the signed hash of a backup.
   */
  public static final String BACKUP_PROPERTY_MAC_KEY_ID = "mac_key_id";



  /**
   * The base filename to use for the archive file containing a backup of the
   * server configuration.
   */
  public static final String CONFIG_BACKUP_BASE_FILENAME = "config-backup-";



  /**
   * The base filename to use for the archive file containing a backup of the
   * server schema.
   */
  public static final String SCHEMA_BACKUP_BASE_FILENAME = "schema-backup-";



  /**
   * The base filename to use for the archive file containing a backup of the
   * task backend.
   */
  public static final String TASKS_BACKUP_BASE_FILENAME = "tasks-backup-";



  /**
   * The name of the directory in which lock files will be placed.
   */
  public static final String LOCKS_DIRECTORY = "locks";



  /**
   * The prefix that will be used for lock filenames used for Directory Server
   * backends.
   */
  public static final String BACKEND_LOCK_FILE_PREFIX = "backend-";



  /**
   * The name that will be used for the server-wide lock to prevent multiple
   * instances of the server from running concurrently.
   */
  public static final String SERVER_LOCK_FILE_NAME = "server";



  /**
   * The suffix that will be used for all lock files created by the Directory
   * Server.
   */
  public static final String LOCK_FILE_SUFFIX = ".lock";



  /**
   * The name of the schema extension that will be used to specify the
   * approximate matching rule that should be used for a given attribute type.
   */
  public static final String SCHEMA_PROPERTY_APPROX_RULE = "X-APPROX";



  /**
   * The name of the schema property that will be used to specify the path to
   * the schema file from which the schema element was loaded.
   */
  public static final String SCHEMA_PROPERTY_FILENAME = "X-SCHEMA-FILE";

  /**
   * The name of the schema property that will be used to specify the origin
   * of a schema element.
   */
  public static final String SCHEMA_PROPERTY_ORIGIN = "X-ORIGIN";

  /**
   * The abbreviated unit that should be used for a size specified in bytes.
   */
  public static final String SIZE_UNIT_BYTES_ABBR = "b";



  /**
   * The full unit that should be used for a size specified in bytes.
   */
  public static final String SIZE_UNIT_BYTES_FULL = "bytes";



  /**
   * The abbreviated unit that should be used for a size specified in kilobytes.
   */
  public static final String SIZE_UNIT_KILOBYTES_ABBR = "kb";



  /**
   * The full unit that should be used for a size specified in kilobytes.
   */
  public static final String SIZE_UNIT_KILOBYTES_FULL = "kilobytes";



  /**
   * The abbreviated unit that should be used for a size specified in kibibytes.
   */
  public static final String SIZE_UNIT_KIBIBYTES_ABBR = "kib";



  /**
   * The full unit that should be used for a size specified in kibibytes.
   */
  public static final String SIZE_UNIT_KIBIBYTES_FULL = "kibibytes";



  /**
   * The abbreviated unit that should be used for a size specified in megabytes.
   */
  public static final String SIZE_UNIT_MEGABYTES_ABBR = "mb";



  /**
   * The full unit that should be used for a size specified in megabytes.
   */
  public static final String SIZE_UNIT_MEGABYTES_FULL = "megabytes";



  /**
   * The abbreviated unit that should be used for a size specified in mebibytes.
   */
  public static final String SIZE_UNIT_MEBIBYTES_ABBR = "mib";



  /**
   * The full unit that should be used for a size specified in mebibytes.
   */
  public static final String SIZE_UNIT_MEBIBYTES_FULL = "mebibytes";



  /**
   * The abbreviated unit that should be used for a size specified in gigabytes.
   */
  public static final String SIZE_UNIT_GIGABYTES_ABBR = "gb";



  /**
   * The full unit that should be used for a size specified in gigabytes.
   */
  public static final String SIZE_UNIT_GIGABYTES_FULL = "gigabytes";



  /**
   * The abbreviated unit that should be used for a size specified in gibibytes.
   */
  public static final String SIZE_UNIT_GIBIBYTES_ABBR = "gib";



  /**
   * The full unit that should be used for a size specified in gibibytes.
   */
  public static final String SIZE_UNIT_GIBIBYTES_FULL = "gibibytes";



  /**
   * The abbreviated unit that should be used for a size specified in terabytes.
   */
  public static final String SIZE_UNIT_TERABYTES_ABBR = "tb";



  /**
   * The full unit that should be used for a size specified in terabytes.
   */
  public static final String SIZE_UNIT_TERABYTES_FULL = "terabytes";



  /**
   * The abbreviated unit that should be used for a size specified in tebibytes.
   */
  public static final String SIZE_UNIT_TEBIBYTES_ABBR = "tib";



  /**
   * The full unit that should be used for a size specified in tebibytes.
   */
  public static final String SIZE_UNIT_TEBIBYTES_FULL = "tebibytes";



  /**
   * The abbreviated unit that should be used for a time specified in
   * nanoseconds.
   */
  public static final String TIME_UNIT_NANOSECONDS_ABBR = "ns";



  /**
   * The full unit that should be used for a time specified in nanoseconds.
   */
  public static final String TIME_UNIT_NANOSECONDS_FULL = "nanoseconds";



  /**
   * The abbreviated unit that should be used for a time specified in
   * microseconds.
   */
  public static final String TIME_UNIT_MICROSECONDS_ABBR = "us";



  /**
   * The full unit that should be used for a time specified in microseconds.
   */
  public static final String TIME_UNIT_MICROSECONDS_FULL = "microseconds";



  /**
   * The abbreviated unit that should be used for a time specified in
   * milliseconds.
   */
  public static final String TIME_UNIT_MILLISECONDS_ABBR = "ms";



  /**
   * The full unit that should be used for a time specified in milliseconds.
   */
  public static final String TIME_UNIT_MILLISECONDS_FULL = "milliseconds";



  /**
   * The abbreviated unit that should be used for a time specified in seconds.
   */
  public static final String TIME_UNIT_SECONDS_ABBR = "s";



  /**
   * The full unit that should be used for a time specified in seconds.
   */
  public static final String TIME_UNIT_SECONDS_FULL = "seconds";



  /**
   * The abbreviated unit that should be used for a time specified in minutes.
   */
  public static final String TIME_UNIT_MINUTES_ABBR = "m";



  /**
   * The full unit that should be used for a time specified in minutes.
   */
  public static final String TIME_UNIT_MINUTES_FULL = "minutes";



  /**
   * The abbreviated unit that should be used for a time specified in hours.
   */
  public static final String TIME_UNIT_HOURS_ABBR = "h";



  /**
   * The full unit that should be used for a time specified in hours.
   */
  public static final String TIME_UNIT_HOURS_FULL = "hours";



  /**
   * The abbreviated unit that should be used for a time specified in days.
   */
  public static final String TIME_UNIT_DAYS_ABBR = "d";



  /**
   * The full unit that should be used for a time specified in days.
   */
  public static final String TIME_UNIT_DAYS_FULL = "days";



  /**
   * The abbreviated unit that should be used for a time specified in weeks.
   */
  public static final String TIME_UNIT_WEEKS_ABBR = "w";



  /**
   * The full unit that should be used for a time specified in weeks.
   */
  public static final String TIME_UNIT_WEEKS_FULL = "weeks";

  /**
   * The name of the system property that can be used to specify the path to the
   * configuration file that should be used to initialize the config handler.
   */
  public static final String PROPERTY_CONFIG_FILE =
       "org.opends.server.ConfigFile";



  /**
   * The name of the system property that can be used to disable any connection
   * handler that may be enabled in the server configuration.  This may be used
   * to start the server in a mode where it will not accept any external
   * connections, but may still be used for processing internal operations.
   */
  public static final String PROPERTY_DISABLE_CONNECTION_HANDLERS =
       "org.opends.server.DisableConnectionHandlers";

  /**
   * The name of the system property that can be used to disable any
   * synchronization provider that may be enabled in the server configuration.
   * This may be used to start the server in a mode where it will not accept any
   * external connections, but may still be used for processing internal
   * operations.
   */
  public static final String PROPERTY_DISABLE_SYNCHRONIZATION =
       "org.opends.server.DisableSynchronization";

  /**
   * The name of the system property that can be used to disable the
   * synchronization between between administration data.
   */
  public static final String PROPERTY_DISABLE_ADMIN_DATA_SYNCHRONIZATION =
       "org.opends.server.DisableAdminDataSynchronization";

  /**
   * The name of the system property that can be used to indicate whether
   * components should be allowed to use the <CODE>Runtime.exec</CODE> method.
   * If this property is set and the value is anything other than "false",
   * "off", "no", or "0", then components should not allow the use of the
   * <CODE>exec</CODE> method.
   */
  public static final String PROPERTY_DISABLE_EXEC =
       "org.opends.server.DisableExec";



  /**
   * The name of the system property that can be used to determine whether all
   * <CODE>DirectoryThread</CODE> instances should be created as daemon threads
   * regardless of whether they would otherwise be configured that way.
   */
  public static final String PROPERTY_FORCE_DAEMON_THREADS =
       "org.opends.server.ForceDaemonThreads";



  /**
   * The name of the system property that can be used to specify the path to the
   * directory in which the server lock files should be written.  If this is not
   * set, then the server will use a directory named "locks" below the server
   * root.  Note that if the server is ever started with a different lock file
   * directory than was used for the previous startup, then the server
   * administrator must ensure that the instance is not already running.
   */
  public static final String PROPERTY_LOCK_DIRECTORY =
       "org.opends.server.LockDirectory";



  /**
   * The name of the system property that can be used to specify the concurrency
   * level for the lock table.  This should be set to the maximum number of
   * threads that could attempt to interact with the lock table at any given
   * time.
   */
  public static final String PROPERTY_LOCK_MANAGER_CONCURRENCY_LEVEL =
       "org.opends.server.LockManagerConcurrencyLevel";



  /**
   * The name of the system property that can be used to specify the initial
   * table size for the server lock table.  This can be used to ensure that the
   * lock table has the appropriate size for the expected number of locks that
   * will be held at any given time.
   */
  public static final String PROPERTY_LOCK_MANAGER_TABLE_SIZE =
       "org.opends.server.LockManagerTableSize";



  /**
   * The name of the system property that can be used to determine whether the
   * server should maintain an archive of previous configurations.  If this is
   * not set, or if the value is anything other than "false", then the server
   * will maintain a configuration archive.
   */
  public static final String PROPERTY_MAINTAIN_CONFIG_ARCHIVE =
       "org.opends.server.MaintainConfigArchive";



  /**
   * The name of the system property that can be used to specify the maximum
   * number of archived configurations to maintain.  If this is not set, or if
   * it set to a zero or negative value, then there will be no limit on the
   * number of archived configurations.
   */
  public static final String PROPERTY_MAX_CONFIG_ARCHIVE_SIZE =
       "org.opends.server.MaxConfigArchiveSize";



  /**
   * The name of the system property that can be used to determine whether the
   * Directory Server is starting up for the purpose of running the unit tests.
   */
  public static final String PROPERTY_RUNNING_UNIT_TESTS =
       "org.opends.server.RunningUnitTests";



  /**
   * The name of the system property that can be used to specify the path to the
   * directory in which the schema configuration files may be found.  If this is
   * not set, then the server wiill use a directory named "schema" below the
   * server root.
   */
  public static final String PROPERTY_SCHEMA_DIRECTORY =
       "org.opends.server.SchemaDirectory";



  /**
   * The name of a command-line script used to launch an administrative tool.
   */
  public static final String PROPERTY_SCRIPT_NAME =
       "org.opends.server.scriptName";



  /**
   * The name of the system property that can be used to specify the path to the
   * server root.
   */
  public static final String PROPERTY_SERVER_ROOT =
       "org.opends.server.ServerRoot";

  /**
   * The name of the system property that can be used to specify the path to the
   * instance root.
   */
  public static final String PROPERTY_INSTANCE_ROOT =
       "org.opends.server.InstanceRoot";


  /**
   * The name of the system property that can be used to specify a target
   * for the debug logger on startup.
   */
  public static final String PROPERTY_DEBUG_TARGET =
      "org.opends.server.debug.target";

  /**
   * The name of the system property that can be used to specify a level
   * for the error logger on startup.
   */
  public static final String PROPERTY_ERROR_LEVEL =
      "org.opends.server.error.level";

  /**
   * The name of the system property that can be used to specify if  the entry
   * lock manager should use a fair ordering policy.
   */
  public static final String PROPERTY_LOCK_MANAGER_FAIR_ORDERING =
       "org.opends.server.LockManagerFairOrdering";



  /**
   * The name of the system property that can be used to indicate that the
   * Directory Server should attempt to start using the last known good
   * configuration, rather than the current active configuration.
   */
  public static final String PROPERTY_USE_LAST_KNOWN_GOOD_CONFIG =
       "org.opends.server.UseLastKnownGoodConfiguration";



  /**
   * The name that should be used for the file to which the latest complete
   * schema data should be concatenated.
   */
  public static final String SCHEMA_CONCAT_FILE_NAME = "schema.ldif.current";



  /**
   * The name that should be used for the concatenated schema file generated at
   * build time with the base schema for the Subversion revision on which the
   * current build is based.  The value of
   * {@code DynamicConstants.REVISION_NUMBER} must be appended to this value in
   * order to get the full name.
   */
  public static final String SCHEMA_BASE_FILE_NAME_WITHOUT_REVISION  =
       "schema.ldif.";



  /** The default SMTP port to use. */
  public static final int SMTP_DEFAULT_PORT = 25;


  /**
   * The name of the JavaMail property that can be used to specify the address
   * of the SMTP server.
   */
  public static final String SMTP_PROPERTY_HOST = "mail.smtp.host";



  /**
   * The name of the JavaMail property that can be used to specify the port for
   * the SMTP server.
   */
  public static final String SMTP_PROPERTY_PORT = "mail.smtp.port";


  /**
   * The name of the JavaMail property that can be used to specify the socket
   * connection timeout value in milliseconds.
   */
  public static final String SMTP_PROPERTY_CONNECTION_TIMEOUT =
          "mail.smtp.connectiontimeout";


  /**
   * The name of the JavaMail property that can be used to specify the socket
   * I/O timeout value in milliseconds.
   */
  public static final String SMTP_PROPERTY_IO_TIMEOUT = "mail.smtp.timeout";


  /**
   * The default timeout value for JavaMail timeout properties.
   */
  public static final String SMTP_DEFAULT_TIMEOUT_VALUE = "5000";



  /**
   * The description for the alert type that will be used for the alert
   * notification generated if multimaster replication detects
   * a conflict that cannot be solved automatically.
   */
  public static final String ALERT_DESCRIPTION_REPLICATION_UNRESOLVED_CONFLICT =
          "This alert type will be used to notify administrators if " +
          "multimaster replication cannot resolve a conflict automatically.";


  /**
   * The alert type string that will be used for the alert notification
   * generated if multimaster replication detects
   * a conflict that cannot be solved automatically.
   */
  public static final String ALERT_TYPE_REPLICATION_UNRESOLVED_CONFLICT =
          "org.opends.server.replication.UnresolvedConflict";



  /**
   * The extensible indexer identifier string that will be used for a substring
   * type.
   */
  public static final String EXTENSIBLE_INDEXER_ID_SUBSTRING="substring";



  /**
   * The extensible indexer identifier string that will be used for a shared
   * type.
   */
  public static final String EXTENSIBLE_INDEXER_ID_SHARED="shared";



  /**
   * The extensible indexer identifier string that will be used for default
   * type.
   */
  public static final String EXTENSIBLE_INDEXER_ID_DEFAULT="ext";



  /**
   * The lines that make up the CDDL header.  They will not have any prefix, so
   * an appropriate prefix may need to be added for some cases (e.g., "# " for
   * shell scripts, "rem " for batch files, etc.).
   */
  public static final String[] CDDL_HEADER_LINES =
  {
    "The contents of this file are subject to the terms of the Common Development and",
    "Distribution License (the License). You may not use this file except in compliance with the",
    "License.",
    "",
    "You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the",
    "specific language governing permission and limitations under the License.",
    "",
    "When distributing Covered Software, include this CDDL Header Notice in each file and include",
    "the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL",
    "Header, with the fields enclosed by brackets [] replaced by your own identifying",
    "information: \"Portions Copyright [year] [name of copyright owner]\".",
    ""
  };

  /**
   * The value representing just one space character.
   */
  public static final ByteString SINGLE_SPACE_VALUE = ByteString.valueOfUtf8(" ");

  /**
   * The normalized true value.
   */
  public static final ByteString TRUE_VALUE = ByteString.valueOfUtf8("TRUE");

  /**
   * The normalized false value.
   */
  public static final ByteString FALSE_VALUE = ByteString.valueOfUtf8("FALSE");

  /**
   * The root Dn for the external change log.
   */
  public static final String DN_EXTERNAL_CHANGELOG_ROOT = "cn=changelog";

  /**
   * Enable overcommit of memory in Old Gen space.
   */
  public static final String ENABLE_MEMORY_OVERCOMMIT = "org.forgerock.opendj.EnableMemoryOvercommit";

  /** System property to use for overriding CertAndKeyGen class location. */
  public static final String CERTANDKEYGEN_PROVIDER = "org.forgerock.opendj.CertAndKeyGenProvider";

}

