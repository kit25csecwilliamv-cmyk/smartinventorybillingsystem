package com.annatraders.service;

import com.annatraders.dto.SaleRequest;
import com.annatraders.model.Product;
import com.annatraders.model.Sale;
import com.annatraders.model.SaleItem;
import com.annatraders.repository.ProductRepository;
import com.annatraders.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public InventoryService(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public Sale createSale(SaleRequest request) {
        Sale sale = new Sale(request.getCustomerName().trim(), BigDecimal.ZERO, LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;

        for (SaleRequest.SaleLineRequest line : request.getItems()) {
            Product product = productRepository.findById(line.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + line.getProductId()));

            if (line.getQuantity() < 1) {
                throw new IllegalArgumentException("Quantity must be at least 1.");
            }
            if (product.getStockQuantity() < line.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getName()
                        + ". Available: " + product.getStockQuantity());
            }

            BigDecimal subtotal = product.getSellingPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
            total = total.add(subtotal);
            product.setStockQuantity(product.getStockQuantity() - line.getQuantity());
            productRepository.save(product);
            sale.addItem(new SaleItem(product, line.getQuantity(), product.getSellingPrice(), subtotal));
        }

        sale.setTotalAmount(total);
        return saleRepository.save(sale);
    }

    public List<Product> searchProducts(String q) {
        if (q == null || q.isBlank()) return productRepository.findAll();
        return productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(q, q);
    }

    public List<Product> lowStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getStockQuantity() <= p.getMinStockLevel())
                .toList();
    }
}
