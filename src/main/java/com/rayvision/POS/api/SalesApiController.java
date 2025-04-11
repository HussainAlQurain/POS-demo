package com.rayvision.POS.api;

import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.service.PosService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SalesApiController {

    private final PosService posService;

    public SalesApiController(PosService posService) {
        this.posService = posService;
    }

    /**
     * Get sales between two dates with pagination support
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated list of sales
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Create pageable object with sorting by date
        Pageable pageable = PageRequest.of(page, size, Sort.by("saleDateTime").ascending());
        
        // Fetch sales for the specified page
        Page<Sale> salesPage = posService.findSalesByDateRange(startDate, endDate, pageable);
        
        // Get total count for the entire date range (not just current page)
        long totalSales = posService.countSalesByDateRange(startDate, endDate);
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("sales", salesPage.getContent());
        response.put("currentPage", salesPage.getNumber());
        response.put("totalItems", totalSales);
        response.put("totalPages", salesPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific sale by ID
     * 
     * @param id Sale ID
     * @return The sale or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        return posService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get the total count of sales between two dates
     * 
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive) 
     * @return Count of sales in the date range
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getSalesCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        long count = posService.countSalesByDateRange(startDate, endDate);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
}