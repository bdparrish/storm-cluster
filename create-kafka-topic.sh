#!/bin/bash

NETWORK=$(docker inspect --format '{{ .NetworkSettings.Networks }}' zookeeper | cut -d '[' -f 2 | cut -d ':' -f 1)
# Only works for container ports, that are mapped/exposed on the Host
ZK_PORT=$(docker inspect --format '{{ (index (index .NetworkSettings.Ports "2181/tcp") 0).HostPort }}' zookeeper)

echo "Network: $NETWORK"
echo "ZooKeeper Port: $ZK_PORT"

# create topic
docker run -it --rm \
		--network=$NETWORK \
        --name create-kafka-topic \
        ches/kafka \
        bash -c "/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:$ZK_PORT --replication-factor $1 --partition $2 --topic $3"