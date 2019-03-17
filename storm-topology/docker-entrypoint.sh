#!/bin/bash

# storm jar will submit the jar to the cluster and configure the StormSubmitter
# class to talk to the right cluster. In this example, after uploading the jar
# storm jar calls the main function on org.me.MyTopology with the arguments "arg1",
# "arg2", "arg3", "arg4", "arg5" and "arg6".

case "$1" in
        submit)
            /apache-storm-1.2.2/bin/storm jar storm-topology.jar ${MAINCLASS} ${TOPOLOGY_NAME} ${ZK_HOST} ${ZK_PORT} ${TOPIC} ${NIMBUS_HOST} ${NIMBUS_THRIFT_PORT}
            ;;

        kill)
            /apache-storm-1.2.2/bin/storm kill ${TOPOLOGY_NAME} -c nimbus.thrift.port=${NIMBUS_THRIFT_PORT} -w 1
            ;;

        *)
            echo $"Usage: $0 {submit|kill}"
            exit 1

esac