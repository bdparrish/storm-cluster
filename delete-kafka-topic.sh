#!/bin/bash

NETWORK=$(docker inspect --format '{{ .NetworkSettings.Networks }}' zookeeper | cut -d '[' -f 2 | cut -d ':' -f 1)
# Only works for container ports, that are mapped/exposed on the Host
ZK_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "2181/tcp") 0).HostPort }}' zookeeper)

echo "Docker Network: $NETWORK"

# create topic
docker run -it --rm \
		--network=$NETWORK \
        --name delete-kafka-topic \
        ches/kafka \
        bash -c "/kafka/bin/kafka-topics.sh --delete --zookeeper zookeeper:$ZK_PORT --topic $1"