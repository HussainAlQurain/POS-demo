package com.rayvision.POS.api;

import com.rayvision.POS.config.SimulatorConfig;
import com.rayvision.POS.service.SalesSimulatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulator")
public class SimulatorController {

    private final SimulatorConfig simulatorConfig;
    private final SalesSimulatorService salesSimulatorService;

    public SimulatorController(SimulatorConfig simulatorConfig, SalesSimulatorService salesSimulatorService) {
        this.simulatorConfig = simulatorConfig;
        this.salesSimulatorService = salesSimulatorService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("enabled", simulatorConfig.isEnabled());
        status.put("intervalMillis", simulatorConfig.getIntervalMillis());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleSimulator(@RequestParam(required = false) Boolean enabled) {
        // Toggle if no parameter provided, otherwise use the provided value
        boolean newState = (enabled == null) ? !simulatorConfig.isEnabled() : enabled;
        simulatorConfig.setEnabled(newState);

        Map<String, Object> response = new HashMap<>();
        response.put("enabled", simulatorConfig.isEnabled());
        response.put("message", simulatorConfig.isEnabled() ? 
                "Sales simulator enabled. Sales will be generated every " + 
                (simulatorConfig.getIntervalMillis() / 1000) + " seconds." : 
                "Sales simulator disabled.");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/interval")
    public ResponseEntity<Map<String, Object>> setInterval(@RequestParam int intervalSeconds) {
        // Validate input (minimum 1 second, maximum 60 seconds)
        int validInterval = Math.max(1, Math.min(60, intervalSeconds));
        simulatorConfig.setIntervalMillis(validInterval * 1000);
        
        // Reschedule the task with the new interval
        salesSimulatorService.scheduleAtCurrentRate();

        Map<String, Object> response = new HashMap<>();
        response.put("intervalMillis", simulatorConfig.getIntervalMillis());
        response.put("intervalSeconds", validInterval);
        response.put("message", "Sales simulator interval set to " + validInterval + " seconds.");
        
        return ResponseEntity.ok(response);
    }
}