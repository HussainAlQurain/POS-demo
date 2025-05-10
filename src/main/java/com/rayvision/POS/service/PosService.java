package com.rayvision.POS.service;

import com.rayvision.POS.domain.Product;
import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.domain.SaleLine;
import com.rayvision.POS.repository.ProductRepository;
import com.rayvision.POS.repository.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PosService {
    
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final InvoiceNumberService invoiceNumberService;
    private final Random random = new Random();
    
    public PosService(ProductRepository productRepository, SaleRepository saleRepository, InvoiceNumberService invoiceNumberService) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.invoiceNumberService = invoiceNumberService;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductByPosCode(String posCode) {
        return productRepository.findByPosCode(posCode);
    }
    
    @Transactional
    public Product createProduct(Product product) {
        // If no POS code provided, generate a random one that doesn't exist yet
        if (product.getPosCode() == null || product.getPosCode().isEmpty()) {
            String posCode;
            do {
                // Generate a random 6-digit POS code
                posCode = String.format("%06d", random.nextInt(1000000));
            } while (productRepository.findByPosCode(posCode).isPresent());
            product.setPosCode(posCode);
        }
        
        // Set default stock if not provided
        if (product.getStock() == null) {
            product.setStock(100);
        }
        
        return productRepository.save(product);
    }
    
    @Transactional
    public Sale createSale(Sale sale) {
        // If saleDateTime is not provided, use current time
        if (sale.getSaleDateTime() == null) {
            sale.setSaleDateTime(LocalDateTime.now());
        }
        
        // Set a default locationId if none is provided
        if (sale.getLocationId() == null) {
            sale.setLocationId(1L);
        }
        
        // Generate a unique invoice number, possibly including location code
        sale.setPosReference(invoiceNumberService.generateNextInvoiceNumber(sale.getLocationId()));
        
        // Process each sale line
        if (sale.getLines() != null) {
            for (SaleLine line : sale.getLines()) {
                line.setSale(sale);
                
                // Optional: Update product stock if needed
                // productRepository.findByPosCode(line.getPosCode())
                //    .ifPresent(product -> {
                //        product.setStock(product.getStock() - line.getQuantity().intValue());
                //        productRepository.save(product);
                //    });
            }
        }
        
        return saleRepository.save(sale);
    }
    
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }
    
    /**
     * Get sales for a specific location
     */
    public List<Sale> getSalesByLocation(Long locationId) {
        return saleRepository.findByLocationId(locationId);
    }
    
    /**
     * Get sales for a specific location with pagination
     */
    public Page<Sale> getSalesByLocation(Long locationId, Pageable pageable) {
        return saleRepository.findByLocationId(locationId, pageable);
    }
    
    /**
     * Get a sale by its ID
     * 
     * @param id The sale ID
     * @return Optional containing the sale if found
     */
    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }
    
    /**
     * Find sales between two dates with pagination support
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @param pageable Pagination information
     * @return Page of sales within the date range
     */
    public Page<Sale> findSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return saleRepository.findBySaleDateTimeBetween(startDate, endDate, pageable);
    }
    
    /**
     * Find sales for a specific location between two dates with pagination support
     */
    public Page<Sale> findSalesByLocationAndDateRange(
            Long locationId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return saleRepository.findByLocationIdAndSaleDateTimeBetween(locationId, startDate, endDate, pageable);
    }
    
    /**
     * Count total sales between two dates
     * 
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return Count of sales within the date range
     */
    public long countSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.countBySaleDateTimeBetween(startDate, endDate);
    }
    
    /**
     * Count sales for a specific location
     */
    public long countSalesByLocation(Long locationId) {
        return saleRepository.countByLocationId(locationId);
    }
    
    /**
     * Count sales for a specific location between two dates
     */
    public long countSalesByLocationAndDateRange(Long locationId, LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.countByLocationIdAndSaleDateTimeBetween(locationId, startDate, endDate);
    }
}