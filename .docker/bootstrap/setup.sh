#!/bin/bash -e

# Base Wren:DS install location
WRENDS_INSTALL_PATH=/opt/wrends

# Directory with scripts for initializing new server
WRENDS_INIT_SOURCE=$WRENDS_INSTALL_PATH/bootstrap/init

# Process init files (LDIF and bash files)
process_init_files() {
  echo "Processing init files..."
  [ -e "$WRENDS_INIT_SOURCE" ] || return 0
  for init_file in $(find "$WRENDS_INIT_SOURCE" -maxdepth 1 -type f -name "*.ldif" -o -name "*.sh" | sort); do
    if [[ $init_file == *.sh ]]; then
      $init_file
    elif [[ $init_file == *.ldif ]]; then
      $WRENDS_INSTALL_PATH/bin/ldapmodify -h localhost -p $LDAP_PORT -D "$ROOT_USER_DN" -w "$ROOT_USER_PASSWORD" -f "$init_file"
    else
      echo "Ignoring file with invalid extension: $init_file"
    fi
  done
}

# Check whether Wren:DS is started
is_started() {
  $WRENDS_INSTALL_PATH/status -D "$ROOT_USER_DN" -w "$ROOT_USER_PASSWORD" | grep -eq 'Server Run Status:\ *Started'
}

# Check whether Wren:DS is stopped
is_stopped() {
  $WRENDS_INSTALL_PATH/status -D "$ROOT_USER_DN" -w "$ROOT_USER_PASSWORD" | grep -eq 'Server Run Status:\ *Stopped'
}

first_start() {
  $WRENDS_INSTALL_PATH/setup \
    --hostname localhost \
    --cli \
    --backendType je \
    --baseDN $BASE_DN \
    --ldapPort $LDAP_PORT \
    --ldapsPort $LDAPS_PORT \
    --enableStartTLS \
    --adminConnectorPort $ADMIN_CONNECTOR_PORT \
    --rootUserDN "$ROOT_USER_DN" \
    --rootUserPassword $ROOT_USER_PASSWORD \
    --no-prompt \
    --noPropertiesFile \
    --doNotStart \
    $SSL_OPTIONS \
    $ADDITIONAL_SETUP_ARGS

  $WRENDS_INSTALL_PATH/bin/start-ds
  while [ ! is_started ]; do sleep 0.1; done

  process_init_files

  $WRENDS_INSTALL_PATH/bin/stop-ds
  while [ ! is_stopped ]; do sleep 0.1; done
}
