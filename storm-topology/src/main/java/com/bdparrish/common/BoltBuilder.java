package com.bdparrish.common;

import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.TopologyBuilder;
import java.util.Map;

public class BoltBuilder {

    private String id;
    private IRichBolt bolt;
    private Map<String, String> componentStreamMap;

    public BoltBuilder id(String id) {
        this.id = id;
        return this;
    }

    public BoltBuilder bolt(IRichBolt bolt) {
        this.bolt = bolt;
        return this;
    }

    public BoltBuilder componentStreamMap(Map<String, String> map) {
        this.componentStreamMap = map;
        return this;
    }

    public void addToTopology(TopologyBuilder builder) {
        for (Map.Entry<String, String> entry : componentStreamMap.entrySet()) {
            builder.setBolt(id, bolt, 1).shuffleGrouping(entry.getKey());
        }
    }

}

