package com.annatraders.repository;

import com.annatraders.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOrderBySaleDateDesc();
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
