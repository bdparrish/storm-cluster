#!/bin/bash

case "$1" in
        initial)
            mvn -f storm-topology/pom.xml clean install package -DskipTests=true

            docker build -t=storm-topology -f storm-topology/Dockerfile .
            
            docker-compose build
            ;;

        *)
            echo $"Usage: $0 {initial}"
            exit 1
esac