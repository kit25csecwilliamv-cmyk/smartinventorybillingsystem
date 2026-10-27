package com.annatraders.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime saleDate;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {}

    public Sale(String customerName, BigDecimal totalAmount, LocalDateTime saleDate) {
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public List<SaleItem> getItems() { return items; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public void setItems(List<SaleItem> items) { this.items = items; }
}
