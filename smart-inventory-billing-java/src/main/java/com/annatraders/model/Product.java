package com.annatraders.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String category;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Min(0)
    @Column(nullable = false)
    private int stockQuantity;

    @Min(0)
    @Column(nullable = false)
    private int minStockLevel;

    public Product() {}

    public Product(String name, String category, BigDecimal purchasePrice,
                   BigDecimal sellingPrice, int stockQuantity, int minStockLevel) {
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMinStockLevel() { return minStockLevel; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
}
