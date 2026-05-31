package com.lzl.experiment11.state.validation;

import com.lzl.experiment11.state.UserRole;

public class ValidationContext {
    private final String operation;
    private final String productName;
    private final int quantity;
    private final int maxQuantity;
    private final String supplier;
    private final UserRole role;

    private ValidationContext(String operation, String productName, int quantity, int maxQuantity,
                              String supplier, UserRole role) {
        this.operation = operation;
        this.productName = productName;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
        this.supplier = supplier;
        this.role = role;
    }

    public static ValidationContext edit(String productName, int quantity, String supplier) {
        return new ValidationContext("编辑采购单", productName, quantity, Integer.MAX_VALUE, supplier, null);
    }

    public static ValidationContext quantity(String operation, int quantity, int maxQuantity) {
        return new ValidationContext(operation, null, quantity, maxQuantity, null, null);
    }

    public static ValidationContext admin(String operation, UserRole role) {
        return new ValidationContext(operation, null, 0, Integer.MAX_VALUE, null, role);
    }

    public String getOperation() {
        return operation;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public UserRole getRole() {
        return role;
    }
}
