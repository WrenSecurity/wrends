#!/bin/bash -e

if [ ! -d $INSTANCE_DIR/config ]; then
  source ./bootstrap/setup.sh
  echo "First start..."
  first_start
  echo "First start done."
fi

exec ./bin/start-ds --nodetach
