package com.rayvision.POS.service;

import com.rayvision.POS.config.SimulatorConfig;
import com.rayvision.POS.domain.Location;
import com.rayvision.POS.domain.Product;
import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.domain.SaleLine;
import com.rayvision.POS.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SalesSimulatorService {

    private static final Logger logger = LoggerFactory.getLogger(SalesSimulatorService.class);
    
    private final PosService posService;
    private final SimulatorConfig config;
    private final LocationRepository locationRepository;
    
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;
    private List<Location> locations;

    @Autowired
    public SalesSimulatorService(PosService posService, SimulatorConfig config, LocationRepository locationRepository) {
        this.posService = posService;
        this.config = config;
        this.locationRepository = locationRepository;
        
        // Create a dedicated task scheduler for this service
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("SalesSimulator-");
        threadPoolTaskScheduler.initialize();
        this.taskScheduler = threadPoolTaskScheduler;
        
        logger.info("Sales Simulator initialized - enabled: {}, interval: {} ms, multi-location: {}, location count: {}", 
            config.isEnabled(), 
            config.getIntervalMillis(),
            config.getLocations().isEnabled(),
            config.getLocations().getCount());
    }
    
    @PostConstruct
    public void startScheduler() {
        // Load all locations for multi-tenant simulation
        reloadLocations();
        
        // Schedule the task with the initial interval
        scheduleAtCurrentRate();
    }
    
    /**
     * Reload locations from the database
     */
    private void reloadLocations() {
        this.locations = locationRepository.findAll();
        logger.info("Loaded {} locations for sales simulation", locations.size());
        
        // If no locations found, create default location
        if (locations.isEmpty()) {
            logger.info("No locations found, simulator will only generate sales for location 1");
        }
    }
    
    @PreDestroy
    public void stopScheduler() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }
    
    /**
     * Reschedule the task with the current interval from config
     * This allows changing the interval at runtime
     */
    public void scheduleAtCurrentRate() {
        // Cancel any existing scheduled task
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
        
        // Schedule the task with the current interval
        PeriodicTrigger trigger = new PeriodicTrigger(config.getIntervalMillis(), TimeUnit.MILLISECONDS);
        scheduledTask = taskScheduler.schedule(this::generateRandomSales, trigger);
        
        logger.info("Sales simulator rescheduled with interval: {} ms", config.getIntervalMillis());
    }
    
    /**
     * Generate random sales for all enabled locations
     */
    public void generateRandomSales() {
        if (!config.isEnabled()) {
            return; // Skip if simulator is disabled
        }
        
        // Reload locations to ensure we always have the latest
        reloadLocations();
        
        // Check if multi-location is enabled
        if (config.getLocations().isEnabled() && !locations.isEmpty()) {
            // Get a subset of locations to generate sales for (not all locations will have sales in each cycle)
            int locationsWithSales = ThreadLocalRandom.current().nextInt(1, Math.min(10, locations.size()) + 1);
            List<Location> selectedLocations = getRandomLocations(locations, locationsWithSales);
            
            logger.info("Generating sales for {} locations: {}", selectedLocations.size(), 
                    selectedLocations.stream().map(l -> l.getName() + " (ID: " + l.getId() + ")").collect(Collectors.joining(", ")));
            
            // Generate sales for each selected location
            for (Location location : selectedLocations) {
                generateRandomSaleForLocation(location.getId());
            }
        } else {
            // Fall back to single location if multi-location is disabled
            generateRandomSaleForLocation(1L);
        }
    }
    
    /**
     * Generate a random sale for a specific location
     */
    public void generateRandomSaleForLocation(Long locationId) {
        // Get all products - this will include any newly created products
        List<Product> availableProducts = posService.getAllProducts();
        
        if (availableProducts.isEmpty()) {
            logger.warn("No products available to create simulated sales");
            return;
        }
        
        // Create a new sale
        Sale sale = new Sale();
        sale.setSaleDateTime(LocalDateTime.now());
        sale.setLocationId(locationId);
        
        // Use the location code as part of the POS reference
        String locationCode = locations.stream()
            .filter(loc -> loc.getId().equals(locationId))
            .findFirst()
            .map(Location::getCode)
            .orElse("LOC001");
        
        // Generate a unique POS reference
        String posReference = locationCode + "-" + 
                              LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 10);
        sale.setPosReference(posReference);
        
        // Determine how many different products to include (1-4)
        int maxProducts = Math.min(availableProducts.size(), 4);
        int numberOfProducts = ThreadLocalRandom.current().nextInt(1, maxProducts + 1);
        
        // Randomly select products without repetition
        List<Product> selectedProducts = getRandomProducts(availableProducts, numberOfProducts);
        
        // Create sale lines
        List<SaleLine> lines = new ArrayList<>();
        for (Product product : selectedProducts) {
            SaleLine line = new SaleLine();
            line.setPosCode(product.getPosCode());
            line.setMenuItemName(product.getName());
            
            // Random quantity between 1 and 5
            double quantity = ThreadLocalRandom.current().nextInt(1, 6);
            line.setQuantity(quantity);
            
            line.setUnitPrice(product.getPrice());
            line.setSale(sale);
            line.calculateExtended(); // Calculate extended price
            
            lines.add(line);
        }
        
        sale.setLines(lines);
        sale.calculateTotal(); // Calculate total
        
        // Save the sale
        Sale savedSale = posService.createSale(sale);
        
        logger.info("Generated simulated sale: ID={}, Location={}, Invoice={}, Total=${}, Items={}", 
                savedSale.getId(),
                locationId,
                savedSale.getPosReference(),
                String.format("%.2f", savedSale.getTotal()), 
                savedSale.getLines().size());
    }
    
    /**
     * Get a random subset of products
     */
    private List<Product> getRandomProducts(List<Product> availableProducts, int count) {
        List<Product> products = new ArrayList<>(availableProducts);
        Collections.shuffle(products);
        return products.subList(0, count);
    }
    
    /**
     * Get a random subset of locations
     */
    private List<Location> getRandomLocations(List<Location> availableLocations, int count) {
        List<Location> locs = new ArrayList<>(availableLocations);
        Collections.shuffle(locs);
        return locs.subList(0, Math.min(count, locs.size()));
    }
}