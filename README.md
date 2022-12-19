<!--
  The contents of this file are subject to the terms of the Common Development and
  Distribution License (the License). You may not use this file except in compliance with the
  License.

  You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
  specific language governing permission and limitations under the License.

  When distributing Covered Software, include this CDDL Header Notice in each file and include
  the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
  Header, with the fields enclosed by brackets [] replaced by your own identifying
  information: "Portions copyright [year] [name of copyright owner]".

  Copyright 2016 ForgeRock AS.
  Portions Copyright 2022 Wren Security.
-->

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://user-images.githubusercontent.com/13997406/203251070-bad574a5-7a79-45b4-a4b3-25ca68fa0724.png">
    <source media="(prefers-color-scheme: light)" srcset="https://user-images.githubusercontent.com/13997406/203251142-037753c0-447e-4670-8b53-966f3ff3d098.png">
    <img alt="Wren:DS logo" src="https://user-images.githubusercontent.com/13997406/203251142-037753c0-447e-4670-8b53-966f3ff3d098.png" width="60%">
  </picture>
</p>

# Wren:DS

[![License](https://img.shields.io/badge/license-CDDL-blue.svg)](https://github.com/WrenSecurity/wrends/blob/master/LICENSE)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/WrenSecurity)

Wren:DS is an [LDAPv3](http://tools.ietf.org/html/rfc4510) compliant directory service, which has been developed
for the Java platform, providing a high performance, highly available, and secure store for the identities managed
by your organization. Its easy installation process, combined with the power of the Java platform makes Wren:DS
the simplest, fastest directory to deploy and manage.

Wren:DS is one of the projects in the Wren Security Suite, a community initiative that adopted open‐source projects
formerly developed by ForgeRock, which has its own roots in Sun Microsystems’ products.

## Contributions

[![Contributing Guide](https://img.shields.io/badge/Contributions-guide-green.svg?style=flat)][contribute]
[![Contributors](https://img.shields.io/github/contributors/WrenSecurity/wrends)][contribute]
[![Pull Requests](https://img.shields.io/github/issues-pr/WrenSecurity/wrends)][contribute]
[![Last commit](https://img.shields.io/github/last-commit/WrenSecurity/wrends.svg)](https://github.com/WrenSecurity/wrends/commits/main)

## Getting the Wren:DS

You can get Wren:DS application in couple of ways:

### Download binary release

The easiest way to get the Wren:DS is to download the latest binary [release](https://github.com/WrenSecurity/wrends/releases).

### Build the source code

In order to build the project from the command line follow these steps:

**Prepare your Environment**

Following software is needed to build the project:

| Software  | Required Version |
| --------- | -------------    |
| OpenJDK   | 8 and above      |
| Git       | 2.0 and above    |
| Maven     | 3.0 and above    |

**Build the source code**

All project dependencies are hosted in JFrog repository and managed by Maven, so to build the project simply execute Maven *package* goal.

```
$ cd $GIT_REPOSITORIES/wrends
$ mvn clean package
```

Built binary can be found in `${GIT_REPOSITORIES}/wrends/opendj-server-legacy/target/package/wrends-${VERSION}.zip`.

### Docker image

You can also run Wren:DS in a Docker container. Official Wren:DS Docker images can be found [here](https://hub.docker.com/r/wrensecurity/wrends).

## Documentation

Project documentation can be found in our documentation platform ([docs.wrensecurity.org](https://docs.wrensecurity.org/wrends/latest/index.html)).

## Acknowledgments

Large portions of the source code are based on the open-source projects
previously released by:
* Sun Microsystems
* ForgeRock

We'd like to thank them for supporting the idea of open-source software.

## Disclaimer

Please note that the acknowledged parties are not affiliated with this project.
Their trade names, product names and trademarks should not be used to refer to
the Wren Security products, as it might be considered an unfair commercial
practice.

Wren Security is open source and always will be.

[contribute]: https://github.com/WrenSecurity/wrensec-docs/wiki/Contributor-Guidelines
