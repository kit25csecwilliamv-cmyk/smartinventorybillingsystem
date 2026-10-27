package com.annatraders.controller;

import com.annatraders.repository.ProductRepository;
import com.annatraders.repository.SaleRepository;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    private final ProductRepository products;
    private final SaleRepository sales;

    public DashboardController(ProductRepository products, SaleRepository sales) {
        this.products = products;
        this.sales = sales;
    }

    @GetMapping
    public Map<String, Object> summary() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        var todaySales = sales.findBySaleDateBetween(start, end);
        BigDecimal todayTotal = todaySales.stream()
                .map(s -> s.getTotalAmount() == null ? BigDecimal.ZERO : s.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long stockUnits = products.findAll().stream().mapToLong(p -> p.getStockQuantity()).sum();
        long lowStockCount = products.findAll().stream()
                .filter(p -> p.getStockQuantity() <= p.getMinStockLevel()).count();

        return Map.of(
                "productCount", products.count(),
                "stockUnits", stockUnits,
                "lowStockCount", lowStockCount,
                "todaySales", todayTotal,
                "totalBillsToday", todaySales.size()
        );
    }
}
