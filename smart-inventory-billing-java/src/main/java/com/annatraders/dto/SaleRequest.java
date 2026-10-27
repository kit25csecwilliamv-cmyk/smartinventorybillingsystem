package com.annatraders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SaleRequest {
    @NotBlank
    private String customerName;

    @Valid
    @NotEmpty
    private List<SaleLineRequest> items;

    public String getCustomerName() { return customerName; }
    public List<SaleLineRequest> getItems() { return items; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setItems(List<SaleLineRequest> items) { this.items = items; }

    public static class SaleLineRequest {
        @NotNull
        private Long productId;
        @Min(1)
        private int quantity;

        public Long getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public void setProductId(Long productId) { this.productId = productId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
