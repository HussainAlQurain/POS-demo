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
    
    /**
     * Configuration for multiple locations
     */
    private Locations locations = new Locations();
    
    public static class Locations {
        /**
         * Whether to enable multi-location sales simulation
         */
        private boolean enabled = true;
        
        /**
         * Number of locations to simulate sales for
         */
        private int count = 50;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

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
    
    public Locations getLocations() {
        return locations;
    }
    
    public void setLocations(Locations locations) {
        this.locations = locations;
    }
}