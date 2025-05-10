package com.rayvision.POS.api;

import com.rayvision.POS.domain.Sale;
import com.rayvision.POS.service.PosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final PosService posService;

    @Autowired
    public SaleController(PosService posService) {
        this.posService = posService;
    }
    
    /**
     * Get all sales with optional pagination
     */
    @GetMapping
    public ResponseEntity<?> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // If date range is provided, filter by date range
        if (startDate != null && endDate != null) {
            Page<Sale> salesPage = posService.findSalesByDateRange(startDate, endDate, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", salesPage.getContent());
            response.put("currentPage", salesPage.getNumber());
            response.put("totalItems", salesPage.getTotalElements());
            response.put("totalPages", salesPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        }

        // Otherwise, get all sales
        List<Sale> sales = posService.getAllSales();
        return ResponseEntity.ok(sales);
    }

    /**
     * Get sales for a specific location with optional date filtering
     */
    @GetMapping("/location/{locationId}")
    public ResponseEntity<?> getSalesByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // If date range is provided, filter by location and date range
        if (startDate != null && endDate != null) {
            Page<Sale> salesPage = posService.findSalesByLocationAndDateRange(locationId, startDate, endDate, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", salesPage.getContent());
            response.put("currentPage", salesPage.getNumber());
            response.put("totalItems", salesPage.getTotalElements());
            response.put("totalPages", salesPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        }
        
        // Otherwise, get all sales for this location
        Page<Sale> salesPage = posService.getSalesByLocation(locationId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", salesPage.getContent());
        response.put("currentPage", salesPage.getNumber());
        response.put("totalItems", salesPage.getTotalElements());
        response.put("totalPages", salesPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get sales for a date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get sales for the date range with pagination
        Page<Sale> salesPage = posService.findSalesByDateRange(startDate, endDate, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", salesPage.getContent());
        response.put("currentPage", salesPage.getNumber());
        response.put("totalItems", salesPage.getTotalElements());
        response.put("totalPages", salesPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get sales for a location and date range
     */
    @GetMapping("/location/{locationId}/date-range")
    public ResponseEntity<?> getSalesByLocationAndDateRange(
            @PathVariable Long locationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Get sales for the specified location and date range with pagination
        Page<Sale> salesPage = posService.findSalesByLocationAndDateRange(locationId, startDate, endDate, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", salesPage.getContent());
        response.put("currentPage", salesPage.getNumber());
        response.put("totalItems", salesPage.getTotalElements());
        response.put("totalPages", salesPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the total count of sales between two dates
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

    /**
     * Get sale by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Optional<Sale> sale = posService.getSaleById(id);
        return sale.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new sale
     */
    @PostMapping
    public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
        Sale createdSale = posService.createSale(sale);
        return new ResponseEntity<>(createdSale, HttpStatus.CREATED);
    }
}