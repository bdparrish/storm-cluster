#!/bin/bash

NETWORK=$(docker inspect --format '{{ .NetworkSettings.Networks }}' zookeeper | cut -d '[' -f 2 | cut -d ':' -f 1)

echo "Docker Network: $NETWORK"

# Only works for container ports, that are mapped/exposed on the Host
ZK_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "2181/tcp") 0).HostPort }}' zookeeper)
NIMBUS_THRIFT_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "6627/tcp") 0).HostPort }}' nimbus)

docker run -it --rm \
        -e TOPOLOGY_NAME=$1 \
        -e NIMBUS_HOST=nimbus \
        -e NIMBUS_THRIFT_PORT=$NIMBUS_THRIFT_PORT \
        --network=$NETWORK \
        --name topology \
        storm-topology \
        "kill"