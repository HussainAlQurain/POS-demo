package com.rayvision.POS.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime saleDateTime;   // date/time of the sale
    private String posReference;          // POS reference
    private Long locationId;              // location ID (always 1 as per requirements)

    @OneToMany(mappedBy="sale", cascade= CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference
    private List<SaleLine> lines = new ArrayList<>();
    
    // Calculated total for the sale
    private Double total;
    
    // Helper method to calculate the total
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (lines != null && !lines.isEmpty()) {
            total = lines.stream()
                .mapToDouble(line -> line.getQuantity() * line.getUnitPrice())
                .sum();
        } else {
            total = 0.0;
        }
    }
}
