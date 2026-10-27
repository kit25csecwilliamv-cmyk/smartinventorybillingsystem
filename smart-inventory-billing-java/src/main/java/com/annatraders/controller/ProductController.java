package com.annatraders.controller;

import com.annatraders.model.Product;
import com.annatraders.repository.ProductRepository;
import com.annatraders.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductRepository repository;
    private final InventoryService inventoryService;

    public ProductController(ProductRepository repository, InventoryService inventoryService) {
        this.repository = repository;
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String q) {
        return inventoryService.searchProducts(q);
    }

    @GetMapping("/low-stock")
    public List<Product> lowStock() {
        return inventoryService.lowStockProducts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        product.setId(null);
        return repository.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product input) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        product.setName(input.getName());
        product.setCategory(input.getCategory());
        product.setPurchasePrice(input.getPurchasePrice());
        product.setSellingPrice(input.getSellingPrice());
        product.setStockQuantity(input.getStockQuantity());
        product.setMinStockLevel(input.getMinStockLevel());
        return repository.save(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("Product not found: " + id);
        repository.deleteById(id);
    }
}
