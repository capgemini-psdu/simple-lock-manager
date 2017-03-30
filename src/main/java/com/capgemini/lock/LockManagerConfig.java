package com.capgemini.lock;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;

import java.util.Arrays;
import java.util.List;

/**
 * JIRA-ID
 * DESCRIPTION
 * <p>
 * Created by jzbhhx on 05/09/16.
 */
public class LockManagerConfig {

    private final Config config;

    /**
     * returns the hazelcast config object.
     * @return hazelcast config object for the lock manager
     */
    protected Config getConfig() {
        return config;
    }

    /**
     * Creates a default config object and underlying default hazelcast config
     */
    public LockManagerConfig(){
        config = new Config();
    }

    /**
     * Creates a config object and underlying hazelcast config using the supplied parameters.
     *
     * @param configName The name for this lock manager config
     * @param hostNames Comma separated list of host names for lock manager members
     * @param password the password for this group
     * @param port the port for this lock manager
     */

    public LockManagerConfig(String configName, String hostNames, String password, int port) {

        config = new Config();

        if (port != 0) {
            config.getNetworkConfig().setPort(port);
            config.getNetworkConfig().setPortAutoIncrement(false);
        }
        if (configName != null && configName.length() != 0)
            config.getGroupConfig().setName(configName);

        if (password != null && password.length() != 0)
            config.getGroupConfig().setPassword(password);

        if (hostNames != null && hostNames.length() != 0) {
            List<String> hcMembers = Arrays.asList(hostNames.split("\\s*,\\s*"));
            NetworkConfig network = config.getNetworkConfig();
            JoinConfig join = network.getJoin();
            join.getMulticastConfig().setEnabled(false);
            join.getTcpIpConfig().setMembers(hcMembers).setEnabled(true);
            network.getInterfaces().setEnabled(false);
        }

        config.setProperty("hazelcast.health.monitoring.level", "OFF");
    }
}
