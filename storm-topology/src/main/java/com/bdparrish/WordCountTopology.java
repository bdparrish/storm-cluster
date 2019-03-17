package com.bdparrish;

import com.bdparrish.common.BoltBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WordCountTopology {

    private static final Logger LOGGER = LoggerFactory.getLogger(WordCountTopology.class);

    private static boolean isLocal;

    public static void main(String[] args) {
        isLocal = args.length > 0 && args[0].equals("local");

        new WordCountTopology();
    }

    public WordCountTopology() {
        Config conf = getConfig();

        StormTopology topology;
        try {
            topology = buildTopology();
        } catch (Exception e) {
            final String message = "Could not build topology";

            LOGGER.error(message, e);

            return;
        }

        String topologyName = getTopologyName();

        if (isLocal()) {
            runLocalTopology(topologyName, conf, topology);
        } else {
            submitTopology(topologyName, conf, topology);
        }
    }
    protected String getTopologyName() {
        return "wordcount";
    }

    protected boolean isLocal() {
        return isLocal;
    }

    protected Pair<String, IRichSpout> getSpout() {
        return new Pair<String, IRichSpout>() {
            @Override
            public String getLeft() {
                return Constants.TOPOLOGY_SPOUT;
            }

            @Override
            public IRichSpout getRight() {
                return new WordCountSpout();
            }

            @Override
            public IRichSpout setValue(IRichSpout value) {
                return null;
            }
        };
    }

    protected List<BoltBuilder> getBolts() {
        List<BoltBuilder> bolts = new ArrayList<>();

        Map<String, String> splitterMap = Maps.newHashMap();
        splitterMap.put(Constants.TOPOLOGY_SPOUT, Constants.WORD_COUNT_STREAM);
        BoltBuilder splitterBoltBuilder = new BoltBuilder()
                .id(Constants.TOPOLOGY_BOLT_SPLITTER)
                .bolt(new SplitterBolt())
                .componentStreamMap(splitterMap);
        bolts.add(splitterBoltBuilder);

        Map<String, String> counterMap = Maps.newHashMap();
        counterMap.put(Constants.TOPOLOGY_BOLT_SPLITTER, null);
        BoltBuilder counterBoltBuilder = new BoltBuilder()
                .id(Constants.TOPOLOGY_BOLT_COUNTER)
                .bolt(new CounterBolt())
                .componentStreamMap(counterMap);
        bolts.add(counterBoltBuilder);

        Map<String, String> rankerMap = Maps.newHashMap();
        rankerMap.put(Constants.TOPOLOGY_BOLT_COUNTER, null);
        BoltBuilder rankerBoltBuilder = new BoltBuilder()
                .id(Constants.TOPOLOGY_BOLT_RANKER)
                .bolt(new RankerBolt())
                .componentStreamMap(rankerMap);
        bolts.add(rankerBoltBuilder);

        return bolts;
    }

    protected Config getConfig() {
        int numSlots = 1;
        int numAckers = 1;
        int writerExecutors = 1;
        int messageTimeout = 1200; // say, 20 minutes max for a batch

        Config conf = new Config();
        conf.setNumWorkers(numSlots);
        conf.setNumAckers(numAckers);
        conf.setMessageTimeoutSecs(messageTimeout);

        // only emit one per executor per slot
        conf.setMaxSpoutPending(writerExecutors);

        return conf;
    }

    protected StormTopology buildTopology() throws Exception {
        LOGGER.info("Building Storm topology");

        Pair<String, IRichSpout> spout = getSpout();

        List<BoltBuilder> bolts = getBolts();

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(spout.getLeft(), spout.getRight());

        for (BoltBuilder boltBuilder : bolts) {
            boltBuilder.addToTopology(builder);
        }

        return builder.createTopology();
    }

    protected void runLocalTopology(String topologyName, Config conf, StormTopology topology) {
        LOGGER.info("Starting local Storm cluster...");

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(topologyName, conf, topology);
        Utils.sleep(10 * 600000);
        cluster.killTopology(topologyName);
        cluster.shutdown();
    }

    protected void submitTopology(String topologyName, Config conf, StormTopology topology) {
        LOGGER.info("Submitting topology to Storm...");

        try {
            StormSubmitter.submitTopology(topologyName, conf, topology);
        } catch (AlreadyAliveException | InvalidTopologyException | AuthorizationException e) {
            final String message = "Failed to submit topology to Storm";

            LOGGER.error(message, e);

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("topology name", topologyName);
            jsonMap.putAll(conf);

//            LoggingUtils.writeLog(LOGGER, PrTopology.class, message, jsonMap, e);
        }
    }
}
