package com.rayvision.POS.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String posCode;
    private String menuItemName;  // Changed from itemName to menuItemName to match API
    private Double quantity;
    private Double unitPrice;     // Changed from price to unitPrice to match API
    
    // Extended price (quantity * unitPrice)
    private Double extended;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sale_id")
    @JsonBackReference
    private Sale sale;
    
    @PrePersist
    @PreUpdate
    public void calculateExtended() {
        this.extended = this.quantity * this.unitPrice;
    }
}
