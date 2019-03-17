Apache Storm Docker Cluster

An updated version from @wipatrick (https://github.com/wipatrick/docker-kafka-storm)

## Building the cluster
```
./build.sh initial
```

## Running the cluster
```
./compose.sh {start-foreground|start-background|stop}
```

## Create Kafka topic

```
./create-kafka-topic.sh $REPLICATION_FACTOR $PARTITION $TOPIC_NAME
# ./create-kafka-topic.sh 1 1 wordcount
```

## Delete Kafka topic

```
./create-kafka-topic.sh $TOPIC_NAME
# ./delete-kafka-topic.sh wordcount
```

## Submit topology
```
./submit-storm-topology.sh $MAIN_CLASS $TOPOLOGY_NAME $TOPIC_NAME
# ./submit-storm-topology.sh com.bdparrish.WordCountTopology wordcount-topology wordcount
```

If you want to use a different topology, then use the storm-topology/ as a guide on how to build a topology Docker image in order to deploy the topology to your Storm cluster on Docker.

## Killing topology
```
./kill-storm-topology.sh $TOPOLOGY_NAME
# ./kill-storm-topology.sh wordcount-topology
```
