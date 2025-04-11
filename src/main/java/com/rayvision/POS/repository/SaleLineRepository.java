package com.rayvision.POS.repository;

import com.rayvision.POS.domain.SaleLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleLineRepository extends JpaRepository<SaleLine, Long> {

}
