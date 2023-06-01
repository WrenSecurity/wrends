#!/bin/bash -eu

# This file is created during Docker build and should always be present
INSTANCE_DIR="$(</opt/wrends/instance.loc)"

if [ ! -d "$INSTANCE_DIR/config" ]; then
  source ./bootstrap/setup.sh
  echo "First start..."
  first_start
  echo "First start done."
fi

exec ./bin/start-ds --nodetach
