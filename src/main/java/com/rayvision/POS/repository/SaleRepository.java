package com.rayvision.POS.repository;

import com.rayvision.POS.domain.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    /**
     * Find sales between two dates with pagination
     */
    Page<Sale> findBySaleDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find sales between two dates without pagination
     */
    List<Sale> findBySaleDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count sales between two dates
     */
    long countBySaleDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find sales by location ID
     */
    List<Sale> findByLocationId(Long locationId);
    
    /**
     * Find sales by location ID with pagination
     */
    Page<Sale> findByLocationId(Long locationId, Pageable pageable);
    
    /**
     * Find sales by location ID between two dates
     */
    List<Sale> findByLocationIdAndSaleDateTimeBetween(Long locationId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find sales by location ID between two dates with pagination
     */
    Page<Sale> findByLocationIdAndSaleDateTimeBetween(Long locationId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Count sales by location ID
     */
    long countByLocationId(Long locationId);
    
    /**
     * Count sales by location ID between two dates
     */
    long countByLocationIdAndSaleDateTimeBetween(Long locationId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find the most recent sale with a posReference that starts with the given prefix
     */
    @Query(value = "SELECT s.pos_reference FROM sale s WHERE s.pos_reference LIKE :prefix% ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopByPosReferenceStartingWithOrderByIdDesc(@Param("prefix") String prefix);
    
    /**
     * Find the most recent sale with a posReference that starts with the given prefix for a specific location
     */
    @Query(value = "SELECT s.pos_reference FROM sale s WHERE s.pos_reference LIKE :prefix% AND s.location_id = :locationId ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<String> findTopByPosReferenceStartingWithAndLocationIdOrderByIdDesc(@Param("prefix") String prefix, @Param("locationId") Long locationId);
}
