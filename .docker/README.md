<p align="center">
  <img alt="Wren:DS logo" src="https://user-images.githubusercontent.com/13997406/203251142-037753c0-447e-4670-8b53-966f3ff3d098.png" width="50%">
</p>

# Wren:DS

[![Organization Website](https://img.shields.io/badge/organization-Wren_Security-c12233)](https://wrensecurity.org)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/WrenSecurity)
[![License](https://img.shields.io/badge/license-CDDL-blue.svg)](https://github.com/WrenSecurity/wrends/blob/main/LICENSE)
[![Source Code](https://img.shields.io/badge/source_code-GitHub-6e40c9)](https://github.com/WrenSecurity/wrends)
[![Contributing Guide](https://img.shields.io/badge/contributions-guide-green.svg)](https://github.com/WrenSecurity/wrensec-docs/wiki/Contributor-Guidelines)

Wren:DS is an [LDAPv3](http://tools.ietf.org/html/rfc4510) compliant directory service, which has been developed
for the Java platform, providing a high performance, highly available, and secure store for the identities managed
by your organization. Its easy installation process, combined with the power of the Java platform makes Wren:DS
the simplest, fastest directory to deploy and manage.

Wren:DS is one of the projects in the Wren Security Suite, a community initiative that adopted open‐source projects
formerly developed by ForgeRock, which has its own roots in Sun Microsystems’ products.

# How to use this image

You can run Wren:DS through this command:

    docker run --rm --name wrends-test -p 1389:1389 -p 1636:1636 wrensecurity/wrends:latest

Then you can perform base LDAP search request through this command:

    docker exec -it wrends-test ./bin/ldapsearch --port 1389 --bindDN "cn=Directory Manager" --bindPassword password --baseDN dc=example,dc=com "(objectClass=*)"

Alternatively you can connect to the server through your favorite directory browser (e.g. _Apache Directory Studio_) using the following properties:

* `hostname` –⁠ localhost
* `port` –⁠ 1389 (LDAP) or 1636 (LDAPS)
* `user` –⁠ cn=Directory Manager
* `password` –⁠ password

## Environment Variables

Wren:DS instance can be configured through the following predefined environment properties:

| Variable | Description | Default Value |
| --- | --- | --- |
| `INSTANCE_DIR` | Directory for Wren:DS configuration / data | `/opt/wrends/instance` |
| `BASE_DN` | Directory Base DN | `dc=example,dc=com` |
| `LDAP_PORT` | LDAP Listener Port | `1389` |
| `LDAPS_PORT` | LDAPS Listener Port | `1636` |
| `ADMIN_CONNECTOR_PORT` | Administration Connector Port | `4444` |
| `ROOT_USER_DN` | Root User DN | `cn=Directory Manager` |
| `ROOT_USER_PASSWORD` | Root User Password | `password` |
| `ADD_BASE_ENTRY` | Create Directory Base Entry if set | `--addBaseEntry` |
| `SSL_OPTIONS` | SSL Configuration Options (see chapter bellow) | `--generateSelfSignedCertificate` |

## Directory Initialization

There is a couple of ways how to initialize empty Wren:DS directory.

### Import Custom Data

Custom data can be imported at first run through LDIF files.
These LDIF files must be mounted to the container's directory `/opt/wrends/bootstrap/init`.

Local file `01-data.ldif` can be imported at first run through this command:

```
docker run --rm --name wrends-test -p 1389:1389 -p 1636:1636 -v "$(pwd)/01-data.ldif":/opt/wrends/bootstrap/init/01-data.ldif wrensecurity/wrends:latest
```

### Perform Custom Configuration

You can perform advanced Wren:DS directory configuration through custom bash scripts.
These bash script files must be mounted to the container's directory `/opt/wrends/bootstrap/init`.
You can use any Wren:DS binary (e.g. `dsconfig`) or any predefined environment variable within bash script.

Local file `01-replication.sh` can be processed at first run through this command:

```
docker run --rm --name wrends-test -p 1389:1389 -p 1636:1636 -v "$(pwd)/01-replication.sh":/opt/wrends/bootstrap/init/01-replication.sh wrensecurity/wrends:latest
```

### Modify Default Schema

You can enhance base Wren:DS directory schema through LDIF files.
These LDIF files must be mounted to the container's directory `/opt/wrends/template/config/schema`.

Local file `50-schema.ldif` can be processed at first run through this command:

```
docker run --rm --name wrends-test -p 1389:1389 -p 1636:1636 -v "$(pwd)/50-schema.ldif":/opt/wrends/template/config/schema/50-schema.ldif wrensecurity/wrends:latest
```

## SSL Configuration

Wren:DS will generate self-signed certificate for LDAPS at first run when using default configuration (see option `--generateSelfSignedCertificate`).
You can use your own certificate through environment variable `SSL_OPTIONS`.

Below is complete example how to use your own certificate (also self-signed):

```
// 1. Ensure that exist local directory 'security'
// 2. Generate RSA key
keytool \
  -genkey \
  -alias server-cert \
  -keyalg rsa \
  -ext "san=dns:example.com" \
  -dname "CN=example.com,DC=wrensecurity,DC=org" \
  -keystore security/keystore \
  -storepass changeit \
  -keypass changeit

// 3. Run Wren:DS with custom SSL options
docker run --rm --name wrends-test -p 1389:1389 -p 1636:1636 -v "$(pwd)/security":/opt/wrends/security -e SSL_OPTIONS="--usePkcs12keyStore /opt/wrends/security/keystore --keyStorePassword changeit" wrensecurity/wrends:latest
```

List of all available SSL options can be found in [Utils](https://github.com/WrenSecurity/wrends/blob/0a25979c4d1f6bdbee79a46e149852cc00f85eaf/opendj-server-legacy/src/main/java/org/opends/quicksetup/util/Utils.java#L1485) class.

# Acknowledgments

Wren:DS is standing on the shoulders of giants and is a continuation of a prior work:

* OpenDS by Sun Microsystems
* OpenDJ by ForgeRock AS

We'd like to thank them for supporting the idea of open-source software.

# Disclaimer

Please note that the acknowledged parties are not affiliated with this project.
Their trade names, product names and trademarks should not be used to refer to
the Wren Security products, as it might be considered an unfair commercial
practice.

Wren Security is open source and always will be.

[contribute]: https://github.com/WrenSecurity/wrensec-docs/wiki/Contributor-Guidelines