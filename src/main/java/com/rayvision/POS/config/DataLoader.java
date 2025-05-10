package com.rayvision.POS.config;

import com.rayvision.POS.domain.Location;
import com.rayvision.POS.domain.Product;
import com.rayvision.POS.repository.LocationRepository;
import com.rayvision.POS.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataLoader {
    
    @Bean
    CommandLineRunner initDatabase(
            ProductRepository productRepository,
            LocationRepository locationRepository) {
        return args -> {
            // Initialize products if not exist
            if (productRepository.count() == 0) {
                List<Product> products = new ArrayList<>();
                Random random = new Random();
                String[] productNames = {
                    "Big Burger", "Cheese Burger", "Chicken Burger", "Veggie Burger",
                    "Coke", "Diet Coke", "Sprite", "Fanta", "Water", "Coffee",
                    "French Fries", "Curly Fries", "Onion Rings", "Mozzarella Sticks",
                    "Chocolate Shake", "Vanilla Shake", "Strawberry Shake", 
                    "Apple Pie", "Ice Cream", "Chicken Nuggets"
                };
                
                // Create 20 products with POS codes from POS1001 to POS1020
                for (int i = 1; i <= 20; i++) {
                    String posCode = "POS" + (1000 + i);
                    String name = productNames[i-1];
                    
                    // Generate a random price between 1.99 and 25.99
                    double price = 1.99 + (random.nextDouble() * 24.0);
                    price = Math.round(price * 100.0) / 100.0;  // Round to 2 decimal places
                    
                    // Generate random stock between 50 and 200
                    int stock = 50 + random.nextInt(151);
                    
                    Product product = Product.builder()
                            .posCode(posCode)
                            .name(name)
                            .price(price)
                            .stock(stock)
                            .build();
                    
                    products.add(product);
                }
                
                productRepository.saveAll(products);
                System.out.println("Sample products (POS1001-POS1020) have been initialized");
            }
            
            // Initialize 50 locations if not exist
            if (locationRepository.count() == 0) {
                List<Location> locations = new ArrayList<>();
                
                // Create a company
                Long defaultCompanyId = 1L;
                
                // Create 50 locations
                for (int i = 1; i <= 50; i++) {
                    Location location = Location.builder()
                            .name("Location " + i)
                            .code("LOC" + String.format("%03d", i))
                            .address("Address for Location " + i)
                            .city("City " + ((i % 10) + 1))
                            .state("State " + ((i % 5) + 1))
                            .phone("555-" + String.format("%04d", i))
                            .companyId(defaultCompanyId)
                            .build();
                    
                    locations.add(location);
                }
                
                locationRepository.saveAll(locations);
                System.out.println("50 sample locations have been initialized");
            }
        };
    }
}