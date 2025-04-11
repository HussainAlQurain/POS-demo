package com.rayvision.POS.service;

import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InvoiceNumberService {

    private final SaleRepository saleRepository;
    private static final String INVOICE_PREFIX = "INV-";
    private static final int PADDING_LENGTH = 6;
    
    @Autowired
    public InvoiceNumberService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }
    
    /**
     * Generates the next invoice number in sequence.
     * Format: INV-000001, INV-000002, etc.
     * 
     * @return The next invoice number
     */
    @Transactional
    public String generateNextInvoiceNumber() {
        // Get the highest invoice number from the database
        Optional<String> lastInvoiceOpt = saleRepository.findTopByPosReferenceStartingWithOrderByIdDesc(INVOICE_PREFIX);
        
        int nextNumber = 1;
        
        if (lastInvoiceOpt.isPresent()) {
            String lastInvoice = lastInvoiceOpt.get();
            
            try {
                // Extract the numeric part of the last invoice number
                String numericPart = lastInvoice.substring(INVOICE_PREFIX.length());
                nextNumber = Integer.parseInt(numericPart) + 1;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // If there's any error parsing the number, just start from 1
                nextNumber = 1;
            }
        }
        
        // Format the new invoice number with leading zeros
        return INVOICE_PREFIX + String.format("%0" + PADDING_LENGTH + "d", nextNumber);
    }
}