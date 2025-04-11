package com.rayvision.POS.config;

import com.rayvision.POS.domain.Product;
import com.rayvision.POS.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {
    
    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            // Check if products already exist to avoid duplicates on restart
            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .posCode("10234")
                        .name("Big Burger")
                        .price(25.0)
                        .stock(100)
                        .build());
                
                productRepository.save(Product.builder()
                        .posCode("COKE01")
                        .name("Coke")
                        .price(5.0)
                        .stock(200)
                        .build());
                
                productRepository.save(Product.builder()
                        .posCode("FRIES01")
                        .name("French Fries")
                        .price(8.0)
                        .stock(150)
                        .build());
                
                productRepository.save(Product.builder()
                        .posCode("SHAKE01")
                        .name("Milkshake")
                        .price(7.5)
                        .stock(50)
                        .build());
                
                System.out.println("Sample products have been initialized");
            }
        };
    }
}