package com.rayvision.POS.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private Long companyId;  // Reference to parent company
}