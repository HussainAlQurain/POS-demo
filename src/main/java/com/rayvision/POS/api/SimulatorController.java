package com.rayvision.POS.api;

import com.rayvision.POS.config.SimulatorConfig;
import com.rayvision.POS.service.SalesSimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final SimulatorConfig config;
    private final SalesSimulatorService simulatorService;

    @Autowired
    public SimulatorController(SimulatorConfig config, SalesSimulatorService simulatorService) {
        this.config = config;
        this.simulatorService = simulatorService;
    }

    /**
     * Get the current status of the simulator
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", config.isEnabled());
        status.put("intervalMillis", config.getIntervalMillis());
        status.put("locationsEnabled", config.getLocations().isEnabled());
        status.put("locationCount", config.getLocations().getCount());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Toggle the simulator on/off
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleSimulator() {
        config.setEnabled(!config.isEnabled());
        
        // Reschedule the simulator with the updated settings
        simulatorService.scheduleAtCurrentRate();
        
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", config.isEnabled());
        result.put("message", config.isEnabled() ? "Simulator enabled" : "Simulator disabled");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Update the simulator interval
     */
    @PostMapping("/interval")
    public ResponseEntity<Map<String, Object>> updateInterval(@RequestParam(required = true) Integer intervalSeconds) {
        int intervalMillis = intervalSeconds * 1000;
        config.setIntervalMillis(intervalMillis);
        
        // Reschedule the simulator with the new interval
        simulatorService.scheduleAtCurrentRate();
        
        Map<String, Object> result = new HashMap<>();
        result.put("intervalSeconds", intervalSeconds);
        result.put("intervalMillis", intervalMillis);
        result.put("message", "Interval updated to " + intervalSeconds + " seconds");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Toggle multi-location simulation
     */
    @PostMapping("/locations")
    public ResponseEntity<Map<String, Object>> toggleLocations(@RequestParam(required = true) boolean enabled) {
        config.getLocations().setEnabled(enabled);
        
        Map<String, Object> result = new HashMap<>();
        result.put("enabled", config.getLocations().isEnabled());
        result.put("message", enabled ? "Multi-location simulation enabled" : "Multi-location simulation disabled");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Manually generate a sale for a specific location
     */
    @PostMapping("/generate-sale")
    public ResponseEntity<Map<String, Object>> generateSale(@RequestParam(required = false) Long locationId) {
        // If no locationId is provided, use a random one
        if (locationId != null) {
            simulatorService.generateRandomSaleForLocation(locationId);
        } else {
            simulatorService.generateRandomSales();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Sale generated" + (locationId != null ? " for location " + locationId : ""));
        
        return ResponseEntity.ok(result);
    }
}