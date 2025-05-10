package com.rayvision.POS.service;

import com.rayvision.POS.domain.Location;
import com.rayvision.POS.repository.LocationRepository;
import com.rayvision.POS.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InvoiceNumberService {

    private final SaleRepository saleRepository;
    private final LocationRepository locationRepository;
    private static final String INVOICE_PREFIX = "INV-";
    private static final int PADDING_LENGTH = 6;
    
    @Autowired
    public InvoiceNumberService(SaleRepository saleRepository, LocationRepository locationRepository) {
        this.saleRepository = saleRepository;
        this.locationRepository = locationRepository;
    }
    
    /**
     * Generates the next invoice number in sequence.
     * Format: INV-000001, INV-000002, etc.
     * 
     * @return The next invoice number
     */
    @Transactional
    public String generateNextInvoiceNumber() {
        return generateNextInvoiceNumber(1L); // Default to location 1
    }
    
    /**
     * Generates the next invoice number in sequence for a specific location.
     * Format: LOC001-000001, LOC002-000002, etc.
     * 
     * @param locationId The location ID
     * @return The next invoice number
     */
    @Transactional
    public String generateNextInvoiceNumber(Long locationId) {
        // Get the location code to use as prefix
        String locationCode = locationRepository.findById(locationId)
                .map(Location::getCode)
                .orElse("LOC001");
        
        String prefix = locationCode + "-";
        
        // Get the highest invoice number for this location from the database
        Optional<String> lastInvoiceOpt = saleRepository.findTopByPosReferenceStartingWithAndLocationIdOrderByIdDesc(prefix, locationId);
        
        int nextNumber = 1;
        
        if (lastInvoiceOpt.isPresent()) {
            String lastInvoice = lastInvoiceOpt.get();
            
            try {
                // Extract the numeric part of the last invoice number
                String numericPart = lastInvoice.substring(prefix.length());
                nextNumber = Integer.parseInt(numericPart) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If there's any error parsing the number, just start from 1
                nextNumber = 1;
            }
        }
        
        // Format the new invoice number with leading zeros
        return prefix + String.format("%0" + PADDING_LENGTH + "d", nextNumber);
    }
}