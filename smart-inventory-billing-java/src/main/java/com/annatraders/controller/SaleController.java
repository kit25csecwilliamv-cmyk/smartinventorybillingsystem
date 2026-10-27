package com.annatraders.controller;

import com.annatraders.dto.SaleRequest;
import com.annatraders.model.Sale;
import com.annatraders.repository.SaleRepository;
import com.annatraders.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin
public class SaleController {
    private final InventoryService inventoryService;
    private final SaleRepository saleRepository;

    public SaleController(InventoryService inventoryService, SaleRepository saleRepository) {
        this.inventoryService = inventoryService;
        this.saleRepository = saleRepository;
    }

    @GetMapping
    public List<Sale> getSales() {
        return saleRepository.findAllByOrderBySaleDateDesc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sale createSale(@Valid @RequestBody SaleRequest request) {
        return inventoryService.createSale(request);
    }
}
