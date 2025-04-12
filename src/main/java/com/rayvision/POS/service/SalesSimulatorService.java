package com.rayvision.POS.service;

import com.rayvision.POS.config.SimulatorConfig;
import com.rayvision.POS.domain.Product;
import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.domain.SaleLine;
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

@Service
public class SalesSimulatorService {

    private static final Logger logger = LoggerFactory.getLogger(SalesSimulatorService.class);
    
    private final PosService posService;
    private final SimulatorConfig config;
    
    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;

    @Autowired
    public SalesSimulatorService(PosService posService, SimulatorConfig config) {
        this.posService = posService;
        this.config = config;
        
        // Create a dedicated task scheduler for this service
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("SalesSimulator-");
        threadPoolTaskScheduler.initialize();
        this.taskScheduler = threadPoolTaskScheduler;
        
        logger.info("Sales Simulator initialized - enabled: {}, interval: {} ms", config.isEnabled(), config.getIntervalMillis());
    }
    
    @PostConstruct
    public void startScheduler() {
        // Schedule the task with the initial interval
        scheduleAtCurrentRate();
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
        scheduledTask = taskScheduler.schedule(this::generateRandomSale, trigger);
        
        logger.info("Sales simulator rescheduled with interval: {} ms", config.getIntervalMillis());
    }
    
    /**
     * Generate a random sale at the configured interval
     */
    public void generateRandomSale() {
        if (!config.isEnabled()) {
            return; // Skip if simulator is disabled
        }
        
        // Get all products - this will include any newly created products
        List<Product> availableProducts = posService.getAllProducts();
        
        if (availableProducts.isEmpty()) {
            logger.warn("No products available to create simulated sales");
            return;
        }
        
        // Create a new sale
        Sale sale = new Sale();
        sale.setSaleDateTime(LocalDateTime.now());
        sale.setLocationId(1L);
        
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
        
        logger.info("Generated simulated sale: ID={}, Invoice={}, Total=${}, Items={}", 
                savedSale.getId(),
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
}