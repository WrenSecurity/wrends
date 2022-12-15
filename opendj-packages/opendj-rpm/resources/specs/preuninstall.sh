#!/bin/sh
#
# The contents of this file are subject to the terms of the Common Development and
# Distribution License (the License). You may not use this file except in compliance with the
# License.
#
# You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
# specific language governing permission and limitations under the License.
#
# When distributing Covered Software, include this CDDL Header Notice in each file and include
# the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
# Header, with the fields enclosed by brackets [] replaced by your own identifying
# information: "Portions Copyright [year] [name of copyright owner]".
#
# Copyright 2013-2015 ForgeRock AS.
# Portions Copyright 2022 Wren Security.

# =================================
# RPM Pre Uninstall Script (%preun)
# =================================

# If the first argument to %preun and %postun is 0, the action is uninstallation.
# If the first argument to %preun and %postun is 1, the action is an upgrade.

if [ "$1" == "0" ] ; then
    echo "Pre Uninstall - uninstall"
    # Unlink the symlink to the process ID.
    test -h "/var/run/wrends.pid" && unlink /var/run/wrends.pid
    # Only if the instance has been configured
    if [ -e "%{_prefix}"/config/buildinfo ] && [ "$(ls -A "%{_prefix}"/config/archived-configs)" ] ; then
	   "%{_prefix}"/bin/./stop-ds
    fi

    if [ -e /etc/init.d/wrends ] ; then
        # Deletes the service.
        /sbin/chkconfig --del wrends
    fi
else if [ "$1" == "1" ] ; then
    echo "Pre Uninstall - upgrade uninstall"
    fi
fi
