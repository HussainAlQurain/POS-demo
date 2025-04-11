package com.rayvision.POS.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pos.simulator")
public class SimulatorConfig {
    
    /**
     * Whether the sales simulator is enabled
     */
    private boolean enabled = false; // disabled by default
    
    /**
     * The interval between generated sales in milliseconds
     */
    private int intervalMillis = 10000; // 10 seconds

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(int intervalMillis) {
        this.intervalMillis = intervalMillis;
    }
}